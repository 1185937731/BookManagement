package com.ccnu.bookmanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ccnu.bookmanagement.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView add_reader,borrow,add_book,reader_list,return_book,query_book,type;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        add_reader = (TextView) this.findViewById(R.id.add_reader);
        borrow = (TextView) this.findViewById(R.id.borrow);
        add_book = (TextView) this.findViewById(R.id.add_book);
        reader_list = (TextView) this.findViewById(R.id.reader_list);
        return_book = (TextView) this.findViewById(R.id.return_book);
        query_book = (TextView) this.findViewById(R.id.query_book);
        type = (TextView) this.findViewById(R.id.type);

        add_reader.setOnClickListener(this);
        borrow.setOnClickListener(this);
        add_book.setOnClickListener(this);
        reader_list.setOnClickListener(this);
        return_book.setOnClickListener(this);
        query_book.setOnClickListener(this);
        type.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reader:
                Intent intent1 = new Intent(MainActivity.this, AddReaderActivity.class);
                startActivity(intent1);
                break;
            case R.id.borrow:
                Intent intent2 = new Intent(MainActivity.this, BorrowActivity.class);
                startActivity(intent2);
                break;
            case R.id.add_book:
                Intent intent3 = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent3);
                break;
            case R.id.reader_list:
                Intent intent4 = new Intent(MainActivity.this, ReaderListActivity.class);
                startActivity(intent4);
                break;
            case R.id.return_book:
                Intent intent5 = new Intent(MainActivity.this, ReturnActivity.class);
                startActivity(intent5);
                break;
            case R.id.query_book:
                Intent intent6 = new Intent(MainActivity.this, BookListActivity.class);
                startActivity(intent6);
                break;
            case R.id.type:
                Intent intent7 = new Intent(MainActivity.this, TypeActivity.class);
                startActivity(intent7);
                break;
            case R.id.stastic:
                Intent intent8 = new Intent(MainActivity.this, StatisticActivity.class);
                startActivity(intent8);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 连续点击两次返回键退出应用
     */
    private void exit(){
        if((System.currentTimeMillis() - exitTime)>2000){
            Log.e("再按一次退出程序","app");
            Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
            Log.e("exitTime","app");
        }else {
            finish();
            Log.e("退出","app");
            System.exit(0);
        }
    }
}
