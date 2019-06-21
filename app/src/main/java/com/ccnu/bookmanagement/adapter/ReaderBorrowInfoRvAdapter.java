package com.ccnu.bookmanagement.adapter;

import android.view.View;
import android.widget.TextView;

import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.base.BaseSimpleRecyclerViewAdapter;
import com.ccnu.bookmanagement.bean.ReaderBorrowInfo;
import com.ccnu.bookmanagement.util.DateUtil;

import java.util.Date;

import butterknife.BindView;

public class ReaderBorrowInfoRvAdapter extends BaseSimpleRecyclerViewAdapter<ReaderBorrowInfo> {

    @Override
    protected int setResId() {
        return R.layout.item_reader_borrow_info;
    }

    @Override
    protected BaseSimpleRecyclerViewAdapter.BaseRvHolder createConcreteViewHolder(View view) {
        return new ReaderBorrowInfoHolder(view);
    }

    class ReaderBorrowInfoHolder extends BaseRvHolder {

        @BindView(R.id.tv_book_msg)
        TextView mTvBookMsg;
        @BindView(R.id.tv_borrow_date)
        TextView mTvBorrowDate;
        @BindView(R.id.tv_return_date)
        TextView mTvReturnDate;
        @BindView(R.id.tv_has_return)
        TextView mTvHasReturn;

        public ReaderBorrowInfoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(ReaderBorrowInfo readerBorrowInfo) {
            mTvBookMsg.setText(readerBorrowInfo.getBookMsg());
            mTvBorrowDate.setText(String.format("借期：%s", DateUtil.dayFormat(new Date(readerBorrowInfo.getBorrowDateTime()))));
            String returnDate = "还期：";
            if (readerBorrowInfo.getReturnDateTime() != 0) {
                returnDate += DateUtil.dayFormat(new Date(readerBorrowInfo.getReturnDateTime()));
                mTvHasReturn.setText("已还");
            } else {
                mTvHasReturn.setText("未还");
            }
            mTvReturnDate.setText(returnDate);
        }
    }
}
