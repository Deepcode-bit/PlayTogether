package view_model;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HostViewModel extends ViewModel {
    public MutableLiveData<String> userName,verify;
    public MutableLiveData<Integer> joinNum,createNum,underNum;

    public HostViewModel(){
        userName=new MutableLiveData<>();
        userName.setValue("登录/注册");
        verify=new MutableLiveData<>();
        joinNum=new MutableLiveData<>();
        createNum=new MutableLiveData<>();
        underNum=new MutableLiveData<>();
    }
}
