package com.nepu.playtogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

public class PublicActivity extends AppCompatActivity {

    private NavController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        controller= Navigation.findNavController(this,R.id.fragment);
    }
}
