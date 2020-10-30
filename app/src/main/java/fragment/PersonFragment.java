package fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nepu.playtogether.ExtensionListActivity;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.MainActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentPersonBinding;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.nepu.playtogether.CertificationActivity;

import model.UserModel;
import util.App;
import util.Connection;
import util.Dao;
import util.TcpClient;
import view_model.HostViewModel;


public class PersonFragment extends Fragment implements View.OnClickListener {

    private FragmentPersonBinding mBinding;
    public HostViewModel mViewModel;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private ImageView headView;

    public static MyHandler handler;
    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    public PersonFragment() {
        // Required empty public constructor
    }


    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding=FragmentPersonBinding.inflate(inflater,container,false);
        mViewModel = ViewModelProviders.of(requireActivity()).get(HostViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((HostActivity) getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UpdateView();
        handler=new MyHandler(this);
    }

    /*
初始化控件方法
 */
    private void showTakePhotoDialog() {
        builder = new AlertDialog.Builder(requireActivity());//创建对话框
        inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_select_photo, null);//获取自定义布局
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框
        layout.findViewById(R.id.take_photo).setOnClickListener(this);
        layout.findViewById(R.id.select_photo).setOnClickListener(this);
        layout.findViewById(R.id.cancel_photo).setOnClickListener(this);
    }

    private void UpdateView() {
        App.localUser.observe(getViewLifecycleOwner(), new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel user) {
                if (user != null) {
                    if (user.getUserName() == null || user.getUserName().isEmpty()) {
                        user.setUserName("默认用户");
                    }
                    mViewModel.userName.setValue(user.getUserName());
                    mViewModel.verify.setValue((App.getStateType(user.getUserState())));
                    mViewModel.joinNum.setValue(user.getJoinNum());
                    mViewModel.createNum.setValue(user.getCreateNum());
                    headView = requireView().findViewById(R.id.image_head);
                    if (App.headImage != null)
                        headView.setImageBitmap(App.headImage);
                } else {
                    mViewModel.userName.setValue("登录/注册");
                    mViewModel.verify.setValue(null);
                    mViewModel.joinNum.setValue(0);
                    mViewModel.createNum.setValue(0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_out:LogOut(); break;
            case R.id.image_head:if(App.localUser.getValue()==null) LogIn();else showTakePhotoDialog();break;
            case R.id.user_name: if(App.localUser.getValue()==null) LogIn();else ChangeName(); break;
            case R.id.take_photo:
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE);
                }else {
                    try {
                        takePhoto();
                        dialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.select_photo:
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_ADD_CASE_CALL_PHONE2);
                } else {
                    //打开相册
                    choosePhoto();
                }
                dialog.dismiss();
                break;
            case R.id.cancel_photo:dialog.dismiss();break;
        }
    }

    private void LogOut() {
        AlertDialog.Builder dialog;
        if (App.localUser.getValue() != null) {
            dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBackground)
                    .setTitle("提示")
                    .setCancelable(false)
                    .setMessage("确定要退出登录吗?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Dao(getActivity()).DeleteUser(Objects.requireNonNull(App.localUser.getValue()));
                            App.localUser.setValue(null);
                            UpdateView();
                            Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
                            try {
                                TcpClient.getInstance().closeClient();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        } else {
            dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBackground).setTitle("提示").setMessage("您还未登录");
        }
        dialog.show();
    }

    private void LogIn(){
        Intent intent=new Intent();
        intent.setClass(requireActivity(), MainActivity.class);
        startActivity(intent);
    }

    private void ChangeName(){
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        final View view=inflater.inflate(R.layout.edit_layout,null);
        final EditText edit=view.findViewById(R.id.edit_name);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.AlertDialogBackground)
                .setTitle("更改名称")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final UserModel user=App.localUser.getValue();
                        user.setUserName(edit.getText().toString());
                        App.localUser.setValue(user);
                        new Dao(getActivity()).UpdateUser(user);
                        App.mThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<String, String>() {
                                    {
                                        put("user", new Gson().toJson(user));
                                    }
                                };
                                Connection.getJson(App.post, App.netUrl, params,"/member/modifyInfo");
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                   {public void onClick(DialogInterface dialog, int which) {}});
        dialog.show();
    }

    public void EnterCertification(View v){
        Intent intent=new Intent();
        intent.setClass(requireActivity(), CertificationActivity.class);
        startActivity(intent);
    }

    public void EnterExtensionList(View v) {
        String type = "";
        switch (v.getId()) {
            case R.id.join_list:
                type = "join";
                break;
            case R.id.create_list:
                type = "create";
                break;
            case R.id.ongoing_list:
                type = "ongoing";
                break;
        }
        Intent intent=new Intent();
        intent.setClass(requireActivity(), ExtensionListActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }


    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed() throws IOException {
        String fileA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/photo";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, "UserIcon.png");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_ADD_CASE_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(requireActivity(),"拒绝了你的请求",Toast.LENGTH_SHORT).show();
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
            }
        }
        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        requireActivity().startActivityForResult(picture, 2);
    }


    private void takePhoto() throws IOException {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, MY_ADD_CASE_CALL_PHONE);
                return;
            }
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 获取文件
//        File file = createFileIfNeed();
//        //拍照后原图回存入此路径下
//        Uri uri;
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//            uri = Uri.fromFile(file);
//        } else {
//            uri = FileProvider.getUriForFile(requireActivity(), "com.example.bobo.getphotodemo.fileprovider", file);
//        }
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        requireActivity().startActivityForResult(intent, 1);
    }

    private Runnable saveUser=new Runnable() {
        @Override
        public void run() {
            UserModel user = App.localUser.getValue();
            if (user == null) return;
            Dao dao = new Dao(getActivity());
            if (dao.getLocalUser() != null) {
                dao.UpdateUser(user);
            } else {
                dao.InsertUser(user);
            }
        }
    };

    public static class MyHandler extends Handler{
        public static final int notify=0x001;
        private WeakReference<PersonFragment> personFragment;

        public MyHandler(PersonFragment fragment){
            this.personFragment=new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case notify:
                    String message = msg.getData().getString("msg");
                    String url=msg.getData().getString("url");
                    if (url!=null){
                        App.localUser.getValue().setHeadImage(url);
                        App.mThreadPool.execute(personFragment.get().saveUser);
                    }
                    Toast.makeText(personFragment.get().requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
