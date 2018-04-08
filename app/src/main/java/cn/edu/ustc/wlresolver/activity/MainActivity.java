package cn.edu.ustc.wlresolver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.wlresolver.MyAdapter;
import cn.edu.ustc.wlresolver.R;
import cn.edu.ustc.wlresolver.element.Wakelock;

/**
 * Created by Shinelon on 2018/2/3.
 */


public class MainActivity extends Activity{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private static RecyclerView.Adapter adapter;

    private WLData selectedProcess;
    //Wakelock Details will be stored in below arrays which are changed dynamically in size
    private List<WLData> wakelocks;
    Wakelock  wakelock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
          wakelock = new Wakelock(this);
        try {
            wakelocks = wakelock.getWakelock();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.mainlayout);
        Context context = getApplicationContext();
        recyclerView =  findViewById(R.id.recycler);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position)
            {
                clicked(view, position);
            }
        }));
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar =findViewById(R.id.toolbar) ;
        //todo
        TextView textView =findViewById(R.id.toolbarText);
        textView.setText("Wakelock数量是："+wakelocks.size());
        adapter = new MyAdapter(wakelocks);
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        wakelocks.clear();
        try {
            wakelocks.addAll(wakelock.getWakelock());
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        TextView textView =findViewById(R.id.toolbarText);
        textView.setText("Wakelock数量是："+wakelocks.size());


    }

    public void clicked(View view, int position)
    {
        int selectedItemPosition = recyclerView.getChildLayoutPosition(view);
        RecyclerView.ViewHolder viewHolder
                = recyclerView.findViewHolderForPosition(selectedItemPosition);
        TextView textViewName
                =  viewHolder.itemView.findViewById(R.id.Process);
        String selectedName = (String) textViewName.getText();
        for(int counter = 0; counter < wakelocks.size(); counter++)
        {
            WLData tmpObject = wakelocks.get(counter);
            if(tmpObject.getProcess().equals((String)selectedName))
            {
                selectedProcess = tmpObject;

            }
        }
        Intent intent = new Intent(this, ProcessActivity.class);
        intent.putExtra("ProcessSelected", selectedProcess);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        Log.d("start process","Process:" + selectedName);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.about) {
//            startActivity(new Intent(this, About.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}