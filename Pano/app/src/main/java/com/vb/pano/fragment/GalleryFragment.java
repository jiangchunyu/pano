package com.vb.pano.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vb.pano.CodeActivity;
import com.vb.pano.R;
import com.vb.pano.WebActivity;
import com.vb.pano.bean.Pano;
import com.vb.pano.util.PanoHelper;
import com.vb.pano.util.Utils;

import org.xutils.x;

import java.util.Collections;
import java.util.List;

/**
 * Created by seven on 2016/11/9.
 */

public class GalleryFragment extends Fragment{

    private View view;
    private SwipeRefreshLayout mSwipeLayout;
    private ListView listView;
    private PanoHelper helper;
    private LayoutInflater inflater;
    private List<Pano> panoList;
    private View emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_gallery,container,false);
        initView();
        return view;
    }
    private void initView()
    {
        inflater=LayoutInflater.from(getActivity());
        mSwipeLayout=(SwipeRefreshLayout)view.findViewById(R.id.id_swipe_ly);
        listView=(ListView)view.findViewById(R.id.id_listview);
        emptyView=view.findViewById(R.id.emptyView);
        helper=new PanoHelper(getActivity());
        mSwipeLayout.setOnRefreshListener(refreshListener);
        mSwipeLayout.setColorSchemeResources(new int[]{
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        });

        listView.setOnItemClickListener(itemClickListener);
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Pano pano=panoList.get(position);
                 Intent intent=new Intent(getActivity(), WebActivity.class);
                 intent.putExtra("panoId",pano.getPanoId());
                 intent.putExtra("name",pano.getName());
                 intent.putExtra("upload",pano.isUpload());
                 //startActivityForResult(intent,0x01);
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
                PanoAdapter panoAdapter=new PanoAdapter(getActivity(),panoList);
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
            ViewHolder holder=null;
            if(convertView==null)
            {
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.gallery_item,null);
                holder.bgView=(ImageView)convertView.findViewById(R.id.thumnail);
                holder.titleView=(TextView)convertView.findViewById(R.id.title);
                holder.timeView=(TextView)convertView.findViewById(R.id.time);
                holder.codeView=(TextView)convertView.findViewById(R.id.qrcode);
                convertView.setTag(holder);
            }
            else
            {
                holder=(ViewHolder) convertView.getTag();
            }
            final Pano pano=list.get(position);
            x.image().bind(holder.bgView,pano.getThumNail(), Utils.getPanoImageOption());
            holder.titleView.setText(pano.getName());
            holder.timeView.setText(pano.getTime());
            holder.codeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(), CodeActivity.class);
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
