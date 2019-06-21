package com.ccnu.bookmanagement.activity;

import android.content.ContentValues;
import android.support.annotation.Nullable;
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

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReturnActivity extends AppCompatActivity {

    private AppCompatAutoCompleteTextView mTvAutoCompleteBookId;
    private  AppCompatAutoCompleteTextView mTvAcReaderId;
    private Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_activity);
        initView();
    }

    private void initView() {
        btnReturn = (Button) this.findViewById(R.id.btn_return);
        mTvAutoCompleteBookId = (AppCompatAutoCompleteTextView) this.findViewById(R.id.tv_auto_complete_book_id);
        mTvAcReaderId = (AppCompatAutoCompleteTextView) this.findViewById(R.id.tv_auto_complete_reader_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("图书归还");
        initBookIdAutoComplete();
        initReaderIdAutoComplete();
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mTvAcReaderId.getText().toString())){
                    Toast.makeText(ReturnActivity.this,"请输入一卡通账号",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mTvAutoCompleteBookId.getText().toString())){
                    Toast.makeText(ReturnActivity.this,"请输入图书编号",Toast.LENGTH_SHORT).show();
                }else {saveReturnData();}
            }
        });
    }

    private void saveReturnData() {

        List<Reader> readerList= DataSupport.where("id_number=?",mTvAcReaderId.getText().toString()).find(Reader.class);
        Reader reader = readerList.get(0);
        if (reader == null) {
            Toast.makeText(this,"该一卡通账号不存在，请检查输入",Toast.LENGTH_SHORT).show();
            return;
        }
        Book book = DataSupport.find(Book.class, (long) getNumberFromEt(mTvAutoCompleteBookId, -1));
        if (book == null) {
            Toast.makeText(this,"该图书编号不存在，请检查",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!book.isBorrowed()) {//目前没有被借，说明已还
            Toast.makeText(this,"该书在馆，无需归还",Toast.LENGTH_SHORT).show();
            return;
        }
        //查找对应的记录
        Borrow borrowRecord = getBorrowRecord(reader, book);
        if (borrowRecord == null) {
            Toast.makeText(this,"您已经归还此书",Toast.LENGTH_SHORT).show();

            return;
        }
        //进行保存
        doSave(reader, book, borrowRecord);
    }

    @Nullable
    private Borrow getBorrowRecord(Reader reader, Book book) {
        List<Borrow> borrowList = DataSupport
                .where("book_id =?and reader_id=?", String.valueOf(book.getId()), String.valueOf(reader.getId()))
                .find(Borrow.class);
        Borrow borrowRecord = null;
        for (Borrow borrow : borrowList) {
            if (borrow.getReturnDate() == null) {//还书日期为空，说明未还
                borrowRecord = borrow;
            }
        }
        return borrowRecord;
    }

    private void doSave(Reader reader, Book book, Borrow borrowRecord) {
        Connector.getDatabase().beginTransaction();//开启事务
        //更新读者信息
        ContentValues readerCv = new ContentValues();
        readerCv.put("currentborrowcount", reader.getCurrentBorrowCount() - 1);
        DataSupport.update(Reader.class, readerCv, reader.getId());
        //更新书籍信息
        ContentValues bookCv = new ContentValues();
        bookCv.put("isborrowed", false);
        DataSupport.update(Book.class, bookCv, book.getId());
        //更新借阅信息
        borrowRecord.setReturnDate(new Date());//归还日期
        borrowRecord.setReader(reader);
        borrowRecord.setBook(book);
        if (borrowRecord.save()) {
            Connector.getDatabase().setTransactionSuccessful();//设置事务成功
            Toast.makeText(this, "还书成功", Toast.LENGTH_SHORT).show();
            Connector.getDatabase().endTransaction();//结束事务
            finish();
        } else {
            Connector.getDatabase().endTransaction();//结束事务
            Toast.makeText(this, "还书失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initBookIdAutoComplete() {
        List<Book> bookList = DataSupport.select("id").find(Book.class);
        List<String> bookIdList = new ArrayList<>();
        for (Book book : bookList) {
            bookIdList.add(String.valueOf(book.getId()));
        }
        ArrayAdapter<String> bookIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bookIdList);
        mTvAutoCompleteBookId.setAdapter(bookIdAdapter);
    }

    private void initReaderIdAutoComplete() {
        List<Reader> readerList = DataSupport.findAll(Reader.class);
        List<String> readerNameList = new ArrayList<>();
        for (Reader reader : readerList) {
            readerNameList.add(reader.getId_number());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, readerNameList);
        mTvAcReaderId.setAdapter(arrayAdapter);
    }

    protected int getNumberFromEt(EditText et, int defaultValue) {
        String str = et.getText().toString();
        return TextUtils.isEmpty(str) ? defaultValue : Integer.parseInt(str);
    }
}
