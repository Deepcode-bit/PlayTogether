package model;

import java.io.Serializable;

public class ExtensionModel implements Serializable {
    private String ID;
    private String name;
    private int type;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private String originator;
    private String startTime;
    private String location;

    public ExtensionModel(String id, String name, int type, int number, String originator, String startTime, String location) {
        ID = id;
        this.name = name;
        this.type = type;
        this.number = number;
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

    public String getID() {
        return ID;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
