package view_model;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends AndroidViewModel {
    public MutableLiveData<String>  email,pwd,REmail,RPwd,verifyCode;
    public MutableLiveData<String> codeButText;
    public int countTime;

    public LoginViewModel(Application application){
        super(application);
        email=new MutableLiveData<>("");
        pwd=new MutableLiveData<>("");
        REmail=new MutableLiveData<>("");
        RPwd=new MutableLiveData<>("");
        verifyCode=new MutableLiveData<>("");
        codeButText=new MutableLiveData<>("");
        codeButText.setValue("获取验证码");
    }

    public void SetCodeCount(){
        if(countTime==0){
            codeButText.setValue("获取验证码");
        }else {
            codeButText.setValue("重新获取:" + countTime);
            countTime--;
        }
    }
}
