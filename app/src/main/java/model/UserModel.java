package model;

public class UserModel {
    private int UID;
    private String email;
    private String pwd;
    private String userName;
    private int joinNum;
    private int createNum;
    private int state;
    private String avatar;
    private String SID;

    public UserModel(int uid, String email, String pwd, int userState, String userName, int joinNum, int createNum, String headImage) {
        UID = uid;
        this.email = email;
        this.pwd = pwd;
        this.state = userState;
        this.userName=userName;
        this.joinNum=joinNum;
        this.createNum=createNum;
        this.avatar=headImage;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public int getCreateNum() {
        return createNum;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public int getUID() {
        return UID;
    }

    public int getUserState() {
        return state;
    }

    public String getEmail() {
        return email;
    }

    public String getHeadImage() {
        return avatar;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setCreateNum(int createNum) {
        this.createNum = createNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHeadImage(String headImage) {
        this.avatar = headImage;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserState(int userState) {
        this.state = userState;
    }
}
