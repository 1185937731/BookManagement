package com.ccnu.bookmanagement.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.adapter.BookRvAdapter;
import com.ccnu.bookmanagement.bean.Book;
import com.ccnu.bookmanagement.bean.BookType;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModifyBookActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ModifyBookActivity";
    private EditText etBookName,etAuthorName,etPressName,etPrice,etPageNum,etKeywords,etRemark;
    private Spinner spBookType;
    private TextView tvPublishDate,tvEnrollDate;
    private Button btnModifyBook;
    private ImageView ivDelete;

    private Book book;
    private long mBookId;
    private Date publishDate,enrollDate;
    private BookRvAdapter bookRvAdapter;
    private List<BookType> bookTypeList;
    private ArrayAdapter<String> spArrayAdapter;
    private List<Book> bookList;

    private static final String KEY_BOOK_ID = "BOOK_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_book);
        initView();
    }

    private void initView() {
        etBookName = (EditText) this.findViewById(R.id.et_book_name);
        etAuthorName = (EditText) this.findViewById(R.id.et_author_name);
        etPressName = (EditText) this.findViewById(R.id.et_press_name);
        etPrice = (EditText) this.findViewById(R.id.et_price);
        etPageNum = (EditText) this.findViewById(R.id.et_page_num);
        etKeywords = (EditText) this.findViewById(R.id.et_keywords);
        etRemark = (EditText) this.findViewById(R.id.et_remark);
        tvPublishDate = (TextView) this.findViewById(R.id.tv_publish_date);
        tvEnrollDate = (TextView) this.findViewById(R.id.tv_enroll_date);
        ivDelete = (ImageView)this.findViewById(R.id.iv_delete);
        spBookType = (Spinner) this.findViewById(R.id.sp_book_type);
        btnModifyBook = (Button)this.findViewById(R.id.btn_modify_book);

        bookRvAdapter = new BookRvAdapter();
        bookTypeList = DataSupport.findAll(BookType.class);

        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
            getSupportActionBar().setTitle("修改书籍信息");

        mBookId = getIntent().getLongExtra(KEY_BOOK_ID, -1);
        book = DataSupport.find(Book.class, mBookId);
        etBookName.setText(book.getBookName());
        setBookTypeSelection();
        enrollDate = book.getEnrollDate();
        publishDate = book.getPublishDate();
        tvEnrollDate.setText(String.format("登记日期：%s", DateUtil.dayFormat(enrollDate)));
        tvPublishDate.setText(String.format("出版日期：%s", DateUtil.dayFormat(publishDate)));
        etAuthorName.setText(book.getAuthorName());
        etPressName.setText(book.getPressName());
        etPrice.setText(String.valueOf(book.getPrice()));
        etPageNum.setText(String.valueOf(book.getPages()));
        etKeywords.setText(book.getKeyWord());
        etRemark.setText(book.getRemark());
        btnModifyBook.setOnClickListener(this);
        tvPublishDate.setOnClickListener(this);
        tvEnrollDate.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
    }

    private void setBookTypeSelection() {
        initSpinnerData();
        long bookTypeId =0;
        Cursor cursor = DataSupport.findBySQL("select booktype_id from book where id = " + mBookId);
        //noinspection ConstantConditions
        if (cursor != null && cursor.moveToFirst()) {
            bookTypeId = cursor.getLong(0);//获取图书类型的 id
            Log.d(TAG, "setBookTypeSelection:bookTypeId " + bookTypeId);
        }
        int index = 0;
        for (BookType bookType : bookTypeList) {
            if (bookTypeId == bookType.getId()) {
                break;
            }
            index++;
        }
        spBookType.setSelection(index);//+1 因为前面添加了一个请选择项目
    }
    private void initSpinnerData() {
        bookTypeList = DataSupport.findAll(BookType.class);
        List<String> typeNameList = new ArrayList<>();
        for (BookType bookType : bookTypeList) {
            typeNameList.add(bookType.getTypeName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, typeNameList);
        spBookType.setAdapter(arrayAdapter);
    }

    public static void startActivity(Context context, long bookId) {
        Intent intent = new Intent(context, ModifyBookActivity.class);
        intent.putExtra(KEY_BOOK_ID, bookId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_publish_date:
                showPickerView(tvPublishDate);
                break;
            case R.id.tv_enroll_date:
                showPickerView(tvEnrollDate);
                break;
            case R.id.btn_modify_book:
                modifyBook();
                break;
            case R.id.iv_delete:
                new AlertDialog.Builder(this)
                        .setMessage("确定删除" + (book == null ? "" : book.getBookName()) + "?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            DataSupport.delete(Book.class, mBookId);
                            finish();
                        }).show();
                bookList = DataSupport.findAll(Book.class);
                bookRvAdapter.updateData(bookList);
                bookRvAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void modifyBook() {
        if (spBookType.getSelectedItemPosition() < 0) {
            Toast.makeText(this,"请选择书籍类型" , Toast.LENGTH_SHORT).show();

            return;
        }

        if (nullCheck(etBookName, "书籍名称")
                || nullCheck(etAuthorName, "作者")
                || nullCheck(etPressName, "出版社")
                ) {
            return;
        }
        if (publishDate == null) {
            Toast.makeText(this,"请选择出版日期",Toast.LENGTH_SHORT).show();
            return;
        }
        if (enrollDate == null) {
            Toast.makeText(this,"请选择登记日期",Toast.LENGTH_SHORT).show();
            return;
        }
        if (enrollDate.before(publishDate)) {
            Toast.makeText(this,"登记日期不能早于出版日期",Toast.LENGTH_SHORT).show();
            return;
        }
//        ContentValues values = new ContentValues();
//        values.put("name", mEtReaderName.getText().toString());
//        values.put("readerType", mReaderTypeList.get(mSpReaderType.getSelectedItemPosition()).getTypeName());
//        values.put("gender",mRbMan.isChecked() ? "男" : "女");
//        values.put("phoneNum", mEtPhone.getText().toString());
//        values.put("id_number", mEtHomeAddress.getText().toString());
//        values.put("email", mEtEmail.getText().toString());
//        values.put("enrollDate",DateUtil.dayFormat(mEnrollDate));
//        values.put("remark", mEtRemark.getText().toString());

        Book book = DataSupport.find(Book.class, mBookId);
        book.setBookName(etBookName.getText().toString());
        book.setBookType(bookTypeList.get(spBookType.getSelectedItemPosition() ));//这里要减一，因为前面手动添加了一个提示项
        book.setAuthorName(etAuthorName.getText().toString());
        book.setPressName(etPressName.getText().toString());
        book.setPublishDate(publishDate);
        book.setEnrollDate(enrollDate);
        book.setPrice(getNumberFromEt(etPrice, 0));
        book.setPages(getNumberFromEt(etPageNum, 0));
        book.setKeyWord(etKeywords.getText().toString());
//        book.setBorrowed(false);//默认未借出
        book.setRemark(etRemark.getText().toString());

        if(book.save()){
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
        };
//        DataSupport.update(Reader.class,values,mReaderId);
//        readerList = DataSupport.findAll(Reader.class);
//        mAdapter.updateData(readerList);
//        mAdapter.notifyDataSetChanged();
    }


    private void showPickerView(TextView tv) {
        hideSoftInput();
        new TimePickerView.Builder(this, (date, v) -> {
            switch (tv.getId()) {
                case R.id.tv_publish_date:
                    publishDate = date;
                    tvPublishDate.setText(String.format("出版日期：%s", DateUtil.dayFormat(date)));
                    break;
                case R.id.tv_enroll_date:
                    enrollDate = date;
                    tvEnrollDate.setText(String.format("登记日期：%s", DateUtil.dayFormat(date)));
                    break;
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setDate(Calendar.getInstance())
                .build()
                .show();
    }

    protected void hideSoftInput() {
        @SuppressLint("WrongConstant") InputMethodManager imm = (InputMethodManager)this.getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }

    }

    protected int getNumberFromEt(EditText et, int defaultValue) {
        String str = et.getText().toString();
        return TextUtils.isEmpty(str) ? defaultValue : Integer.parseInt(str);
    }

    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(et.getText().toString())) {
            Toast.makeText(this, msg+"不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
