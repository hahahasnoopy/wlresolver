package cn.edu.ustc.wlresolver.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import cn.edu.ustc.wlresolver.R;

/**
 * Created by Shinelon on 2018/2/5.
 */

public class ProcessActivity extends Activity {
    private String packageName;
    private int  PID;
    private String processName;
    private Drawable icon;
    private String wakelockType;
    private int CPUUsage;
    private String version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("oncreate","created");
        setContentView(R.layout.process_item);//设置视图
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);//must extends AppCompatActivity
        final WLData  processSelected =getIntent().getParcelableExtra("ProcessSelected");
        CardView cardView = findViewById(R.id.kill);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                killProcess(processSelected.getPackage());//按下按钮时 杀掉进程
            }
        });
        packageName =  processSelected.getPackage();
        PID = processSelected.getPID();
        processName = processSelected.getProcess();
        icon = getIcon(PID);
        wakelockType = processSelected.getWakelockType();
        CPUUsage  = getCPUUsage(PID);


        ImageView iconHolder = findViewById(R.id.Icon);
        TextView processHolder = findViewById(R.id.Process);
        TextView versionHolder =  findViewById(R.id.Version);
        TextView typeHolder = findViewById(R.id.Type);
        TextView riskHolder = findViewById(R.id.Risk);
        TextView cpuHolder =  findViewById(R.id.CPU);
        TextView killer =  findViewById(R.id.killer);
        if(icon != null)
            iconHolder.setImageDrawable(icon);

        killer.setText("FORCE KILL");
        processHolder.setText(processName);
        versionHolder.setText("Version: "+ version);
        typeHolder.setText(wakelockType);
        cpuHolder.setText(CPUUsage+"%");
        if(CPUUsage < 5)
        {
            riskHolder.setText("High");
            riskHolder.setTextColor(Color.RED);
        }
        else if(CPUUsage >= 5 && CPUUsage <= 10)
        {
            riskHolder.setText("Medium");
            riskHolder.setTextColor(Color.BLUE);
        }
        else
        {
            riskHolder.setText("Low");
            riskHolder.setTextColor(Color.GREEN);
        }


    }



    //根据top指令获取cpu消耗
    public int getCPUUsage(int PID)
    {
        int Usage = 0;
        int skipLines = 0;
        String cmdOutLine;
//        try
//        {
//            Process p = Runtime.getRuntime().exec(new String[]{"top", "-n", "1"});
//            BufferedReader breader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            while ((cmdOutLine = breader.readLine()) != null)
//            {
//                //System.out.println(cmdOutLine);
//                if(skipLines < 7)
//                {
//                    skipLines = skipLines + 1;
//                }
//                else
//                {
//                    String[] values = new String[100];
//                    String[] v = new String[5]; //for splitting CPU Usage which is of the form 5%
//                    cmdOutLine = cmdOutLine.replaceAll("\\s+", " ");
//                    values = cmdOutLine.split(" "); //get single line command output
//                    int pid = (int)Integer.parseInt(values[1]);
//                    v = values[3].split("%"); //get integer CPU Usage
//                    int cpu = (int)Integer.parseInt(v[0]);
//                    if(pid == PID)
//                    {
//                        Usage = cpu;
//                        //System.out.println(Usage+"%");
//                    }
//                }
//
//            }
//        } catch (Exception exception) {
//            exception.getMessage();
//        }
        return Usage;
    }

    //使用这个方法来杀掉正在运行的后台程序
    public void killProcess(String Package){
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(Package);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);//回到主界面
    }

//    //该方法用于获取唤醒锁类型 通过解析命令行结果得出
//    public String  getWakelockType(int PID){
//        int pid;
//        String wacklocktype = null;
//        try
//        {
//            //导出电源电源管理数据
//            Process process = Runtime.getRuntime().exec(new String[]{ "su","-c","dumpsys power"});
//
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//
//            int count=0;//to skip the first line of bufferreader
//            String s; //to store bufferreader nextline
//
//            while ((s =bufferedReader.readLine())!= null)
//            {
//                if (isPresent(s))
//                {
//                    if(count==0)
//                        count++;
//                    else
//                    {
//                        String intValue = s.replaceAll("[^0-9]", " "); //replace all alpa to space
//                        intValue = intValue.replaceAll("\\s+", " ");//replace extra space
//
                        //split the string into words
//                        String[] values = new String[4];
//                        values = intValue.split(" ");
//
//                        //uid and pid
//                        pid=Integer.parseInt(values[2]);
//
//                        if(pid==PID)
//                        {
//                            s = s.replaceAll("\\s+", " ");
//                            String [] temp = s.split(" ");
//                            wakelockType = temp[1];
                            //System.out.println(temp[1]);
//
//                        }
//
//                    }
//
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return  wakelockType;
//
//    }
//    //判断字段中是否有wakelock信息
//    private boolean isPresent(String s)
//    {
//        String [] deli1 = s.split("_");
//        String [] deli2 = s.split(" ");
//        String  a="WAKE";
//
//        //split with the '_'
//        for(int i=0;i<deli1.length;i++)
//            if(a.equalsIgnoreCase(deli1[i]))
//                return true;
//        //split with ' '
//        for(int i=0;i<deli2.length;i++)
//            if(a.equalsIgnoreCase(deli2[i]))
//                return true;
//
//        return false;//does not contain wakelock
//    }

    //这个方法根据PID获取应用的图标
    public Drawable getIcon(int PID)
    {
        Drawable Icon = null;
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        PackageManager pm = this.getPackageManager();

        List l = am.getRunningAppProcesses();//list of all rnning processes
        Iterator i = l.iterator();

        while(i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try
            {
                if(info.pid == PID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
                    Icon = pm.getApplicationIcon(info.processName);
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;
                }
            }
            catch(Exception e)
            {
                Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return Icon;
    }


}
