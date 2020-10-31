package com.nepu.playtogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.nepu.playtogether.databinding.ActivityCertificationBinding;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import util.HandlerMsg;
import view_model.CertificationViewModel;

public class CertificationActivity extends AppCompatActivity {

    ActivityCertificationBinding mBinding;
    public CertificationViewModel mViewModel;
    public static mHandler handler;
    private ImageView verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_certification);
        mBinding.setData(this);
        mBinding.setLifecycleOwner(this);
        mViewModel= ViewModelProviders.of(this).get(CertificationViewModel.class);
        handler=new mHandler(this);
        verifyCode=findViewById(R.id.verify_code);
        App.mThreadPool.execute(mViewModel.getVerifyCode);
    }

    public void OnBackButClick(View v){
        onBackPressed();
    }

    public void onVerifyCodeClick(View v){
        App.mThreadPool.execute(mViewModel.getVerifyCode);
    }

    public void onCertificationClick(View v){
        if(App.localUser.getValue()==null){
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mViewModel.stuNumber.getValue().isEmpty() || mViewModel.stuPwd.getValue().isEmpty()){
            Toast.makeText(this,"学号和密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        final HashMap<String,String> params=new HashMap<String,String>(){
            {
                put("account", mViewModel.stuNumber.getValue());
                put("pwd", mViewModel.stuPwd.getValue());
                put("verifycode", mViewModel.verifyCode.getValue());
            }
        };
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    //教务系统验证
                    JSONObject json= Connection.getJson(App.post,App.EASUrl,params,null);
                    String msg=json.getString("message");
                    if(msg.equals("登录成功")){
                        String SID=mViewModel.stuNumber.getValue();
                        App.localUser.getValue().setSID(SID);
                        App.localUser.getValue().setUserState(1);
                        final Map<String,String> params=new HashMap<String,String>() {
                            {
                                put("user",new Gson().toJson(App.localUser.getValue()));
                            }
                        };
                        //服务端验证
                        JSONObject json2=Connection.getJson(App.post,App.netUrl,params,"/member/authentication");
                        String msg2=json2.getString("msg");
                        //验证成功
                        if(msg2.equals("success")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("statue", "认证成功");
                            handler.sendMessage(HandlerMsg.getMsg(0x003, bundle));
                        }else{
                            Bundle bundle=new Bundle();
                            bundle.putString("alert","认证失败");
                            bundle.putString("msg",msg2);
                            handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                            App.mThreadPool.execute(mViewModel.getVerifyCode);
                        }
                    }
                }catch (Exception ex){
                    Bundle bundle=new Bundle();
                    bundle.putString("alert","认证失败");
                    bundle.putString("msg","学号不存在或密码错误");
                    handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                }
            }
        });
    }

    public static class mHandler extends Handler {
        WeakReference<CertificationActivity> certificationActivity;

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x001) {
                CertificationActivity activity = certificationActivity.get();
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.AlertDialogBackground)
                        .setTitle("提示").setMessage(msg.getData().getString("alert") + msg.getData().getString("msg"));
                dialog.show();
            } else if (msg.what == 0x003) {
                Toast.makeText(certificationActivity.get(), "认证成功", Toast.LENGTH_SHORT).show();
                Dao dao = new Dao(certificationActivity.get());
                certificationActivity.get().mViewModel.statue.setValue(msg.getData().getString("statue"));
                if (App.localUser.getValue() == null) return;
                if (dao.getLocalUser() != null) {
                    dao.UpdateUser(App.localUser.getValue());
                } else {
                    dao.InsertUser(App.localUser.getValue());
                }
            } else if (msg.what == 0x004) {
                certificationActivity.get().verifyCode.setImageBitmap(certificationActivity.get().mViewModel.codeBitmap);
            }
        }

        mHandler(CertificationActivity activity){
            this.certificationActivity=new WeakReference<>(activity);
        }
    }
}
