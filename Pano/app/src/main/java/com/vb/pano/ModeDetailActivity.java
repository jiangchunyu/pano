package com.vb.pano;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vb.pano.bean.Config;
import com.vb.pano.bean.Param;
import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeDetailActivity extends BaseActivity {

    private Button backBtn;
    private Button addMode;
    private ExpandableListView expandableListView;
    private String config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_detail);
        initTopBar();
        initView();
    }


    private void initTopBar()
    {
        backBtn=(Button)findViewById(R.id.backBtn);
        addMode=(Button)findViewById(R.id.add_mode);
        Drawable drawable=getResources().getDrawable(R.drawable.backbtn);
        drawable.setBounds(0,0,40,60);
        backBtn.setCompoundDrawables(drawable,null,null,null);
        backBtn.setCompoundDrawablePadding(-100);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ModeDetailActivity.this,NewModeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String params= ParamsUtils.getParams(this,config);
        List<Config> configs=ParamsUtils.convertToBeans(params);
        List<String > group=new ArrayList<>();
        Map<Integer, List<Param>> child=new HashMap<>();
        if(configs!=null&&configs.size()>0)
        {
            for(int i=0;i<configs.size();i++)
            {
                group.add((i+1)+"号相机");
                List<Param> list=new ArrayList<>();
                Config c=configs.get(i);
                //亮度
                String brightness=c.getBrightness();
                //对比度
                String contrast=c.getContrast();
                //饱和度
                String saturation=c.getSaturation();
                //锐度
                String sharpness=c.getSharpness();
                //快门
                String shutter=c.getShutter_spd();
                //iso
                String iso=c.getIso();
                //wb
                String wb=c.getWb();
                Param param1=new Param();
                param1.setName("饱和度");
                param1.setNumber(saturation);
                Param param2=new Param();
                param2.setName("对比度");
                param2.setNumber(contrast);
                Param param3=new Param();
                param3.setName("亮度");
                param3.setNumber(brightness);
                Param param4=new Param();
                param4.setName("锐度");
                param4.setNumber(sharpness);
                Param param5=new Param();
                param5.setName("快门");
                param5.setNumber(shutter);
                Param param6=new Param();
                param6.setName("ISO");
                param6.setNumber(iso);
                Param param7=new Param();
                param7.setName("白平衡");
                param7.setNumber(wb);
                list.add(param1);
                list.add(param2);
                list.add(param3);
                list.add(param4);
                list.add(param5);
                list.add(param6);
                list.add(param7);
                child.put(i,list);
            }
            MyBaseExpandableListAdapter adapter=new MyBaseExpandableListAdapter(this,group,child);
            expandableListView.setAdapter(adapter);
            expandableListView.expandGroup(0);

        }

    }

    private void initView()
    {
        expandableListView=(ExpandableListView)findViewById(R.id.expand_listview);
        config= getIntent().getStringExtra("config");
        addView();
    }
    private void addView()
    {
        //添加头部view
        View header=LayoutInflater.from(this).inflate(R.layout.modedetail_head,null);
        TextView modeView=(TextView) header.findViewById(R.id.mode_name);
        if(!TextUtils.isEmpty(config))
        {
            modeView.setText("模式名称:"+config);
        }
        expandableListView.addHeaderView(header);
        //添加底部view
        View footer=LayoutInflater.from(this).inflate(R.layout.modedetail_bottom,null);
        Button setBtn=(Button)footer.findViewById(R.id.bottom_setting);
        setBtn.setOnClickListener(setClickListener);
        expandableListView.addFooterView(footer);
    }
    View.OnClickListener setClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(ModeDetailActivity.this, DeviceActivity.class);
            intent.putExtra("config",config);
            intent.putExtra("ip", Utils.getIp(ModeDetailActivity.this));
            startActivity(intent);
        }
    };
    class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {
        public List<String> group;
        private Map<Integer, List<Param>> childMap;
        public LayoutInflater inflater;

        public MyBaseExpandableListAdapter(Context context,List<String> group,Map<Integer, List<Param>> childMap)
        {
            this.group=group;
            this.childMap=childMap;
            this.inflater=LayoutInflater.from(context);
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childMap.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childMap.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null)
            {
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.modedetail_item,null);
                holder.nameView=(TextView)convertView.findViewById(R.id.title);
                holder.numView=(TextView)convertView.findViewById(R.id.number);
                convertView.setTag(holder);
            }
            else
            {
                holder=(ViewHolder) convertView.getTag();
            }
            Param param=childMap.get(groupPosition).get(childPosition);
            holder.numView.setText(param.getNumber());
            holder.nameView.setText(param.getName());
            return convertView;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return group.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return group.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView=inflater.inflate(R.layout.modedetail_groupitem,null);
            TextView groupName=(TextView)convertView.findViewById(R.id.groupname);
            String title=group.get(groupPosition);
            groupName.setText(title);
            return convertView;
        }
        class GroupHolder
        {
            TextView groupName;
        }
        class ViewHolder
        {
            TextView nameView;
            TextView numView;
        }
    }
}
