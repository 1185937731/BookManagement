package com.ccnu.bookmanagement.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.activity.ModifyBookTypeActivity;
import com.ccnu.bookmanagement.bean.BookType;
import com.ccnu.bookmanagement.bean.ClickType;
import com.ccnu.bookmanagement.bean.ShowSelectAll;
import com.ccnu.bookmanagement.adapter.BookTypeAdapter;
import com.ccnu.bookmanagement.widget.ScrollControlViewPager;
import com.ccnu.bookmanagement.listener.OnClickRecyclerViewListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

public class BookTypeFragment extends Fragment{

    View view;
    private RecyclerView mRv;
    private LinearLayout mLlDelete;
    private ImageView mIvDelete;
    private List<BookType> bookTypeList;
    private BookTypeAdapter bookTypeAdapter;

    public static BookTypeFragment newInstance() {
        return new BookTypeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_book_type,null);
        }
        mRv = (RecyclerView)view.findViewById(R.id.rv);
        mLlDelete = (LinearLayout)view.findViewById(R.id.ll_delete);
        mIvDelete = (ImageView)view.findViewById(R.id.iv_delete);
//        initData(savedInstanceState);
        setListener();
        mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        initData(savedInstanceState);
        return view;

    }

    protected void initData(Bundle bundle) {
        bookTypeList = DataSupport.findAll(BookType.class);
        bookTypeAdapter = new BookTypeAdapter();
        bookTypeAdapter.updateData(bookTypeList);
        setRvClickListener();
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRv.setAdapter(bookTypeAdapter);
    }

    private void setRvClickListener() {
        bookTypeAdapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
            @Override
            public void onItemClick(int i) {
                BookType bookType = bookTypeAdapter.getDataList().get(i);
                if (mLlDelete.getVisibility() == View.VISIBLE) {
                    bookType.setSelected(!bookType.isSelected());
                    bookTypeAdapter.notifyItemChanged(i);
                } else {
                    ModifyBookTypeActivity.startActivity(getActivity(), bookType.getId());
                }
            }

            @Override
            public boolean onItemLongClick(int i) {
                if (mLlDelete.getVisibility() == View.VISIBLE) {//目前已经显示了批量处理界面
                    BookType bookType = bookTypeAdapter.getDataList().get(i);
                    bookType.setSelected(!bookType.isSelected());
                    bookTypeAdapter.notifyItemChanged(i);
                    return true;
                }
                bookTypeAdapter.getDataList().get(i).setSelected(true);
                bookTypeAdapter.setShowCheckBox(true);
                mLlDelete.setVisibility(View.VISIBLE);
                for (BookType bookType : bookTypeList) {//批量操作开始时默认没有选中任何项
                    bookType.setSelected(false);
                }
                EventBus.getDefault().post(new ShowSelectAll());//发送
                return true;
            }
        });
    }

    protected void setListener() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onSelectAllClick(Enum<ClickType> clickType) {
        if (clickType == ClickType.CANCEL) {
            mLlDelete.setVisibility(View.GONE);
            bookTypeAdapter.setShowCheckBox(false);//内部自动调用 notifyDataSetChanged
            return;
        }

        for (BookType bookType : bookTypeList) {
            bookType.setSelected(clickType == ClickType.SELECT_ALL);
        }
        bookTypeAdapter.notifyDataSetChanged();
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("该操作将删除与选中类型相关联的所有数据，确定删除？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    for (BookType bookType : bookTypeList) {
                        if (bookType.isSelected()) {
                            DataSupport.delete(BookType.class, bookType.getId());
                        }
                    }
                    bookTypeList = DataSupport.findAll(BookType.class);
                    bookTypeAdapter.updateData(bookTypeList);
                    bookTypeAdapter.notifyDataSetChanged();
                })
                .show();
    }

}
