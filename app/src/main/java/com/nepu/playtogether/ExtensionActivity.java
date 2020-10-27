package com.nepu.playtogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nepu.playtogether.databinding.ActivityExtensionBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import adapter.MemberAdapter;
import fragment.ExtensionFragment;
import model.ExtensionModel;
import model.Member;
import model.MessageModel;
import model.UserModel;
import util.App;
import util.Connection;
import util.HandlerMsg;
import util.TcpClient;

public class ExtensionActivity extends AppCompatActivity implements MemberAdapter.onItemClickListener {

    private RecyclerView memberRecycler;
    private MemberAdapter memberAdapter;
    private List<Member> members;
    private Button joinBut,chatBut;
    private static MyHandler handler;
    //此数据作为ViewModel
    public MutableLiveData<ExtensionModel> extension;

    ActivityExtensionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_extension);
        mBinding.setData(this);
        mBinding.setLifecycleOwner(this);
        InitInterface();
        InitData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void InitInterface(){
        //初始化头部背景
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        extension=new MutableLiveData<>();
        extension.setValue((ExtensionModel) bundle.getSerializable("extension"));
        joinBut = findViewById(R.id.join_but);
        chatBut=findViewById(R.id.chat_but);
        if(extension!=null) {
            int type = extension.getValue().getType();
            findViewById(R.id.head_layout).setBackgroundResource(App.getExtensionDrawable(type));
            if(extension.getValue().getUID()== Objects.requireNonNull(App.localUser.getValue()).getUID()){
                joinBut.setText("取消活动");
                joinBut.setBackgroundResource(R.drawable.corn_button2);
                chatBut.setEnabled(true);
            }
            for(ExtensionModel ex:App.ongoingExtensions){
                if(ex.getUID()==extension.getValue().getUID()){
                    joinBut.setBackgroundResource(R.drawable.corn_button2);
                    joinBut.setText("退出活动");
                    chatBut.setEnabled(true);
                    break;
                }
            }
        }
    }

    private void InitData(){
        handler=new MyHandler(this);
        members=new ArrayList<>();
        App.mThreadPool.execute(getMembers);
        memberAdapter=new MemberAdapter();
        memberRecycler=findViewById(R.id.member_list);
        memberRecycler.setLayoutManager(new LinearLayoutManager(this));
        memberRecycler.setAdapter(memberAdapter);
        memberAdapter.setMembers(members);
        memberAdapter.notifyDataSetChanged();
        memberAdapter.setOnItemClickListener(this);
    }


    public void onEnterChatRoomClick(View v){
        if(App.localUser.getValue()==null){
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("receiverType", MessageModel.EXTENSION);
        bundle.putInt("receiverId",extension.getValue().getID());
        bundle.putString("title",extension.getValue().getName());
        bundle.putString("senderName",extension.getValue().getName());
        intent.putExtras(bundle);
        intent.setClass(this,ChatRoomActivity.class);
        startActivity(intent);
    }

    public void onJoinExtension(View v) throws JSONException {
        final UserModel user=App.localUser.getValue();
        if(joinBut.getText().equals("取消活动")){
            App.mThreadPool.execute(cancelExtension);
            //IM服务器请求
            TcpClient.getInstance().cancelExtension(extension.getValue().getID());
            Toast.makeText(this,"已取消",Toast.LENGTH_SHORT).show();
            this.finish();
        }else if(joinBut.getText().equals("加入活动")){
            App.mThreadPool.execute(joinExtension);
        }else if(joinBut.getText().equals("退出活动")){
            App.mThreadPool.execute(exitExtension);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Member member = members.get(position);
        if(member.getUID()!=App.localUser.getValue().getUID()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("receiverType", MessageModel.PERSON);
            bundle.putInt("receiverId", member.getUID());
            bundle.putString("title", member.getUserName());
            bundle.putString("senderName",App.localUser.getValue().getUserName());
            intent.putExtras(bundle);
            intent.setClass(this, ChatRoomActivity.class);
            startActivity(intent);
        }
    }

    private Runnable joinExtension=new Runnable() {
        @Override
        public void run() {
            final UserModel user=App.localUser.getValue();
            final String eid=String.valueOf(extension.getValue().getID());
            final String uid=String.valueOf(user.getUID());
            HashMap<String,String> params=new HashMap<String, String>(){
                {
                    put("eid",eid);
                    put("uid",uid);
                }};
            Connection.getJson(App.post,App.netUrl,params,"/ue/add/"+uid+"/"+eid);
        }
    };

    private Runnable cancelExtension=new Runnable() {
        @Override
        public void run() {
            final String eid = String.valueOf(extension.getValue().getID());
            HashMap<String, String> params = new HashMap<>();
            params.put("eid", eid);
            Connection.getJson(App.post, App.netUrl, params, "/ue/deleteByEid/" + eid);
        }
    };

    private Runnable exitExtension=new Runnable() {
        @Override
        public void run() {
            final UserModel user = App.localUser.getValue();
            final String eid = String.valueOf(extension.getValue().getID());
            final String uid = String.valueOf(user.getUID());
            HashMap<String, String> params = new HashMap<String, String>() {
                {
                    put("eid", eid);
                    put("uid", uid);
                }
            };
            Connection.getJson(App.post, App.netUrl, params, "/extension/exit/" + uid + "/" + eid);
        }
    };

    private Runnable getMembers=new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> params = new HashMap<>();
            params.put("eid", String.valueOf(extension.getValue().getID()));
            JSONObject resultJson = Connection.getJson(App.get, App.netUrl, params, "/ue/getAttendUser/" + extension.getValue().getID());
            try {
                Gson gson = new Gson();
                if (resultJson.get("data").toString().isEmpty()) return;
                ArrayList<UserModel> userModels = new ArrayList<>();
                JSONArray jsonArray = resultJson.getJSONArray("data");
                //循环遍历
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    UserModel userModel = gson.fromJson(jsonObject.toString(), UserModel.class);
                    userModels.add(userModel);
                }
                members.clear();
                for(UserModel user: userModels){
                    members.add(new Member(user.getUID(),user.getEmail(),user.getUserName(),user.getHeadImage()));
                }
                //通知UI更新
                ExtensionActivity.handler.sendEmptyMessage(MyHandler.changeData);
            } catch (Exception ex) {
                ex.printStackTrace();
                Bundle bundle = new Bundle();
                bundle.putString("error", "加载成员失败");
                Message msg = HandlerMsg.getMsg(MyHandler.notifyError, bundle);
                ExtensionActivity.handler.sendMessage(msg);
            }
        }
    };

    static class MyHandler extends Handler {
        WeakReference<ExtensionActivity> extensionActivity;
        static final int joinExtension = 0x001;
        static final int notifyError = 0x002;
        static final int exitExtension = 0x003;
        static final int changeData=0x004;

        MyHandler(ExtensionActivity activity) {
            extensionActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case joinExtension:
                    try {
                        UserModel user = App.localUser.getValue();
                        //IM服务器请求
                        TcpClient.getInstance().sendJoinExtension(user.getUID(), extensionActivity.get().extension.getValue().getID());
                        App.ongoingExtensions.add(extensionActivity.get().extension.getValue());
                        extensionActivity.get().joinBut.setBackgroundResource(R.drawable.corn_button2);
                        extensionActivity.get().joinBut.setText("退出活动");
                        extensionActivity.get().chatBut.setEnabled(true);
                        extensionActivity.get().members.add(new Member(user.getUID(), user.getEmail(), user.getUserName(), user.getHeadImage()));
                        extensionActivity.get().memberAdapter.notifyDataSetChanged();
                        Toast.makeText(extensionActivity.get(), "加入成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(extensionActivity.get(), "加入失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case notifyError:
                    Bundle data = msg.getData();
                    String error = data.getString("error");
                    Toast.makeText(extensionActivity.get(), error, Toast.LENGTH_SHORT).show();
                    break;
                case exitExtension:
                    UserModel user = App.localUser.getValue();
                    try {
                        TcpClient.getInstance().exitExtension(user.getUID(), extensionActivity.get().extension.getValue().getID());
                        //本地操作
                        App.ongoingExtensions.remove(extensionActivity.get().extension.getValue().getID());
                        extensionActivity.get().joinBut.setBackgroundResource(R.drawable.login_button);
                        extensionActivity.get().joinBut.setText("加入活动");
                        extensionActivity.get().chatBut.setEnabled(false);
                        for (int i = 0; i < extensionActivity.get().members.size(); i++) {
                            Member member = extensionActivity.get().members.get(i);
                            if (member.getUID() == App.localUser.getValue().getUID()) {
                                extensionActivity.get(). members.remove(member);
                                break;
                            }
                        }
                        extensionActivity.get().memberAdapter.notifyDataSetChanged();
                        Toast.makeText(extensionActivity.get(), "已退出", Toast.LENGTH_SHORT).show();
                    }catch (Exception ex){
                        Toast.makeText(extensionActivity.get(), "退出失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case changeData:
                    extensionActivity.get().memberAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
