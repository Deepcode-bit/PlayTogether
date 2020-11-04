package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import adapter.MAdapter;
import model.ExtensionModel;
import util.App;
import util.Connection;

public class ExtensionListActivity extends AppCompatActivity implements MAdapter.onItemClickListener {

    private RecyclerView extensionList;
    private MAdapter mAdapter;
    private List<ExtensionModel> extensions;
    private String type;
    private ExtensionModel extensionModel;
    private float starNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension_list);
        extensionList=findViewById(R.id.extension_list);
        Init();
    }

    private void Init() {
        mAdapter = new MAdapter();
        mAdapter.SetHeadView(false);
        extensionList.setLayoutManager(new LinearLayoutManager(this));
        extensionList.setAdapter(mAdapter);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        String type = extras.getString("type");
        if (type == null) return;
        this.type = type;
        switch (type) {
            case "over":
                extensions = App.overExtensions;
                break;
            case "create":
                extensions = App.createdExtensions;
                break;
            case "ongoing":
                extensions = App.ongoingExtensions;
                break;
        }
        mAdapter.SetAllExtension(extensions);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, final int Position) {
        if (!type.equals("over")) {
            Toast.makeText(this, "只有结束的活动才能评价", Toast.LENGTH_SHORT).show();
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        final View layout = inflater.inflate(R.layout.evaluation_layout, null);
        final RatingBar ratingBar = layout.findViewById(R.id.rating_bar);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogBackground)
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        extensionModel = extensions.get(Position);
                        starNum = ratingBar.getRating();
                        App.mThreadPool.execute(evaluation);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }

    private Runnable evaluation=new Runnable() {
        @Override
        public void run() {
            JSONObject json = Connection.getJson(App.post, App.netUrl, new HashMap<String, String>(), "/evaluation/commit/" + extensionModel.getID() + "/" + starNum);
            final String msg = json == null ? "评价失败" : "评价成功";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
