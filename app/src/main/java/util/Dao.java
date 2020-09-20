package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import model.UserModel;

public class Dao {
    private MyDataBase dataBase;

    public Dao(Context context){
        dataBase=new MyDataBase(context);
    }

    public void InsertUser(UserModel user){
        SQLiteDatabase db=dataBase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("UID",user.getUID());
        values.put("userName",user.getUserName());
        values.put("pwd",user.getPwd());
        values.put("email",user.getEmail());
        values.put("joinNum",user.getJoinNum());
        values.put("createNum",user.getCreateNum());
        values.put("userState",user.getUserState());
        values.put("headImage",user.getHeadImage());
        db.insert("user",null,values);
        db.close();
    }


    public void UpdateUser(UserModel user){
        SQLiteDatabase db=dataBase.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("UID",user.getUID());
        values.put("userName",user.getUserName());
        values.put("pwd",user.getPwd());
        values.put("email",user.getEmail());
        values.put("joinNum",user.getJoinNum());
        values.put("createNum",user.getCreateNum());
        values.put("userState",user.getUserState());
        values.put("headImage",user.getHeadImage());
        db.update("user",values,"uid=?",new String[]{String.valueOf(user.getUID())});
        db.close();
    }

    public void DeleteUser(UserModel user){
        SQLiteDatabase db=dataBase.getWritableDatabase();
        db.delete("user","uid=?",new String[]{String.valueOf(user.getUID())});
        db.close();
    }

    public UserModel getLocalUser(){
        SQLiteDatabase db=dataBase.getWritableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            int UID = cursor.getInt(cursor.getColumnIndex("uid"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
            String userName=cursor.getString(cursor.getColumnIndex("userName"));
            int joinNum=cursor.getInt(cursor.getColumnIndex("joinNum"));
            int createNum=cursor.getInt(cursor.getColumnIndex("createNum"));
            String headImage=cursor.getString(cursor.getColumnIndex("headImage"));
            int userState = cursor.getInt(cursor.getColumnIndex("userState"));
            return new UserModel(UID, email, pwd,userState,userName,joinNum,createNum,headImage);
        }catch (Exception ex){
            Log.d("Exception",ex.toString());
        }
        cursor.close();
        db.close();
        return null;
    }
}
