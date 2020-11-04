package model;

import java.io.Serializable;

public class ExtensionModel implements Serializable {
    private int id;
    private int uid;
    private int state;

    private String name;
    private int type;
    private int number;

    private String originator;
    private String startTime;
    private String location;
    
    public int getNumber() {
        return Math.max(number, 0);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ExtensionModel(String name, int UID,int type, String originator, String startTime, String location) {
        this.name = name;
        this.type = type;
        this.uid=UID;
        this.originator = originator;
        this.startTime = startTime;
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getOriginator() {
        return originator;
    }

    public int getType() {
        return type;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){this.id=id;}

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getUID() {
        return uid;
    }

    public void setUID(int UID) {
        this.uid = UID;
    }
}
