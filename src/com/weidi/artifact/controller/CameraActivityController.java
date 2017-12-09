package com.weidi.artifact.controller;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.weidi.artifact.activity.CameraActivity;
import com.weidi.artifact.controller.basecontroller.BaseActivityController;
import com.weidi.log.Log;
import com.weidi.threadpool.ThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 17-1-13.
 */

public class CameraActivityController extends BaseActivityController {

    private static final String TAG = "MainActivityController";
    private CameraActivity mCameraActivity;
    private SurfaceHolder mSurfaceHolder;
    private Camera myCamera;
    private int mCameraAngleTag;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public CameraActivityController(Activity activity) {
        super(activity);
        mCameraActivity = (CameraActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCameraActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 初始化surface
        initSurface();

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {
        // 这里得开线程进行拍照，因为Activity还未完全显示的时候，是无法进行拍照的，SurfaceView必须先显示
        ThreadPool.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 初始化camera并对焦拍照
                initCamera();
            }
        });
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (myCamera != null) {
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    public Object onEvent(int what, Object[] object) {
        return null;
    }

    // 初始化surface
    private void initSurface() {
        // 初始化surfaceholder
        mSurfaceHolder = mCameraActivity.mCameraSurfaceview.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // 初始化摄像头
    private void initCamera() {
        // 如果存在摄像头
        if (checkCameraHardware(mContext)) {
            Log.d(TAG, "initCamera(): 存在摄像头");
            // 获取摄像头（首选前置，无前置选后置）
            if (openFacingFrontCamera(0)) {
                Log.d(TAG, "initCamera(): 开启摄像头成功");
                // 开启摄像头成功

                // 进行对焦
                autoFocus();

            } else {
                Log.d(TAG, "initCamera(): 开启摄像头失败");
                // 开启摄像头失败就关闭服务然后退出

            }

        } else {
            Log.d(TAG, "initCamera(): 没有摄像头");
            // 没有摄像头的话就关闭服务然后退出

        }
    }

    // 对焦并拍照
    private void autoFocus() {
        Log.d(TAG, "autoFocus(): 对焦并拍照1");
        SystemClock.sleep(2000);
        // 自动对焦(现在不能这样了,不知道为什么)
        //        myCamera.autoFocus(myAutoFocus);
        //        myCamera.autoFocus(myAutoFocus);
        //        myCamera.autoFocus(myAutoFocus);
        // 对焦后拍照
        myCamera.takePicture(null, null, mPictureCallback);
        Log.d(TAG, "autoFocus(): 对焦并拍照2");
    }

    // 判断是否存在摄像头
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 设备存在摄像头
            return true;
        } else {
            // 设备不存在摄像头
            return false;
        }
    }

    // 得到后置摄像头
    // “0”表示开启前置，“1”表示开启后置；如果在开启前置失败时，则去开启后置；如果在开启后置失败时，则退出
    private boolean openFacingFrontCamera(int startFront) {
        try {
            mCameraAngleTag = startFront;
            boolean front = false;
            // 尝试开启前置摄像头
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            if (startFront == 0) {
                // 如果开启前置失败（无前置）则开启后置
                for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras();
                     camIdx < cameraCount; camIdx++) {
                    Camera.getCameraInfo(camIdx, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        myCamera = Camera.open(camIdx);
                        break;
                    }
                }
                if (myCamera == null) {
                    front = true;
                }
                if (front) {
                    for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras();
                         camIdx < cameraCount; camIdx++) {
                        Camera.getCameraInfo(camIdx, cameraInfo);
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            myCamera = Camera.open(camIdx);
                            break;
                        }
                    }
                }
            } else if (startFront == 1) {
                for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras();
                     camIdx < cameraCount; camIdx++) {
                    Camera.getCameraInfo(camIdx, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        myCamera = Camera.open(camIdx);
                        break;
                    }
                }
            }
            //开启后置失败
            if (myCamera == null) {
                return false;
            }
            // 这里的myCamera为已经初始化的Camera对象
            myCamera.setPreviewDisplay(mSurfaceHolder);
            myCamera.startPreview();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (myCamera != null) {
                myCamera.stopPreview();
                myCamera.release();
                myCamera = null;
            }
        }
        return false;
    }

    // 自动对焦回调函数(空实现)
    private Camera.AutoFocusCallback myAutoFocus = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // 应该是自动对焦成功后拍照
        }
    };

    // 拍照成功回调函数 文件有点大，需要处理一下
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken(): 拍照成功回调函数");
            try {
                // 完成拍照后关闭Activity
                mCameraActivity.finish();

                File tmpFile = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "/Pictures/Screenshots");
                if (!tmpFile.exists()) {
                    tmpFile.mkdirs();
                }

                String time = mSimpleDateFormat.format(new Date());
                String fname = time + ".jpg";
                File picture = new File(tmpFile, fname);

                // 将得到的照片进行270°旋转，使其竖直
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (mCameraAngleTag == 0) {
                    matrix.preRotate(270);//前置时
                } else if (mCameraAngleTag == 1) {
                    matrix.preRotate(90);//后置时
                }
                bitmap = Bitmap.createBitmap(bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        matrix,
                        true);
                FileOutputStream fos = new FileOutputStream(picture);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();
                fos = null;
                bitmap = null;
                // Toast.makeText(mContext, "拍照成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (myCamera != null) {
                    myCamera.stopPreview();
                    myCamera.release();
                    myCamera = null;
                }
            }
        }
    };


}
