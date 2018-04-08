package cn.edu.ustc.wlresolver.element;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    private  List<WLData> wakelocks;
    private String[] packageName;
    private int[] userid;
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


    public List<WLData> getWakelock() throws IOException {
        int pid,uid;
        String WLType;


            //adb command to read the dumpsys power
            Process process = Runtime.getRuntime().exec(new String[]{ "su","-c","dumpsys power"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int count=0;//to skip the first line of bufferreader
            String s; //to store bufferreader nextline
            while (count==0) {
                if ((s =bufferedReader.readLine())!= null){
                //System.out.println(s);
                if (isPresent(s)) {
                    System.out.println(s);
                    if (count == 0) {//从第一行读取wakelock数量,利用正则表达式
                        String regEx = "[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(s);
                        counterSize = Integer.parseInt(m.replaceAll("").trim());

                        packageName = new String[counterSize];
                        userid = new int[counterSize];
                        imageId = new Drawable[counterSize];
                        processName = new String[counterSize];
                        wakelockType = new String[counterSize];
                        System.out.println("检测到的wakelock数量是 " + counterSize);
                        count++;
                    }
                }
                }
            }

                    for (int i=0;i<counterSize;i++)
                    {
                        System.out.println("读取第"+(i+1)+"行");

                        s=bufferedReader.readLine();
                        System.out.println(s);
                        String substring =  s.substring(s.indexOf("(uid"),s.length());
                        String intValue = substring.replaceAll("[^0-9]", " "); //删除所有字母
                        intValue = intValue.replaceAll("\\s+", " ");//删除多余空格

                        //split the string into words
                        String[] values ;
                        values = intValue.split(" ");
                        //uid and pid
                        pid=Integer.parseInt(values[2]);
                        uid=Integer.parseInt(values[1]);
                        System.out.println("uid 是"+uid +"pid 是"+ pid);
                        s = s.replaceAll("\\s+", " ");
                        String [] temp = s.split(" ");
                        WLType = temp[1];

                            if(pid!=0&&uid>10000)
                            getAppInfo(uid,WLType);

                    }

            wakelocks = new ArrayList<>();

            for (int counter = 0; counter < num; counter++) {
                if (userid[counter]>10000)
                wakelocks.add(new WLData(processName[counter], userid[counter], packageName[counter],wakelockType[counter],imageId[counter]));
            }

        System.out.println("非系统程序产生的wakelock数量为： "+wakelocks.size());
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
//        ActivityManager am = (ActivityManager) mcontext.getSystemService(ACTIVITY_SERVICE);
        PackageManager pm = mcontext.getPackageManager();
        List<ApplicationInfo> list =  pm.getInstalledApplications(0);//获取所有已安装的应用程序列表
        Iterator i = list.iterator();

        while(i.hasNext())
        {
            ApplicationInfo info = (ApplicationInfo) (i.next());
            //System.out.println(info.toString());
            //System.out.println(info.uid);
                try
            {
                if(info.uid == UID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    System.out.println( "Id: "+ info.uid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
                    userid[num] = UID;
                    System.out.println("UID is "+UID);

                    processName[num]=c.toString();
                    System.out.println("processName is "+ c.toString());

                    packageName[num]=info.processName;
                    System.out.println("packageName is "+info.packageName);

                    imageId[num] = pm.getApplicationIcon(info.processName);

                    wakelockType[num]=wltype;//获取wakelock类型
                    System.out.println("wltype is"+wltype);

                    num++;
                }
            }
            catch(Exception e)
            {
                Log.d("Wakelock.java ", "Error>> :"+ e.toString());
            }
        }
    }

}
