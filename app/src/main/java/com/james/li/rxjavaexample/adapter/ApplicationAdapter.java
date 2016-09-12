package com.james.li.rxjavaexample.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.james.li.rxjavaexample.R;
import com.james.li.rxjavaexample.bean.AppInfo;
import com.james.li.rxjavaexample.utils.Utils;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lsy-android on 9/12/16 in zsl-tech.
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<AppInfo> mApplications;

    //private int mRowLayout;

    public ApplicationAdapter(List<AppInfo> mApplications) {
        this.mApplications = mApplications;
        //this.mRowLayout = mRowLayout;
    }

    public void addApplications(List<AppInfo> applications) {
        mApplications.clear();
        mApplications.addAll(applications);
        notifyDataSetChanged();
    }

    public void addApplication(int position, AppInfo info) {
        if (position < 0) {
            position = 0;
        }

        mApplications.add(position, info);
        notifyDataSetChanged();
    }

    public void clearApplications() {
        mApplications.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AppInfo info = mApplications.get(position);
        holder.tvName.setText(info.getName());
        Utils.getBitmap(info.getIcon()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        holder.ivImg.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mApplications == null ? 0 : mApplications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ImageView ivImg;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.name);
            ivImg = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
