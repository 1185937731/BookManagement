package com.ccnu.bookmanagement.activity;

import android.opengl.ETC1;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;

public class AddReaderTypeActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etReaderCategoryName,etBorrowCount,etBorrowLen,etRemark;
    private TextView tvExpireDate;
    private Date ExpDate;
    private Button btnAddReaderType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reader_type);
        initView();
    }

    private void initView() {
        etReaderCategoryName = (EditText) this.findViewById(R.id.et_reader_category_name);
        etBorrowCount = (EditText) this.findViewById(R.id.et_borrow_count);
        etBorrowLen = (EditText) this.findViewById(R.id.et_borrow_len);
        etRemark = (EditText) this.findViewById(R.id.et_remark);
        tvExpireDate = (TextView) this.findViewById(R.id.tv_expire_date);
        btnAddReaderType = (Button)this.findViewById(R.id.btn_add_reader_type);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("添加读者类型");
        btnAddReaderType.setOnClickListener(this);
        tvExpireDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_reader_type:
                System.out.println("添加读者");
                saveReaderType();
                break;
            case R.id.tv_expire_date:
                showDatePicker();
                break;
            default:
                break;
        }
    }

    private void saveReaderType() {
        if (nullCheck(etReaderCategoryName, "读者类型名称")
                || nullCheck(etBorrowCount, "最大借书数量")
                || nullCheck(etBorrowLen, "借书期限")) {
            return;
        }

        if (ExpDate == null) {
            Toast.makeText(AddReaderTypeActivity.this,  "请设置失效日期", Toast.LENGTH_SHORT).show();
            return;
        }

        ReaderType readerType = new ReaderType();
        readerType.setTypeName(etReaderCategoryName.getText().toString());
        readerType.setBorrowCount(getNumberFromEt(etBorrowCount, 5));//若为空 默认为 5 本
        readerType.setBorrowLon(getNumberFromEt(etBorrowLen, 1));//如果为空默认为 1 个月
        readerType.setRemark(etRemark.getText().toString());
        readerType.setExpDate(ExpDate);
        resolveSave(readerType, "新读者类型添加成功", "添加失败，请重试");
    }

    protected void showDatePicker() {
        hideSoftInput();//隐藏软键盘
        new TimePickerView.Builder(this, (date, v1) -> {
            ExpDate = date;
            tvExpireDate.setText(String.format("失效日期：%s", DateUtil.dayFormat(date)));
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setDate(Calendar.getInstance())
                .build()
                .show();
    }

    //隐藏软键盘
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
            Toast.makeText(AddReaderTypeActivity.this, msg + "不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    protected void resolveSave(DataSupport dataSupport, String successMsg, String errMsg) {
        if (dataSupport.save()) {
            Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
