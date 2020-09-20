package com.nepu.playtogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.nepu.playtogether.databinding.ActivityExtensionBinding;

import java.util.ArrayList;
import java.util.List;

import adapter.MemberAdapter;
import model.ExtensionModel;
import model.Member;
import util.App;
import view_model.LoginViewModel;

public class ExtensionActivity extends AppCompatActivity {

    private RecyclerView memberRecycler;
    private MemberAdapter memberAdapter;
    private List<Member> members;
    //此数据作为ViewModel
    public MutableLiveData<ExtensionModel> extension;

    ActivityExtensionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_extension);
        mBinding.setData(this);
        mBinding.setLifecycleOwner(this);
        Init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void Init(){
        //初始化头部背景
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        extension=new MutableLiveData<>();
        extension.setValue((ExtensionModel) bundle.getSerializable("extension"));
        if(extension!=null) {
            int type = extension.getValue().getType();
            findViewById(R.id.head_layout).setBackgroundResource(App.getExtensionDrawable(type));
        }
        members=new ArrayList<>();
        memberAdapter=new MemberAdapter();
        memberRecycler=findViewById(R.id.member_list);
        memberRecycler.setLayoutManager(new LinearLayoutManager(this));
        memberRecycler.setAdapter(memberAdapter);

        for(int i=0;i<10;i++)
        members.add(new Member(1,"asd","张三","asdsad"));
        memberAdapter.setMembers(members);
        memberAdapter.notifyDataSetChanged();
    }
}
