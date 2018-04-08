package cn.edu.ustc.wlresolver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    //广播接收器，用于定时运行后台服务
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,BackgroundService.class);
        context.startService(i);
    }
}
