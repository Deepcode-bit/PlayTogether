package util;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.MessageModel;

public class TcpClient {
    private static Socket socket;
    private static TcpClient client;
    private boolean isConn;
    public interface MessageReceiveListener{
        void onMessageReceive(MessageModel msg);
    }
    private MessageReceiveListener listener1;
    private MessageReceiveListener listener2;

    public static TcpClient getInstance(){
        if(client==null){
            client=new TcpClient();
        }
        return client;
    }

    public void setOnMessageReceiveListener1(MessageReceiveListener listener) {
            listener1 = listener;
    }
    public void setOnMessageReceiveListener2(MessageReceiveListener listener) {
        listener2 = listener;
    }

    public void startClient(final String address ,final int port){
        if (address == null){
            return;
        }
        if (socket == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isConn=true;
                        Log.i("tcp", "启动客户端");
                        socket = new Socket(address, port);
                        Log.i("tcp", "客户端连接成功");
                        sendID();
                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1 && isConn) {
                            String data = new String(buffer, 0, len);
                            Log.i("tcp", "收到服务器的数据---------------------------------------------:" + data);
                            MessageModel messageModel = new Gson().fromJson(data, MessageModel.class);
                            if (listener1 != null)
                                listener1.onMessageReceive(messageModel);
                            if(listener2!=null)
                                listener2.onMessageReceive(messageModel);
                        }
                        isConn=false;
                        Log.i("tcp", "客户端断开连接");
                        pw.close();

                    } catch (Exception EE) {
                        EE.printStackTrace();
                        Log.i("tcp", "客户端无法连接服务器"+EE.getMessage());

                    }finally {
                        socket = null;
                    }
                }
            }).start();
        }
    }

    private void sendTcpMessage(final String msg){
        if (socket != null && socket.isConnected()) {
            App.mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
                        out.println(msg);
                        socket.getOutputStream().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void sendChatMsg(MessageModel message) throws JSONException {
        JSONObject json=new JSONObject();
        json.put("operate","sendChatMsg").
                put("message",new Gson().toJson(message));
        sendTcpMessage(json.toString());
    }
    public void sendJoinExtension(int UID,int EID) throws JSONException {
        JSONObject json=new JSONObject();
        json.put("operate","sendJoinExtension");
        json.put("UID",UID).put("EID",EID);
        sendTcpMessage(json.toString());
    }
    public void exitExtension(int UID,int EID) throws JSONException {
        JSONObject json=new JSONObject();
        json.put("operate","exitExtension");
        json.put("UID",UID).put("EID",EID);
        sendTcpMessage(json.toString());
    }
    public void cancelExtension(int EID) throws JSONException {
        JSONObject json=new JSONObject();
        json.put("operate","cancelExtension");
        json.put("EID",EID);
        sendTcpMessage(json.toString());
    }

    private void sendID() throws JSONException {
        JSONObject json=new JSONObject();
        json.put("operate","sendID").put("ID",App.localUser.getValue().getUID());
        sendTcpMessage(json.toString());
    }

    public void closeClient() throws IOException {
        if(socket!=null){
            isConn=false;
            socket=null;
        }
    }
}
