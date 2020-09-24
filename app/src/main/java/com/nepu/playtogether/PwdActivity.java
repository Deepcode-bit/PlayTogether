package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nepu.playtogether.databinding.ActivityPwdBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import view_model.LoginViewModel;

import static util.App.post;

public class PwdActivity extends AppCompatActivity {

    ActivityPwdBinding mBinding;
    public LoginViewModel mViewModel;
    private Timer codeTimer;
    private TimerTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_pwd);
        mBinding.setData(this);
        mBinding.setLifecycleOwner(this);
    }


    public void OnVerifyCodeClick(final View view){
        if (mViewModel.REmail.getValue() == null || mViewModel.REmail.getValue().isEmpty()) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<String, String>(){
                    {
                        put("type", "pwdBack");
                        put("email", mViewModel.REmail.getValue());
                    }
                };
                Connection.getJson(App.post, App.netUrl, params,"/admin/getCaptcha");
            }
        });
        codeTimer=new Timer();
        mViewModel.countTime=30;
        view.setEnabled(false);
        task=new TimerTask() {
            @Override
            public void run() {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.SetCodeCount();
                        if(mViewModel.codeButText.getValue().equals("获取验证码")) {
                            view.setEnabled(true);
                            codeTimer.cancel();
                            task.cancel();
                        }
                    }
                });
            }
        };
        codeTimer.schedule(task,0,1000);
    }


    public void OnPwdBackClick(final View view){
        if(mViewModel.REmail.getValue()==null || mViewModel.REmail.getValue().isEmpty() ||
                mViewModel.RPwd.getValue()==null || mViewModel.RPwd.getValue().isEmpty()) {
            Toast.makeText(this, "邮箱和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(mViewModel.verifyCode.getValue()==null || mViewModel.verifyCode.getValue().isEmpty()){
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final PwdActivity activity=this;
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Map<String,String> params=new HashMap<String, String>(){
                    {
                        put("code2",mViewModel.verifyCode.getValue());
                        put("email",mViewModel.REmail.getValue());
                        put("pwd",mViewModel.RPwd.getValue());
                    }
                };
                try {
                    JSONObject json = Connection.getJson(App.post, App.netUrl, params,"/member/modifyPassword");
                    if (json == null)
                        throw new Exception("服务器连接超时");
                    final String msg = json.get("msg").toString();
                    if (msg.equals("success")) {
                        task.cancel();
                        codeTimer.cancel();
                        activity.finish();
                    }else{
                        Objects.requireNonNull(view.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.AlertDialogBackground)
                                        .setTitle("提示").setMessage("修改失败:" + msg);
                                dialog.show();
                            }
                        }));
                    }
                }catch (final Exception ex){
                    Objects.requireNonNull(view.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.AlertDialogBackground)
                                    .setTitle("提示").setMessage(ex.getMessage());
                            dialog.show();
                        }
                    }));
                }
            }
        });
    }
}
