package fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.MainActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentRegistBinding;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import util.HandlerMsg;
import view_model.LoginViewModel;


public class RegisterFragment extends Fragment {

    public LoginViewModel mViewModel;
    private MyHandler handler;
    private Timer codeTimer;
    private TimerTask task;
    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.nepu.playtogether.databinding.FragmentRegistBinding mBinding = FragmentRegistBinding.inflate(inflater, container, false);
        mViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((MainActivity) getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler= new MyHandler(this);
    }

    public void OnVerifyCodeClick(final View view) {
        if (mViewModel.REmail.getValue() == null || mViewModel.REmail.getValue().isEmpty()) {
            Toast.makeText(getActivity(), "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String,String>()
                {{
                    put("type", "register");
                    put("email", mViewModel.REmail.getValue());
                }};
                final JSONObject json = Connection.getJson(App.post, App.netUrl, params,"/admin/getCaptcha");
                try {
                    if (json != null) {
                        final String msg;
                        msg = json.get("msg").toString();
                        Bundle bundle=new Bundle();
                        bundle.putString("alert","注册失败");
                        bundle.putString("msg",msg);
                        handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                    }
                } catch (final Exception e) {
                    Bundle bundle=new Bundle();
                    bundle.putString("alert","注册失败");
                    bundle.putString("msg",e.getMessage());
                    handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                }
            }
        });
        codeTimer = new Timer();
        mViewModel.countTime = 30;
        view.setEnabled(false);
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.SetCodeCount();
                        if (mViewModel.codeButText.getValue().equals("获取验证码")) {
                            view.setEnabled(true);
                            codeTimer.cancel();
                            task.cancel();
                        }
                    }
                });
            }
        };
        codeTimer.schedule(task, 0, 1000);
    }

    public void OnRegisterButClick(View view){
        if(mViewModel.REmail.getValue()==null || mViewModel.REmail.getValue().isEmpty() ||
        mViewModel.RPwd.getValue()==null || mViewModel.RPwd.getValue().isEmpty()) {
            Toast.makeText(getActivity(), "邮箱和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(mViewModel.verifyCode.getValue()==null || mViewModel.verifyCode.getValue().isEmpty()){
            Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final UserModel user=new UserModel(0,mViewModel.REmail.getValue(),mViewModel.RPwd.getValue(),
                0,null,0,0,null);
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Map<String,String> params=new HashMap<String, String>()
                {{
                    put("captcha",mViewModel.verifyCode.getValue());
                    put("user",new Gson().toJson(user));
                }};
                try {
                    JSONObject json = Connection.getJson(App.post, App.netUrl, params,"/admin/registry");
                    if (json == null)
                        throw new Exception("服务器连接超时");
                    final String msg = json.get("msg").toString();
                    if (msg.equals("success")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                App.localUser.setValue(user);
                            }
                        });
                        Dao dao=new Dao(getActivity());
                        if(dao.getLocalUser()!=null){
                            dao.UpdateUser(user);
                        }else {
                            dao.InsertUser(user);
                        }
                        task.cancel();
                        codeTimer.cancel();
                        Intent intent=new Intent();
                        intent.setClass(requireActivity(), HostActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Bundle bundle=new Bundle();
                        bundle.putString("alert","注册失败");
                        bundle.putString("msg",msg);
                        handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                    }
                }catch (final Exception ex){
                    Bundle bundle=new Bundle();
                    bundle.putString("alert","注册失败");
                    bundle.putString("msg",ex.getMessage());
                    handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                }
            }
        });
    }

    private static class MyHandler extends Handler{
        private WeakReference<RegisterFragment> registerFragment;

        @Override
        public void handleMessage(@NonNull Message msg) {
            RegisterFragment fragment= registerFragment.get();
            if (msg.what == 0x001) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(fragment.getActivity(), R.style.AlertDialogBackground)
                        .setTitle("提示").setMessage(msg.getData().getString("alert") + msg.getData().getString("msg"));
                dialog.show();
            }
        }

        MyHandler(RegisterFragment fragment){
            registerFragment =new WeakReference<>(fragment);
        }
    }
}
