package com.webenvelope.wk;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class QiangHongBaoService extends AccessibilityService {

    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";//微信包名
    private static final String ENVELOPE_TEXT_KEY  = "[微信红包]"; //红包消息关键字
    private static final String ENVLOP_CHECKED_KEY =  "com.tencent.mm:id/bdh" ;
    private static final String ENVLOP_OPEN_KEY = "领取红包";

    /**
     * 触发了通知栏界面变化等
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        switch (type) {
            //监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                for (CharSequence text : texts) {
                    String str = text.toString();
                    if (!str.isEmpty())
                        if (str.contains(ENVELOPE_TEXT_KEY)) {
                            // 模拟打开通知消息
                            openNotification(event);
                        }
                }
                break;

            //是否进入微信红包消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    //在聊天界面,去点中红包
                    clickHongBao();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    //点中了红包，下一步就是去拆红包
                    openHongBao();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    //拆完红包后看详细的纪录界面
                    detailHongBao();
                }
                break;

            default:
                break;
        }

    }

    /**
     * 红包详情
     */
    private void detailHongBao() {

    }

    /**
     * 点中红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void clickHongBao() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(ENVLOP_OPEN_KEY);
        if (list.isEmpty()) {
            list = nodeInfo.findAccessibilityNodeInfosByText(ENVELOPE_TEXT_KEY);
            for (AccessibilityNodeInfo n : list) {
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        } else {
            //最新的红包领起
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                if (parent != null) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    /**
     *拆红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openHongBao() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfos = rootInActiveWindow.findAccessibilityNodeInfosByViewId(ENVLOP_CHECKED_KEY);
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 模拟打开微信通知消息栏
     * @param event
     */
    private void openNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            //判断是否锁屏
            boolean isLocked = NotifyHelper.isScreenLocked(this);
            if (!isLocked) {
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else {
                   NotifyHelper.Vibrate(this,3000);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "抢红包服务中断", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "抢红包服务连接成功", Toast.LENGTH_SHORT).show();
    }

}
