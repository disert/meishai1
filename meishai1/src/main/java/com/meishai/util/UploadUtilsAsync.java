package com.meishai.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.net.RespData;

/**
 * 发布帖子
 *
 * @author sh
 */
public class UploadUtilsAsync extends AsyncTask<String, Integer, String> {
    /**
     * 服务器路径
     **/
    private String url = "http://www.meishai.com/app.php";
    private CustomProgress mProgress;
    /**
     * 上传的参数
     **/
    private Map<String, String> paramMap;
    /**
     * 要上传的文件 的路径
     **/
    private List<String> paths;
    private Context context;
    // 图片上传成功之后的回调
    private OnSuccessListener listener;
    private OnUpdateProgress updateListener;

    public void setUpdateListener(OnUpdateProgress updateListener) {
        this.updateListener = updateListener;
    }


    public UploadUtilsAsync(Context context, Map<String, String> paramMap,
                            List<String> paths) {
        this.context = context;
        this.paramMap = paramMap;
        this.paths = paths;
    }

    public void setListener(OnSuccessListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {// 执行前的初始化
//		if (null == mProgress) {
//			mProgress = CustomProgress.show(context,
//					context.getString(R.string.network_wait), true, null);
//		} else {
//			mProgress.show();
//		}
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return uploadImage();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (null != mProgress) {
            mProgress.hide();
        }
        if (updateListener != null) {
            updateListener.onUpdate(values[0], values[1], values[2], values[3]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if (null != mProgress) {
            mProgress.hide();
        }
        // 执行结果
        RespData respData = GsonHelper.parseObject(result, RespData.class);
        // if (null != respData) {
        // AndroidUtil.showToast(respData.getTips());
        // }
        if (listener != null) {
            listener.onSuccess(respData);
        }
        super.onPostExecute(result);
    }

    public String uploadImage() {
        String res = "";
        HttpURLConnection conn = null;
        try {
            String BOUNDARY = "---------------------------"
                    + System.currentTimeMillis(); // boundary就是request头和上传文件内容的分隔符
            String requestUrl = url + "?op=" + paramMap.get("op"); // 图片上传地址
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            // 允许Input、Output，不使用Cache
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // 设置以POST方式进行传送
            conn.setRequestMethod("POST");
            // 设置RequestProperty
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + BOUNDARY);
            // 构造DataOutputStream流
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            // if (paramMap != null) {
            // StringBuffer strBuf = new StringBuffer();
            // for (String inputName : paramMap.keySet()) {
            // String inputValue = (String) paramMap.get(inputName);
            // if (inputValue == null) {
            // continue;
            // }
            // strBuf.append("\r\n").append("--").append(BOUNDARY)
            // .append("\r\n");
            // strBuf.append("Content-Disposition: form-data; name=\""
            // + inputName + "\"\r\n\r\n");
            // strBuf.append(inputValue);
            // }
            // out.write(strBuf.toString().getBytes("utf-8"));
            // }
            // file
            if (paths != null && !paths.isEmpty()) {

                for (int i = 0; i < paths.size(); i++) {
                    String path = paths.get(i);
                    DebugLog.w("上传图片路径:" + path);
                    File file = new File(path);
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + "pics[]" + "\"; filename=\"" + file.getName()
                            + "\"\r\n");
                    // strBuf.append("Content-Type:image/jpeg \r\n\r\n");
                    strBuf.append("Content-Type: application/octet-stream \r\n\r\n");
                    out.write(strBuf.toString().getBytes("utf-8"));
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    int temp = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                        temp += bytes;
                        publishProgress(paths.size(), i, (int) file.getTotalSpace(), temp);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            DebugLog.d("发布或修改返回数据:" + res);
            reader.close();
            reader = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    /**
     * 发布时所用到的监听接口,主要是用于显示进度条
     *
     * @author Administrator
     */
    public interface OnUpdateProgress {
        /**
         * @param count       有几张照片
         * @param position    当前是第几张
         * @param total       当前图片的大小
         * @param currentsize 读取了当前图片的大小
         */
        void onUpdate(int count, int position, int total, int currentsize);
    }

    /**
     * 图片上传成功之后的回调
     *
     * @author sh
     */
    public interface OnSuccessListener {
        public void onSuccess(RespData respData);
    }
}
