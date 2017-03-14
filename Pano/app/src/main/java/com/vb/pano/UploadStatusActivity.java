package com.vb.pano;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vb.pano.bean.Upload;
import com.vb.pano.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.activity_upload_status)
public class UploadStatusActivity extends BaseActivity {

    @ViewInject(R.id.backBtn)
    private Button backBtn;

    @ViewInject(R.id.swipe)
    private SwipeRefreshLayout refreshLayout;

    @ViewInject(R.id.list)
    private ListView listview;

    @ViewInject(R.id.emptyView)
    private RelativeLayout emptyView;

    @ViewInject(R.id.loadView)
    private RelativeLayout loadView;

    private View bottomView;
    private ProgressDialog dialog;
    private String remoteIp;
    private int totalPage = 0;
    private int pageIndex = 0;
    private boolean isMore = false;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTopBar();
        initView();

    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在启动上传");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        remoteIp = getIntent().getStringExtra("remoteIp");
        refreshLayout.setColorSchemeResources(new int[]{
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light});
        bottomView = getLayoutInflater().inflate(R.layout.bottom, null);
        View headerView = getLayoutInflater().inflate(R.layout.header, null);
        listview.addFooterView(bottomView);
        listview.addHeaderView(headerView);
        bottomView.setVisibility(View.GONE);
        listview.setOnScrollListener(scrollListener);
        refreshLayout.setOnRefreshListener(listener);
        fetchUploadInfo(remoteIp, pageIndex, PAGE_SIZE);
      //  startRefresh();
    }

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            totalPage = 0;
            pageIndex = 0;
            isMore = true;
            adapter = null;
            fetchUploadInfo(remoteIp, pageIndex, PAGE_SIZE);
        }
    };

    private void refreshUploadInfo() {
        Utils.log("刷新中");
        String url = "http://" + remoteIp + "/query?fromPage=" + 0 + "&size=" + listview.getCount();
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("0")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        List<Upload> list = new ArrayList<Upload>();
                        JSONArray array = object.getJSONArray("searchResult");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject resultObject = array.getJSONObject(i);
                            Upload upload = new Upload();
                            upload.setCaptureTime(resultObject.getLong("captureTime"));
                            upload.setPanoId(resultObject.getString("panoId"));
                            upload.setPanoName(resultObject.getString("panoName"));
                            upload.setUploadPercent(resultObject.getDouble("uploadPercent"));
                            upload.setUploadTime(resultObject.getLong("uploadTime"));
                            upload.setUploadStatus(resultObject.getInt("uploadStatus"));
                            list.add(upload);
                        }
                        if (adapter != null) {
                            adapter.notifyAll(list);
                        }
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                String result="{\n" +
//                        "  \"result\": \"0\", \n" +
//                        "  \"msg\": \"成功\",\n" +
//                        "  \"data\": {\n" +
//                        "    \"totalCount\": 2,\n" +
//                        "    \"totalPage\": 10,\n" +
//                        "    \"searchResult\": [\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 60,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 1\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 9\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 2\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100, \n" +
//                        "        \"captureTime\": 1483499695.647, \n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      }\n" +
//                        "    ], \n" +
//                        "    \"size\": 10\n" +
//                        "  }\n" +
//                        "}\n";
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("result").equals("0")) {
//                        JSONObject object = jsonObject.getJSONObject("data");
//                        List<Upload> list = new ArrayList<Upload>();
//                        JSONArray array = object.getJSONArray("searchResult");
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject resultObject = array.getJSONObject(i);
//                            Upload upload = new Upload();
//                            upload.setCaptureTime(resultObject.getDouble("captureTime"));
//                            upload.setPanoId(resultObject.getString("panoId"));
//                            upload.setPanoName(resultObject.getString("panoName"));
//                            upload.setUploadPercent(resultObject.getDouble("uploadPercent"));
//                            upload.setUploadTime(resultObject.getDouble("uploadTime"));
//                            upload.setUploadStatus(resultObject.getInt("uploadStatus"));
//                            list.add(upload);
//                        }
//                        if(adapter!=null)
//                        {
//                            adapter.notifyAll(list);
//                        }
//                    }
//
//                } catch (Exception e) {
//                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void fetchUploadInfo(final String remoteIp, final int fromPage, int size) {
        String url = "http://" + remoteIp + "/query?fromPage=" + fromPage + "&size=" + size;
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                refreshLayout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("0")) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        int totalCount = object.getInt("totalCount");
                        totalPage = object.getInt("totalPage");
                        listview.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.INVISIBLE);
                        loadView.setVisibility(View.GONE);
                        if (fromPage < totalPage - 1) {
                            isMore = true;
                        } else {
                            isMore = false;
                        }
                        pageIndex++;
                        List<Upload> list = new ArrayList<Upload>();
                        JSONArray array = object.getJSONArray("searchResult");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject resultObject = array.getJSONObject(i);
                            Upload upload = new Upload();
                            upload.setCaptureTime(resultObject.getLong("captureTime"));
                            upload.setPanoId(resultObject.getString("panoId"));
                            upload.setPanoName(resultObject.getString("panoName"));
                            upload.setUploadPercent(resultObject.getDouble("uploadPercent"));
                            upload.setUploadTime(resultObject.getLong("uploadTime"));
                            upload.setUploadStatus(resultObject.getInt("uploadStatus"));
                            list.add(upload);
                        }
                        notifyListView(list);
                        if (fromPage == 0) {
                            startRefresh();
                        }
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                refreshLayout.setRefreshing(false);
                if (fromPage == 0) {
                    listview.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                }

//                String result="{\n" +
//                        "  \"result\": \"0\", \n" +
//                        "  \"msg\": \"成功\",\n" +
//                        "  \"data\": {\n" +
//                        "    \"totalCount\": 2,\n" +
//                        "    \"totalPage\": 10,\n" +
//                        "    \"searchResult\": [\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 60,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 1\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 9\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 2\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100,\n" +
//                        "        \"captureTime\": 1483499695.647,\n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"34D43540C33D4F94AFC1520EC3919FFB\",\n" +
//                        "        \"uploadTime\": 0,\n" +
//                        "        \"panoName\": null,\n" +
//                        "        \"uploadPercent\": 0,\n" +
//                        "        \"captureTime\": 1483500713.039,\n" +
//                        "        \"uploadStatus\": 0\n" +
//                        "      },\n" +
//                        "      {\n" +
//                        "        \"panoId\": \"D0145C88FE56461697E51A690D35CC05\",\n" +
//                        "        \"uploadTime\": 1483500701.268741,\n" +
//                        "        \"panoName\": \"欢迎莅临微景天下！\",\n" +
//                        "        \"uploadPercent\": 100, \n" +
//                        "        \"captureTime\": 1483499695.647, \n" +
//                        "        \"uploadStatus\": 3\n" +
//                        "      }\n" +
//                        "    ], \n" +
//                        "    \"size\": 10\n" +
//                        "  }\n" +
//                        "}\n";
//
//                //测试
//                refreshLayout.setRefreshing(false);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    if (jsonObject.getString("result").equals("0")) {
//                        JSONObject object = jsonObject.getJSONObject("data");
//                        int totalCount = object.getInt("totalCount");
//                        totalPage = object.getInt("totalPage");
//                        listview.setVisibility(View.VISIBLE);
//                        emptyView.setVisibility(View.INVISIBLE);
//                        loadView.setVisibility(View.GONE);
//                        if (fromPage < totalPage - 1) {
//                            isMore = true;
//                        } else {
//                            isMore = false;
//                        }
//                        pageIndex++;
//                        List<Upload> list = new ArrayList<Upload>();
//                        JSONArray array = object.getJSONArray("searchResult");
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject resultObject = array.getJSONObject(i);
//                            Upload upload = new Upload();
//                            upload.setCaptureTime(resultObject.getDouble("captureTime"));
//                            upload.setPanoId(resultObject.getString("panoId"));
//                            upload.setPanoName(resultObject.getString("panoName"));
//                            upload.setUploadPercent(resultObject.getDouble("uploadPercent"));
//                            upload.setUploadTime(resultObject.getDouble("uploadTime"));
//                            upload.setUploadStatus(resultObject.getInt("uploadStatus"));
//                            list.add(upload);
//                        }
//                        notifyListView(list);
//                    }
//
//                } catch (Exception e) {
//                }

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private UploadAdapter adapter;

    private Timer timer;

    private void startRefresh() {
        try {
            if(timer==null)
            {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        refreshUploadInfo();
                    }
                }, 1000, 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyListView(List<Upload> list) {
        if (adapter == null) {
            adapter = new UploadAdapter(this, list);
            listview.setAdapter(adapter);
        } else {
            adapter.notify(list);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (isMore) {
                        fetchUploadInfo(remoteIp, pageIndex, PAGE_SIZE);
                        bottomView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(UploadStatusActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                        bottomView.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub

        }
    };

    class UploadAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Upload> list;

        public UploadAdapter(Context context, List<Upload> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return super.areAllItemsEnabled();
        }

        public void notify(List<Upload> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void notifyAll(List<Upload> list) {
            this.list.clear();
            this.list.addAll(list);
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.upload_item, null, false);
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                holder.timeView = (TextView) convertView.findViewById(R.id.time);
                holder.bar = (ProgressBar) convertView.findViewById(R.id.bar);
                holder.uploadBtn = (LinearLayout) convertView.findViewById(R.id.btn);
                holder.statusView = (TextView) convertView.findViewById(R.id.status);
                holder.btnText = (TextView) convertView.findViewById(R.id.btn_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Upload upload = list.get(position);
            if (TextUtils.isEmpty(upload.getPanoName())) {
                holder.nameView.setText("未命名");
            } else {
                holder.nameView.setText(upload.getPanoName());
            }
            final int status = upload.getUploadStatus();
            switch (status) {
                case 0:
                    //待上传
                    holder.bar.setVisibility(View.GONE);
                    holder.statusView.setVisibility(View.GONE);
                    holder.uploadBtn.setVisibility(View.VISIBLE);
                    holder.timeView.setText("未知");
                    holder.btnText.setText("待上传");
                    break;
                case 1:
                    //上传中
                    holder.bar.setVisibility(View.VISIBLE);
                    holder.statusView.setVisibility(View.GONE);
                    holder.uploadBtn.setVisibility(View.GONE);
                    holder.timeView.setText("未知");
                    holder.bar.setProgress((int) (upload.getUploadPercent()));
                    break;
                case 2:
                    //上传暂停
                    holder.bar.setVisibility(View.GONE);
                    holder.statusView.setVisibility(View.GONE);
                    holder.uploadBtn.setVisibility(View.VISIBLE);
                    holder.btnText.setText("上传暂停");
                    holder.timeView.setText("未知");
                    break;
                case 3:
                    //上传完成
                    holder.bar.setVisibility(View.GONE);
                    holder.statusView.setVisibility(View.VISIBLE);
                    holder.uploadBtn.setVisibility(View.GONE);
                    holder.statusView.setText("上传完成");
                    long uploadTime = upload.getUploadTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date(uploadTime));
                    holder.timeView.setText(time);
                    break;
                case 9:
                    //发生错误
                    holder.bar.setVisibility(View.GONE);
                    holder.statusView.setVisibility(View.VISIBLE);
                    holder.uploadBtn.setVisibility(View.GONE);
                    holder.statusView.setText("上传错误");
                    holder.timeView.setText("未知");
                    break;
            }
            holder.uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload(upload.getPanoId(), status);
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameView;
            TextView timeView;
            TextView statusView;
            TextView btnText;
            ProgressBar bar;
            LinearLayout uploadBtn;
        }
    }

    //开始上传
    private void upload(String panoId, int status) {
        if (status == 0) {
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra("panoId", panoId);
            intent.putExtra("ip", remoteIp);
            startActivity(intent);
        } else {
            dialog.show();
            String url = "http://" + remoteIp + "/restartUpload?panoId=" + panoId+"&test="+ !Utils.isUpLoadOnline(this);
            RequestParams params = new RequestParams(url);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    dialog.dismiss();
                    Toast.makeText(UploadStatusActivity.this, "启动上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("result").equals("0")) {
                            Toast.makeText(UploadStatusActivity.this, "启动上传成功", Toast.LENGTH_SHORT).show();
                            //refreshUploadInfo();
                        } else {
                            Toast.makeText(UploadStatusActivity.this, "启动上传失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UploadStatusActivity.this, "启动上传失败", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }

    private void initTopBar() {
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
    }
}
