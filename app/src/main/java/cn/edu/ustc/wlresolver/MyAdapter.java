package cn.edu.ustc.wlresolver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.ustc.wlresolver.activity.WLData;

/**
 * Created by Shinelon on 2018/2/1.
 * this is the recyclerView adapter of the app
 */

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private ArrayList<WLData> wakelockData;


    public MyAdapter(ArrayList<WLData> wakelock)
    {
        this.wakelockData = wakelock;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView processName;
        ImageView iconimage;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.processName = itemView.findViewById(R.id.Process);
            this.iconimage = itemView.findViewById(R.id.icon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        TextView process  = holder.processName;
        ImageView icon =holder.iconimage;
        process.setText(wakelockData.get(position).getProcess());
        icon.setImageDrawable(wakelockData.get(position).getIcon());

    }

    @Override
    public int getItemCount() {

             return wakelockData.size();
    }
}
