package cn.edu.ustc.wlresolver.activity;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import cn.edu.ustc.wlresolver.element.Wakelock;

/**
 * Created by Shinelon on 2018/1/3.
 */
public class WLData implements Parcelable {
    //继承Parcelable接口方便传递
    public String Process;
    public int PID;
    public String Package;
    public String WakelockType;
    public Drawable Icon;
    public WLData()
    {
        Process = "NO WAKELOCKS";
    }

    public WLData(String Process, int PID, String Package,String wakelockType,Drawable Icon) {
        this.Process = Process;
        this.PID = PID;
        this.Package = Package;
        this.Icon = Icon;
        this.WakelockType = wakelockType;
    }

    public WLData(Parcel source)
    {
        ClassLoader cl = getClass().getClassLoader();
        Process = source.readString();
        PID = source.readInt();
        Package = source.readString();
//        Icon = (Drawable)source.readValue(cl);
        WakelockType = source.readString();
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(Process);
        dest.writeInt(PID);
        dest.writeString(Package);
//        dest.writeValue(Icon);
        dest.writeString(WakelockType);
    }



    public static final Creator<WLData> CREATOR = new Creator<WLData>() {
        @Override
        public WLData createFromParcel(Parcel source) {

            return new WLData(source);
        }

        @Override
        public WLData[] newArray(int size) {
            return new WLData[0];
        }
    };


    public String getPackage()
    {
        return this.Package;
    }
    public Drawable getIcon()
    {
        return this.Icon;
    }
    public String getProcess() { return this.Process; }
    public int getPID() { return this.PID; }
    public String getWakelockType() {
        return WakelockType;
    }

    public void setProcess(String Process) { this.Process = Process; }
    public void setPID(int PID) { this.PID = PID; }
    public void setPackage(String Package){ this.Package = Package; }
    public void setIcon(Drawable Icon) { this.Icon = Icon; }
    public void setWakelockType(String wakelockType) {
        WakelockType = wakelockType;
    }

}
