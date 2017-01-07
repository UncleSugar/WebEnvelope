package com.webenvelope.wk;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Description:   红包状态提示
 * Date       : 2017/1/4 17:18
 */
public class NotifyHelper {

    //判断是否息屏
    public  static boolean isScreenLocked(Context c) {
        android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(c.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();

    }

    //设置提示音，震动
    public static void Vibrate(Context c, long milliseconds) {
        Vibrator vib = (Vibrator) c.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
}
