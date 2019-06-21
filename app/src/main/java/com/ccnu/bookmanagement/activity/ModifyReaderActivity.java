package com.ccnu.bookmanagement.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.adapter.ReaderRvAdapter;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.contract.ModifyReaderContract;
import com.ccnu.bookmanagement.presenter.ModifyReaderPresenter;
import com.ccnu.bookmanagement.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModifyReaderActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEtReaderName,mEtHomeAddress,mEtPhone,mEtEmail,mEtRemark;
    private Spinner mSpReaderType;
    private RadioButton mRbMan,mRbWomen;
    private TextView mTvEnrollDate;
    private ImageView ivDelete;
    private Button mBtnModifyReader;

    private Reader reader;
    private long mReaderId;
    private Date mEnrollDate;
    private List<ReaderType> mReaderTypeList;
    private ReaderRvAdapter mAdapter;
    private static final String KEY_READER_ID = "READER_ID";
    private List<Reader> readerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reader);
        initView();

    }


    private void initView() {
        mEtReaderName = (EditText) this.findViewById(R.id.et_reader_name);
        mEtHomeAddress = (EditText) this.findViewById(R.id.et_home_address);
        mEtPhone = (EditText) this.findViewById(R.id.et_phone);
        mEtEmail = (EditText) this.findViewById(R.id.et_email);
        mEtRemark = (EditText) this.findViewById(R.id.et_remark);
        mTvEnrollDate = (TextView) this.findViewById(R.id.tv_enroll_date);
        mSpReaderType = (Spinner) this.findViewById(R.id.sp_reader_type);
        ivDelete = (ImageView)this.findViewById(R.id.iv_delete);
        mRbMan = (RadioButton) this.findViewById(R.id.rb_man);
        mRbWomen = (RadioButton) this.findViewById(R.id.rb_women);
        mBtnModifyReader = (Button) this.findViewById(R.id.btn_modify_reader);
        mAdapter = new ReaderRvAdapter();

        mReaderTypeList = DataSupport.findAll(ReaderType.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("修改读者信息");
        mReaderId = getIntent().getLongExtra(KEY_READER_ID, -1);
        reader = DataSupport.find(Reader.class, mReaderId);
        mEnrollDate = reader.getEnrollDate();
        mEtReaderName.setText(reader.getName());
        initSpinnerSelection();
        if (reader.getGender().equals("男")) {
            mRbMan.setChecked(true);
        } else {
            mRbWomen.setChecked(true);
        }
        mEtHomeAddress.setText(reader.getId_number());
        mEtPhone.setText(reader.getPhoneNum());
        mEtEmail.setText(reader.getEmail());
        mTvEnrollDate.setText(String.format("登记日期：%s", DateUtil.dayFormat(mEnrollDate)));
        mEtRemark.setText(reader.getRemark());
        mBtnModifyReader.setOnClickListener(this);
        mTvEnrollDate.setOnClickListener(this);
        ivDelete.setOnClickListener(this);

    }

    private void initSpinnerSelection() {
        initSpinnerData();
        // 初始化 spinner  选中的位置
        long readerTypeId = 0;
        Cursor cursor = DataSupport.findBySQL("select readertype_id from reader where id = " + mReaderId);
        if (cursor != null && cursor.moveToFirst()) {
            readerTypeId = cursor.getLong(0);
        }
        int index = 0;
        for (ReaderType readerType : mReaderTypeList) {
            if (readerType.getId() == readerTypeId) {
                break;
            }
            index++;
        }
        mSpReaderType.setSelection(index);//加一，因为前面增加了「请选择项」
    }

    private void initSpinnerData() {
        mReaderTypeList = DataSupport.findAll(ReaderType.class);
        List<String> typeNameList = new ArrayList<>();
        for (ReaderType readerType : mReaderTypeList) {
            typeNameList.add(readerType.getTypeName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, typeNameList);
        mSpReaderType.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_reader:
                System.out.println("修改读者信息");
                modifyReader();
                break;
            case R.id.iv_delete:
                new AlertDialog.Builder(this)
                        .setMessage("确定删除" + (reader == null ? "" : reader.getName()) + "?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            DataSupport.delete(Reader.class, mReaderId);
                            finish();
                        }).show();

                readerList = DataSupport.findAll(Reader.class);
                mAdapter.updateData(readerList);
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.tv_enroll_date:
                showDatePicker();
                break;
            default:
                break;
        }
    }


    public static void startActivity(Context context, long ID) {
        Intent intent = new Intent(context, ModifyReaderActivity.class);
        intent.putExtra(KEY_READER_ID, ID);
        context.startActivity(intent);
    }

    private void modifyReader() {
        if (mSpReaderType.getSelectedItemPosition() < 0) {
            Toast.makeText(this,"请选择读者类型" , Toast.LENGTH_SHORT).show();

            return;
        }

        if (nullCheck(mEtReaderName, "读者姓名")
                || nullCheck(mEtPhone, "联系方式")
                || nullCheck(mEtHomeAddress, "一卡通账号")) {
            return;
        }

        if (mEnrollDate == null) {
            Toast.makeText(this, "请选择登记日期", Toast.LENGTH_SHORT).show();
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

        Reader reader = DataSupport.find(Reader.class, mReaderId);
        reader.setPhoneNum(mEtPhone.getText().toString());
        reader.setEmail(mEtEmail.getText().toString());
        reader.setEnrollDate(mEnrollDate);
        reader.setGender(mRbMan.isChecked() ? "男" : "女");
        reader.setRemark(mEtRemark.getText().toString());
        reader.setReaderType(mReaderTypeList.get(mSpReaderType.getSelectedItemPosition()));//这里要减一，因为前面手动添加了一个提示项
        if(reader.save()){
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
        }    ;


//        DataSupport.update(Reader.class,values,mReaderId);
//        readerList = DataSupport.findAll(Reader.class);
//        mAdapter.updateData(readerList);
//        mAdapter.notifyDataSetChanged();
    }

    private void showDatePicker() {
        hideSoftInput();//隐藏软键盘，防止遮挡日期选择框
        new TimePickerView.Builder(this, (date, v1) -> {
            mEnrollDate = date;
            mTvEnrollDate.setText(String.format("登记日期：%s", DateUtil.dayFormat(date)));
        }).setDate(Calendar.getInstance())
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .build()
                .show();
    }

    protected void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }

    }

    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(et.getText().toString())) {
            Toast.makeText(this, msg+"不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}
