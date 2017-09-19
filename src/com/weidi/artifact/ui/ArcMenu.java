package com.weidi.artifact.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-7-8.
 */
public class ArcMenu {
    private Activity context;
    private Context mContext;
    private int[] imageRes;
    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    private boolean isShowMenu = false;
    private int radius = 180;
    private double angle;

    public ArcMenu(Activity context, int[] imageRes) {
        this.context = context;
        angle = Math.PI / 2 / (imageRes.length - 2);
        radius = dip2px(context, radius);
        this.imageRes = imageRes;
        for (int imagRe : imageRes) {
            ImageView imageView = (ImageView) context.findViewById(imagRe);
            imageViewList.add(imageView);
        }
    }

    public ArcMenu(Context context, View view, int[] imageRes) {
        this.mContext = context;
        angle = Math.PI / 2 / (imageRes.length - 2);
        radius = dip2px(context, radius);
        this.imageRes = imageRes;
        for (int imagRe : imageRes) {
            ImageView imageView = (ImageView) view.findViewById(imagRe);
            imageViewList.add(imageView);
        }
    }

    public void openMenu() {
        isShowMenu = true;
        setItemVisible(true);
        ObjectAnimator animator1 = null;
        ObjectAnimator animator2 = null;
        ObjectAnimator animator3 = null;
        List<ObjectAnimator> objectAnimators = new ArrayList<ObjectAnimator>();
        AnimatorSet set = new AnimatorSet();
        int imageResLength = imageRes.length;
        for (int i = 1; i < imageResLength; i++) {
            animator1 = ObjectAnimator.ofFloat(
                    imageViewList.get(i),
                    "translationX",
                    (float) (-radius * Math.sin(angle * (i - 1))));
            animator2 = ObjectAnimator.ofFloat(
                    imageViewList.get(i),
                    "translationY",
                    (float) (-radius * Math.cos(angle * (i - 1))));
            animator3 = ObjectAnimator.ofFloat(
                    imageViewList.get(i),
                    "rotation",
                    0,
                    360f);
            objectAnimators.add(animator1);
            objectAnimators.add(animator2);
            objectAnimators.add(animator3);
        }

        int objectAnimatorsSize = objectAnimators.size();
        for (int i = 0; i < objectAnimatorsSize; i++) {
            set.playTogether(objectAnimators.get(i));
        }
        set.setDuration(500);
        set.start();
        //第0个图标，菜单图标，加入动画
        ObjectAnimator.ofFloat(imageViewList.get(0), "rotation", 0, 135f).setDuration(500).start();
    }

    public void closeMenu() {
        isShowMenu = false;
        ObjectAnimator animator1 = null;
        ObjectAnimator animator2 = null;
        ObjectAnimator animator3 = null;
        List<ObjectAnimator> objectAnimators = new ArrayList<ObjectAnimator>();
        AnimatorSet set = new AnimatorSet();

        for (int i = 1; i < imageRes.length; i++) {
            animator1 = ObjectAnimator.ofFloat(imageViewList.get(i), "translationX", 0);
            animator2 = ObjectAnimator.ofFloat(imageViewList.get(i), "translationY", 0);
            animator3 = ObjectAnimator.ofFloat(
                    imageViewList.get(i),
                    "rotation",
                    0,
                    -360f);
            objectAnimators.add(animator1);
            objectAnimators.add(animator2);
            objectAnimators.add(animator3);
        }

        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setItemVisible(false);
                super.onAnimationEnd(animation);
            }
        });

        for (int i = 0; i < objectAnimators.size(); i++) {
            set.playTogether(objectAnimators.get(i));
        }

        set.setDuration(500);
        set.start();
        ObjectAnimator.ofFloat(imageViewList.get(0), "rotation", 135f, 0).setDuration(500).start();
    }

    public void switchMenu() {
        if (isShowMenu) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    public void clickItem() {
        setItemVisible(false);
        closeMenu();
    }

    private void setItemVisible(boolean isVisible) {
        int imageResLength = imageRes.length;
        for (int i = 1; i < imageResLength; i++) {
            if (isVisible) {
                imageViewList.get(i).setVisibility(View.VISIBLE);
            } else {
                imageViewList.get(i).setVisibility(View.GONE);
            }
        }
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
