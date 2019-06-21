package com.ccnu.bookmanagement.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.adapter.ReaderRvAdapter;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.listener.OnClickRecyclerViewListener;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ReaderListActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private LinearLayout mLlNoMatchResult;
    private ReaderRvAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_list);
        initView();
    }

    private void initView() {
        mRv = (RecyclerView)findViewById(R.id.rv);
        mLlNoMatchResult=(LinearLayout)findViewById(R.id.ll_no_match_result) ;
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("读者信息查询");

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Reader> readerList = DataSupport.findAll(Reader.class);
        updateUi(readerList);
    }

    private void updateUi(List<Reader> readerList) {
        if (readerList.isEmpty()) {
            mRv.setVisibility(View.GONE);
            mLlNoMatchResult.setVisibility(View.VISIBLE);
            return;
        } else {
            mRv.setVisibility(View.VISIBLE);
            mLlNoMatchResult.setVisibility(View.GONE);
        }
        if (mAdapter == null) {
            mAdapter = new ReaderRvAdapter();
            mAdapter.updateData(readerList);
            mRv.setAdapter(mAdapter);
            mRv.setLayoutManager(new LinearLayoutManager(this));
            mAdapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
                @Override
                public void onItemClick(int i) {
                    showItemDialog(i);
                }

                @Override
                public boolean onItemLongClick(int i) {
                    return false;
                }
            });
        } else {
            mAdapter.updateData(readerList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showItemDialog(int i) {
        new AlertDialog.Builder(ReaderListActivity.this)
                .setItems(new String[]{"修改读者信息", "查询借阅信息"}, (dialog, which) -> {
                    long readerId = mAdapter.getDataList().get(i).getId();
                    switch (which) {
                        case 0:
                            ModifyReaderActivity.startActivity(ReaderListActivity.this, readerId);
                            break;
                        case 1:
                            ReaderBorrowInfoActivity.startActivity(ReaderListActivity.this, readerId);
                            break;
                    }
                })
                .show();
    }
}
