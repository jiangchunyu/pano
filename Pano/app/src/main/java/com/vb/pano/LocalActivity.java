package com.vb.pano;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vb.pano.bean.Pano;
import com.vb.pano.util.PanoHelper;
import com.vb.pano.util.Utils;

import org.xutils.x;

import java.util.Collections;
import java.util.List;

public class LocalActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeLayout;
    private ListView listView;
    private PanoHelper helper;
    private LayoutInflater inflater;
    private List<Pano> panoList;
    private View emptyView;
    private Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        initView();
        getData();
    }
    private void initView()
    {
        inflater=LayoutInflater.from(this);
        backBtn=(Button)findViewById(R.id.backBtn);

        mSwipeLayout=(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
        listView=(ListView)findViewById(R.id.id_listview);
        emptyView=findViewById(R.id.emptyView);
        helper=new PanoHelper(this);
        mSwipeLayout.setOnRefreshListener(refreshListener);
        mSwipeLayout.setColorSchemeResources(new int[]{
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        });
        Drawable drawable = getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0, 0, 40, 60);
        backBtn.setCompoundDrawables(drawable, null, null, null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setOnItemClickListener(itemClickListener);
    }
    AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Pano pano=panoList.get(position);
            Intent intent=new Intent(LocalActivity.this, WebActivity.class);
            intent.putExtra("panoId",pano.getPanoId());
            intent.putExtra("name",pano.getName());
            intent.putExtra("upload",pano.isUpload());
            startActivity(intent);
        }
    };
    SwipeRefreshLayout.OnRefreshListener refreshListener=new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData();
        }
    };
    private void getData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Pano> panos=helper.queryAll();
                Collections.sort(panos);
                Message msg=new Message();
                msg.obj=panos;
                handler.sendMessage(msg);
            }
        }).start();
    }
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mSwipeLayout.setRefreshing(false);
            panoList=(List<Pano>)msg.obj;
            if(panoList!=null&&panoList.size()>0)
            {
               PanoAdapter panoAdapter=new PanoAdapter(LocalActivity.this,panoList);
                listView.setAdapter(panoAdapter);
                listView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            else
            {
                listView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    };
    class PanoAdapter extends BaseAdapter
    {
        private List<Pano> list;
        private LayoutInflater inflater;
        public PanoAdapter(Context context, List<Pano> list)
        {
            this.list=list;
            this.inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PanoAdapter.ViewHolder holder=null;
            if(convertView==null)
            {
                holder=new PanoAdapter.ViewHolder();
                convertView=inflater.inflate(R.layout.gallery_item,null);
                holder.bgView=(ImageView)convertView.findViewById(R.id.thumnail);
                holder.titleView=(TextView)convertView.findViewById(R.id.title);
                holder.timeView=(TextView)convertView.findViewById(R.id.time);
                holder.codeView=(TextView)convertView.findViewById(R.id.qrcode);
                convertView.setTag(holder);
            }
            else
            {
                holder=(PanoAdapter.ViewHolder) convertView.getTag();
            }
            final Pano pano=list.get(position);
            x.image().bind(holder.bgView,pano.getThumNail(), Utils.getPanoImageOption());
            holder.titleView.setText(pano.getName());
            holder.timeView.setText(pano.getTime());
            holder.codeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(LocalActivity.this, CodeActivity.class);
                    intent.putExtra("panoId",pano.getPanoId());
                    intent.putExtra("qrCode",pano.getQrCode());
                    intent.putExtra("upload",pano.isUpload());
                    startActivity(intent);
                }
            });
            return convertView;
        }
        class ViewHolder
        {
            ImageView bgView;
            TextView titleView;
            TextView timeView;
            TextView codeView;
        }
    }
}
