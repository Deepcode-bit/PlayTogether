package util;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataBase extends SQLiteOpenHelper {

    public MyDataBase(@Nullable Context context) {
        super(context, App.dataBaseName, null, App.dataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql="create table user(uid integer primary key AUTOINCREMENT," +
                "userName,pwd,email,joinNum,createNum,userState,headImage)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
