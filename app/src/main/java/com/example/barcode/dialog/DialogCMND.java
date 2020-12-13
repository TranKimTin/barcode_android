package com.example.barcode.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.barcode.R;

import androidx.annotation.NonNull;

public class DialogCMND extends Dialog implements View.OnClickListener {
    private Button btnCancel, btnSave;
    EditText edtCMND, edtName, edtDateOfBirth, edtAdress, edtRegion, edtStartDate, edtAdress2;
    Spinner spGender;

    public DialogCMND(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cmnd);
        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.90);

        getWindow().setLayout(width, height);

        setView();
    }

    void setView(){
        btnSave = (Button) findViewById(R.id.btnSaveCmnd);
        btnCancel = (Button) findViewById(R.id.btnCancel_dialog_cmnd);
        spGender = (Spinner) findViewById(R.id.spGender_dialog_cmnd);
        edtAdress = (EditText) findViewById(R.id.edtAdress_dialog_cmnd);
        edtAdress2 = (EditText) findViewById(R.id.edtAdress2_dialog_cmnd);
        edtCMND = (EditText) findViewById(R.id.edtCMND_dialog_cmnd);
        edtDateOfBirth = (EditText) findViewById(R.id.edtBirthday_dialog_cmnd);
        edtName = (EditText) findViewById(R.id.edtName_dialog_cmnd);
        edtRegion = (EditText) findViewById(R.id.edtRegion_dialog_cmnd);
        edtStartDate = (EditText) findViewById(R.id.edtStartDate_dialog_cmnd);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSaveCmnd:
                break;
            case R.id.btnCancel_dialog_cmnd:
                dismiss();
                break;
        }
    }
}
