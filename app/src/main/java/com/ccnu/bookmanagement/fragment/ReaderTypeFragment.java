package com.ccnu.bookmanagement.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.activity.ModifyReaderTypeActivity;
import com.ccnu.bookmanagement.adapter.ReaderTypeAdapter;
import com.ccnu.bookmanagement.bean.BookType;
import com.ccnu.bookmanagement.bean.ClickType;
import com.ccnu.bookmanagement.bean.ReaderType;
import com.ccnu.bookmanagement.bean.ShowSelectAll;
import com.ccnu.bookmanagement.listener.OnClickRecyclerViewListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

public class ReaderTypeFragment extends Fragment{

    View view;
    private RecyclerView mRv;
    private LinearLayout mLlDelete;
    private List<ReaderType> readerTypeList;
    private ReaderTypeAdapter readerTypeAdapter;

    public static ReaderTypeFragment newInstance() {
        return new ReaderTypeFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        if (view == null){
            view = inflater.inflate(R.layout.fragment_reader_type, null);
        }
        mRv = (RecyclerView)view.findViewById(R.id.rv);
        mLlDelete = (LinearLayout)view.findViewById(R.id.ll_delete);
        setListener();
        mLlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        initData(savedInstanceState);
        return view;
    }

    protected void initData(Bundle bundle) {
        readerTypeList = DataSupport.findAll(ReaderType.class);
        readerTypeAdapter = new ReaderTypeAdapter();
        readerTypeAdapter.updateData(readerTypeList);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRv.setAdapter(readerTypeAdapter);
        setRvClickListener();
    }

    private void setRvClickListener() {
        readerTypeAdapter.setOnRecyclerViewListener(new OnClickRecyclerViewListener() {
            @Override
            public void onItemClick(int i) {
                ReaderType readerType = readerTypeAdapter.getDataList().get(i);
                if (mLlDelete.getVisibility() == View.VISIBLE) {//下方的删除布局可见，说明是批量操作状态
                    readerType.setSelected(!readerType.isSelected());
                    readerTypeAdapter.notifyItemChanged(i);
                } else {//否则打开类型详情
                    ModifyReaderTypeActivity.startActivity(getActivity(),
                            readerType.getId());
                }
            }

            @Override
            public boolean onItemLongClick(int i) {
                if (mLlDelete.getVisibility() == View.VISIBLE) {//目前已经显示了批量处理界面
                    ReaderType readerType = readerTypeAdapter.getDataList().get(i);
                    readerType.setSelected(!readerType.isSelected());
                    readerTypeAdapter.notifyItemChanged(i);
                    return true;
                }
                readerTypeAdapter.getDataList().get(i).setSelected(true);
                readerTypeAdapter.setShowCheckBox(true);
                mLlDelete.setVisibility(View.VISIBLE);
                for (ReaderType readerType : readerTypeList) {
                    readerType.setSelected(false);
                }
                EventBus.getDefault().post(new ShowSelectAll());
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
            readerTypeAdapter.setShowCheckBox(false);//内部自动调用 notifyDataSetChanged
            return;
        }

        for (ReaderType readerType : readerTypeList) {
            readerType.setSelected(clickType == ClickType.SELECT_ALL);
        }
        readerTypeAdapter.notifyDataSetChanged();
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("该操作将删除与选中类型相关联的所有数据，确定删除？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    for (ReaderType readerType : readerTypeList) {
                        if (readerType.isSelected()) {
                            DataSupport.delete(ReaderType.class, readerType.getId());
                        }
                    }
                    readerTypeList = DataSupport.findAll(ReaderType.class);
                    readerTypeAdapter.updateData(readerTypeList);
                    readerTypeAdapter.notifyDataSetChanged();
                })
                .show();
    }

}
