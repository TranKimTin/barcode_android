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
import com.example.barcode.object.DriverLicense;
import com.example.barcode.object.IdNumber;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DialogDriverLicense extends Dialog implements View.OnClickListener {
    private Button btnSave;
    private EditText edtNumberDriverLicense, edtName, edtDateOfBirth, edtAdress, edtStartDate, edtAdress2, edtContry;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date2;
    private Calendar myCalendar2 = Calendar.getInstance();
    private ImageView imvDriverLicense;
    private String TAG = "DIALOG_DRIVER_LICENSE";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];

    public DialogDriverLicense(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_driver_license);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8);

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
        date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                edtStartDate.setText(sdf.format(myCalendar2.getTime()));
            }
        };
    }

    void setView() {
        btnSave = (Button) findViewById(R.id.btnSaveDriverLicense);
        edtContry = (EditText) findViewById(R.id.edtContry_dialog_driver_license);
        edtAdress = (EditText) findViewById(R.id.edtAdress_dialog_driver_license);
        edtAdress2 = (EditText) findViewById(R.id.edtAdress2_dialog_driver_license);
        edtNumberDriverLicense = (EditText) findViewById(R.id.edtNumber_dialog_driver_license);
        edtDateOfBirth = (EditText) findViewById(R.id.edtBirthday_dialog_driver_license);
        edtName = (EditText) findViewById(R.id.edtName_dialog_driver_license);
        edtStartDate = (EditText) findViewById(R.id.edtStartDate_dialog_driver_license);
        imvDriverLicense = (ImageView) findViewById(R.id.imvDriverLicense);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.90);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);

        btnSave.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
        edtStartDate.setOnClickListener(this);
        imvDriverLicense.setOnClickListener(this);
    }

    public DriverLicense toObject() {
        DriverLicense driverLicense = new DriverLicense();
        if (edtNumberDriverLicense.getText() != null)
            driverLicense.setNumber(edtNumberDriverLicense.getText().toString());
        if (edtName.getText() != null) driverLicense.setName(edtName.getText().toString());
        if (myCalendar != null) driverLicense.setDateOfBirth(myCalendar.getTime());
        if (myCalendar2 != null) driverLicense.setStartDate(myCalendar2.getTime());
        if (edtAdress.getText() != null) driverLicense.setAdress(edtAdress.getText().toString());
        if (edtAdress2.getText() != null) driverLicense.setAdress2(edtAdress2.getText().toString());
        if (edtContry.getText() != null) driverLicense.setContry(edtContry.getText().toString());

        return driverLicense;
    }

    public void setObject(DriverLicense obj) {
        if (obj == null) return;
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtAdress.setText(obj.getAdress());
        edtAdress2.setText(obj.getAdress2());
        edtNumberDriverLicense.setText(obj.getNumber());
        edtDateOfBirth.setText(sdf.format(obj.getDateOfBirth()));
        edtName.setText(obj.getName());
        edtContry.setText(obj.getContry());
        edtStartDate.setText(sdf.format(obj.getStartDate()));

        if (obj.getUrl() != null) {
            Log.d(TAG, obj.getUrl());
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imvDriverLicense);
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imv);
        }
    }

    public void disableView() {
        edtAdress.setFocusable(false);
        edtAdress2.setFocusable(false);
        edtNumberDriverLicense.setFocusable(false);
        edtDateOfBirth.setFocusable(false);
        edtName.setFocusable(false);
        edtContry.setFocusable(false);
        edtStartDate.setFocusable(false);
        edtStartDate.setOnClickListener(null);
        edtDateOfBirth.setOnClickListener(null);
        imvDriverLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public void setUri(Uri uri) {
        imvDriverLicense.setImageURI(uri);
        imv.setImageURI(uri);
    }

    public void setBytes(byte[] b) {
        bytes = b;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveDriverLicense:
                dismiss();
                break;
            case R.id.edtBirthday_dialog_driver_license:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edtStartDate_dialog_driver_license:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imvDriverLicense:
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);
                break;
        }
    }
}
