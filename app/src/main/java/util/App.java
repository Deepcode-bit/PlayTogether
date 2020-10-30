package util;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.nepu.playtogether.R;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import model.ExtensionModel;
import model.MessageModel;
import model.UserModel;

public class App extends Application {

    public static final int sport=0,study=1,life=2,game=3;
    public static final int post=1,get=2;
    //不带路由的IP地址和端口号
    public static final String netUrl="http://188.131.156.21:8080";
    //教务系统API接口
    public static final String EASUrl="http://jwgl.nepu.edu.cn/new/login";
    //IM TCP连接的地址和接口
    public static final String IPAddress="122.51.222.186";
    public static final int port=1230;

    public static final ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    public static final String dataBaseName = "my_data";
    public static final int dataBaseVersion = 1;
    //本地存储的账号信息
    public static MutableLiveData<UserModel> localUser=new MutableLiveData<>();
    //正在进行的活动(参加)
    public static List<ExtensionModel> ongoingExtensions;
    //参加的活动
    public static List<ExtensionModel> joinExtensions;
    //创建的活动
    public static List<ExtensionModel> createdExtensions;
    //消息集合
    public static List<MessageModel> messages;
    //用户头像
    public static Bitmap headImage;

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
