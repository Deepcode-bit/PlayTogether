package fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapter.MsgAdapter;
import adapter.MsgAdapter.onItemClickListener;
import adapter.MsgAdapter.onItemDeleteClickListener;
import adapter.MsgAdapter.onItemFirstClickListener;
import model.MessageModel;
import util.App;
import util.Connection;
import util.HandlerMsg;
import util.TcpClient;
import view.SlideRecyclerView;
import view_model.HostViewModel;


public class ForumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, onItemClickListener,onItemDeleteClickListener,onItemFirstClickListener {

    private SlideRecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private FragmentForumBinding mBinding;
    public HostViewModel mViewModel;
    private SwipeRefreshLayout refreshLayout;
    public static MyHandler handler;

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
        mViewModel.openChatUID = -1;
        mBinding.setLifecycleOwner(getActivity());
        mBinding.setData((HostActivity) getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout = getView().findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = requireView().findViewById(R.id.recycler_msg);
        msgAdapter = new MsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(msgAdapter);
        msgAdapter.setOnItemClickListener(this);
        msgAdapter.setOnItemDeleteClickListener(this);
        msgAdapter.setOnItemFirstClickListener(this);
        msgAdapter.setAllMsg(mViewModel.messages.getValue());
        msgAdapter.notifyDataSetChanged();
        handler = new MyHandler(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.openChatUID = -1;
    }

    public static class MyHandler extends Handler{
        public static final int UpdateView=0x001;
        private WeakReference<ForumFragment> forumFragment;

        public MyHandler(ForumFragment fragment){
            forumFragment=new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==UpdateView){
                forumFragment.get().msgAdapter.notifyDataSetChanged();
            }
        }
    }
}
