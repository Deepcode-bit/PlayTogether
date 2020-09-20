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
    private List<ExtensionModel> list;
    private FragmentExtensionBinding mBinding;
    private HostViewModel mViewModel;

    public ExtensionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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
        list=new ArrayList<>();
        int number=5;
        list.add(new ExtensionModel("100","篮球", App.sport, number, "张三","暂定","暂定"));
        list.add(new ExtensionModel("101","王者荣耀排位", App.game, number, "文剑旭","暂定","暂定"));
        list.add(new ExtensionModel("102","看电影:《花木兰》", App.life, number, "潘博飞","暂定","暂定"));
        list.add(new ExtensionModel("103","图书馆自习", App.study, number, "冯帅","暂定","暂定"));
        list.add(new ExtensionModel("104","怪物猎人冰原", App.game, number, "玛丽","暂定","暂定"));
        list.add(new ExtensionModel("105","人类一败涂地", App.game, number, "玛丽","暂定","暂定"));
        mAdapter.SetAllExtension(list);
        mAdapter.SetHeadView(true);
        mAdapter.notifyDataSetChanged();
        refreshLayout = getView().findViewById(R.id.ex_fresh_layout);
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int Position) {
        Intent intent=new Intent();
        ExtensionModel extension=list.get(Position);
        Bundle bundle=new Bundle();
        bundle.putSerializable("extension",extension);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), ExtensionActivity.class);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "head_bg");

        startActivity(intent,activityOptionsCompat.toBundle());
    }

    public void onPublicExtension(View v){
        Intent intent=new Intent();
        intent.setClass(getActivity(), PublicActivity.class);
        startActivity(intent);
    }
}
