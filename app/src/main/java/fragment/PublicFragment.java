package fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentPublicBinding;
import com.nepu.playtogether.databinding.FragmentSelectBinding;

import org.json.JSONException;

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

    public void OnBackClick(View v){
        requireActivity().onBackPressed();
    }

    public void onExtensionCreate(View v){
        if(mViewModel.extensionName.getValue().isEmpty() || mViewModel.extensionDate.getValue().isEmpty() || mViewModel.extensionPlace.getValue().isEmpty()){
            Toast.makeText(requireActivity(),"活动信息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        ExtensionModel extension=new ExtensionModel(
                mViewModel.extensionName.getValue(),
                Objects.requireNonNull(App.localUser.getValue()).getUID(),
                Objects.requireNonNull(mViewModel.typeSelectIndex.getValue()),
                Objects.requireNonNull(App.localUser.getValue()).getUserName(),
                mViewModel.extensionDate.getValue(),
                mViewModel.extensionPlace.getValue());
        //TODO:服务端请求添加活动
        Intent intent=new Intent();
        intent.putExtra("extension",extension);
        requireActivity().setResult(0,intent);
        try {
            TcpClient.getInstance().sendJoinExtension(App.localUser.getValue().getUID(), extension.getID());
            Toast.makeText(requireActivity(), "创建成功", Toast.LENGTH_SHORT).show();
        }catch (Exception ex) {
            Log.e("error", Objects.requireNonNull(ex.getMessage()));
        }
        requireActivity().finish();
    }
}
