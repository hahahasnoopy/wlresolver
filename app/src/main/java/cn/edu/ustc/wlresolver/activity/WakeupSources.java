package cn.edu.ustc.wlresolver.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.wlresolver.element.Wakelockinfo;
import eu.chainfire.libsuperuser.Shell;

import static android.content.ContentValues.TAG;

/**
 * Created by Shinelon on 2018/1/27.
 * name		active_count	event_count	wakeup_count	expire_count	active_sincetotal_time	max_time	last_change	prevent_suspend_time

 */

public class WakeupSources {
    private static String FILE_PATH ="";//wakelock data store path

    public static  ArrayList<Wakelockinfo>  parseWakeupSources(Context context)
    {
        Log.i(TAG,"parsing"+FILE_PATH);
        String delimiter = String.valueOf('\t');
        delimiter =delimiter+"+";
        ArrayList<Wakelockinfo> m_wl = new ArrayList<>();
        ArrayList<String[]> rows  = parserFile(FILE_PATH,delimiter);

        //time since boot
        long msSinceBoot = SystemClock.elapsedRealtime();

        //list of running processes
        ActivityManager activityManager  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();


        for (int i=1;i<rows.size();i++){
            String[] data = rows.get(i);
            String name = data[0].trim(); 						// name   (use trim to remove space after string)
            int count = Integer.valueOf(data[1]);				// active_count
            int expire_count = Integer.valueOf(data[4]);		// expire_count
            int wake_count = Integer.valueOf(data[3]);			// wakeup_count
            long active_since = Long.valueOf(data[5]);			// active_since
            long total_time = Long.valueOf(data[6]);			// total_time
            long sleep_time = Long.valueOf(data[9]);			// prevent_suspend_time
            long max_time = Long.valueOf(data[7]);				// max_time
            long last_change = Long.valueOf(data[8]);			// last_change
            Log.d(TAG, "Native Kernel wakelock parsed"
                    + " name=" + name
                    + " count=" + count
                    + " expire_count=" + expire_count
                    + " wake_count=" + wake_count
                    + " active_since=" + active_since
                    + " total_time=" + total_time
                    + " sleep_time=" + sleep_time
                    + " max_time=" + max_time
                    + "last_change=" + last_change
                    + "ms_since_boot=" + msSinceBoot);
            Wakelockinfo wl = new Wakelockinfo(name,count,expire_count,wake_count,active_since,total_time,sleep_time,max_time,last_change,msSinceBoot);
            m_wl.add(wl);
        }
        return  m_wl;
    }


    protected static ArrayList<String[]> parserFile(String FILE_PATH,String delimiter){//delimiter 分隔符
        ArrayList<String[]> rows = new ArrayList<>();
        try{
            FileReader fileReader  = new FileReader(FILE_PATH);
            BufferedReader bufferedReader =  new BufferedReader(fileReader);
            String currentRecored;
            while ((currentRecored= bufferedReader.readLine())!=null)
                rows.add(currentRecored.split(delimiter));//use String.split method to split string array
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //retry with root
        List<String> res = Shell.SU.run("cat"+FILE_PATH);
        for(int i = 0;i<res.size();i++)
        {
            rows.add(res.get(i).split(delimiter));
        }
        if(res.isEmpty())
        {
            Log.i("empty","wakelock could not be read from " + FILE_PATH);
        }
        return rows;
    }
}
