package com.cpacm;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 * 装载旋转进度按钮位图的按钮，继承自{@link FloatingActionButton}
 * </p>
 * <p>
 *
 * @author cpacm
 * </p>
 */
public class FloatingMusicButton extends FloatingActionButton {

    private RotatingProgressDrawable coverDrawable;
    private int percent, color;
    private ColorStateList backgroundHint;
    private float progress = 0f;
    private boolean isRotation = false;

    public FloatingMusicButton(Context context) {
        super(context);
        setMaxImageSize();
    }

    public FloatingMusicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxImageSize();
    }

    public FloatingMusicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMaxImageSize();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 利用反射重新定义fab图片的大小，默认充满整个fab
     */
    public void setMaxImageSize() {
        try {
            Class clazz = getClass().getSuperclass();
            Method sizeMethod = clazz.getDeclaredMethod("getSizeDimension");
            sizeMethod.setAccessible(true);
            int size = (Integer) sizeMethod.invoke(this);
            //set fab maxsize
            Field field = clazz.getDeclaredField("maxImageSize");
            field.setAccessible(true);
            field.setInt(this,size);
            //get fab impl
            Field field2 = clazz.getDeclaredField("impl");
            field2.setAccessible(true);
            Object o = field2.get(this);
            //set fabimpl maxsize
            Method maxMethod = o.getClass().getSuperclass().getDeclaredMethod("setMaxImageSize", int.class);
            maxMethod.setAccessible(true);
            maxMethod.invoke(o, size);

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        //postInvalidate();
    }

    /**
     * 对fmb进行配置
     *
     * @param percent        进度条宽度百分比
     * @param color          进度条颜色
     * @param backgroundHint fmb背景颜色
     */
    public void config(int percent, int color, ColorStateList backgroundHint) {
        this.percent = percent;
        this.color = color;
        this.backgroundHint = backgroundHint;
        config();
    }

    public void config() {
        if (coverDrawable != null) {
            coverDrawable.setProgressWidthPercent(percent);
            coverDrawable.setProgressColor(color);
            if (backgroundHint != null) {
                setBackgroundTintList(backgroundHint);
            }
            coverDrawable.setProgress(progress);
            coverDrawable.rotate(isRotation);
            //setMaxImageSize();
        }
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
        if (coverDrawable != null) {
            coverDrawable.setProgress(progress);
        }
    }

    /**
     * 设置按钮背景
     *
     * @param drawable
     */
    public void setCoverDrawable(Drawable drawable) {
        this.coverDrawable = new RotatingProgressDrawable(drawable);
        config();
        setImageDrawable(this.coverDrawable);
        postInvalidate();
    }

    public void setCover(Bitmap bitmap) {
        coverDrawable = new RotatingProgressDrawable(getResources(),bitmap);
        config();
        setImageDrawable(this.coverDrawable);
        postInvalidate();
    }

    public void rotate(boolean rotate) {
        coverDrawable.rotate(rotate);
        isRotation = rotate;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putBoolean("rotation", isRotation);
        bundle.putFloat("progress", progress);
        if (coverDrawable != null) {
            bundle.putFloat("rotation_angle", coverDrawable.getRotation());
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        Bundle bundle = (Bundle) state;
        isRotation = bundle.getBoolean("rotation");
        progress = bundle.getFloat("progress");
        requestLayout();
    }
}
