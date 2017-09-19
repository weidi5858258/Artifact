package com.weidi.artifact.ui.internal;

import com.weidi.artifact.R;
import com.weidi.artifact.ui.PullToRefreshBase;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView headerImage;
	private final ProgressBar headerProgress;
	private final TextView headerText;

	private String pullLabel;
	private String refreshingLabel;
	private String releaseLabel;

	private final Animation rotateAnimation, resetRotateAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
		headerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);

		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180, 
						  Animation.RELATIVE_TO_SELF, 0.5f, 
						  Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);//动画持续时间
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0, 
							   Animation.RELATIVE_TO_SELF, 0.5f,
					           Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		/*
			 public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
			 public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
			 public static final int MODE_BOTH = 0x3;
		*/
		switch (mode) {
			case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH://0x2
				headerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
				break;
			case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH://0x1
			default:
				headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
				break;
		}
	}//LoadingLayout

	public void pullToRefresh() {//拉动到一定距离后刷新
		headerText.setText(pullLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(resetRotateAnimation);
	}
	
	public void releaseToRefresh() {//释放后立即刷新
		headerText.setText(releaseLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(rotateAnimation);
	}

	public void refreshing() {//正在刷新
		headerText.setText(refreshingLabel);
		headerImage.clearAnimation();
		headerImage.setVisibility(View.INVISIBLE);
		headerProgress.setVisibility(View.VISIBLE);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			headerText.setVisibility(TextView.VISIBLE);
			headerText.setText(pullLabel);
			headerImage.setVisibility(View.VISIBLE);
		};
	};
	public void reset() {
		headerText.setVisibility(TextView.INVISIBLE);
		headerImage.setVisibility(ImageView.INVISIBLE);
		headerProgress.setVisibility(View.GONE);
		handler.sendEmptyMessageDelayed(0, 500);//需要暂停一下才完美，不然刷新好后，会出现一下“向下的箭头和刷新的文本”，这样看起来就不好了。
	}
	
	public void setPullLabel(String pullLabel) {//设置拉动时的文本
		this.pullLabel = pullLabel;
	}


	public void setRefreshingLabel(String refreshingLabel) {//设置正在刷新时要显示的文本
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {//设置释放手指后要显示的文本
		this.releaseLabel = releaseLabel;
	}

	public void setTextColor(int color) {//设置显示的文本的颜色
		headerText.setTextColor(color);
	}

}
