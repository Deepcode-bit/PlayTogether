package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        }
        App.messages=new ArrayList<>();
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
}
