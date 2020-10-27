package fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nepu.playtogether.databinding.FragmentPublicBinding;

import java.lang.ref.WeakReference;
import java.util.Objects;

import model.ExtensionModel;
import util.App;
import util.TcpClient;
import view_model.PublicViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicFragment extends Fragment {

    public PublicViewModel mViewModel;
    public static MyHandler handler;

    public PublicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.nepu.playtogether.databinding.FragmentPublicBinding mBinding = FragmentPublicBinding.inflate(inflater, container, false);
        mViewModel= ViewModelProviders.of(requireActivity()).get(PublicViewModel.class);
        mBinding.setLifecycleOwner(requireActivity());
        mBinding.setData(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler=new MyHandler(this);
    }

    public void OnBackClick(View v){
        requireActivity().onBackPressed();
    }

    public void onExtensionCreate(View v){
        if(mViewModel.extensionName.getValue().isEmpty() || mViewModel.extensionDate.getValue().isEmpty() || mViewModel.extensionPlace.getValue().isEmpty()){
            Toast.makeText(requireActivity(),"活动信息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        App.mThreadPool.execute(mViewModel.addExtension);
    }

    public static class MyHandler extends Handler{
        WeakReference<PublicFragment> publicFragment;
        public static final int addExtension=0x001;

        MyHandler(PublicFragment fragment) {
            publicFragment = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(publicFragment ==null)return;
            if (msg.what == addExtension) {
                FragmentActivity requireActivity = publicFragment.get().requireActivity();
                Intent intent = new Intent();
                ExtensionModel extension = (ExtensionModel) msg.obj;
                intent.putExtra("extension", extension);
                requireActivity.setResult(0, intent);
                requireActivity.finish();
                try {
                    TcpClient.getInstance().sendJoinExtension(App.localUser.getValue().getUID(), extension.getID());
                    Toast.makeText(requireActivity, "创建成功", Toast.LENGTH_SHORT).show();
                    App.ongoingExtensions.add(extension);
                } catch (Exception ex) {
                    Toast.makeText(requireActivity, "创建失败", Toast.LENGTH_SHORT).show();
                    Log.e("error", Objects.requireNonNull(ex.getMessage()));
                }
            }
        }
    }
}
