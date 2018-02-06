package cn.edu.ustc.wlresolver.element;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shinelon on 2018/1/29.
 *
 * not in use yet
 */

public class Wakelockinfo {

    /**
     * the name of the wakelock holder
     */
    @SerializedName("name")
    private String m_name;

    /**
     * the details (packages) for that wakelock (if any
     */
    @SerializedName("details")
    private String m_details;

    /**
     * the count
     */
    @SerializedName("count")
    private int m_count;

    /**
     * the expire count
     */
    @SerializedName("expire_count")
    private int m_expireCount;

    /**
     * the wake count
     */
    @SerializedName("wake_count")
    private int m_wakeCount;

    /**
     * the active_since time
     */
    @SerializedName("active_since")
    private long m_activeSince;

    /**
     * the total_time
     */
    @SerializedName("total_time")
    private long m_ttlTime;

    /**
     * the sleep time
     */
    @SerializedName("sleep_time")
    private long m_sleepTime;

    /**
     * the max time
     */
    @SerializedName("max_time")
    private long m_maxTime;

    /**
     * the last change
     */
    @SerializedName("last_change")
    private long m_lastChange;

    public Wakelockinfo(String name, int count, int expire_count, int wake_count, long active_since, long total_time, long sleep_time, long max_time, long last_change, long time)
    {
        // hack: remove "deleted: " from wakelock label (Siyah 1.5b6)
        if (name.startsWith("\"deleted: "))
        {
            m_name = "\"" + name.split(" ")[1];
        }
        else
        {
            m_name			= name;
        }
        m_count			= count;
        m_expireCount	= expire_count;
        m_wakeCount		= wake_count;
        m_activeSince	= active_since;
        m_ttlTime		= total_time;
        m_sleepTime		= sleep_time;
        m_maxTime		= max_time;
        m_lastChange	= last_change;

    }
}
