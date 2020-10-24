package com.nepu.playtogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.ChatAdapter;
import model.MessageModel;
import model.UserModel;
import util.App;
import util.HandlerMsg;
import util.TcpClient;

public class ChatRoomActivity extends AppCompatActivity implements TcpClient.MessageReceiveListener {

    RecyclerView chatView;
    List<MessageModel> messages;
    EditText msgEdit;
    ChatAdapter adapter;
    public static MyHandler myHandler;
    private int receiverId;
    private int senderType;
    private String senderName;
    private LinearLayoutManager linearLayoutManager;
    static final private int RECEIVE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Init();
    }

    private void Init() {
        myHandler = new MyHandler(this);
        //初始化控件
        chatView = findViewById(R.id.chat_view);
        msgEdit = findViewById(R.id.msg_edit);
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        chatView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
        chatView.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
        //尝试连接IM服务器
        TcpClient.getInstance().startClient(App.IPAddress, App.port);
        //初始化变量
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null) {
            receiverId = bundle.getInt("receiverId");
            senderType = bundle.getInt("receiverType");
            senderName=bundle.getString("senderName");
            ((TextView)findViewById(R.id.title3)).setText(bundle.getString("title"));
        }
        //初始化历史消息
        for(MessageModel msg : App.messages){
            //获取对方消息
            if(msg.getSenderId()==receiverId && msg.getSendType()==MessageModel.PERSON) {
                messages.add(msg);
            //获取自己的消息
            }else if(App.localUser.getValue().getUID()==msg.getSenderId() && msg.getReceiverId()==receiverId){
                messages.add(msg);
            //获取群聊的消息
            }else if(msg.getReceiverId()==receiverId && msg.getSendType()==MessageModel.EXTENSION){
                messages.add(msg);
            }
        }
        TcpClient.getInstance().setOnMessageReceiveListener(this);
    }

    public void onSendMsgClick(View v) throws JSONException {
        if(!msgEdit.getText().toString().isEmpty()){
            UserModel localUser = App.localUser.getValue();
            String msg=msgEdit.getText().toString();
            MessageModel message = new MessageModel(localUser.getUID(),receiverId,senderName,msg,new Date().toLocaleString(),senderType);
            messages.add(message);
            App.messages.add(message);
            adapter.notifyDataSetChanged();
            msgEdit.setText(null);
            linearLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
            TcpClient.getInstance().sendChatMsg(message);
        }
    }

    /**
     * 接受消息的回调函数
     * @param msg
     */
    @Override
    public void onMessageReceive(MessageModel msg) {
        Bundle bundle = new Bundle();
        //判断是否为当前聊天框消息
        if (senderType == MessageModel.EXTENSION && msg.getReceiverId() != receiverId)
            return;
        if (senderType == MessageModel.PERSON && msg.getSenderId() != receiverId)
            return;
        if (senderType != msg.getSendType())
            return;
        if (msg.getSendType() == MessageModel.EXTENSION)
            App.messages.add(msg);
        bundle.putSerializable("msg", msg);
        Message message = HandlerMsg.getMsg(RECEIVE, bundle);
        myHandler.sendMessage(message);
    }

    public static class MyHandler extends Handler{
        WeakReference<ChatRoomActivity> chatRoomActivity;
        MyHandler(ChatRoomActivity activity){
            chatRoomActivity=new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case RECEIVE:
                    Bundle bundle=msg.getData();
                    MessageModel messageModel = (MessageModel) bundle.getSerializable("msg");
                    if(messageModel !=null){
                        chatRoomActivity.get().messages.add(messageModel);
                        chatRoomActivity.get().adapter.notifyDataSetChanged();
                        chatRoomActivity.get().linearLayoutManager.scrollToPositionWithOffset(chatRoomActivity.get().adapter.getItemCount() - 1, Integer.MIN_VALUE);
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance().removeListener(this);
    }
}
