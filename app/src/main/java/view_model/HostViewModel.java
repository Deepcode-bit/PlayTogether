package view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import model.ExtensionModel;
import model.MessageModel;
import util.App;
import util.TcpClient;

public class HostViewModel extends ViewModel implements TcpClient.MessageReceiveListener {
    public MutableLiveData<String> userName,verify;
    public MutableLiveData<Integer> joinNum,createNum,underNum;
    public MutableLiveData<List<ExtensionModel>> extensions;
    public MutableLiveData<List<MessageModel>> messages;
    public int openChatUID;

    public HostViewModel(){
        userName=new MutableLiveData<>();
        userName.setValue("登录/注册");
        verify=new MutableLiveData<>();
        joinNum=new MutableLiveData<>();
        createNum=new MutableLiveData<>();
        underNum=new MutableLiveData<>();
        extensions= new MutableLiveData<List<ExtensionModel>>(new ArrayList<ExtensionModel>());
        extensions.getValue().add(null);
        messages= new MutableLiveData<List<MessageModel>>(new ArrayList<MessageModel>());
        TcpClient.getInstance().setOnMessageReceiveListener(this);
    }

    
    public  void addMessage(MessageModel msg) {
        List<MessageModel> messageModels = messages.getValue();
        if (messageModels == null) return;
        for (int i = 0; i < messageModels.size(); i++) {
            MessageModel message = messageModels.get(i);
            //判断改消息的发送者之前是否存在
            if (msg.getSenderId()==message.getSenderId()) {
                int unReadNum = msg.getSenderId() == openChatUID ? 0 : message.getUnReadNum() + 1;
                msg.setUnReadNum(unReadNum);
                messageModels.remove(i);
                messageModels.add(i, msg);
                return;
            }
        }
        messageModels.add(0, msg);
    }

    @Override
    public void onMessageReceive(MessageModel msg) {
        if(msg.getSendType()!=MessageModel.EXTENSION){
            addMessage(msg);
            App.messages.add(msg);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        TcpClient.getInstance().removeListener(this);
    }
}
