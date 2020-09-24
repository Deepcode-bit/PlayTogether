package fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.nepu.playtogether.PwdActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentLoginBinding;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import util.HandlerMsg;
import view_model.LoginViewModel;


public class LoginFragment extends Fragment {

    public LoginViewModel mViewModel;
    private mHandler handler;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLoginBinding mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((MainActivity) getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler=new mHandler(this);
    }


    public void Login(View v){
        if(mViewModel.email.getValue().isEmpty()|| mViewModel.pwd.getValue().isEmpty()) {
            Toast.makeText(getActivity(),"邮箱和密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        final HashMap params=new HashMap<String,String>(){
            {
                put("email",mViewModel.email.getValue());
                put("pwd",mViewModel.pwd.getValue());
            }
        };

        //进行异步登录操作
        App.mThreadPool.execute(new Runnable(){
            @Override
            public void run() {
                try {
                    JSONObject json = Connection.getJson(App.post, App.netUrl, params,"/admin/login");
                    if(json==null)
                        throw new Exception("服务器连接超时");
                    final String msg=json.get("msg").toString();
                    if(msg.equals("success")) {
                        final UserModel user = new Gson().fromJson(json.get("user").toString(), UserModel.class);
                        handler.sendMessage(HandlerMsg.getMsg(0x002,user));
                        Dao dao=new Dao(getActivity());
                        if(dao.getLocalUser()!=null){
                            dao.UpdateUser(user);
                        }else {
                            dao.InsertUser(user);
                        }
                        Intent intent=new Intent();
                        intent.setClass(requireActivity(), HostActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Bundle bundle=new Bundle();
                        bundle.putString("alert","登录失败:");
                        bundle.putString("msg",msg);
                        handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                    }
                } catch (final Exception ex) {
                    Bundle bundle=new Bundle();
                    bundle.putString("alert","登录失败:");
                    bundle.putString("msg",ex.getMessage());
                    handler.sendMessage(HandlerMsg.getMsg(0x001,bundle));
                }
            }
        });
    }

    public void OnBackPwdClick(View view){
        Intent intent=new Intent();
        intent.setClass(requireActivity(), PwdActivity.class);
        startActivity(intent);
    }

    private static class mHandler extends Handler{
        WeakReference<LoginFragment> loginFragment;

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x001) {
                LoginFragment fragment=loginFragment.get();
                AlertDialog.Builder dialog = new AlertDialog.Builder(fragment.getActivity(), R.style.AlertDialogBackground)
                        .setTitle("提示").setMessage(msg.getData().getString("alert") + msg.getData().getString("msg"));
                dialog.show();
            }else if(msg.what==0x002){
                App.localUser.setValue((UserModel) msg.obj);
            }
        }

        mHandler(LoginFragment fragment){
            this.loginFragment=new WeakReference<>(fragment);
        }
    }
}
