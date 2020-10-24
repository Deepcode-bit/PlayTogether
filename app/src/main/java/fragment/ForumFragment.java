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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nepu.playtogether.ChatRoomActivity;
import com.nepu.playtogether.HostActivity;
import com.nepu.playtogether.R;
import com.nepu.playtogether.databinding.FragmentForumBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapter.MsgAdapter;
import adapter.MsgAdapter.onItemClickListener;
import adapter.MsgAdapter.onItemDeleteClickListener;
import adapter.MsgAdapter.onItemFirstClickListener;
import model.MessageModel;
import util.App;
import util.TcpClient;
import view.SlideRecyclerView;
import view_model.HostViewModel;


public class ForumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, onItemClickListener,onItemDeleteClickListener,onItemFirstClickListener,TcpClient.MessageReceiveListener {

    private SlideRecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private FragmentForumBinding mBinding;
    private HostViewModel mViewModel;
    private SwipeRefreshLayout refreshLayout;

    public ForumFragment() {
        // Required empty public constructor
    }


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
        mBinding = FragmentForumBinding.inflate(inflater, container, false);
        mViewModel = ViewModelProviders.of(requireActivity()).get(HostViewModel.class);
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((HostActivity) getActivity());
        mViewModel.openChatUID = -1;
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
        msgAdapter.setAllMsg(mViewModel.messages.getValue());
        msgAdapter.notifyDataSetChanged();

        TcpClient.getInstance().setOnMessageReceiveListener(this);
    }

    @Override
    public void onRefresh() {
        msgAdapter.setAllMsg(mViewModel.messages.getValue());
        msgAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        MessageModel msg = mViewModel.messages.getValue().get(position);
        msg.setUnReadNum(0);
        msgAdapter.notifyDataSetChanged();
        bundle.putInt("receiverType", MessageModel.PERSON);
        bundle.putInt("receiverId", msg.getSenderId());
        bundle.putString("title", msg.getSenderName());
        bundle.putString("senderName", App.localUser.getValue().getUserName());
        intent.putExtras(bundle);
        intent.setClass(requireActivity(), ChatRoomActivity.class);
        mViewModel.openChatUID = msg.getSenderId();
        startActivity(intent);
    }


    @Override
    public void onDeleteClick(View view, int position) {
        mViewModel.messages.getValue().remove(position);
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 置顶操作
     * @param view
     * @param position
     */
    @Override
    public void onFirstClick(View view, int position) {
        MessageModel msg = mViewModel.messages.getValue().get(position);
        mViewModel.messages.getValue().remove(msg);
        mViewModel.messages.getValue().add(0,msg);
        msgAdapter.notifyDataSetChanged();
    }

    /**
     * 收到消息的回调函数
     * @param msg
     */
    @Override
    public void onMessageReceive(final MessageModel msg) {
        requireView().post(new Runnable() {
            @Override
            public void run() {
                if(msg.getSendType()!=MessageModel.EXTENSION){
                    msgAdapter.notifyDataSetChanged();
                }else{
                    App.messages.add(msg);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance().removeListener(this);
    }
}
