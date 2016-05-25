package com.meishai.ui.dialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TrialInfo;
import com.meishai.entiy.TrialProcess;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.req.TrialReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 试用-查看详情
 *
 * @author sh
 */
public class TrialDetailDialog extends Dialog {

    private Context mContext;
    private CustomProgress mProgressDialog;
    private Button mBtnClose;
    private TrialInfo trialInfo;
    private ListView process_listview;
    private ProcessAdapter processAdapter;
    private List<TrialProcess> processes = new ArrayList<TrialProcess>();

    public TrialDetailDialog(Context context) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_trial_detail);
        this.initView();
        this.setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.tryProcess();
    }

    private void initView() {
        this.mBtnClose = (Button) this.findViewById(R.id.btn_close);
        this.process_listview = (ListView) this
                .findViewById(R.id.process_listview);
    }

    private void setListener() {
        this.mBtnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTrialInfo(TrialInfo trialInfo) {
        this.trialInfo = trialInfo;
    }

    private void notifyProcessAdapter() {
        if (null == processAdapter) {
            processAdapter = new ProcessAdapter(processes);
            this.process_listview.setAdapter(processAdapter);
        } else {
            processAdapter.setProcesses(processes);
            this.process_listview.setAdapter(processAdapter);
        }

    }

    private void tryProcess() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance()
                .getUserInfo().getUserID());
        data.put("appid", String.valueOf(trialInfo.getAppid()));
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        TrialReq.tryProcess(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<TrialProcess>>() {
                    }.getType();
                    processes = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != processes && !processes.isEmpty()) {
                        notifyProcessAdapter();
                    } else {
                        AndroidUtil.showToast(response.getTips());
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }

        });

    }

    class ProcessAdapter extends BaseAdapter {
        private List<TrialProcess> processes;

        public ProcessAdapter(List<TrialProcess> processes) {
            this.processes = processes;
        }

        public void setProcesses(List<TrialProcess> processes) {
            this.processes = processes;
        }

        @Override
        public int getCount() {
            return processes.size();
        }

        @Override
        public Object getItem(int position) {
            return processes.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProcessViewHolder holder = null;
            if (null == convertView) {
                holder = new ProcessViewHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(
                        R.layout.dialog_trial_detail_item, null);
                holder.sy_process = (TextView) convertView
                        .findViewById(R.id.sy_process);
                convertView.setTag(holder);
            } else {
                holder = (ProcessViewHolder) convertView.getTag();
            }
            TrialProcess process = processes.get(position);
            holder.sy_process.setText(process.getTime() + " "
                    + process.getText());
            return convertView;
        }

    }

    class ProcessViewHolder {
        // 试用进程文字 试用时间+文字
        private TextView sy_process;
    }
}
