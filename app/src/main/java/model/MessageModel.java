package model;

public class MessageModel {
    private String senderName;
    private String msg;
    private String sendTime;
    private int unReadNum;

    public MessageModel(String senderName, String msg, int unReadNum,String sendTime){
        this.senderName = senderName;
        this.msg = msg;
        this.unReadNum = unReadNum;
        this.sendTime=sendTime;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public String getMsg() {
        return msg;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSendTime() {
        return sendTime;
    }
}
