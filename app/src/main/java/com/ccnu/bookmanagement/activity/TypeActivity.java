package com.ccnu.bookmanagement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ccnu.bookmanagement.adapter.ViewPagerAdapter;
import com.ccnu.bookmanagement.bean.ClickType;
import com.ccnu.bookmanagement.bean.ShowSelectAll;
import com.ccnu.bookmanagement.fragment.BookTypeFragment;
import com.ccnu.bookmanagement.fragment.ReaderTypeFragment;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.widget.ScrollControlViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TypeActivity extends AppCompatActivity implements View.OnClickListener{

    private TabLayout tabLayout;
    private ScrollControlViewPager viewpager;
    private TextView tvSelectAll,tvCancel;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private boolean mSelectAll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        initView();
    }


    private void initView() {
        EventBus.getDefault().register(this);
        tabLayout =(TabLayout) findViewById(R.id.tab_layout);
        viewpager =(ScrollControlViewPager)findViewById(R.id.view_pager);
        tvSelectAll =(TextView) findViewById(R.id.tv_select_all);
        tvCancel =(TextView) findViewById(R.id.tv_cancel);
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        floatingActionButton =(FloatingActionButton)findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("类型信息");
        tabLayout.setupWithViewPager(viewpager, true);
        Fragment[] fragments = new Fragment[2];
        fragments[0] = BookTypeFragment.newInstance();
        fragments[1] = ReaderTypeFragment.newInstance();
        String[] tabTitles = new String[]{"书籍类型", "读者类型"};
        viewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), tabTitles, fragments));
        showTabDivider();
        initListener();
        tvCancel.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
    }
    protected void initListener() {
        floatingActionButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setItems(new String[]{"添加书籍类型", "添加读者类型"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Intent intent1 = new Intent(TypeActivity.this, AddBookTypeActivity.class);
                                startActivity(intent1);
                                break;
                            case 1:
                                Intent intent2 = new Intent(TypeActivity.this, AddReaderTypeActivity.class);
                                startActivity(intent2);
                                break;

                        }
                    }).show();
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_select_all:
                onSelectAllClick();
                break;
            case R.id.tv_cancel:
                onCancelClickOrBackPressed();
                break;
            default:
                break;
        }
    }

    private void showTabDivider() {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        tabStrip.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.divider_tab_vertical));
        tabStrip.setDividerPadding(40);
        tabStrip.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    }



    /**
     * 选中时可能显示的文字是「全选」或者「全不选」，在这两个状态之间进行转换
     * */
    private void onSelectAllClick() {
        EventBus.getDefault().post(mSelectAll ? ClickType.SELECT_ALL : ClickType.UN_SELECT_ALL);
        mSelectAll = !mSelectAll;//下一次显示与上一次不同
        tvSelectAll.setText(mSelectAll ? "全选" : "全不选");
    }

    private void onCancelClickOrBackPressed() {
        EventBus.getDefault().post(ClickType.CANCEL);//发送消息，提示点击了「取消」按钮
        tvSelectAll.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        viewpager.setScrollable(true);
        showActionBar(true);
        setTabItemClickable(true);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    private void showActionBar(boolean show) {
        getSupportActionBar().setDisplayShowHomeEnabled(show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        getSupportActionBar().setTitle(show ? "类型信息" : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void showButton(ShowSelectAll showSelectAll) {
        tvSelectAll.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
        showActionBar(false);
        viewpager.setScrollable(false);
        setTabItemClickable(false);
        floatingActionButton.setVisibility(View.GONE);//隐藏浮动按钮
    }
    private void setTabItemClickable(boolean clickable) {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            if (tabView != null) {
                tabView.setClickable(clickable);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (tvCancel.getVisibility() == View.VISIBLE){//如果是现在显示批量删除界面，则隐藏批量删除界面
            onCancelClickOrBackPressed();
            return;
        }
        super.onBackPressed();
    }
}
