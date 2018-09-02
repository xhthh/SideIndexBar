package com.xht.indexbar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xht.indexbar.R;
import com.xht.indexbar.bean.CityBean;

import java.util.List;

/**
 * Created by xht on 2018/3/5.
 */

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CityListViewHolder> {

    private Context mContext;
    private List<CityBean> mDatas;

    public CityListAdapter(Context context, List<CityBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void updateData(List<CityBean> mResults) {
        this.mDatas = mResults;
        notifyDataSetChanged();
    }

    @Override
    public CityListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CityListViewHolder holder, final int position) {
        holder.tvNmae.setText(mDatas.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mDatas.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class CityListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNmae;
        private ImageView ivImg;
        private View itemView;

        public CityListViewHolder(View itemView) {
            super(itemView);
            tvNmae = itemView.findViewById(R.id.tv_item_name);
            ivImg = itemView.findViewById(R.id.iv_item_img);
            this.itemView = itemView;
        }
    }

}
