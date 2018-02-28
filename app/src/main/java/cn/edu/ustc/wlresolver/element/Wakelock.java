package cn.edu.ustc.wlresolver.element;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.ustc.wlresolver.activity.WLData;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Shinelon on 2018/2/7.
 */

public class Wakelock {


    Context mcontext;
    private  ArrayList<WLData> wakelocks;
    private String[] packageName;
    private int[] userPid;
    private   String[] processName ;//list of user process  name
    private Drawable[] imageId ;
    private String[] wakelockType ;
    int counterSize ;
    int num = 0; //计数



//    public ArrayList<WLData> getWakelocks() {
//        return wakelocks;
//    }

    public Wakelock(Context context) {//获取上下文给getSystemService
        this.mcontext = context;

    }


    public ArrayList<WLData> getWakelock() throws IOException {
        int pid,uid;
        String WLType;


            //adb command to read the dumpsys power
            Process process = Runtime.getRuntime().exec(new String[]{ "su","-c","dumpsys power"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int count=0;//to skip the first line of bufferreader
            String s; //to store bufferreader nextline
            while (count==0) {
                if ((s =bufferedReader.readLine())!= null){
                System.out.println(s);
                if (isPresent(s)) {
                    if (count == 0) {//从第一行读取wakelock数量,利用正则表达式
                        String regEx = "[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(s);
                        counterSize = Integer.parseInt(m.replaceAll("").trim());

                        packageName = new String[counterSize];
                        userPid = new int[counterSize];
                        imageId = new Drawable[counterSize];
                        processName = new String[counterSize];
                        wakelockType = new String[counterSize];
                        System.out.println("wakelock数量是 " + counterSize);
                        count++;
                    }
                }
                }
            }



                    for (int i=0;i<counterSize;i++)
                    {

                        s=bufferedReader.readLine();
                        System.out.println(s);
                        String substring =  s.substring(s.indexOf("uid"),s.length());
                        String intValue = substring.replaceAll("[^0-9]", " "); //replace all alpa to space
                        intValue = intValue.replaceAll("\\s+", " ");//replace extra space

                        //split the string into words
                        String[] values = new String[4];
                        values = intValue.split(" ");
                        //uid and pid
                        pid=Integer.parseInt(values[2]);
                        uid=Integer.parseInt(values[1]);
                        System.out.println("uid is"+uid +"pid is"+ pid);
                        s = s.replaceAll("\\s+", " ");
                        String [] temp = s.split(" ");
                        WLType = temp[1];
                        //System.out.println(temp[1]);
                        //system level application
//                        if(uid<=10000)
//                            ;
//
//                            //user level application
//                        else
                            if(pid!=0)
                            getAppInfo(uid,WLType);
                        i++;
                    }

            wakelocks = new ArrayList<WLData>();



            for (int counter = 0; counter < counterSize; counter++) {
                if (userPid[counter]>10000)
                wakelocks.add(new WLData(processName[counter], userPid[counter], packageName[counter],wakelockType[counter],imageId[counter]));
            }



        System.out.println("wakelocks.size "+wakelocks.size());
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
    private void getAppInfo(int UID,String wltype)
    {
        ActivityManager am = (ActivityManager) mcontext.getSystemService(ACTIVITY_SERVICE);
        PackageManager pm = mcontext.getPackageManager();
        List<ApplicationInfo> list =  pm.getInstalledApplications(0);
        System.out.println("list.size() "+list.size());
        Iterator i = list.iterator();

        while(i.hasNext())
        {
            ApplicationInfo info = (ApplicationInfo) (i.next());
            System.out.println(info.toString());
            System.out.println(info.uid);
            try
            {
                if(info.uid == UID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    System.out.println( "Id: "+ info.uid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
                    userPid[num] = UID;
                    System.out.println("UID is"+UID);

                    processName[num]=c.toString();
                    System.out.println("processName is"+ c.toString());

                    packageName[num]=info.processName;
                    System.out.println("packageName is"+info.processName);

                    imageId[num] = pm.getApplicationIcon(info.processName);

                    wakelockType[num]=wltype;//获取wakelock类型
                    System.out.println("wltype is"+wltype);


                    num++;
                }
            }
            catch(Exception e)
            {
                //Log.d("Process", "Error>> :"+ e.toString());
            }
        }
    }
//
//    private String[] stringdynamic(String[] array)
//    {
//        String[] newArray = new String[array.length+1];
//
//        // we assume that the old array is full
//
//        for (int j=0; j<array.length; j++)
//        {
//            newArray[j] = array[j];
//        }
//        return newArray;
//    }
//
//    private int[] intdynamic(int[] array)
//    {
//        int[] newArray = new int[array.length+1];
//
//        // we assume that the old array is full
//
//        for (int j=0; j<array.length; j++)
//        {
//            newArray[j] = array[j];
//        }
//        return newArray;
//    }

}
