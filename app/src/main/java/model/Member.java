package model;

import android.graphics.Bitmap;

public class Member {
    private int UID;
    private String email;
    private String userName;
    private String headImage;
    private Bitmap headBitmap;


    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    public Member(int UID, String email, String userName, String headImage) {
        this.UID = UID;
        this.email = email;
        this.userName = userName;
        this.headImage = headImage;
    }
}
