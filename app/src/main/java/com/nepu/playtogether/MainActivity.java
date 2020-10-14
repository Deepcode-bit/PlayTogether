package com.nepu.playtogether;

import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import fragment.LoginFragment;
import fragment.RegisterFragment;

public class MainActivity extends AppCompatActivity {

    private SparseArray<Fragment> fragments;
    public LoginFragment loginFragment;
    public RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    private void InitView(){
        loginFragment=LoginFragment.newInstance();
        registerFragment = RegisterFragment.newInstance();
        RadioGroup loginGroup = findViewById(R.id.login_group);
        fragments = new SparseArray<Fragment>() {};
        fragments.append(R.id.login_but, loginFragment);
        fragments.append(R.id.register_but, registerFragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                loginFragment).commit();
        loginGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragments.get(checkedId)).commit();
            }
        });
    }
}
