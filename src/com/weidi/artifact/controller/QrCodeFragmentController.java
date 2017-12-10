package com.weidi.artifact.controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.WriterException;
import com.google.zxing.encoding.EncodingHandler;
import com.weidi.activity.ScanCodeActivity;
import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.QrCodeFragment;
import com.weidi.log.Log;
import com.weidi.utils.MyToast;

/**
 * Created by root on 17-1-13.
 */
public class QrCodeFragmentController extends BaseFragmentController {

    private static final String TAG = "QrCodeFragmentController";
    private static final boolean DEBUG = false;
    private QrCodeFragment mQrCodeFragment;

    public QrCodeFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mQrCodeFragment = (QrCodeFragment) fragment;
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
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
        if (mQrCodeFragment.getActivity() instanceof MainActivity) {
            ((MainActivity) mQrCodeFragment.getActivity()).title.setText("二维码");
        }
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
        switch (view.getId()) {
            case R.id.generate_qr_code_btn: {
                String contentString = mQrCodeFragment.input_content_et.getText().toString().trim();
                try {
                    if (!TextUtils.isEmpty(contentString)) {
                        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                        mQrCodeFragment.qrcode_bitmap_iv.setImageBitmap(qrCodeBitmap);
                        mQrCodeFragment.input_content_et.setText("");
                        mQrCodeFragment.scan_result_tv.setText(contentString);
                    } else {
                        MyToast.show("Text can be not empty");
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.generate_barcode_btn: {
                String contentString = mQrCodeFragment.input_content_et.getText().toString();
                int size = contentString.length();
                for (int i = 0; i < size; i++) {
                    int c = contentString.charAt(i);
                    if ((19968 <= c && c < 40623)) {
                        MyToast.show("text not be chinese");
                        return;
                    }
                }
                Bitmap mBmpOneCode = null;
                try {
                    if (!TextUtils.isEmpty(contentString)) {
                        mBmpOneCode = EncodingHandler.CreateOneDCode(contentString);
                        mQrCodeFragment.input_content_et.setText("");
                        mQrCodeFragment.scan_result_tv.setText(contentString);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                if (mBmpOneCode != null) {
                    mQrCodeFragment.qrcode_bitmap_iv.setImageBitmap(mBmpOneCode);
                }
                break;
            }

            case R.id.scan_code_btn: {
                Intent intent = new Intent(mBaseActivity, ScanCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mBaseActivity.startActivityForResult(intent, Constant.SCANNIN_GREQUEST_CODE);
                mBaseActivity.enterActivity();
                break;
            }

            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.SCANNIN_GREQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String content = bundle.getString("result");
                    mQrCodeFragment.input_content_et.setText(content);
                    //显示扫描到的内容
                    mQrCodeFragment.scan_result_tv.setText(content);
                    //显示
                    mQrCodeFragment.qrcode_bitmap_iv.setImageBitmap(
                            (Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

    //初始化
    private void init() {
        // mBaseActivity.getMainActivityController().setILifeCycle(mQrCodeFragment);
    }

}
