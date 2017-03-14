package com.vb.pano.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vb.pano.DeviceActivity;
import com.vb.pano.HomeActivity;
import com.vb.pano.R;
import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.vb.pano.R.id.battery;
import static com.vb.pano.R.id.heading;

/**
 * Created by seven on 2016/11/9.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView batteryView, networkView, headingView, pitchView, rollView;
    private CheckBox uploadbox;
    private SwipeRefreshLayout mSwipeLayout;
    private Button button;
    private HomeActivity a;
    private String ip;
    private LinearLayout rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView();

        return view;
    }

    private void initView() {
        a = (HomeActivity) getActivity();
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        rootView=(LinearLayout)view.findViewById(R.id.rootView);
        batteryView = (TextView) view.findViewById(battery);
        networkView = (TextView) view.findViewById(R.id.network);
        headingView = (TextView) view.findViewById(heading);
        pitchView = (TextView) view.findViewById(R.id.pitch);
        rollView = (TextView) view.findViewById(R.id.roll);
        button = (Button) view.findViewById(R.id.set);
        uploadbox = (CheckBox) view.findViewById(R.id.upload_checkbox);
        button.setOnClickListener(this);
        initData();
        mSwipeLayout.setOnRefreshListener(refreshListener);
        mSwipeLayout.setColorSchemeResources(new int[]{
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        });
        uploadbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveUploadOnLine(getActivity(), isChecked);
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };

    private void initData() {
        boolean isUploadOnline = Utils.isUpLoadOnline(getActivity());
        uploadbox.setChecked(isUploadOnline);
        ip = a.device.getIpAddress();
        if (TextUtils.isEmpty(ip)) {
            return;
        }
        String url = "http://" + ip + "/status";
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                mSwipeLayout.setRefreshing(false);
                parseJson(result);
            }
        });
    }

    private void parseJson(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            int battery = jsonData.getInt("battery");
            boolean network = jsonData.getBoolean("network");
            JSONObject attitudeJson = jsonData.getJSONObject("attitude");
            double heading = attitudeJson.getDouble("heading");
            double pitch = attitudeJson.getDouble("pitch");
            double roll = attitudeJson.getDouble("roll");
            batteryView.setText(battery + "");
            if (network) {
                networkView.setText("正常");
            } else {
                networkView.setText("异常");
            }
            headingView.setText(heading+"");
            pitchView.setText(pitch+"");
            rollView.setText(roll+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DeviceActivity.class);
        intent.putExtra("config", ParamsUtils.getConfig(getActivity()));
        intent.putExtra("ip", ip);
        startActivity(intent);
    }
}
