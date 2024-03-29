package com.ccnu.bookmanagement.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ccnu.bookmanagement.base.BaseSimpleRecyclerViewAdapter;
import com.ccnu.bookmanagement.R;
import com.ccnu.bookmanagement.bean.BookType;

import butterknife.BindView;

public class BookTypeAdapter extends BaseSimpleRecyclerViewAdapter<BookType> {

    private boolean mShowCheckBox;//是否显示 CheckBox

    @Override
    protected int setResId() {
        return R.layout.item_text;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new StringHolder(view);
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
        notifyDataSetChanged();
    }

    class StringHolder extends BaseRvHolder implements CompoundButton.OnCheckedChangeListener {

        @BindView(R.id.tv_text)
        TextView mTvText;
        @BindView(R.id.cb_select)
        CheckBox mCb;

        private BookType mBookType;

        public StringHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(BookType bookType) {
            mBookType = bookType;
            mTvText.setText(bookType.getTypeName());
            if (mShowCheckBox) {
                mCb.setVisibility(View.VISIBLE);
                mCb.setChecked(bookType.isSelected());
            } else {
                mCb.setVisibility(View.GONE);
            }
            mCb.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mBookType.setSelected(isChecked);
        }
    }
}