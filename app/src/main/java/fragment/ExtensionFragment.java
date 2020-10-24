package fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepu.playtogether.ExtensionActivity;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.PublicActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentExtensionBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.ExtensionModel;
import util.App;
import adapter.MAdapter;
import adapter.MAdapter.onItemClickListener;
import view_model.HostViewModel;

public class ExtensionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,onItemClickListener {

    private RecyclerView recyclerView;
    private MAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private FragmentExtensionBinding mBinding;
    private HostViewModel mViewModel;

    public ExtensionFragment() {
        // Required empty public constructor
    }

    public static ExtensionFragment newInstance() {
        ExtensionFragment fragment = new ExtensionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding=FragmentExtensionBinding.inflate(inflater,container,false);
        mViewModel= ViewModelProviders.of(requireActivity()).get(HostViewModel.class);
        mBinding.setLifecycleOwner(requireActivity());
        mBinding.setData((HostActivity) requireActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView= requireActivity().findViewById(R.id.recycler_view);
        mAdapter=new MAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.SetAllExtension(mViewModel.extensions.getValue());
        //TODO:测试数据，记得删掉这个
        mViewModel.extensions.getValue().add(new ExtensionModel("篮球",5,App.sport,"李勇","18:00","大活篮球场"));
        mAdapter.SetHeadView(true);
        refreshLayout = getView().findViewById(R.id.ex_fresh_layout);
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int Position) {
        Intent intent=new Intent();
        ExtensionModel extension=mViewModel.extensions.getValue().get(Position);
        Bundle bundle=new Bundle();
        bundle.putSerializable("extension",extension);
        intent.putExtras(bundle);
        intent.setClass(requireActivity(), ExtensionActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "head_bg");

        startActivity(intent,activityOptionsCompat.toBundle());
    }

    public void onPublicExtension(View v){
        Intent intent=new Intent();
        intent.setClass(requireActivity(), PublicActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        ExtensionModel extension = (ExtensionModel) data.getSerializableExtra("extension");
        mViewModel.extensions.getValue().add(extension);
        mAdapter.notifyDataSetChanged();
    }
}
