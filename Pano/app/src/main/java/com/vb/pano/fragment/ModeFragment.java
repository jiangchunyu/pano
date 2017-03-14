package com.vb.pano.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vb.pano.HomeActivity;
import com.vb.pano.ModeDetailActivity;
import com.vb.pano.R;
import com.vb.pano.bean.Config;
import com.vb.pano.bean.Device;
import com.vb.pano.util.ParamsUtils;
import com.vb.pano.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by seven on 2016/11/9.
 */

public class ModeFragment extends Fragment {

    private View view;
    private ListView listView;
    private List<String> list;
    private Device device;
    private HomeActivity activity;
    private ProgressDialog dialog;
    private ModeAdapter adapter;
    private LinearLayout rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mode, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在同步至相机");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        activity = (HomeActivity) getActivity();
        device = activity.device;
        rootView = (LinearLayout) view.findViewById(R.id.rootView);
        listView = (ListView) view.findViewById(R.id.listview);
        String modeConfig = ParamsUtils.getConfig(getActivity());
        List<String> modes = ParamsUtils.getAllMode(getActivity());
        list = modes;
        if (list != null && list.size() > 0) {
            adapter = new ModeAdapter(list, getActivity());
            listView.setAdapter(adapter);
            adapter.notifySelected(modeConfig);
        }

        listView.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView selectedView = (ImageView) view.findViewById(R.id.mode_select);
            if (selectedView.getVisibility() != View.VISIBLE) {
                String name = list.get(position);
                dialog.show();

                String params = ParamsUtils.getParams(getActivity(), name);
                if (TextUtils.isEmpty(params)) {
                    Utils.Toast(rootView, "异常");
                    dialog.dismiss();
                }
                List<Config> configs = ParamsUtils.convertToBeans(params);
                modifyPano(device.getIpAddress(), configs, name);
            }


        }
    };

    private void modifyPano(String ip, final List<Config> configs, final String name) {
        try {
            String url = "http://" + ip + "/captureParam";
            RequestParams params = new RequestParams(url);
            List<Config> curConfigs = Utils.convertConfigs(configs);
            String curJson = ParamsUtils.convertToJson(curConfigs);
            params.setBodyContent(curJson);
            params.setAsJsonContent(true);
            params.setConnectTimeout(10000);
            x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    dialog.dismiss();
                    Utils.Toast(rootView, "连接小黑失败");
                }


                @Override
                public void onSuccess(String result) {
                    Utils.log("result:" + result);
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int jsonResult = jsonObject.getInt("result");
                        if (jsonResult == 0) {
                            Utils.Toast(rootView, "模式修改成功");
                            ParamsUtils.saveConfig(getActivity(), name);
                            adapter.notifySelected(name);
                        } else {
                            Utils.Toast(rootView, "模式修改失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.Toast(rootView, "模式修改失败");
                    }
                }
            });
        } catch (Exception e) {
            Utils.Toast(rootView, "模式修改失败");
        }

    }

    class ModeAdapter extends BaseAdapter {
        public String selectedName;
        public List<String> list;
        public LayoutInflater inflater;

        public ModeAdapter(List<String> list, Context c) {
            this.inflater = LayoutInflater.from(c);
            this.list = list;
        }

        public void notifyAll(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void notifySelected(String selectedName) {
            this.selectedName = selectedName;
            notifyDataSetChanged();
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.mode_item, null);
                holder.nameView = (TextView) convertView.findViewById(R.id.mode_name);
                holder.selectView = (ImageView) convertView.findViewById(R.id.mode_select);
                holder.detailView = (TextView) convertView.findViewById(R.id.mode_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final String name = list.get(position);
            holder.nameView.setText(name);
            if (selectedName.equals(name)) {
                holder.selectView.setVisibility(View.VISIBLE);
            } else {
                holder.selectView.setVisibility(View.INVISIBLE);
            }
            holder.detailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ModeDetailActivity.class);
                    intent.putExtra("config", name);
                    startActivity(intent);

                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameView;
            ImageView selectView;
            TextView detailView;
        }
    }
}
