package com.ccnu.bookmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.adapter.ReaderBorrowInfoRvAdapter;
import com.ccnu.bookmanagement.bean.Reader;
import com.ccnu.bookmanagement.bean.ReaderBorrowInfo;
import com.ccnu.bookmanagement.listener.OnClickRecyclerViewListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ReaderBorrowInfoActivity extends AppCompatActivity {

    private static final String KEY_READER_ID = "READER_ID";
    private RecyclerView rv;
    private LinearLayout llNoMatchResult;
    private long readerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_borrow_info);
        initView();
    }

    public static void startActivity(Context context, long readerId) {
        Intent intent = new Intent(context, ReaderBorrowInfoActivity.class);
        intent.putExtra(KEY_READER_ID, readerId);
        context.startActivity(intent);
    }

    protected void initView() {
        rv = (RecyclerView)this.findViewById(R.id.rv);
        llNoMatchResult = (LinearLayout)this.findViewById(R.id.ll_no_match_result);
        readerId = getIntent().getLongExtra(KEY_READER_ID, -1);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("读者借阅信息");
        List<ReaderBorrowInfo> readerBorrowInfoList = getReaderInfoList();
        Reader reader = DataSupport.find(Reader.class, readerId);
        getSupportActionBar().setSubtitle(reader.getName() + "借阅了 " + readerBorrowInfoList.size() + " 本书");

        if (readerBorrowInfoList.isEmpty()) {
            rv.setVisibility(View.GONE);
            llNoMatchResult.setVisibility(View.VISIBLE);
            return;
        }

        ReaderBorrowInfoRvAdapter adapter = new ReaderBorrowInfoRvAdapter();
        readerBorrowInfoList.size();
        adapter.updateData(getReaderInfoList());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
//        adapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
//            @Override
//            public void onItemClick(int i) {
//                ModifyBookActivity.startActivity(ReaderBorrowInfoActivity.this,
//                        adapter.getDataList().get(i).getBookId());//点击跳转到书籍修改页
//            }
//
//            @Override
//            public boolean onItemLongClick(int i) {
//                return false;
//            }
//        });
    }

    private List<ReaderBorrowInfo> getReaderInfoList() {
        //根据读者 id ，查询所借阅所有的书籍 ,首先去 borrow 表中查询 该 id 借阅的所有书籍的 id，根据 bookId 去 book 表中查询所有对应的 book
        Cursor cursor = DataSupport.findBySQL("select book_id,bookname,authorname,borrowdate,returndate from borrow,book where book.id = borrow.book_id and reader_id = " + readerId);
        List<ReaderBorrowInfo> readerBorrowInfoList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            readerBorrowInfoList.add(getReaderBorrowBeanFromCursor(cursor));
            while (cursor.moveToNext()) {
                readerBorrowInfoList.add(getReaderBorrowBeanFromCursor(cursor));
            }
        }
        return readerBorrowInfoList;
    }
    private ReaderBorrowInfo getReaderBorrowBeanFromCursor(Cursor cursor) {
        return new ReaderBorrowInfo()
                .setBookId(cursor.getLong(0))
                .setBookMsg(cursor.getString(1) +" / "+ cursor.getString(2))
                .setBorrowDateTime(cursor.getLong(3))
                .setReturnDateTime(cursor.getLong(4));
    }
}
