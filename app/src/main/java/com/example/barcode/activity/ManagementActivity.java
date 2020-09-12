package com.example.barcode.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.barcode.R;

public class ManagementActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        btnAddUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddUser:
                Intent i = new Intent(ManagementActivity.this, AddUserActivity.class);
                i.putExtra("type","add");
                startActivity(i);
                break;
        }
    }
}