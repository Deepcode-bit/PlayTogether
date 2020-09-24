package fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentSelectBinding;

import view_model.PublicViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectFragment extends Fragment {

    FragmentSelectBinding mBinding;
    public PublicViewModel mViewModel;

    public SelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding=FragmentSelectBinding.inflate(inflater,container,false);
        mViewModel= ViewModelProviders.of(requireActivity()).get(PublicViewModel.class);
        mBinding.setLifecycleOwner(requireActivity());
        mBinding.setData(this);
        return mBinding.getRoot();
    }

    public void onSelectClick(View v){
        int index=0;
        switch (v.getId()){
            case R.id.type_0:break;
            case R.id.type_1:index=1;break;
            case R.id.type_2:index=2;break;
            case R.id.type_3:index=3;break;
            default:return;
        }
        mViewModel.typeSelectIndex.setValue(index);
        //自定义的一个单选按钮组
        ResetVisibility(getView().findViewById(R.id.type_0),View.INVISIBLE);
        ResetVisibility(getView().findViewById(R.id.type_1),View.INVISIBLE);
        ResetVisibility(getView().findViewById(R.id.type_2),View.INVISIBLE);
        ResetVisibility(getView().findViewById(R.id.type_3),View.INVISIBLE);
        ResetVisibility(v,View.VISIBLE);
    }

    private void ResetVisibility(View v,int isVisible){
        RelativeLayout layout = (RelativeLayout)v.getParent();
        layout.getChildAt(2).setVisibility(isVisible);
    }

    public void NextPage(View v){
        NavController controller= Navigation.findNavController(v);
        controller.navigate(R.id.action_selectFragment_to_publicFragment);
    }
}
