package com.ihealth.demo.business.device;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ihealth.demo.R;

import java.util.List;

public class Am6Adapter extends RecyclerView.Adapter<Am6Adapter.ViewHolder> {
    private List<AM6API> mData;

    public Am6Adapter(List<AM6API> data) {
        mData = data;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String name);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // 创建 ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_am6, parent, false);
        return new ViewHolder(view);
    }

    // 绑定数据到 ViewHolder 上
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mData.get(position).name);
        holder.mRlAm6.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                int p = holder.getAdapterPosition();
                mOnItemClickListener.onItemClick(mData.get(p).id);
            }
        });
    }

    // 获取列表项的数量
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // ViewHolder 内部类
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRlAm6;
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRlAm6 = (RelativeLayout) itemView.findViewById(R.id.ll_am6_api);
            mTextView = (TextView) itemView.findViewById(R.id.tv_am6_api);
        }
    }

}
