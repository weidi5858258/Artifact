package com.weidi.artifact.controller;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.TestImageFragment;
import com.weidi.artifact.modle.Image;
import com.weidi.artifact.modle.Sms;
import com.weidi.customadapter.CustomRecyclerViewAdapter;
import com.weidi.customadapter.CustomViewHolder;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.dbutil.SimpleDao;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.volley.ICustomerCallback;
import com.weidi.volley.image.ImageVolley;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.weidi.artifact.R.drawable.sms;

/**
 * Created by root on 17-1-13.
 */

public class TestImageFragmentController extends BaseFragmentController {

    private static final String TAG = "TestImageFragmentController";
    private static final boolean DEBUG = false;
    private TestImageFragment mTestImageFragment;
    private ArrayList<Image> mImageList = new ArrayList<Image>();

    public TestImageFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mTestImageFragment = (TestImageFragment) fragment;
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
        mImageList.clear();
    }

    public void afterInitView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "afterInitView():savedInstanceState = " + savedInstanceState);
        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        ((MainActivity) mTestImageFragment.getActivity()).title.setText("图片");
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
    }

    public void onClick(View view) {

    }

    //初始化
    private void init() {
        ThreadPool.getCachedThreadPool().execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        // showLoading();
                    }

                    @Override
                    public Object running() {
                        int count = urls.length;
                        for (int i = 0; i < count; ++i) {
                            Image image = new Image();
                            image.number = String.valueOf(i);
                            File fileSavePath = mMainActivity.getCacheDir();
                            image.path = fileSavePath.getAbsolutePath() + "/pictures";
                            image.url = urls[i];
                            mImageList.add(image);
                        }
                        return mImageList;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        // hideLoading();
                        if (object == null) {
                            return;
                        }
                        mImageList = (ArrayList<Image>) object;
                        TestImageFragmentAdapter testImageFragmentAdapter =
                                new TestImageFragmentAdapter(
                                        mContext, mImageList, R.layout.image_view_item);
                        testImageFragmentAdapter.setOnItemClickListener(mOnItemClickListener);
                        mTestImageFragment.imagelist_recycleview.setLayoutManager(
                                new LinearLayoutManager(mContext));
                        mTestImageFragment.imagelist_recycleview.setAdapter(
                                testImageFragmentAdapter);
                    }

                    @Override
                    public void runError() {
                        // hideLoading();
                    }
                }));
    }

    private void showLoading() {
        mTestImageFragment.loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mTestImageFragment.loading.setVisibility(View.GONE);
    }

    private class TestImageFragmentAdapter extends CustomRecyclerViewAdapter<Image> {

        private Handler mHandler = new Handler(Looper.getMainLooper());

        public TestImageFragmentAdapter(Context context, List items, int layoutResId) {
            super(context, items, layoutResId);
        }

        @Override
        public void onBind(final CustomViewHolder customViewHolder,
                           int viewType,
                           int layoutPosition,
                           Image image) {
            Log.d(TAG, "onBind(): layoutPosition = " + layoutPosition);
            customViewHolder.setText(R.id.tv_image_number, image.number);
//            customViewHolder.setText(R.id.tv_image_path, image.path);
//            customViewHolder.setText(R.id.tv_image_url, image.url);
            ImageVolley.newInstance()
                    .setFileSavePath(image.path)
                    .setURL(image.url)
                    .setHandler(mHandler)
                    .setImageView((ImageView) customViewHolder.getView(R.id.iv_image_icon))
                    .setIDataCallback(new ICustomerCallback<Bitmap>() {

                        @Override
                        public void onSuccess(Bitmap result) {
                            customViewHolder.setImageBitmap(R.id.iv_image_icon, result);
                        }

                        @Override
                        public void onFailed(String response) {
                            Log.d(TAG, "onFailed(): " + response);
                            customViewHolder.setImageResource(
                                    R.id.iv_image_icon, R.drawable.item_bg);
                        }

                        @Override
                        public boolean isIntercept() {
                            return false;
                        }
                    })
                    .execute();
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {

        }
    };

    private static final String urls[] = {
            "http://img.taopic.com/uploads/allimg/140826/267848-140R60T34860.jpg",
            "http://pic36.nipic.com/20131203/3822951_101052690000_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006796611&di" +
                    "=401482c39adea503082ac2155805d2c0&imgtype=0&src=http%3A%2F%2Fimg4q.duitang" +
                    ".com%2Fuploads%2Fitem%2F201505%2F06%2F20150506110455_CLvWs.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006799135&di" +
                    "=ea1b17d3bdb2793e977d6f0fa614b2fd&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F120911%2F1-120911092K2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831181&di" +
                    "=9f9b9cb7f6f7444d4183cb10e11efce5&imgtype=0&src=http%3A%2F%2Fimg2.niutuku" +
                    ".com%2Fdesk%2F001%2F001-38264.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831181&di" +
                    "=2003f59d38aa2233dfca7039168d1204&imgtype=0&src=http%3A%2F%2Fwww.benbenla" +
                    ".cn%2Fimages%2F20110810%2Fbenbenla-02d.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831180&di" +
                    "=f58b4dd3eed6085221bfcf9e181d0ea4&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2F7%2F56efc26bd5529.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831180&di" +
                    "=00a5a65cf85a0bd9db9442e75767ca7e&imgtype=0&src=http%3A%2F%2Fimg3.iqilu" +
                    ".com%2Fdata%2Fattachment%2Fforum%2F201308%2F22%2F085546j0ririiunwsiiwbe.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831178&di" +
                    "=143d365e5ed9327152195621001c0182&imgtype=0&src=http%3A%2F%2Fimg2.niutuku" +
                    ".com%2Fdesk%2F1208%2F1723%2Fntk-1723-75697.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831178&di" +
                    "=a9f390e762ba3b073dad9fddd0ff872b&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Fb%2F55597435bb036.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831178&di" +
                    "=a6cb1ae812ca5a6906dc3137de6feed2&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150422%2F140-1504220U109.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=000307c447d5305de2f02bf00cda3f17&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150407%2F140-15040F93046.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=9f4fa730f97e57baa5b6c7d01afa344d&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150207%2F140-15020F95950.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=0e5ef671a54c6a0fa276f3f8eaa76478&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F130923%2F1-1309231J118.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=2750bfef695ee2b9a83f8a703b6ccbec&imgtype=0&src=http%3A%2F%2Fpic1.5442.com" +
                    "%2F2015%2F0707%2F15%2F02.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=04b1f40fbd5d0cb64900a77a2b1e7262&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Ff%2F56eb571018776.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831176&di" +
                    "=50ad03ecec1381e9a81700180ec9959e&imgtype=0&src=http%3A%2F%2Fwww.benbenla" +
                    ".cn%2Fimages%2F20131202%2Fbenbenla-08d.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831176&di" +
                    "=7cb61b7dbcebc3bf0eaaa64707bd0a1b&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F130130%2F1-130130143309.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831176&di" +
                    "=e33e50988eba52c311f6beee8cf36c2d&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Ff%2F54462cba45425.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831175&di" +
                    "=5476a02df5319884f2e3bda45df4dc6d&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150304%2F140-1503040U613.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831175&di" +
                    "=90bd5c1cdccc73ccb06c7e7e8b15b57b&imgtype=0&src=http%3A%2F%2Fwww.benbenla" +
                    ".cn%2Fimages%2F20120816%2Fbenbenla-02b.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831175&di" +
                    "=811b3935e437d574b6d8fe7336604204&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2F201502%2F2015020205.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831173&di" +
                    "=56a2219cd29e7930b2015955fb44feab&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150312%2F139-150312144331.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831173&di" +
                    "=393af53a9d74dbffe9837573e94581a2&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150630%2F140-150630122358.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940480&di" +
                    "=f7be3601802040c436b222f1d592a838&imgtype=0&src=http%3A%2F%2Fimg5q.duitang" +
                    ".com%2Fuploads%2Fitem%2F201505%2F06%2F20150506015016_zQjdr.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940480&di" +
                    "=470b511318cd09c3137887d53b6a1b58&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2F4%2F566f6fcb5ce1a.jpg",
            "https://ss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item" +
                    "/203fb80e7bec54e7e68c83f4bd389b504ec26afc.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940479&di" +
                    "=ae3a157e8eec90e0b6d740a6bfa21a68&imgtype=0&src=http%3A%2F%2Fimg17.3lian" +
                    ".com%2Fd%2Ffile%2F201702%2F17%2Fb4aa328b5a89bde5972325aa42c3272e.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940474&di" +
                    "=2020875244c8cb7bf28f7c28328c8868&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F130516%2F1-130516155F5-50.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940473&di" +
                    "=f8bc1ea333d267243cf66fa027dfea55&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2F4%2F5841385dc4c07.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940473&di" +
                    "=9c2623974955f4a80ec2a9bff0be4a07&imgtype=0&src=http%3A%2F%2Fimg.pconline" +
                    ".com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1207%2F16%2Fc0" +
                    "%2F12347883_1342409469170.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940473&di" +
                    "=e824e94f4482b05ce6a6b3e8318874c0&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Ff%2F546b12c23f5c2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=acf119349daf87317c3395aa5e551e37&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2F6%2F53e0446d06363.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=96f85436a5afb90566327ded4d3e1b27&imgtype=0&src=http%3A%2F%2Fimg5.duitang" +
                    ".com%2Fuploads%2Fitem%2F201309%2F07%2F20130907173129_5sVJf.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=a19c7003c8b3e1467a4a13a1df1f8f4a&imgtype=0&src=http%3A%2F%2Fwww.benbenla" +
                    ".cn%2Fimages%2F20131018%2Fbenbenla-09c.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=68091f95cfad4b0f9ae73598d560e06d&imgtype=0&src=http%3A%2F%2Fwww.benbenla" +
                    ".cn%2Fimages%2F20131102%2Fbenbenla-02c.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=3b2f2c983151d807d1269ab14140186d&imgtype=0&src=http%3A%2F%2Fimg.pconline" +
                    ".com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1209%2F04%2Fc0" +
                    "%2F13594204_1346729316001.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940470&di" +
                    "=51e7064a2d19d4e1bfe473705a50aff0&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150422%2F140-1504220U112-50.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940470&di" +
                    "=5dd9ab84cd6a3f7a1f5fa4aca9c4236f&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2Fallimg%2F111122%2F112U12H1-2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940469&di" +
                    "=348670d1ce67dd328a80ebbc4b1bbe59&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F150223%2F140-1502231S455.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940469&di" +
                    "=2f624e1d743bd66f26b005d0413f00cd&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".bz55.com%2Fuploads%2Fallimg%2F121120%2F1-121120095211.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940469&di" +
                    "=fe84b7988855c2e79135464842cb206d&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2F201512%2F2015122204.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940469&di" +
                    "=641cc574960e1fa9e579a6cf82fe3f07&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2F201512%2F2015121205.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940469&di" +
                    "=434ba9417c47bf0c614e8830109cff50&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".xp71.com%2Fuploads%2Fallimg%2F150515%2F1-150515103A0358.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=803089073," +
                    "560770256&fm=27&gp=0.jpg",
            "http://pic36.nipic.com/20131203/3822951_101052690000_2.jpg"
    };

}
