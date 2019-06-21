package com.ccnu.bookmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.BookType;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ModifyBookTypeActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etBookCategoryName,etKeyword,etRemark;
    private Button btnModifyBookType;
    private long mBookTypeId;
    private BookType bookType;
    private static final String KEY_BOOK_TYPE_ID = "BOOK_TYPE_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_book_type);
        initView();
    }

    private void initView() {
        etBookCategoryName = (EditText) this.findViewById(R.id.et_book_category_name);
        etKeyword = (EditText) this.findViewById(R.id.et_keyword);
        etRemark = (EditText) this.findViewById(R.id.et_remark);
        btnModifyBookType = (Button)this.findViewById(R.id.btn_modify_book_type);
        mBookTypeId = getIntent().getLongExtra(KEY_BOOK_TYPE_ID, -1);
        bookType = DataSupport.find(BookType.class, mBookTypeId);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("修改书籍类型");
        etBookCategoryName.setText(bookType.getTypeName());
        etKeyword.setText(bookType.getKeyWord());
        etRemark.setText(bookType.getRemark());
        btnModifyBookType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_book_type:
                if (nullCheck(etBookCategoryName, "书籍类型")) {
                    return;
                }

                //查询同名的书籍类型列表
                List<BookType> bookTypeIdList = DataSupport
                        .select("id")
                        .where("typeName=?", getString(etBookCategoryName))
                        .find(BookType.class);

                bookType.setTypeName(getString(etBookCategoryName));
                bookType.setKeyWord(getString(etKeyword));
                bookType.setRemark(getString(etRemark));

                if (bookTypeIdList.size() > 1) {
                    Toast.makeText(this, "您指定的书籍类型名称已存在", Toast.LENGTH_SHORT).show();
                } else {
                    int rowAffect = bookType.update(mBookTypeId);
                    if (rowAffect > 0) {
                        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

     public static void startActivity(Context context, long bookTypeId) {
        Intent intent = new Intent(context, ModifyBookTypeActivity.class);
        intent.putExtra(KEY_BOOK_TYPE_ID, bookTypeId);
        context.startActivity(intent);
    }

    protected boolean nullCheck(EditText et, String msg) {
        if (TextUtils.isEmpty(getString(et))) {
            Toast.makeText(this, msg + "不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    protected String getString(EditText et) {
        return et.getText().toString();
    }

}
