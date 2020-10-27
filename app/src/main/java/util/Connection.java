package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Connection {

    private static InputStream DoGet(String path, Map<String,String> params){
        StringBuilder sb=new StringBuilder(path);
        try{
            //拼接url参数
            if(!params.isEmpty()){
                sb.append("?");
                for(Map.Entry<String,String> entry:params.entrySet()){
                    sb.append(entry.getKey()).append("=");
                    sb.append(entry.getValue()).append("&");
                }
                sb.delete(sb.length()-1,sb.length());
            }
            URL url=new URL(sb.toString());
            Log.i("Test",url.toString());
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            //设置超时时间
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            //请求成功
            if(conn.getResponseCode()==200){
                InputStream is=conn.getInputStream();
                return is;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream DoPost(String path,Map<String,String> params){
        StringBuilder sb=new StringBuilder();
        if(!params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        try{
            URL url=new URL(path);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
            dos.write(sb.toString().getBytes());
            dos.flush();
            dos.close();
            if(conn.getResponseCode()==200){
                InputStream is=conn.getInputStream();
                return is;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream OKHttpDoPost(String path, FormBody.Builder params) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        try {
            Request request = new Request.Builder()//创建Request 对象。
                    .url(path)
                    .post(params.build())//传递请求体
                    .build();
            Response response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                assert response.body() != null;
                return response.body().byteStream();
            }
        }catch (Exception ex){
            Log.i("Exception",ex.toString());
        }
        return null;
    }

    public static JSONObject getJson(int type,String path,Map<String,String> params,String route) {
        InputStream is = null;
        if (route != null)
            path += route;
        if(type==1)
            is=DoPost(path,params);
        else if(type==2)
            is=DoGet(path,params);
        if(is!=null) {
            try {
                byte[] buffer = new byte[1024 * 10];
                int len = is.read(buffer, 0, 10240);
                if (len != -1) {
                    String result = new String(buffer, 0, len, StandardCharsets.UTF_8);
                    Log.i("Test", result);
                    return new JSONObject(result);
                }
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                Log.i("Exception",ex.toString());
            }
        }
        return null;
    }

    public static Bitmap getBitmap(String path) throws IOException {
        InputStream is=DoGet(path,new HashMap<String, String>());
        if(is!=null) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        }
        return null;
    }
}
