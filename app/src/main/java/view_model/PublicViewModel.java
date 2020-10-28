package view_model;

import android.app.Application;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import fragment.PublicFragment;
import model.ExtensionModel;
import util.App;
import util.Connection;
import util.HandlerMsg;

public class PublicViewModel extends AndroidViewModel {
    public MutableLiveData<String> extensionName;
    public MutableLiveData<String> extensionDate;
    public MutableLiveData<String> extensionPlace;
    public MutableLiveData<Integer> typeSelectIndex;

    public PublicViewModel(@NonNull Application application) {
        super(application);
        extensionName=new MutableLiveData<>("");
        extensionDate=new MutableLiveData<>("");
        extensionPlace=new MutableLiveData<>("");
        typeSelectIndex=new MutableLiveData<>(0);
    }

    /**
     * 增加活动的线程
     */
    public Runnable addExtension=new Runnable() {
        @Override
        public void run() {
            final ExtensionModel extension=new ExtensionModel(
                    extensionName.getValue(),
                    Objects.requireNonNull(App.localUser.getValue()).getUID(),
                    Objects.requireNonNull(typeSelectIndex.getValue()),
                    Objects.requireNonNull(App.localUser.getValue()).getUserName(),
                    extensionDate.getValue(),
                    extensionPlace.getValue());
            HashMap<String,String> params=new HashMap<String, String>(){
                {
                    put("extension", new Gson().toJson(extension));
                }
            };
            Message msg = HandlerMsg.getMsg(PublicFragment.MyHandler.addExtension, extension);
            PublicFragment.handler.sendMessage(msg);
            Connection.getJson(App.post, App.netUrl, params, "/extension/save");
        }
    };
}
