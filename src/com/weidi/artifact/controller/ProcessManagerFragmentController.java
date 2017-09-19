package com.weidi.artifact.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.db.bean.ProcessInfos;
import com.weidi.artifact.fragment.ProcessManagerFragment;
import com.weidi.customadapter.CustomRecyclerViewAdapter;
import com.weidi.customadapter.CustomViewHolder;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;

import java.util.List;

/**
 * Created by root on 17-1-13.
 */

public class ProcessManagerFragmentController extends BaseFragmentController {

    private static final String TAG = "ProcessManagerFragmentController";
    private ProcessManagerFragment mProcessManagerFragment;
    private ProcessManagerAdapter mProcessManagerAdapter;
    private List<ProcessInfos> data;

    public ProcessManagerFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mProcessManagerFragment = (ProcessManagerFragment)fragment;
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void afterInitView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final CustomRunnable mCustomRunnable = new CustomRunnable();
        mCustomRunnable.setCallBack(
                new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {

                    }

                    @Override
                    public Object running() {

                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        mProcessManagerAdapter = new ProcessManagerAdapter(
                                mContext, data, R.layout.processesmanager_view_item);

                    }

                    @Override
                    public void runError() {

                    }

                });
        ThreadPool.getCachedThreadPool().execute(mCustomRunnable);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    private static class ProcessManagerAdapter extends CustomRecyclerViewAdapter<ProcessInfos> {

        public ProcessManagerAdapter(Context context, List items, int layoutResId) {
            super(context, items, layoutResId);
        }

        @Override
        public void onBind(CustomViewHolder customViewHolder,
                           int viewType,
                           int layoutPosition,
                           ProcessInfos item) {

        }

    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {

        }

    };


}
