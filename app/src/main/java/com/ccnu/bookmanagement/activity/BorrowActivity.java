package com.ccnu.bookmanagement.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.Book;
import com.ccnu.bookmanagement.bean.Borrow;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.bean.ReaderType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class BorrowActivity extends AppCompatActivity {

    private AppCompatAutoCompleteTextView mTvAutoCompleteBookId;
    private  AppCompatAutoCompleteTextView mTvAcReaderId;
    private Button btnBorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        initView();
    }

    private void initView() {
        btnBorrow = (Button)this.findViewById(R.id.btn_borrow);
        mTvAutoCompleteBookId=(AppCompatAutoCompleteTextView)this.findViewById(R.id.tv_auto_complete_book_id);
        mTvAcReaderId=(AppCompatAutoCompleteTextView)this.findViewById(R.id.tv_auto_complete_reader_id);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("图书借阅");
        initReaderIdAutoComplete();
        initBookIdAutoComplete();
        btnBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mTvAcReaderId.getText().toString())){
                    Toast.makeText(BorrowActivity.this,"请输入一卡通账号",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mTvAutoCompleteBookId.getText().toString())){
                    Toast.makeText(BorrowActivity.this,"请输入图书编号",Toast.LENGTH_SHORT).show();
                }else {saveBorrowData();}

            }
        });
    }
    private void saveBorrowData() {
        // 检查书籍是否存在，是否已被借出，检查读者的借书数量是否已经达到上限
        List<Reader> readerList= DataSupport.where("id_number=?",mTvAcReaderId.getText().toString()).find(Reader.class);
        Reader reader = readerList.get(0);
// Reader reader = DataSupport.find(Reader.class, (long) getNumberFromEt(mTvAcReaderId, -1));
        if (reader == null) {
            Toast.makeText(this,"该借书证号不存在，请检查输入",Toast.LENGTH_SHORT).show();
            return;
        }
        Book book = DataSupport.find(Book.class, (long) getNumberFromEt(mTvAutoCompleteBookId, -1));
        if (book == null) {
            Toast.makeText(this,"该图书编号不存在，请检查输入",Toast.LENGTH_SHORT).show();
            return;
        }
        if (book.isBorrowed()) {
            Toast.makeText(this,"抱歉，该图书已经借出",Toast.LENGTH_SHORT).show();
            return;
        }

        int maxBorrowCount = getMaxBorrowCount(reader);
        if (maxBorrowCount <= reader.getCurrentBorrowCount()) {
            Toast.makeText(this,"您的借书数量已达上限，请先还书",Toast.LENGTH_SHORT).show();
            return;
        }

        doSave(reader, book);
    }

    private int getMaxBorrowCount(Reader reader) {
        int maxBorrowCount = 4;//最大借书数量（本科生为 4 本，其他都大于该值）
        //先找到 readertype_id,然后再根据 readertype_id 找出该读者的最大借书数量
        Cursor cursor = DataSupport.findBySQL(
                "select * " +
                        " from reader " +
                        " where id=" + reader.getId());
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("readertype_id");
            if (index != -1) {
                ReaderType readerType = DataSupport.find(ReaderType.class, cursor.getLong(index));
                maxBorrowCount = readerType.getBorrowCount();
            }
            cursor.close();
        }
        return maxBorrowCount;
    }

    private void doSave(Reader reader, Book book) {

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setReader(reader);
        borrow.setBorrowDate(new Date());

        if (borrow.save()) {
            Toast.makeText(this, "借阅成功", Toast.LENGTH_SHORT).show();
            reader.increaseCurrentBorrowCount();//当前借书数目 + 1
            reader.update(reader.getId());
            book.setBorrowed(true);
            book.update(book.getId());
            finish();
        } else {
            Toast.makeText(this, "借阅失败，请重试", Toast.LENGTH_SHORT).show();
        }

    }

    private void initBookIdAutoComplete() {
        List<Book> bookList = DataSupport.select("id").find(Book.class);
        List<String> bookIdList = new ArrayList<>();
        for (Book book : bookList) {
            bookIdList.add(String.valueOf(book.getId()));
        }
        ArrayAdapter<String> bookIdAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, bookIdList);
        mTvAutoCompleteBookId.setAdapter(bookIdAdapter);
    }

    private void initReaderIdAutoComplete() {
        List<Reader> readerList = DataSupport.findAll(Reader.class);
        List<String> readerNameList = new ArrayList<>();
//        List<String> readerIdList = new ArrayList<>();
        for (Reader reader : readerList) {
            readerNameList.add(reader.getId_number());
//            readerIdList.add(String.valueOf(reader.getId()));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, readerNameList);
        mTvAcReaderId.setAdapter(arrayAdapter);
    }

    protected int getNumberFromEt(EditText et, int defaultValue) {
        String str = et.getText().toString();
        return TextUtils.isEmpty(str) ? defaultValue : Integer.parseInt(str);
    }

}
