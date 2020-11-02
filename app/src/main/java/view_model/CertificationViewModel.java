package view_model;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nepu.playtogether.CertificationActivity;

import util.App;
import util.Connection;

public class CertificationViewModel extends ViewModel {
    public MutableLiveData<String> stuNumber;
    public MutableLiveData<String> stuPwd;
    public MutableLiveData<String> statue;
    public MutableLiveData<String> verifyCode;
    public MutableLiveData<String> verifyText;
    public Bitmap codeBitmap;

    public CertificationViewModel() {
        stuNumber = new MutableLiveData<>("");
        stuPwd = new MutableLiveData<>("");
        statue = new MutableLiveData<>("未登录");
        verifyCode=new MutableLiveData<>("");
        verifyText=new MutableLiveData<>("您未进行学生认证");
    }

    public Runnable getVerifyCode=new Runnable() {
        @Override
        public void run() {
            try {
                codeBitmap = Connection.getBitmap("http://jwgl.nepu.edu.cn/yzm");
                CertificationActivity.handler.sendEmptyMessage(0x004);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
