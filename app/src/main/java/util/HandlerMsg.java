package util;

import android.os.Bundle;
import android.os.Message;

public class HandlerMsg {

    public static Message getMsg(int what,Object obj){
        Message message=new Message();
        message.what=what;
        message.obj=obj;
        return message;
    }

    public static Message getMsg(int what, Bundle bundle){
        Message message=new Message();
        message.what=what;
        message.setData(bundle);
        return message;
    }
}
