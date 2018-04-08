package cn.edu.ustc.wlresolver;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.wlresolver.activity.ProcessActivity;
import cn.edu.ustc.wlresolver.activity.WLData;
import cn.edu.ustc.wlresolver.element.Wakelock;

/**
 * Created by Shinelon on 2018/2/7.
 */

public class BackgroundService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service","-------后台服务运行中-------");
        final List<WLData> wakelocks = new ArrayList<>();
        final Wakelock wakelock = new Wakelock(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    wakelocks.addAll(wakelock.getWakelock());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<wakelocks.size();i++){
                    if(!wakelocks.get(i).getProcess().equals("KingRoot")){//测试机的root授权需要kingroot 这货每次启动都会产生一个短时间唤醒锁，傻逼
                    ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    activityManager.killBackgroundProcesses(wakelocks.get(i).Package);
                    System.out.println("程序 " +wakelocks.get(i).Package+" 已被kill " );
                    }
                }

            }
        }).start();
        AlarmManager manager  = (AlarmManager) getSystemService(ALARM_SERVICE);
        int milisecondes  =  120000;//定时时间，单位为毫秒
        long  triggerAtTime  = SystemClock.elapsedRealtime()+ milisecondes;
        Intent i = new Intent(this,AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
