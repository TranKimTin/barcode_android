package com.example.barcode.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.barcode.R;
import com.example.barcode.object.CGV;
import com.example.barcode.object.IdNumber;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DialogCGV extends Dialog implements View.OnClickListener {
    private Button btnSave;
    private EditText edtCMND, edtNumberCGV, edtName, edtDateOfBirth;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar = Calendar.getInstance();
    private ImageView imvCGV;
    private String TAG = "DIALOG_CGV";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];

    public DialogCGV(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cgv);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.80);

        getWindow().setLayout(width, height);

        setView();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                edtDateOfBirth.setText(sdf.format(myCalendar.getTime()));
            }
        };
    }

    void setView() {
        btnSave = (Button) findViewById(R.id.btnSaveCGV);
        edtCMND = (EditText) findViewById(R.id.edtCMND_dialog_cgv);
        edtDateOfBirth = (EditText) findViewById(R.id.edtBirthday_dialog_cgv);
        edtName = (EditText) findViewById(R.id.edtName_dialog_cgv);
        edtNumberCGV = (EditText) findViewById(R.id.edtNumber_cgv);
        imvCGV = (ImageView) findViewById(R.id.imvCGV);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);

        btnSave.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
        imvCGV.setOnClickListener(this);
    }

    public CGV toObject() {
        CGV cgv = new CGV();
        if (edtCMND.getText() != null) cgv.setIdNUmber(edtCMND.getText().toString());
        if (edtName.getText() != null) cgv.setName(edtName.getText().toString());
        if (myCalendar != null) cgv.setDateOfBirth(myCalendar.getTime());
        if (edtNumberCGV.getText() != null) cgv.setNumberCGV(edtNumberCGV.getText().toString());
        return cgv;
    }

    public void setObject(CGV obj) {
        if (obj == null) return;

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtCMND.setText(obj.getIdNUmber());
        edtDateOfBirth.setText(sdf.format(obj.getDateOfBirth()));
        edtName.setText(obj.getName());
        edtNumberCGV.setText(obj.getNumberCGV());

        if (obj.getUrl() != null) {
            Log.d(TAG, obj.getUrl());
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imvCGV);
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imv);
        }
    }

    public void disableView() {
        edtCMND.setFocusable(false);
        edtDateOfBirth.setFocusable(false);
        edtName.setFocusable(false);
        edtNumberCGV.setFocusable(false);
        edtDateOfBirth.setOnClickListener(null);
        imvCGV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public void setUri(Uri uri) {
        imvCGV.setImageURI(uri);
        imv.setImageURI(uri);
    }

    public void setBytes(byte[] b) {
        bytes = b;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveCGV:
                dismiss();
                break;
            case R.id.edtBirthday_dialog_cgv:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imvCGV:
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);
                break;
        }
    }
}
