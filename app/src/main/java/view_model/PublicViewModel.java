package view_model;

import android.app.Application;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import fragment.PublicFragment;
import model.ExtensionModel;
import util.App;
import util.Connection;
import util.HandlerMsg;

public class PublicViewModel extends AndroidViewModel {
    public MutableLiveData<String> extensionName;
    public String extensionDate;
    public MutableLiveData<String> year, month, date, time;
    public MutableLiveData<String> extensionPlace;
    public MutableLiveData<Integer> typeSelectIndex;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PublicViewModel(@NonNull Application application) {
        super(application);
        final Calendar mCalendar = Calendar.getInstance();
        year = new MutableLiveData<>(String.valueOf(mCalendar.get(Calendar.YEAR)));
        month = new MutableLiveData<>(String.valueOf(mCalendar.get(Calendar.MONTH) + 1));
        date = new MutableLiveData<>(String.valueOf(mCalendar.get(Calendar.DATE)));
        extensionName = new MutableLiveData<>("");
        extensionPlace = new MutableLiveData<>("");
        typeSelectIndex = new MutableLiveData<>(0);
        time = new MutableLiveData<>("");
    }

    /**
     * 增加活动的线程
     */
    public Runnable addExtension = new Runnable() {
        @Override
        public void run() {
            final ExtensionModel extension = new ExtensionModel(
                    extensionName.getValue(),
                    Objects.requireNonNull(App.localUser.getValue()).getUID(),
                    Objects.requireNonNull(typeSelectIndex.getValue()),
                    Objects.requireNonNull(App.localUser.getValue()).getUserName(),
                    extensionDate,
                    extensionPlace.getValue());
            HashMap<String, String> params = new HashMap<String, String>() {
                {
                    put("extension", new Gson().toJson(extension));
                }
            };
            try {
                JSONObject json = Connection.getJson(App.post, App.netUrl, params, "/extension/save");
                if (json == null) throw new Exception("json为空");
                int eid = (int) json.get("data");
                extension.setID(eid);
                Message msg = HandlerMsg.getMsg(PublicFragment.MyHandler.addExtension, extension);
                PublicFragment.handler.sendMessage(msg);
            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", "创建失败");
                Message msg = HandlerMsg.getMsg(PublicFragment.MyHandler.notifyError, bundle);
                PublicFragment.handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    };
}
