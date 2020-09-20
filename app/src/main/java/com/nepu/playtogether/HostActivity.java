package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import fragment.ExtensionFragment;
import fragment.ForumFragment;
import fragment.PersonFragment;

public class HostActivity extends AppCompatActivity {

    RadioGroup bottomNav;
    private SparseArray<Fragment> fragments;
    public ExtensionFragment extensionFragment;
    public ForumFragment forumFragment;
    public PersonFragment personFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        InitView();
    }

    private void InitView(){
        bottomNav=findViewById(R.id.radiogroup);
        extensionFragment=ExtensionFragment.newInstance();
        forumFragment=ForumFragment.newInstance();
        personFragment=PersonFragment.newInstance();
        fragments = new SparseArray<Fragment>() {};
        fragments.append(R.id.radio1, extensionFragment);
        fragments.append(R.id.radio2, forumFragment);
        fragments.append(R.id.radio3, personFragment);
        bottomNav.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId>0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                            fragments.get(checkedId)).commit();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                extensionFragment).commit();
    }
}
