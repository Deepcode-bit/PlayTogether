package fragment;
import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepu.playtogether.ChatRoomActivity;
import com.nepu.playtogether.ExtensionActivity;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.PublicActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentExtensionBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.ExtensionModel;
import util.App;
import adapter.MAdapter;
import adapter.MAdapter.onItemClickListener;
import view_model.HostViewModel;

public class ExtensionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,onItemClickListener,MAdapter.onSearchButtonClickListener, RadioGroup.OnCheckedChangeListener {

    private RecyclerView recyclerView;
    private MAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private FragmentExtensionBinding mBinding;
    private HostViewModel mViewModel;
    private ProgressBar progressBar;
    public static MyHandler handler;
    private final String[] extensionTypes=new String[]{"全部","学习","运动","生活","游戏"};
    public static final int extensionDataChange=0x001;
    public static final int notifyError=0x002;

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
        handler = new MyHandler(this);
        progressBar = requireView().findViewById(R.id.progressBar);
        refreshLayout = requireView().findViewById(R.id.ex_fresh_layout);
        recyclerView = requireActivity().findViewById(R.id.recycler_view);
        progressBar.setVisibility(View.INVISIBLE);
        //初始化recyclerView
        mAdapter = new MAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.SetAllExtension(mViewModel.extensions.getValue());
        mAdapter.SetSearchButListener(this);
        mAdapter.SetonCheckChangeListener(this);
        mAdapter.SetHeadView(true);
        refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
        //获取数据源
        if (App.localUser.getValue() == null) {
            Toast.makeText(requireActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.localUser.getValue().getUserState() == 0) {
            Toast.makeText(requireActivity(), "请先认证", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        App.mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    App.mThreadPool.execute(mViewModel.getAllExtension);
                }catch (Exception ex){}
            }
        });
    }

    @Override
    public void onRefresh() {
        //获取数据源
        if (App.localUser.getValue() == null) {
            Toast.makeText(requireActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.localUser.getValue().getUserState() == 0) {
            Toast.makeText(requireActivity(), "请先认证", Toast.LENGTH_SHORT).show();
            return;
        }
        App.mThreadPool.execute(mViewModel.getAllExtension);
        refreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.VISIBLE);
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
        if (App.localUser.getValue() == null) {
            Toast.makeText(requireActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.localUser.getValue().getUserState() == 0) {
            Toast.makeText(requireActivity(), "请先认证", Toast.LENGTH_SHORT).show();
            return;
        }
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

    @Override
    public void onSearchButtonClick(View v, String content) {
        //获取数据源
        if (App.localUser.getValue() == null) {
            Toast.makeText(requireActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.localUser.getValue().getUserState() == 0) {
            Toast.makeText(requireActivity(), "请先认证", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content == null || content.isEmpty()) return;
        int id = Integer.parseInt(content);
        if (id == 0) return;
        mViewModel.openChatUID = id;
        App.mThreadPool.execute(mViewModel.getExtensionByID);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = 0;
        switch (checkedId){
            case R.id.zero_radio_button:id=0;break;
            case R.id.lean_radio:id=1;break;
            case R.id.sport_radio:id=2;break;
            case R.id.game_radio:id=3;break;
        }
        //获取数据源
        if (App.localUser.getValue() == null) {
            Toast.makeText(requireActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (App.localUser.getValue().getUserState() == 0) {
            Toast.makeText(requireActivity(), "请先认证", Toast.LENGTH_SHORT).show();
            return;
        }
        mViewModel.type=extensionTypes[id];
        App.mThreadPool.execute(mViewModel.getExtensionsByType);
        progressBar.setVisibility(View.VISIBLE);
    }

    public static class MyHandler extends Handler{
        WeakReference<ExtensionFragment> extensionFragment;

        MyHandler(ExtensionFragment fragment){
            this.extensionFragment=new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            extensionFragment.get().refreshLayout.setRefreshing(false);
            extensionFragment.get().progressBar.setVisibility(View.INVISIBLE);
            switch (msg.what) {
                case extensionDataChange:
                    if(msg.obj!=null) {
                        List<ExtensionModel> extensionModels = (List<ExtensionModel>) msg.obj;
                        extensionFragment.get().mViewModel.extensions.setValue(extensionModels);
                        extensionFragment.get().mAdapter.SetAllExtension(extensionModels);
                    }

                    extensionFragment.get().mAdapter.notifyDataSetChanged();
                    break;
                case notifyError:
                    String errorMsg = msg.getData().getString("error");
                    if (extensionFragment.get().getContext() != null)
                        Toast.makeText(extensionFragment.get().getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    }
}
