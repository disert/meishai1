package com.meishai.ui.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.SDCardUtils;
import com.meishai.entiy.Upgrade;
import com.meishai.entiy.Upgrade.Cancel;
import com.meishai.entiy.Upgrade.Confirm;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 升级弹出框
 *
 * @author sh
 */
public class UpgradeDialog extends Dialog {

    private Context mContext;
    private CompleteReceiver completeReceiver;
    private Upgrade upgrade;
    private TextView title;
    private TextView content;
    private LinearLayout lay_close;
    private Button btn_close;
    private LinearLayout lay_upgrade;
    private Button btn_upgrade;

    public UpgradeDialog(Context context, CompleteReceiver completeReceiver) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
        this.completeReceiver = completeReceiver;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_upgrade);
        this.initView();
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fillData();
    }

    private void initView() {
        title = (TextView) this.findViewById(R.id.title);
        content = (TextView) this.findViewById(R.id.content);
        lay_close = (LinearLayout) this.findViewById(R.id.lay_close);
        btn_close = (Button) this.findViewById(R.id.btn_close);
        lay_upgrade = (LinearLayout) this.findViewById(R.id.lay_upgrade);
        btn_upgrade = (Button) this.findViewById(R.id.btn_upgrade);
    }

    private void fillData() {
        if (null != upgrade) {
            title.setText(upgrade.getTitle());
            content.setText(upgrade.getContent());
            List<Cancel> cancels = upgrade.getCancel();
            if (null != cancels && !cancels.isEmpty()) {
                btn_close.setTextColor(Color.parseColor("#"
                        + cancels.get(0).getColor()));
                btn_close.setText(cancels.get(0).getText());
                lay_close.setVisibility(View.VISIBLE);
                setColoseListener();
            } else {
                lay_close.setVisibility(View.GONE);
            }
            List<Confirm> confirms = upgrade.getConfirm();
            if (null != confirms && !confirms.isEmpty()) {
                btn_upgrade.setTextColor(Color.parseColor("#"
                        + confirms.get(0).getColor()));
                btn_upgrade.setText(confirms.get(0).getText());
                lay_upgrade.setVisibility(View.VISIBLE);
                this.setUpgradeListener();
            } else {
                lay_upgrade.setVisibility(View.GONE);
            }
        }
    }

    private void setColoseListener() {
        btn_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setUpgradeListener() {
        btn_upgrade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    AndroidUtil.showToast("无法下载安装文件，请检查SD卡是否挂载");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
//						File downFile = downLoadFile(upgrade.getConfirm().get(0).getUrl());
//						openFile(downFile);
                        downloadApk(upgrade.getConfirm().get(0).getUrl());
                    }
                }.start();

                dismiss();
            }
        });
    }

    @SuppressLint("NewApi")
    private void downloadApk(String downUrl) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);

        String apkUrl = downUrl;
        String dir = isFolderExist("meishaiUpgrde");
//        System.out.println("Lein ===> "+dir);  
        completeReceiver.save_path = dir + "/meishai.apk";

        File f = new File(dir + "/meishai.apk");
        if (f.exists()) f.delete();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir("meishaiUpgrde", "meishai.apk");
        request.allowScanningByMediaScanner();//表示允许MediaScanner扫描到这个文件，默认不允许。  
        request.setTitle("程序更新");//设置下载中通知栏提示的标题  
        request.setDescription("程序更新正在下载中...");//设置下载中通知栏提示的介绍  
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        @SuppressWarnings("unused")
        long downloadId = downloadManager.enqueue(request);

    }

    private String isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        boolean rs = (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
        return folder.getAbsolutePath();
    }

    protected File downLoadFile(String httpUrl) {
        final String fileName = "updata.apk";
        File tmpFile = new File(SDCardUtils.getSDCardPath() + "mupdate");
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(SDCardUtils.getSDCardPath() + "mupdate/"
                + fileName);
        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                double count = 0;
                if (conn.getResponseCode() >= 400) {
                    DebugLog.d("链接超时");
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }

                        } else {
                            break;
                        }
                    }
                }
                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}
