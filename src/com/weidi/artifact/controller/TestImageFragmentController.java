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
import com.weidi.imageload.ImageLoader;
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
                           final int layoutPosition,
                           Image image) {
            customViewHolder.setText(R.id.tv_image_number, image.number);
            //            customViewHolder.setText(R.id.tv_image_path, image.path);
            //            customViewHolder.setText(R.id.tv_image_url, image.url);
            final ImageView imageView = (ImageView) customViewHolder.getView(R.id.iv_image_icon);
            ImageVolley.newInstance()
                    .setURL(image.url)
                    .setHandler(mHandler)
                    .setImageView(imageView)
                    /*.setIDataCallback(new ICustomerCallback<Bitmap>() {

                        @Override
                        public void onSuccess(Bitmap result) {
                            Log.d(TAG, "imageView = " + imageView +
                                    " layoutPosition = " + layoutPosition);
                            // customViewHolder.setImageBitmap(R.id.iv_image_icon, result);
                        }

                        @Override
                        public void onFailed(String response) {
                            Log.d(TAG, "imageView = " + imageView +
                                    " layoutPosition = " + layoutPosition +
                                    " onFailed(): " + response);
                            customViewHolder.setImageResource(
                                    R.id.iv_image_icon, R.drawable.item_bg);
                        }

                        @Override
                        public boolean isIntercept() {
                            return false;
                        }
                    })*/
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
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2749695605," +
                    "681965793&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006796611&di" +
                    "=401482c39adea503082ac2155805d2c0&imgtype=0&src=http%3A%2F%2Fimg4q.duitang" +
                    ".com%2Fuploads%2Fitem%2F201505%2F06%2F20150506110455_CLvWs.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506050570408&di" +
                    "=ec05df1e0eaed90d3895ea74ca0c5bda&imgtype=0&src=http%3A%2F%2Fpic.58pic" +
                    ".com%2F58pic%2F14%2F46%2F80%2F82r58PICYeM_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831181&di" +
                    "=9f9b9cb7f6f7444d4183cb10e11efce5&imgtype=0&src=http%3A%2F%2Fimg2.niutuku" +
                    ".com%2Fdesk%2F001%2F001-38264.jpg",
            // 5
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506050873667&di" +
                    "=aeb132cb386d0f18b476042ee8d2d22a&imgtype=0&src=http%3A%2F%2Fpic6.nipic" +
                    ".com%2F20100319%2F4399587_210315008320_2.jpg",
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
            // 10
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506060713554&di" +
                    "=6593a0a348e19a5dd90a7e952c80c68d&imgtype=0&src=http%3A%2F%2Fwww.pptbz" +
                    ".com%2Fd%2Ffile%2Fp%2F201701%2F35de641008804551148fe5e79fd1a8d5.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506050648&di" +
                    "=96fd8079bf217c76ce3a11415003cd24&src=http://pic9.nipic" +
                    ".com/20100819/2531170_184417755901_2.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3207847815," +
                    "3678420689&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506655498&di" +
                    "=86101d0c023547d3523c81ba1c7558b0&imgtype=jpg&er=1&src=http%3A%2F%2Fh" +
                    ".hiphotos.baidu" +
                    ".com%2Fzhidao%2Fpic%2Fitem%2Faec379310a55b3197c24e90b45a98226cefc17ea.jpg",
            // 14
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=2750bfef695ee2b9a83f8a703b6ccbec&imgtype=0&src=http%3A%2F%2Fpic1.5442.com" +
                    "%2F2015%2F0707%2F15%2F02.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831177&di" +
                    "=04b1f40fbd5d0cb64900a77a2b1e7262&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Ff%2F56eb571018776.jpg",
            // 16
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1917881102," +
                    "2193387785&fm=27&gp=0.jpg",
            // 17
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506655547&di" +
                    "=8586174d628a8a3e21e86f763ec8f550&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fpic41.nipic.com%2F20140522%2F18651187_092523426168_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831176&di" +
                    "=e33e50988eba52c311f6beee8cf36c2d&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2Ff%2F54462cba45425.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506655563&di" +
                    "=101e21139d7c11c34e951de48dbd0235&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fpic17.nipic.com%2F20111112%2F2428028_143427780000_2.jpg",
            // 20
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506050747775&di" +
                    "=70bbbe8dcaa85c182728d7fe44fb5b43&imgtype=0&src=http%3A%2F%2Fpic8.nipic" +
                    ".com%2F20100802%2F5304618_142618055446_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006831175&di" +
                    "=811b3935e437d574b6d8fe7336604204&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2F201502%2F2015020205.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506655581&di" +
                    "=e6f30311ddf5882291db76f06e1df4a5&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fpic39.nipic.com%2F20140312%2F18182031_192648468173_2.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1827175145," +
                    "2920212423&fm=27&gp=0.jpg",
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
            // 28
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506050764&di" +
                    "=cc2502587cc9a03396f51804a0e9668c&src=http://pic24.nipic" +
                    ".com/20121105/8172133_102932323157_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940473&di" +
                    "=f8bc1ea333d267243cf66fa027dfea55&imgtype=0&src=http%3A%2F" +
                    "%2Fpic1.win4000.com%2Fwallpaper%2F4%2F5841385dc4c07.jpg",
            // 30
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
            // 34
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1167367351," +
                    "2337216795&fm=27&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=275389721," +
                    "290980604&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940472&di" +
                    "=3b2f2c983151d807d1269ab14140186d&imgtype=0&src=http%3A%2F%2Fimg.pconline" +
                    ".com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1209%2F04%2Fc0" +
                    "%2F13594204_1346729316001.jpg",
            // 37
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506655674&di" +
                    "=51e14f51f4a23a775dca472bf5856926&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fimg1.3lian.com%2Fimg013%2Fv1%2F76%2Fd%2F113.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502006940470&di" +
                    "=5dd9ab84cd6a3f7a1f5fa4aca9c4236f&imgtype=0&src=http%3A%2F%2Fwww" +
                    ".pp3.cn%2Fuploads%2Fallimg%2F111122%2F112U12H1-2.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2721049557," +
                    "1576638292&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506060988378&di" +
                    "=7e264184f4aeaf55b05b2806881db6d0&imgtype=0&src=http%3A%2F%2Fimg" +
                    ".bimg.126.net%2Fphoto%2Fx7E8bRuNrYxmwsSJYz4aFg%3D%3D" +
                    "%2F2020990332799396513.jpg",
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
            // 45
            "http://pic36.nipic.com/20131203/3822951_101052690000_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065094078&di" +
                    "=7a889bb6e1d3de2c7643f2c2a6cb0dac&imgtype=0&src=http%3A%2F%2Ffile06.16sucai" +
                    ".com%2F2016%2F0224%2F45bd13295ffab7662e4d91035ee8ec58.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065139744&di" +
                    "=58d8616f88de705a8c614d7ed03697f2&imgtype=0&src=http%3A%2F%2Fc.hiphotos" +
                    ".baidu" +
                    ".com%2Fzhidao%2Fpic%2Fitem%2F95eef01f3a292df5226e4abebe315c6035a873e8.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506055069&di" +
                    "=01cbc7eace96adb548e641908ca97891&src=http://imgsrc.baidu" +
                    ".com/forum/pic/item/c1b6a9014c086e069a2b2abe02087bf40bd1cb77.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506055078&di" +
                    "=1d41ce8bc0b0249231ca56173e24cac5&src=http://www.kumi" +
                    ".cn/photo/3d/d7/c7/3dd7c7b7a1660103.jpg",
            // 50
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214715&di" +
                    "=d0350a926f22da2d35d2d66b962f7150&imgtype=0&src=http%3A%2F%2Fd.5857.com" +
                    "%2Fxzmnw_160719%2F005.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214715&di" +
                    "=d7120a62a1e7c66ecabd8c668d3dcea4&imgtype=0&src=http%3A%2F%2Ffile06.16sucai" +
                    ".com%2F2016%2F0624%2Fdf2696da1daf6457d637fba5f48d7cb4.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214715&di" +
                    "=5f73853fbf29bb89e3fa58d47be3b526&imgtype=0&src=http%3A%2F%2Fpic.58pic" +
                    ".com%2F01%2F35%2F65%2F51bOOOPICb0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214715&di" +
                    "=e6262fcf7d4777ba5752620f437511c1&imgtype=0&src=http%3A%2F%2Fwww.taopic" +
                    ".com%2Fuploads%2Fallimg%2F110624%2F6445-1106241S54729.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214714&di" +
                    "=0322f88f8d583f44d51401d594059388&imgtype=0&src=http%3A%2F%2Fimg.daimg" +
                    ".com%2Fuploads%2Fallimg%2F120425%2F3-1204251F4403Q.jpg",
            // 55
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506057834&di" +
                    "=9b67c4589d9fd87e2b3c997bd635b53d&src=http://pic72.nipic" +
                    ".com/file/20150716/8572479_150637066000_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214706&di" +
                    "=89afe403c68eec07e45c400479e3b76c&imgtype=0&src=http%3A%2F%2Fwww.taopic" +
                    ".com%2Fuploads%2Fallimg%2F110325%2F551-11032512091171.jpghttps://timgsa" +
                    ".baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214705&di" +
                    "=47889eaab04a18e0361944bd5761973d&imgtype=0&src=http%3A%2F%2Fpic.58pic" +
                    ".com%2F58pic%2F13%2F09%2F48%2F87358PICT5I_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214705&di" +
                    "=47889eaab04a18e0361944bd5761973d&imgtype=0&src=http%3A%2F%2Fpic.58pic" +
                    ".com%2F58pic%2F13%2F09%2F48%2F87358PICT5I_1024.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214705&di" +
                    "=8fa5b5e7679fc8e2ffd53d9677cfca29&imgtype=0&src=http%3A%2F%2Fimg.daimg" +
                    ".com%2Fuploads%2Fallimg%2F110707%2F3-110FG34511410.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214705&di" +
                    "=27acf341f217417ede5abace4e2d3d17&imgtype=0&src=http%3A%2F%2Fpic36.nipic" +
                    ".com%2F20131228%2F8097124_180838483000_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214704&di" +
                    "=fe31012dbdc53b08a9f301ca7bdcbe31&imgtype=0&src=http%3A%2F%2Fpic64.nipic" +
                    ".com%2Ffile%2F20150408%2F8665332_172559813162_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214704&di" +
                    "=18cd89ba0b583cd263163457dead112c&imgtype=0&src=http%3A%2F%2Fpic9.nipic" +
                    ".com%2F20100902%2F2531170_125907676897_2.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214703&di" +
                    "=b60ff0cba83d2018b2162a0e17151b34&imgtype=0&src=http%3A%2F%2Ffile06.16sucai" +
                    ".com%2F2016%2F0304%2F323680336d8f5ec5096b8434e7b85b9e.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065214702&di" +
                    "=c2265d05907075e8070483354953a45b&imgtype=0&src=http%3A%2F%2Fpic62.nipic" +
                    ".com%2Ffile%2F20150319%2F8665332_153743590617_2.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4207502575," +
                    "1912978869&fm=27&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=9593763," +
                    "1594527254&fm=27&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3910580287," +
                    "2095174572&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506660063&di" +
                    "=d728074cca5792e4888ca9136c5a062f&imgtype=jpg&er=1&src=http%3A%2F%2Fp1.image" +
                    ".hiapk.com%2Fuploads%2Fallimg%2F150413%2F7730-150413103526-51.jpghttps" +
                    "://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065349150" +
                    "&di=3ea98dae06f925ee68b199f33d28ebdf&imgtype=0&src=http%3A%2F" +
                    "%2Fimg05.tooopen.com%2Fimages%2F20141020%2Fsy_73111417435.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065349150&di" +
                    "=3ea98dae06f925ee68b199f33d28ebdf&imgtype=0&src=http%3A%2F%2Fimg05.tooopen" +
                    ".com%2Fimages%2F20141020%2Fsy_73111417435.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506660077&di" +
                    "=6b5b79c5241b46d411c29282ec0a1f96&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fimg2.pconline.com.cn%2Fpconline%2F0708%2F01%2F1070260_06.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506065362953&di" +
                    "=ca10d425846de13a6963309bbe21223c&imgtype=0&src=http%3A%2F%2Fimg05.tooopen" +
                    ".com%2Fimages%2F20141020%2Fsy_73111417435.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506660087&di" +
                    "=1864a9f0319ade757f4ce02ef1e3b550&imgtype=jpg&er=1&src=http%3A%2F" +
                    "%2Fpic.58pic.com%2F58pic%2F11%2F73%2F88%2F74M58PICti9.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=735769285," +
                    "2766993657&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b10000_10000&sec=1506055293&di" +
                    "=5cc152cded4175b422dec5766990ded9&src=http://pic.58pic" +
                    ".com/58pic/13/17/93/44G58PICAwu_1024.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4089904835," +
                    "3312332039&fm=27&gp=0.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506660115&di" +
                    "=eb453b48251ff28d823d2f2900012cbd&imgtype=jpg&er=1&src=http%3A%2F%2Fa" +
                    ".hiphotos.baidu" +
                    ".com%2Fzhidao%2Fpic%2Fitem%2F6a600c338744ebf8bafcf437dff9d72a6059a72d.jpg"
    };

}
