package com.ccnu.bookmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.util.DateUtil;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;

public class ModifyReaderTypeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_READER_TYPE_ID = "READER_TYPE_ID";

    private EditText etReaderCategoryName,etBorrowCount,etBorrowLen,etRemark;
    private TextView tvExpireDate;
    private Button btnModifyReaderType;

    private long mReaderTypeId;
    private ReaderType readerType;
    private Date mExpDate;//「到期时间」

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reader_type);
        initView();
    }

    private void initView() {
        etReaderCategoryName = (EditText) this.findViewById(R.id.et_reader_category_name);
        etBorrowCount = (EditText) this.findViewById(R.id.et_borrow_count);
        etBorrowLen = (EditText) this.findViewById(R.id.et_borrow_len);
        etRemark = (EditText) this.findViewById(R.id.et_remark);
        tvExpireDate = (TextView) this.findViewById(R.id.tv_expire_date);
        btnModifyReaderType = (Button)this.findViewById(R.id.btn_modify_reader_type);
        mReaderTypeId = getIntent().getLongExtra(KEY_READER_TYPE_ID, -1);
        readerType = DataSupport.find(ReaderType.class, mReaderTypeId);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("读者类型修改");
        mExpDate = readerType.getExpDate();
        etReaderCategoryName.setText(readerType.getTypeName());
        etBorrowCount.setText(readerType.getBorrowCount()+"");
        etBorrowLen.setText(readerType.getBorrowLon()+"");
        tvExpireDate.setText(String.format("失效日期：%s", DateUtil.dayFormat(mExpDate)));
        etRemark.setText(readerType.getRemark());
        tvExpireDate.setOnClickListener(this);
        btnModifyReaderType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_reader_type:
                System.out.println("修改读者类型");
                saveReaderType();
                break;
            case R.id.tv_expire_date:
                showDatePicker();
                break;
            default:
                break;
        }
    }

    public static void startActivity(Context context, long readerTypeId) {
        Intent intent = new Intent(context, ModifyReaderTypeActivity.class);
        intent.putExtra(KEY_READER_TYPE_ID, readerTypeId);
        context.startActivity(intent);
    }

    private void showDatePicker() {
        new TimePickerView.Builder(this, (date, v1) -> {
            mExpDate = date;
            tvExpireDate.setText(String.format("失效日期：%s", DateUtil.dayFormat(date)));
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setDate(Calendar.getInstance())
                .build()
                .show();
    }

    private void saveReaderType() {
        if (nullCheck(etReaderCategoryName, "读者类型名称")
                || nullCheck(etBorrowCount, "最大借书数量")
                || nullCheck(etBorrowLen, "借书期限")) {
            return;
        }

        if (mExpDate == null) {
            Toast.makeText(this, "请设置失效日期", Toast.LENGTH_SHORT).show();
            return;
        }

        readerType.setTypeName(getString(etReaderCategoryName));
        readerType.setBorrowCount(getNumberFromEt(etBorrowCount, 5));//若为空 默认为 5 本
        readerType.setBorrowLon(getNumberFromEt(etBorrowLen, 1));//如果为空默认为 1 个月
        readerType.setRemark(getString(etRemark));
        readerType.setExpDate(mExpDate);

        int rowAffect = readerType.update(mReaderTypeId);
        if (rowAffect > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
        }

//        ContentValues contentValues = new ContentValues();
//        contentValues.put("typename", mReaderType.getTypeName());
//        contentValues.put("borrowcount",mReaderType.getBorrowCount());
//        contentValues.put("borrowmon",mReaderType.getBorrowCount());
//        DataSupport.update(ReaderType.class, contentValues, mReaderTypeId);
    }


    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(getString(et))) {
            Toast.makeText(this, msg + "不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private String getString(EditText et) {
        return et.getText().toString();
    }
    private int getNumberFromEt(EditText et, int defaultValue) {
        String str = getString(et);
        return TextUtils.isEmpty(str) ? defaultValue : Integer.parseInt(str);
    }

}
