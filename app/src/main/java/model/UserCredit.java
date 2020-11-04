package model;

import android.icu.math.BigDecimal;

public class UserCredit {
    private int uid;
    private int credit;
    private int evalNum;

    public UserCredit(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getEvalNum() {
        return evalNum;
    }

    public void setEvalNum(int evalNum) {
        this.evalNum = evalNum;
    }
}
