package cn.edu.ustc.wlresolver.element;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.ustc.wlresolver.activity.WLData;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Shinelon on 2018/2/7.
 */

public class Wakelock {


    Context mcontext;
    private  ArrayList<WLData> wakelocks;
    private String[] packageName = new String[0];
    private int[] userPid=new int[0];
    private   String[] processName = new String[0];//list of user process  name
    private Drawable[] imageId =new Drawable[0];

    public ArrayList<WLData> getWakelocks() {
        return wakelocks;
    }

    public Wakelock(Context context) {//获取上下文给getSystemService
        this.mcontext = context;

    }


    private ArrayList<WLData> getWakelock()
    {
        int pid,uid;

        try
        {
            //adb command to read the dumpsys power
            Process process = Runtime.getRuntime().exec(new String[]{ "su","-c","dumpsys power"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int count=0;//to skip the first line of bufferreader
            String s; //to store bufferreader nextline

            while ((s =bufferedReader.readLine())!= null)
            {
                if (isPresent(s))
                {
                    if(count==0)
                        count++;
                    else
                    {
                        String intValue = s.replaceAll("[^0-9]", " "); //replace all alpa to space
                        intValue = intValue.replaceAll("\\s+", " ");//replace extra space

                        //split the string into words
                        String[] values = new String[4];
                        values = intValue.split(" ");

                        //uid and pid
                        pid=Integer.parseInt(values[2]);
                        uid=Integer.parseInt(values[1]);

                        //system level application
                        if(uid<=10000);

                            //user level application
                        else if(pid!=0)
                            getAppInfo(pid);
                    }
                }
            }
            wakelocks = new ArrayList<WLData>();


            int counterSize = userPid.length;

            for (int counter = 0; counter < counterSize; counter++) {
                wakelocks.add(new WLData(processName[counter], userPid[counter], packageName[counter], imageId[counter]));
            }
        }
        catch (Exception e)
        {

            CharSequence text = "Exception found";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(mcontext, text, duration);
            toast.show();
        }
        return wakelocks;
    }

    //to check wakelock in dumpsys data
    private boolean isPresent(String s)
    {
        String [] deli1 = s.split("_");
        String [] deli2 = s.split(" ");
        String  a="WAKE";

        //split with the '_'
        for(int i=0;i<deli1.length;i++)
            if(a.equalsIgnoreCase(deli1[i]))
                return true;
        //split with ' '
        for(int i=0;i<deli2.length;i++)
            if(a.equalsIgnoreCase(deli2[i]))
                return true;

        return false;//does not contain wakelock
    }

    //get the app name using pid
    private void getAppInfo(int pID)
    {
        ActivityManager am = (ActivityManager)mcontext.getSystemService(ACTIVITY_SERVICE);
        PackageManager pm = mcontext.getPackageManager();

        List l = am.getRunningAppProcesses();//list of all rnning processes
        Iterator i = l.iterator();

        while(i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try
            {
                if(info.pid == pID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    //Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());

                    userPid=intdynamic(userPid);
                    userPid[userPid.length-1] = pID;

                    processName=stringdynamic(processName);
                    processName[processName.length-1]=c.toString();

                    packageName=stringdynamic(packageName);
                    packageName[packageName.length-1]=info.processName;

                    Drawable[] newArray = new Drawable[imageId.length + 1];
                    for (int j = 0; j < imageId.length; j++)
                    {
                        newArray[j] = imageId[j];
                    }
                    imageId = newArray;
                    imageId[imageId.length - 1] = pm.getApplicationIcon(info.processName);
                    //packageName = getApplicationContext().getPackageName();
                    //  appLabel = (String) pm.getApplicationLabel(info);
                }
            }
            catch(Exception e)
            {
                //Log.d("Process", "Error>> :"+ e.toString());
            }
        }
    }

    private String[] stringdynamic(String[] array)
    {
        String[] newArray = new String[array.length+1];

        // we assume that the old array is full

        for (int j=0; j<array.length; j++)
        {
            newArray[j] = array[j];
        }
        return newArray;
    }

    private int[] intdynamic(int[] array)
    {
        int[] newArray = new int[array.length+1];

        // we assume that the old array is full

        for (int j=0; j<array.length; j++)
        {
            newArray[j] = array[j];
        }
        return newArray;
    }

}
