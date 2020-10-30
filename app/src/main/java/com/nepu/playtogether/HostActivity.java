package com.nepu.playtogether;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import fragment.ExtensionFragment;
import fragment.ForumFragment;
import fragment.PersonFragment;
import model.ExtensionModel;
import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import util.HandlerMsg;
import util.TcpClient;

public class HostActivity extends AppCompatActivity {

    RadioGroup bottomNav;
    private SparseArray<Fragment> fragments;
    public ExtensionFragment extensionFragment;
    public ForumFragment forumFragment;
    public PersonFragment personFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        InitView();
        InitData();
    }

    private void InitView(){
        bottomNav=findViewById(R.id.radiogroup);
        extensionFragment=ExtensionFragment.newInstance();
        forumFragment=ForumFragment.newInstance();
        personFragment=PersonFragment.newInstance();
        fragments = new SparseArray<Fragment>() {};
        fragments.append(R.id.radio1, extensionFragment);
        fragments.append(R.id.radio2, forumFragment);
        fragments.append(R.id.radio3, personFragment);
        bottomNav.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId>0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                            fragments.get(checkedId)).commit();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                extensionFragment).commit();
    }

    private void InitData(){
        UserModel user=new Dao(this).getLocalUser();
        App.ongoingExtensions=new ArrayList<>();
        App.createdExtensions=new ArrayList<>();
        App.joinExtensions=new ArrayList<>();
        if(user!=null){
            App.localUser.setValue(user);
            TcpClient.getInstance().startClient(App.IPAddress,App.port);
            App.mThreadPool.execute(getOngoingExtensions);
            App.mThreadPool.execute(getCreatedExtensions);
            App.mThreadPool.execute(getJoinExtensions);
            App.mThreadPool.execute(getHeadImage);
        }
        App.messages=new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && null != data) {
            try {
                ImageView imageView = personFragment.getView().findViewById(R.id.image_head);
                Uri selectedImage = data.getData();
                App.mThreadPool.execute(new LoadFileUtil(getRealPath(selectedImage)));
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                imageView.setImageBitmap(bit);
                App.headImage=bit;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this,"上传失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Runnable getOngoingExtensions=new Runnable() {
        @Override
        public void run() {
            UserModel user = App.localUser.getValue();
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, new HashMap<String, String>(), "/extension/getRunningByUid/" + user.getUID());
            if (resultJson == null) return;
            try {
                Gson gson = new Gson();
                if (resultJson.get("data").toString().isEmpty()) return;
                App.ongoingExtensions.clear();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionModel extension = gson.fromJson(jsonObject.toString(), ExtensionModel.class);
                    App.ongoingExtensions.add(extension);
                }
                if (ExtensionFragment.handler != null)
                    ExtensionFragment.handler.sendEmptyMessage(ExtensionFragment.personalDataChange);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Runnable getCreatedExtensions=new Runnable() {
        @Override
        public void run() {
            UserModel user = App.localUser.getValue();
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, new HashMap<String, String>(), "/extension/getByUid/" + user.getUID());
            if (resultJson == null) return;
            try {
                Gson gson = new Gson();
                if (resultJson.get("data").toString().isEmpty()) return;
                ArrayList<ExtensionModel> extensionModels = new ArrayList<>();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionModel extension = gson.fromJson(jsonObject.toString(), ExtensionModel.class);
                    extensionModels.add(extension);
                }
                //设置数据源
                App.createdExtensions = extensionModels;
                if (App.localUser.getValue() != null)
                    App.localUser.getValue().setCreateNum(extensionModels.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Runnable getJoinExtensions=new Runnable() {
        @Override
        public void run() {
            UserModel user = App.localUser.getValue();
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, new HashMap<String, String>(), "/extension/getJoinByUid/" + user.getUID());
            if (resultJson == null) return;
            try {
                Gson gson = new Gson();
                if (resultJson.get("data").toString().isEmpty()) return;
                ArrayList<ExtensionModel> extensionModels = new ArrayList<>();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionModel extension = gson.fromJson(jsonObject.toString(), ExtensionModel.class);
                    extensionModels.add(extension);
                }
                //设置数据源
                App.joinExtensions = extensionModels;
                if (App.localUser.getValue() != null)
                    App.localUser.getValue().setJoinNum(extensionModels.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable getHeadImage=new Runnable() {
        @Override
        public void run() {
            try {
                if (App.localUser.getValue() != null && App.localUser.getValue().getHeadImage() != null) {
                    String url = App.localUser.getValue().getHeadImage();
                    App.headImage= Connection.getBitmap(url);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };

    private static class LoadFileUtil implements Runnable{
        String imagePath="";
        LoadFileUtil(String path) {
            imagePath = path;
        }
        @Override
        public void run() {
            String message = "上传失败";
            Bundle bundle = new Bundle();
            try {
                JSONObject json = Connection.uploadImage(App.localUser.getValue().getEmail(), App.netUrl + "/minio/upload", imagePath);
                if (json == null) throw new JSONException("json为空");
                if (json.get("msg").equals("操作成功")) {
                    JSONObject data = json.getJSONObject("data");
                    String url = data.getString("url");
                    url = url.replace('\\', '/');
                    bundle.putString("url",url);
                    message = "上传成功";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                bundle.putString("msg", message);
                PersonFragment.handler.sendMessage(HandlerMsg.getMsg(PersonFragment.MyHandler.notify, bundle));
            }
        }
    }

    private String getRealPath(Uri fileUrl) {
        String fileName = null;
        if (fileUrl != null &&fileUrl.getScheme()!=null) {
            if (fileUrl.getScheme().compareTo("content") == 0) { // content://开头的uri
                Cursor cursor = getApplication().getContentResolver().query(fileUrl,
                        null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                    fileName = cursor.getString(column_index); // 取出文件路径
                    cursor.close();
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) { // file:///开头的uri
                fileName = fileUrl.toString().replace("file://", "");
            }
        }
        if (fileName != null) {
            // 避免空指针
            fileName = new String(fileName.getBytes(), StandardCharsets.UTF_8);
            System.out.println("编码了！！！！！");
            // 编码含有中文路径
            fileName = URLDecoder.decode(fileName);
        }
        return fileName;
    }

}
