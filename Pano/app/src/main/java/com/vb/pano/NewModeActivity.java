package com.vb.pano;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewModeActivity extends BaseActivity {

    private EditText modeName_Edit;
    private Spinner spinner;
    private Button newModeBtn;
    private Button setModeBtn;
    private LinearLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mode);
        initTopBar();
        initView();
    }
    private void initTopBar()
    {
        Button backBtn=(Button)findViewById(R.id.backBtn);
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
    }
    private void initView()
    {
        List<String> mItems=new ArrayList<>();
        rootView=(LinearLayout)findViewById(R.id.activity_new_mode);
        modeName_Edit=(EditText)findViewById(R.id.newmode_name);
        spinner=(Spinner)findViewById(R.id.newmode_spinner);
        newModeBtn=(Button)findViewById(R.id.newmode_btn);
        setModeBtn=(Button)findViewById(R.id.newmode_setbtn);
        List<String> modes=ParamsUtils.getAllMode(this);
        mItems.add("请选择");
        mItems.addAll(modes);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,mItems);
        spinner.setAdapter(adapter);
        newModeBtn.setOnClickListener(clickListener);
        setModeBtn.setOnClickListener(setModeClickListener);
    }
    View.OnClickListener setModeClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String modeName=modeName_Edit.getText().toString();
            if(!TextUtils.isEmpty(modeName))
            {
                String modeParam=spinner.getSelectedItem().toString();
                String json;
                if(modeParam.equals("请选择"))
                {
                    String config=ParamsUtils.getConfig(NewModeActivity.this);
                    json=ParamsUtils.getParams(NewModeActivity.this,config);
                }
                else
                {
                    json=ParamsUtils.getParams(NewModeActivity.this,modeParam);
                }

                if(!TextUtils.isEmpty(json))
                {
                    ParamsUtils.saveParams(NewModeActivity.this,modeName,json);
                }
                Intent intent=new Intent(NewModeActivity.this, DeviceActivity.class);
                intent.putExtra("config",modeName);
                intent.putExtra("ip", Utils.getIp(NewModeActivity.this));
                startActivity(intent);

            }
            else
            {
                Utils.Toast(rootView,"模式名称不能为空");
            }
        }
    };
    View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         String modeName=modeName_Edit.getText().toString();
            if(!TextUtils.isEmpty(modeName))
            {

                String modeParam=spinner.getSelectedItem().toString();
                String json;
                if(modeParam.equals("请选择"))
                {
                    String config=ParamsUtils.getConfig(NewModeActivity.this);
                    json=ParamsUtils.getParams(NewModeActivity.this,config);
                }
                else
                {
                    json=ParamsUtils.getParams(NewModeActivity.this,modeParam);
                }

                if(!TextUtils.isEmpty(json))
                {
                    ParamsUtils.saveParams(NewModeActivity.this,modeName,json);
                    Utils.Toast(rootView,"新建模式成功");
                }
                else
                {
                    Utils.Toast(rootView,"新建模式失败");
                }

            }
            else
            {
                Utils.Toast(rootView,"模式名称不能为空");
            }
        }
    };
}
