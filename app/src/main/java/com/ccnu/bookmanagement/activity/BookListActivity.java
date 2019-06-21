package com.ccnu.bookmanagement.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.adapter.BookRvAdapter;
import com.ccnu.bookmanagement.adapter.ReaderRvAdapter;
import com.ccnu.bookmanagement.bean.Book;
import com.ccnu.bookmanagement.listener.OnClickRecyclerViewListener;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BookListActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private LinearLayout mLlNoMatchResult;
    private BookRvAdapter mRvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
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
        getSupportActionBar().setTitle("书籍查询");

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Book> bookList = DataSupport.findAll(Book.class);//默认显示所有的书籍
        updateBookList(bookList);
    }

    private void updateBookList(List<Book> bookList) {
        if (bookList.isEmpty()) {
            mRv.setVisibility(View.GONE);
            mLlNoMatchResult.setVisibility(View.VISIBLE);
            return;
        } else {
            mRv.setVisibility(View.VISIBLE);
            mLlNoMatchResult.setVisibility(View.GONE);
        }
        if (mRvAdapter == null) {
            mRvAdapter = new BookRvAdapter();
            mRvAdapter.updateData(bookList);
            mRv.setLayoutManager(new LinearLayoutManager(this));
            mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mRv.setAdapter(mRvAdapter);
            mRvAdapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
                @Override
                public void onItemClick(int i) {
                    ModifyBookActivity.startActivity(BookListActivity.this,
                            mRvAdapter.getDataList().get(i).getId());
                }

                @Override
                public boolean onItemLongClick(int i) {
                    return false;
                }
            });
        } else {
            mRvAdapter.updateData(bookList);
            mRvAdapter.notifyDataSetChanged();
        }
    }
}
