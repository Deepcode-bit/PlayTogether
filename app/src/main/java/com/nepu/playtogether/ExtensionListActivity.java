package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import adapter.MAdapter;
import model.ExtensionModel;
import util.App;

public class ExtensionListActivity extends AppCompatActivity {

    private RecyclerView extensionList;
    private MAdapter mAdapter;
    private List<ExtensionModel> extensions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension_list);
        extensionList=findViewById(R.id.extension_list);
        Init();
    }

    private void Init(){
        mAdapter=new MAdapter();
        mAdapter.SetHeadView(false);
        extensionList.setLayoutManager(new LinearLayoutManager(this));
        extensionList.setAdapter(mAdapter);
        Intent intent=getIntent();
        Bundle extras = intent.getExtras();
        if(extras==null)return;
        String type = extras.getString("type");
        if(type==null)return;
        switch (type){
            case "join":extensions= App.joinExtensions; break;
            case "create":extensions=App.createdExtensions; break;
            case "ongoing":extensions=App.ongoingExtensions; break;
        }
        mAdapter.SetAllExtension(extensions);
        mAdapter.notifyDataSetChanged();
    }
}
