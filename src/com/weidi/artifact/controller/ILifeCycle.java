package com.weidi.artifact.controller;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * Created by root on 17-1-17.
 * 如果要实现,感觉这样的流程比较合适:
 * Activity传递到Controller,然后Controller再把这些生命周期传递到Fragment,
 * 最后由Fragment再传递到各自的Controller.
 * 而不是由Activity的Controller直接传递到Fragment的Controller.
 */

public interface ILifeCycle extends Serializable {

    //    void onCreate(Bundle savedInstanceState);

    //    void onResume();

    //    void onPause();

    //    void onStop();

    //    void onDestroy();

    //    void onBackPressed();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onConfigurationChanged(Configuration newConfig);

}
