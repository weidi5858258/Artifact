package com.weidi.artifact.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.ILifeCycle;
import com.weidi.artifact.controller.QrCodeFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

/**
 * Created by root on 17-1-13.
 */
public class QrCodeFragment extends BaseFragment implements ILifeCycle {

    private static final String TAG = "QrCodeFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.input_content_et)
    public EditText input_content_et;

    @InjectView(R.id.generate_qr_code_btn)
    public Button generate_qr_code_btn;

    @InjectView(R.id.generate_barcode_btn)
    public Button generate_barcode_btn;

    @InjectView(R.id.scan_code_btn)
    public Button scan_code_btn;

    @InjectView(R.id.scan_result_tv)
    public TextView scan_result_tv;

    @InjectView(R.id.qrcode_bitmap_iv)
    public ImageView qrcode_bitmap_iv;

    private QrCodeFragmentController mQrCodeFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            MLog.d(TAG, "onAttach(): activity = " + activity);
        mQrCodeFragmentController = new QrCodeFragmentController(this);
        mQrCodeFragmentController.beforeInitView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) MLog.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        if (DEBUG) MLog.d(TAG, "onCreateView():savedInstanceState = " + savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG)
            MLog.d(TAG, "onActivityCreated(): savedInstanceState = " + savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            MLog.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) MLog.d(TAG, "onResume()");
        mQrCodeFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mQrCodeFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mQrCodeFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) MLog.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mQrCodeFragmentController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) MLog.d(TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mQrCodeFragmentController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    public QrCodeFragmentController getQrCodeFragmentController() {
        return mQrCodeFragmentController;
    }

    @InjectOnClick({
            R.id.generate_qr_code_btn,
            R.id.generate_barcode_btn,
            R.id.scan_code_btn})
    public void onClick(View view) {
        mQrCodeFragmentController.onClick(view);
    }

    @Override
    protected int provideLayout() {
        return R.layout.fragment_qr_code;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mQrCodeFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) MLog.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mQrCodeFragmentController.onPause();
        } else {
            mQrCodeFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
