package view_model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.HashMap;

import util.App;
import util.Connection;

public class CertificationViewModel extends ViewModel {
    public MutableLiveData<String> stuNumber;
    public MutableLiveData<String> stuPwd;
    public MutableLiveData<String> statue;

    public CertificationViewModel() {
        stuNumber = new MutableLiveData<>("");
        stuPwd = new MutableLiveData<>("");
        statue = new MutableLiveData<>("未登录");
        if (App.localUser.getValue() != null)
            statue.setValue(App.getStateType(App.localUser.getValue().getUserState()));
    }
}
