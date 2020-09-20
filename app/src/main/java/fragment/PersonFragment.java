package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.MainActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentPersonBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import view_model.HostViewModel;


public class PersonFragment extends Fragment implements View.OnClickListener {

    private FragmentPersonBinding mBinding;
    public HostViewModel mViewModel;

    public PersonFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding=FragmentPersonBinding.inflate(inflater,container,false);
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(HostViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((HostActivity) getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserModel user=new Dao(getActivity()).getLocalUser();
        UpdateView();
        if(user!=null){
          App.localUser.setValue(user);
        }
    }

    private void UpdateView(){
        App.localUser.observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel user) {
                if(user!=null) {
                    if (user.getUserName() == null || user.getUserName().isEmpty()) {
                        user.setUserName("默认用户");
                    }
                    mViewModel.userName.setValue(user.getUserName());
                    mViewModel.verify.setValue((App.getStateType(user.getUserState())));
                    mViewModel.joinNum.setValue(user.getJoinNum());
                    mViewModel.createNum.setValue(user.getCreateNum());
                }else{
                    mViewModel.userName.setValue("登录/注册");
                    mViewModel.verify.setValue(null);
                    mViewModel.joinNum.setValue(0);
                    mViewModel.createNum.setValue(0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_out:LogOut(); break;
            case R.id.image_head:
            case R.id.user_name: if(App.localUser.getValue()==null) LogIn();else ChangeName(); break;
        }
    }

    private void LogOut(){
        AlertDialog.Builder dialog;
        if(App.localUser.getValue()!=null) {
            dialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialogBackground)
                    .setTitle("提示")
                    .setCancelable(false)
                    .setMessage("确定要退出登录吗?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Dao(getActivity()).DeleteUser(Objects.requireNonNull(App.localUser.getValue()));
                            App.localUser.setValue(null);
                            UpdateView();
                            Toast.makeText(getActivity(),"已退出登录",Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }else{
            dialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialogBackground).setTitle("提示").setMessage("您还未登录");
        }
        dialog.show();
    }

    private void LogIn(){
        Intent intent=new Intent();
        intent.setClass(Objects.requireNonNull(getActivity()), MainActivity.class);
        startActivity(intent);
    }

    private void ChangeName(){
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        final View view=inflater.inflate(R.layout.edit_layout,null);
        final EditText edit=view.findViewById(R.id.edit_name);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialogBackground)
                .setTitle("更改名称")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final UserModel user=App.localUser.getValue();
                        user.setUserName(edit.getText().toString());
                        App.localUser.setValue(user);
                        new Dao(getActivity()).UpdateUser(user);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>() {
                                    {
                                        put("operate", "update");
                                        put("table", "user");
                                        put("user", new Gson().toJson(user));
                                    }
                                };
                                Connection.getJson(App.post, App.netUrl, params);
                            }
                        }).start();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                   {public void onClick(DialogInterface dialog, int which) {}});
        dialog.show();
    }
}
