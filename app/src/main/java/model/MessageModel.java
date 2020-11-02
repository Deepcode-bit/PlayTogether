package model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private int senderId;
    private int receiverId;
    private String msg;
    private String sendTime;
    private String senderImage;
    private String senderName;
    private int sendType;
    private int unReadNum;
    private Bitmap headImage;

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }

    public MessageModel(int senderId, int receiverId, String senderName, String msg, String sendTime, int sendType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.msg = msg;
        this.sendTime = sendTime;
        this.sendType = sendType;
        this.senderName = senderName;
        this.unReadNum = 1;
    }

    public static final int PERSON=0;
    public static final int EXTENSION=1;


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public Bitmap getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Bitmap headImage) {
        this.headImage = headImage;
    }
}
