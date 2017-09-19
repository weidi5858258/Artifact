package com.weidi.artifact;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.IDisplayManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.InputEventReceiver;
import android.view.Surface;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

/**
 * Created by root on 17-3-1.
 */

public class SampleWindow {

    private IWindowSession mSession;
    private InputChannel mInputChannel;
    private Rect mInsets = new Rect();
    private Rect mFrame = new Rect();
    private Rect mVisibleInsets = new Rect();
    private Configuration mConfig = new Configuration();
    private Surface mSurface = new Surface();
    private Paint mPaint = new Paint();
    private IBinder mToken = new Binder();
    private MyWindow mWindow = new MyWindow();
    private WindowManager.LayoutParams mLp = new WindowManager.LayoutParams();
    private Choreographer mChoreographer;
    private InputHandler mInputHandler;
    private boolean mContinueAnime = true;

    public static void main(String[] args) {
        try {
            new SampleWindow().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        Looper.prepare();

        IWindowManager wms = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            mSession = WindowManagerGlobal.getWindowSession(Looper.myLooper());
            IDisplayManager dm = IDisplayManager.Stub.asInterface(
                    ServiceManager.getService(Context.DISPLAY_SERVICE));
            DisplayInfo di = dm.getDisplayInfo(Display.DEFAULT_DISPLAY);
            Point scrnSize = new Point(di.appWidth, di.appHeight);
            initLayoutParams(scrnSize);
            installWindow(wms);
            mChoreographer = Choreographer.getInstance();
            scheduleNextFrame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Looper.loop();

        mContinueAnime = false;
        uninstallWindow(wms);
    }

    private void initLayoutParams(Point screenSize) {

    }

    private void scheduleNextFrame() {

    }

    private void installWindow(IWindowManager wms) {

    }

    private void uninstallWindow(IWindowManager wms) {

    }

    private class InputHandler extends InputEventReceiver {

        /**
         * Creates an input event receiver bound to the specified input channel.
         *
         * @param inputChannel The input channel.
         * @param looper       The looper to use when invoking callbacks.
         */
        public InputHandler(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }
    }

    private class MyWindow extends IWindow.Stub {

        @Override
        public void closeSystemDialogs(String s) throws RemoteException {

        }

        @Override
        public void dispatchAppVisibility(boolean b) throws RemoteException {

        }

        @Override
        public void dispatchDragEvent(DragEvent dragEvent) throws RemoteException {

        }

        @Override
        public void dispatchGetNewSurface() throws RemoteException {

        }

        @Override
        public void dispatchScreenState(boolean b) throws RemoteException {

        }

        @Override
        public void dispatchSystemUiVisibilityChanged(int i, int i1, int i2, int i3) throws
                RemoteException {

        }

        @Override
        public void dispatchWallpaperCommand(String s, int i, int i1, int i2, Bundle bundle,
                                             boolean b) throws RemoteException {

        }

        @Override
        public void dispatchWallpaperOffsets(float v, float v1, float v2, float v3, boolean b)
                throws RemoteException {

        }

        @Override
        public void doneAnimating() throws RemoteException {

        }

        @Override
        public void executeCommand(String s, String s1, ParcelFileDescriptor
                parcelFileDescriptor) throws RemoteException {

        }

        @Override
        public void moved(int i, int i1) throws RemoteException {

        }

        @Override
        public void resized(Rect rect, Rect rect1, Rect rect2, boolean b, Configuration
                configuration) throws RemoteException {

        }

        @Override
        public void windowFocusChanged(boolean b, boolean b1) throws RemoteException {

        }
    }// MyWindow

}
