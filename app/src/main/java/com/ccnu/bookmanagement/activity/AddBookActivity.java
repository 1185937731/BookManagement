package com.ccnu.bookmanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.Book;
import com.ccnu.bookmanagement.bean.BookType;
import com.ccnu.bookmanagement.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etBookName,etAuthorName,etPressName,etPrice,etPageNum,etKeywords,etRemark;
    private Spinner spBookType;
    private TextView tvPublishDate,tvEnrollDate;
    private Button btnAddBook;

    private Date publishDate,enrollDate;
    private List<BookType> bookTypeList;
    private ArrayAdapter<String> spArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        initView();
        initListener();
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
        spBookType = (Spinner) this.findViewById(R.id.sp_book_type);

        btnAddBook = (Button)this.findViewById(R.id.btn_add_book);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("添加图书");
        btnAddBook.setOnClickListener(this);
        tvPublishDate.setOnClickListener(this);
        tvEnrollDate.setOnClickListener(this);
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
            case R.id.btn_add_book:
                saveBook();
                break;
            default:
                break;
        }
    }

    protected void initListener() {
        spBookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == bookTypeList.size()) {//如果点击了末尾项，跳转到添加类型页面。因为头部 + 1，后面 +1，所以总长度为 size+2,最后一项下标为 size+1
                    startActivity(AddBookTypeActivity.class);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookTypeList = DataSupport.findAll(BookType.class);
        updateBookTypeSpinner();
    }

    private void updateBookTypeSpinner() {
        List<String> stringList = new ArrayList<>();
        for (BookType bookType : bookTypeList) {
            stringList.add(bookType.getTypeName());
        }
        stringList.add("添加新类型");
        if (spArrayAdapter == null) {
            spArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stringList);
            spBookType.setAdapter(spArrayAdapter);
        } else {
            spArrayAdapter.clear();//清除旧数据
            spArrayAdapter.addAll(stringList);//添加新数据
            spArrayAdapter.notifyDataSetChanged();
        }
    }
    private void saveBook() {
        if (spBookType.getSelectedItemPosition() < 0) {
            Toast.makeText(this,"请选择书籍类型",Toast.LENGTH_SHORT).show();
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

        Book book = new Book();
        book.setBookName(etBookName.getText().toString());
        book.setBookType(bookTypeList.get(spBookType.getSelectedItemPosition()));//这里要减一，因为前面手动添加了一个提示项
        book.setAuthorName(etAuthorName.getText().toString());
        book.setPressName(etPressName.getText().toString());
        book.setPublishDate(publishDate);
        book.setPrice(getNumberFromEt(etPrice, 0));
        book.setPages(getNumberFromEt(etPageNum, 0));
        book.setKeyWord(etKeywords.getText().toString());
        book.setEnrollDate(enrollDate);
        book.setBorrowed(false);//刚登记入库，默认未借出
        book.setRemark(etRemark.getText().toString());
        resolveSave(book);
    }

    private void showPickerView(TextView tv) {
        hideSoftInput();//隐藏软键盘
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

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        this.startActivity(intent);
    }

    protected void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService("input_method");
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
            Toast.makeText(this,msg + "不能为空",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    protected void resolveSave(DataSupport dataSupport) {
        if (dataSupport.save()) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}
