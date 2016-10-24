package com.jimbo.myapplication.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import com.jimbo.myapplication.view.activity.ConnectActivity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jsj1996m on 2016/10/20.
 */
public class AnimatorUtils {
    private Queue<Animator> animators;
    private Queue<View> views;
    private Animator.AnimatorListener listener;
    Context context;
    private boolean isEnd;

    public AnimatorUtils(Context context) {
        animators = new LinkedList<>();
        views = new LinkedList<>();
        this.context = context;
        listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animators.size() == 0) {
                    isEnd = true;
                    return;
                }
                animators.poll().start();
                views.poll().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    public void addAnimator(View view) {
        Animator animator = getViewAnimator(view);
        animators.offer(animator);
        views.offer(view);
        animator.addListener(listener);
    }

    public void start() {
        if (isEnd = false) {
            return;
        } else {

            isEnd = false;
            if (animators.size() == 0) {
                throw new RuntimeException("Animator队列为空");
            }
            animators.poll().start();
            views.poll().setVisibility(View.VISIBLE);
        }
    }

    private Animator getViewAnimator(final View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        ObjectAnimator move = ObjectAnimator.ofFloat(view, "y", view.getY() + ConnectActivity.dip2px(context, 10), view.getY());
//        alpha.setTarget(view);
//        alpha.setDuration(200);
        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.setTarget(view);
        animatorSet.setDuration(1000);
        animatorSet.play(alpha).with(move);
        return animatorSet;

    }
}
