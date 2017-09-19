package com.weidi.artifact.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.weidi.artifact.R;


public class TakePictureService extends IntentService {
	
	private Context myContext;
	private SurfaceView mySurfaceView;
	private SurfaceHolder myHolder;
	private Camera myCamera;
	private int mCamera;
	
	public TakePictureService() {//必须要有一个无参的构造方法
		super("TakePictureService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myContext = getApplicationContext();
		View view = View.inflate(myContext, R.layout.activity_camera, null);
		// 初始化surfaceview
		mySurfaceView = (SurfaceView) view.findViewById(R.id.camera_surfaceview);
		// 初始化surfaceholder
		myHolder = mySurfaceView.getHolder();
		myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		initCamera();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		myHolder = null;
		if(myCamera != null){
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;
		}
	}
	
	// 开始工作
	private void initCamera() {
		// 如果存在摄像头
		if (checkCameraHardware(myContext)) {
			// 获取摄像头（首选前置，无前置选后置）
			if (openFacingFrontCamera(0)) {// 开启摄像头成功
				
				autoFocus();// 进行对焦

			} else {// 开启摄像头失败就关闭服务然后退出
				
			}
		} else {// 没有摄像头的话就关闭服务然后退出
			
		}
	}
	
	// 对焦并拍照
	private void autoFocus() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 自动对焦
				myCamera.autoFocus(myAutoFocus);
				myCamera.autoFocus(myAutoFocus);
				myCamera.autoFocus(myAutoFocus);
				// 对焦后拍照
				myCamera.takePicture(null, null, myPicCallback);
			}
		}, 2000);
	}
	
	// 自动对焦回调函数(空实现)
	private AutoFocusCallback myAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// 应该是自动对焦成功后拍照

		}
	};
	
	// 拍照成功回调函数 文件有点大，需要处理一下
	private PictureCallback myPicCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				File tmpFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Cache");
				if (!tmpFile.exists()) {
					tmpFile.mkdirs();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				String time = sdf.format(new Date());
				String fname = time + ".jpg";
				File picture = new File(tmpFile, fname);

				// 将得到的照片进行270°旋转，使其竖直
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				Matrix matrix = new Matrix();
				if (mCamera == 0) {
					matrix.preRotate(270);// 前置时
				} else if (mCamera == 1) {
					matrix.preRotate(90);// 后置时
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
				fos = null;
				bitmap = null;
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
	
	// 得到后置摄像头
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private boolean openFacingFrontCamera(int startFront) {// “0”表示开启前置，“1”表示开启后置；如果在开启前置失败时，则去开启后置；如果在开启后置失败时，则退出
		try {
			mCamera = startFront;
			boolean front = false;
			// 尝试开启前置摄像头
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			if (startFront == 0) {
				// 如果开启前置失败（无前置）则开启后置
				for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
					Camera.getCameraInfo(camIdx, cameraInfo);
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						myCamera = Camera.open(camIdx);
					}
				}
				if (myCamera == null) {
					front = true;
				}
				if (front) {
					for (int camIdx = 0, cameraCount = Camera
							.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
						Camera.getCameraInfo(camIdx, cameraInfo);
						if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
							myCamera = Camera.open(camIdx);
						}
					}
				}
			} else if (startFront == 1) {
				for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
					Camera.getCameraInfo(camIdx, cameraInfo);
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
						myCamera = Camera.open(camIdx);
					}
				}
			}
			// 开启后置
			if (myCamera == null) {
				return false;
			}
			// 这里的myCamera为已经初始化的Camera对象
			myCamera.setPreviewDisplay(myHolder);
			myCamera.startPreview();
			return true;
		} catch (Exception e) {
			if (myCamera != null) {
				myCamera.stopPreview();
				myCamera.release();
				myCamera = null;
			}
			e.printStackTrace();
		}
		return false;
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
	
	
}
