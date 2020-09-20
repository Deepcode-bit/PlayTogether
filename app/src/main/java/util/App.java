package util;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.nepu.playtogether.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import model.UserModel;

public class App extends Application {

    public static final int sport=0,study=1,life=2,game=3;
    public static final int post=1,get=2;
    public static final String netUrl="http://192.168.1.106:8080/PTServer/userservlet";
    public static final ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    public static final String dataBaseName = "my_data";
    public static final int dataBaseVersion = 1;
    public static MutableLiveData<UserModel> localUser=new MutableLiveData<>();

    public static String getExtensionType(int type){
        String typeString=null;
        switch (type){
            case 0:typeString="运动"; break;
            case 1:typeString="学习"; break;
            case 2:typeString="生活"; break;
            case 3:typeString="游戏"; break;
        }
        return typeString;
    }
    public static int getExtensionDrawable(int type){
        switch (type){
            case 0:return R.drawable.rippler_color1;
            case 1:return R.drawable.rippler_color2;
            case 2:return R.drawable.rippler_color3;
            case 3:return R.drawable.rippler_color4;
            default:return 0;
        }
    }
    public static String getStateType(int type){
        switch (type){
            case 0:return "(未认证)";
            case 1:return "(已认证)";
            case 2:return "(管理员)";
            case 3:return "(被禁用)";
            default:return null;
        }
    }
}
