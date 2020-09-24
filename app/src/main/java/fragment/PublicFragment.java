package fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentPublicBinding;
import com.nepu.playtogether.databinding.FragmentSelectBinding;

import view_model.PublicViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicFragment extends Fragment {

    FragmentPublicBinding mBinding;
    public PublicViewModel mViewModel;

    public PublicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding= FragmentPublicBinding.inflate(inflater,container,false);
        mViewModel= ViewModelProviders.of(requireActivity()).get(PublicViewModel.class);
        mBinding.setLifecycleOwner(requireActivity());
        mBinding.setData(this);
        return mBinding.getRoot();
    }


    public void OnBackClick(View v){
        requireActivity().onBackPressed();
    }
}
