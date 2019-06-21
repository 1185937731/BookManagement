package com.ccnu.bookmanagement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.util.DateUtil;
import com.bigkoo.pickerview.TimePickerView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddReaderActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private EditText mEtReaderName,mEtHomeAddress,mEtPhone,mEtEmail,mEtRemark;
    private Spinner mSpReaderType;
    private RadioButton mRbMan,mRbWomen;
    private TextView mTvEnrollDate;
    private Button mBtnAddReader;
    private Date mEnrollDate;
    private List<ReaderType> mReaderTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reader);
        initView();
        initListener();
    }



    private void initView() {
        mEtReaderName = (EditText) this.findViewById(R.id.et_reader_name);
        mEtHomeAddress = (EditText) this.findViewById(R.id.et_home_address);
        mEtPhone = (EditText) this.findViewById(R.id.et_phone);
        mEtEmail = (EditText) this.findViewById(R.id.et_email);
        mEtRemark = (EditText) this.findViewById(R.id.et_remark);
        mTvEnrollDate = (TextView) this.findViewById(R.id.tv_enroll_date);
        mSpReaderType = (Spinner) this.findViewById(R.id.sp_reader_type);
        mRbMan = (RadioButton) this.findViewById(R.id.rb_man);
        mRbWomen = (RadioButton) this.findViewById(R.id.rb_women);
        mBtnAddReader = (Button)this.findViewById(R.id.btn_add_reader);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("添加读者");
        mBtnAddReader.setOnClickListener(this);
        mTvEnrollDate.setOnClickListener(this);
    }

    protected void initListener() {
        mReaderTypeList = DataSupport.findAll(ReaderType.class);
        List<String> typeNameList = new ArrayList<>();
        for (ReaderType readerType : mReaderTypeList) {
            typeNameList.add(readerType.getTypeName());
            System.out.print("读者类型"+readerType.getTypeName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, typeNameList);
        mSpReaderType.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_reader:
                System.out.println("添加读者");
                saveReader();
                break;
            case R.id.tv_enroll_date:
                showDatePicker();
                break;
            default:
                break;
        }
    }

    private void saveReader() {
        if (mSpReaderType.getSelectedItemPosition() < 0) {
            Toast.makeText(AddReaderActivity.this,"请选择读者类型",Toast.LENGTH_SHORT).show();
            return;
        }

        if (nullCheck(mEtReaderName, "读者姓名")
                || nullCheck(mEtHomeAddress, "一卡通账号")) {
            return;
        }

        if (mEnrollDate == null) {
            Toast.makeText(AddReaderActivity.this,"请选择登记日期",Toast.LENGTH_SHORT).show();
            return;
        }

        List<Reader> readerList= DataSupport.where("id_number=?",mEtHomeAddress.getText().toString()).find(Reader.class);

        Reader reader = new Reader();
        if (readerList.size()!=0){
            Toast.makeText(this,"该账号已存在，请确认",Toast.LENGTH_SHORT).show();
        }else {
            reader.setName(mEtReaderName.getText().toString());
            reader.setPhoneNum(mEtPhone.getText().toString());
            reader.setId_number(mEtHomeAddress.getText().toString());
            reader.setEmail(mEtEmail.getText().toString());
            reader.setEnrollDate(mEnrollDate);
//            reader.setPassword("123456");
//            reader.setMonitor(0);
            reader.setGender(mRbMan.isChecked() ? "男" : "女");
            reader.setRemark(mEtRemark.getText().toString());
            reader.setReaderType(mReaderTypeList.get(mSpReaderType.getSelectedItemPosition()));//这里要减一，因为前面手动添加了一个提示项
            resolveSave(reader,"添加成功","添加失败，请重试");
        }

    }

    private void showDatePicker() {
        hideSoftInput();//隐藏软键盘
        new TimePickerView.Builder(AddReaderActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v){mEnrollDate = date;
                mTvEnrollDate.setText(String.format("登记日期：%s", DateUtil.dayFormat(date)));}
        }).setDate(Calendar.getInstance())
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .build()
                .show();
    }
    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(et.getText().toString())) {
            Toast.makeText(AddReaderActivity.this, msg + "不能为空", Toast.LENGTH_SHORT).show();
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

    //隐藏软键盘
    protected void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }

    }


}
