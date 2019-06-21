package com.ccnu.bookmanagement.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.BookType;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AddBookTypeActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etBookCategoryName,etKeyword,etRemark;
    private Button btnAddBookType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_type);
        initView();
    }

    private void initView() {
        etBookCategoryName = (EditText) this.findViewById(R.id.et_book_category_name);
        etKeyword = (EditText) this.findViewById(R.id.et_keyword);
        etRemark = (EditText) this.findViewById(R.id.et_remark);
        btnAddBookType = (Button)this.findViewById(R.id.btn_add_book_type);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("添加书籍类型");
        btnAddBookType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_book_type:
                System.out.println("添加书籍类型");
                saveBookType();
                break;
            default:
                break;
        }
    }

    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(et.getText().toString())) {
            Toast.makeText(AddBookTypeActivity.this, msg + "不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    protected void saveBookType() {
        //查询同名的书籍类型列表
        List<BookType> bookTypeIdList = DataSupport
                .select("id")
                .where("typeName=?", etBookCategoryName.getText().toString())
                .find(BookType.class);

        BookType bookType = new BookType();
        bookType.setTypeName(etBookCategoryName.getText().toString());
        bookType.setKeyWord(etKeyword.getText().toString());
        bookType.setRemark(etRemark.getText().toString());

        if (!bookTypeIdList.isEmpty()) {
            new AlertDialog.Builder(this)
                    //书籍类型检测防止重名
                    .setMessage("您指定的书籍类型已存在，是否对其内容进行更新？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        bookType.update(bookTypeIdList.get(0).getId());//对内容进行更新
                        finish();
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        finish();
                    })
                    .show();
        } else {
            resolveSave(bookType);
        }
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
