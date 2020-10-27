package view_model;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nepu.playtogether.HostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fragment.ExtensionFragment;
import fragment.PublicFragment;
import model.ExtensionModel;
import model.MessageModel;
import util.App;
import util.Connection;
import util.HandlerMsg;
import util.TcpClient;

public class HostViewModel extends ViewModel implements TcpClient.MessageReceiveListener {
    public MutableLiveData<String> userName,verify;
    public MutableLiveData<Integer> joinNum,createNum,underNum;
    public MutableLiveData<List<ExtensionModel>> extensions;
    public MutableLiveData<List<MessageModel>> messages;
    public int openChatUID;
    public int searchID;
    public String type;

    public HostViewModel(){
        userName=new MutableLiveData<>();
        userName.setValue("登录/注册");
        verify=new MutableLiveData<>();
        joinNum=new MutableLiveData<>(App.joinExtensions.size());
        createNum=new MutableLiveData<>(App.createdExtensions.size());
        underNum=new MutableLiveData<>(App.ongoingExtensions.size());
        extensions= new MutableLiveData<List<ExtensionModel>>(new ArrayList<ExtensionModel>());
        extensions.getValue().add(null);
        messages= new MutableLiveData<List<MessageModel>>(new ArrayList<MessageModel>());
        TcpClient.getInstance().setOnMessageReceiveListener(this);
    }

    
    public  void addMessage(MessageModel msg) {
        List<MessageModel> messageModels = messages.getValue();
        if (messageModels == null) return;
        for (int i = 0; i < messageModels.size(); i++) {
            MessageModel message = messageModels.get(i);
            //判断改消息的发送者之前是否存在
            if (msg.getSenderId()==message.getSenderId()) {
                int unReadNum = msg.getSenderId() == openChatUID ? 0 : message.getUnReadNum() + 1;
                msg.setUnReadNum(unReadNum);
                messageModels.remove(i);
                messageModels.add(i, msg);
                return;
            }
        }
        messageModels.add(0, msg);
    }

    @Override
    public void onMessageReceive(MessageModel msg) {
        if(msg.getSendType()!=MessageModel.EXTENSION){
            addMessage(msg);
            App.messages.add(msg);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        TcpClient.getInstance().removeListener(this);
    }

    public Runnable getAllExtension = new Runnable() {
        @Override
        public void run() {
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, new HashMap<String, String>(),"/extension/getAll");
            try {
                Gson gson = new Gson();
                if(resultJson==null) throw new JSONException("获取活动为空");
                if(resultJson.get("data").toString().isEmpty()) throw new JSONException("获取活动为空");
                ArrayList<ExtensionModel> extensionModels = new ArrayList<>();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    ExtensionModel extension = gson.fromJson(jsonObject.toString(), ExtensionModel.class);
                    extensionModels.add(extension);
                }
                //设置数据源
                extensions.setValue(extensionModels);
                //通知UI更新
                ExtensionFragment.handler.sendEmptyMessage(ExtensionFragment.extensionDataChange);
            } catch (JSONException e) {
                e.printStackTrace();
                Bundle bundle=new Bundle();
                bundle.putString("error","获取活动失败");
                Message msg = HandlerMsg.getMsg(ExtensionFragment.notifyError, bundle);
                ExtensionFragment.handler.sendMessage(msg);
            }
        }
    };

    public Runnable getExtensionByID=new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> params = new HashMap<>();
            params.put("eid", String.valueOf(searchID));
            try {
                JSONObject json = Connection.getJson(App.get, App.netUrl, params, "/extension/getByEid/" + searchID);
                if (json == null) throw new JSONException("获取活动为空");
                if (json.get("data").toString().isEmpty()) throw new JSONException("获取活动为空");
                ExtensionModel extension = new Gson().fromJson(json.get("data").toString(), ExtensionModel.class);
                ArrayList<ExtensionModel> extensionModels = new ArrayList<>();
                extensionModels.add(extension);
                extensions.setValue(extensionModels);
                ExtensionFragment.handler.sendEmptyMessage(ExtensionFragment.extensionDataChange);
            } catch (Exception ex) {
                ex.printStackTrace();
                Bundle bundle = new Bundle();
                bundle.putString("error", "获取失败");
                Message msg = HandlerMsg.getMsg(ExtensionFragment.notifyError, bundle);
                ExtensionFragment.handler.sendMessage(msg);
            }
        }
    };

    public Runnable getExtensionsByType=new Runnable() {
        @Override
        public void run() {
            HashMap<String,String> params=new HashMap<>();
            params.put("tag",type);
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, params,"/extension/getByTag/"+type);
            try {
                if(resultJson==null) throw new JSONException("获取活动为空");
                Gson gson = new Gson();
                if(resultJson.get("data").toString().isEmpty()) throw new JSONException("获取活动为空");
                ArrayList<ExtensionModel> extensionModels = new ArrayList<>();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    ExtensionModel extension = gson.fromJson(jsonObject.toString(), ExtensionModel.class);
                    extensionModels.add(extension);
                }
                //设置数据源
                extensions.setValue(extensionModels);
                //通知UI更新
                ExtensionFragment.handler.sendEmptyMessage(ExtensionFragment.extensionDataChange);
            } catch (JSONException e) {
                e.printStackTrace();
                Bundle bundle=new Bundle();
                bundle.putString("error","获取活动失败");
                Message msg = HandlerMsg.getMsg(ExtensionFragment.notifyError, bundle);
                ExtensionFragment.handler.sendMessage(msg);
            }
        }
    };
}
