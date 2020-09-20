package fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentForumBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.MessageModel;
import adapter.MsgAdapter;
import adapter.MsgAdapter.onItemClickListener;
import adapter.MsgAdapter.onItemDeleteClickListener;
import adapter.MsgAdapter.onItemFirstClickListener;
import view.SlideRecyclerView;
import view_model.HostViewModel;


public class ForumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, onItemClickListener,onItemDeleteClickListener,onItemFirstClickListener {

    private SlideRecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private FragmentForumBinding mBinding;
    private HostViewModel mViewModel;
    private SwipeRefreshLayout refreshLayout;
    private List<MessageModel> list;
    private ActivityOptionsCompat activityOptionsCompat;

    public ForumFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance() {
        return new ForumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding= FragmentForumBinding.inflate(inflater,container,false);
        mViewModel = ViewModelProviders.of(requireActivity()).get(HostViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((HostActivity) getActivity());
        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout=getView().findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView= requireView().findViewById(R.id.recycler_msg);
        msgAdapter=new MsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(msgAdapter);
        msgAdapter.setOnItemClickListener(this);
        msgAdapter.setOnItemDeleteClickListener(this);
        msgAdapter.setOnItemFirstClickListener(this);
        list=new ArrayList<>();
        list.add(new MessageModel("冯帅","你代码怎么还没交",2,"16:30"));
        list.add(new MessageModel("文剑旭","等我一下我打排位",1,"16:50"));
        msgAdapter.setAllMsg(list);
        msgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        addMessage(new MessageModel("海棠","今天有个姑娘来找你",1,"20:50"));
        msgAdapter.setAllMsg(list);
        msgAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(),"点击了"+position,Toast.LENGTH_SHORT).show();
    }

    private void addMessage(MessageModel msg){
        for(int i=0;i<list.size();i++){
            MessageModel message=list.get(i);
            //判断改消息的发送者之前是否存在
            if(msg.getSenderName().equals(message.getSenderName())){
                msg.setUnReadNum(msg.getUnReadNum()+message.getUnReadNum());
                list.remove(i);
                list.add(i,msg);
                return;
            }
        }
        list.add(0,msg);
    }

    @Override
    public void onDeleteClick(View view, int position) {
        list.remove(position);
        msgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFirstClick(View view, int position) {
        MessageModel msg = list.get(position);
        list.remove(msg);
        list.add(0,msg);
        msgAdapter.notifyDataSetChanged();
    }
}
