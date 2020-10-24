package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nepu.playtogether.databinding.ActivityExtensionBinding;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapter.MemberAdapter;
import model.ExtensionModel;
import model.Member;
import model.MessageModel;
import model.UserModel;
import util.App;
import util.TcpClient;

public class ExtensionActivity extends AppCompatActivity implements MemberAdapter.onItemClickListener {

    private RecyclerView memberRecycler;
    private MemberAdapter memberAdapter;
    private List<Member> members;
    private Button joinBut,chatBut;
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
        members=new ArrayList<>();
        //TODO:向服务端请求活动成员
        memberAdapter=new MemberAdapter();
        memberRecycler=findViewById(R.id.member_list);
        memberRecycler.setLayoutManager(new LinearLayoutManager(this));
        memberRecycler.setAdapter(memberAdapter);
        memberAdapter.setMembers(members);
        members.add(new Member(1,"12","李航",""));
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
        UserModel user=App.localUser.getValue();
        if(joinBut.getText().equals("取消活动")){
            //TODO:服务端请求取消活动
            //IM服务器请求
            TcpClient.getInstance().cancelExtension(extension.getValue().getID());
            //本地操作
            Toast.makeText(this,"已取消",Toast.LENGTH_SHORT).show();
            this.finish();
        }else if(joinBut.getText().equals("加入活动")){
            //TODO:服务端请求加入活动
            //IM服务器请求
            TcpClient.getInstance().sendJoinExtension(user.getUID(),extension.getValue().getID());
            //本地操作
            App.ongoingExtensions.add(extension.getValue());
            joinBut.setBackgroundResource(R.drawable.corn_button2);
            joinBut.setText("退出活动");
            chatBut.setEnabled(true);
            members.add(new Member(user.getUID(),user.getEmail(),user.getUserName(),user.getHeadImage()));
            memberAdapter.notifyDataSetChanged();
            Toast.makeText(this,"加入成功",Toast.LENGTH_SHORT).show();
        }else if(joinBut.getText().equals("退出活动")){
            //TODO:服务器请求退出活动
            //IM服务器请求
            TcpClient.getInstance().exitExtension(user.getUID(),extension.getValue().getID());
            //本地操作
            App.ongoingExtensions.remove(extension.getValue().getID());
            joinBut.setBackgroundResource(R.drawable.login_button);
            joinBut.setText("加入活动");
            chatBut.setEnabled(false);
            for(int i=0;i<members.size();i++){
                Member member=members.get(i);
                if(member.getUID()==App.localUser.getValue().getUID()){
                    members.remove(member);
                    break;
                }
            }
            memberAdapter.notifyDataSetChanged();
            Toast.makeText(this,"已退出",Toast.LENGTH_SHORT).show();
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

}
