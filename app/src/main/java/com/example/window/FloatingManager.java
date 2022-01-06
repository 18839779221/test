package com.example.window;

/**
 * Auther: Crazy.Mo
 * DateTime: 2019/3/6 11:31
 * Summary:
 */
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * 悬浮窗管理类
 */
public class FloatingManager {

    private WindowManager mWindowManager;
    private volatile static FloatingManager mInstance;
    private Context mContext;

    //单例模式，其实这里可以不用DLC形式的
    public static FloatingManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized(FloatingManager.class) {
                mInstance = new FloatingManager(context);
            }
        }
        return mInstance;
    }

    private FloatingManager(Context context) {
        mContext = context;
        //初始化获取WindowManager对象
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 添加悬浮窗的View
     * @param view
     * @param params
     * @return
     */
    protected boolean addView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.addView(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除悬浮窗View
     * @param view
     * @return
     */
    protected boolean removeView(View view) {
        try {
            mWindowManager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新悬浮窗参数
     * @param view
     * @param params
     * @return
     */
    protected boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

