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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.barcode.R;
import com.example.barcode.object.IdNumber;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DialogCMND extends Dialog implements View.OnClickListener {
    private Button btnSave, btnCancel;
    private EditText edtCMND, edtName, edtDateOfBirth, edtAdress, edtRegion, edtStartDate, edtAdress2;
    private Spinner spGender;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date2;
    private Calendar myCalendar2 = Calendar.getInstance();
    private ImageView imvCMND;
    private String TAG = "DIALOG_CMND";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+84","0");

    public DialogCMND(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cmnd);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8);

        getWindow().setLayout(width, height);

        storageRef = FirebaseStorage.getInstance().getReference();
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
        btnSave = (Button) findViewById(R.id.btnSaveCmnd);
        spGender = (Spinner) findViewById(R.id.spGender_dialog_cmnd);
        edtAdress = (EditText) findViewById(R.id.edtAdress_dialog_cmnd);
        edtAdress2 = (EditText) findViewById(R.id.edtAdress2_dialog_cmnd);
        edtCMND = (EditText) findViewById(R.id.edtCMND_dialog_cmnd);
        edtDateOfBirth = (EditText) findViewById(R.id.edtBirthday_dialog_cmnd);
        edtName = (EditText) findViewById(R.id.edtName_dialog_cmnd);
        edtRegion = (EditText) findViewById(R.id.edtRegion_dialog_cmnd);
        edtStartDate = (EditText) findViewById(R.id.edtStartDate_dialog_cmnd);
        imvCMND = (ImageView) findViewById(R.id.imvCMND);
        btnCancel = (Button) findViewById(R.id.btnCancel_dialog_cmnd);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.9);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);


        String[] items = new String[]{"Nam", "Nữ"};
        // here you can use array or list
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, items);
        spGender.setAdapter(adapter);

        btnSave.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
        edtStartDate.setOnClickListener(this);
        imvCMND.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imvCMND.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                dialog.show();
                return true;
            }
        });
        db.collection("cmnd").document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null){
                        setObject(doc.toObject(IdNumber.class));
                    }
                }else{
                    Log.e(TAG, "lỗi get cmnd");
                }
            }
        });
    }

    public IdNumber toObject() {
        IdNumber cmnd = new IdNumber();
        if (edtCMND.getText() != null) cmnd.setIdNUmber(edtCMND.getText().toString());
        if (edtName.getText() != null) cmnd.setName(edtName.getText().toString());
        if (myCalendar != null) cmnd.setDateOfBirth(myCalendar.getTime());
        if (myCalendar2 != null) cmnd.setStartDate(myCalendar2.getTime());
        if (edtAdress.getText() != null) cmnd.setAdress(edtAdress.getText().toString());
        if (edtAdress2.getText() != null) cmnd.setAdress2(edtAdress2.getText().toString());
        if (edtRegion.getText() != null) cmnd.setRegion(edtRegion.getText().toString());
        if (spGender.getSelectedItem() != null)
            cmnd.setGender(spGender.getSelectedItem().toString());
        cmnd.setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/cmnd%2F" + phoneNumber + "?alt=media");
        return cmnd;
    }

    public void setObject(IdNumber obj) {
        if (obj == null) return;

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        int pos = obj.getGender().equals("Nam") ? 0 : 1;

        spGender.setSelection(pos);
        edtAdress.setText(obj.getAdress());
        edtAdress2.setText(obj.getAdress2());
        edtCMND.setText(obj.getIdNUmber());
        edtDateOfBirth.setText(sdf.format(obj.getDateOfBirth()));
        edtName.setText(obj.getName());
        edtRegion.setText(obj.getRegion());
        edtStartDate.setText(sdf.format(obj.getStartDate()));

        if (obj.getUrl() != null) {
            Log.d(TAG, obj.getUrl());
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imvCMND);
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imv);
        }
    }

    public void disableView() {
        spGender.setFocusable(false);
        edtAdress.setFocusable(false);
        edtAdress2.setFocusable(false);
        edtCMND.setFocusable(false);
        edtDateOfBirth.setFocusable(false);
        edtName.setFocusable(false);
        edtRegion.setFocusable(false);
        edtStartDate.setFocusable(false);
        edtStartDate.setOnClickListener(null);
        edtDateOfBirth.setOnClickListener(null);
        imvCMND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setUri(Uri uri) {
        imvCMND.setImageURI(uri);
        imv.setImageURI(uri);
    }

    public void setBytes(byte[] b) {
        bytes = b;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveCmnd:
                if (bytes != null) storageRef.child("cmnd").child(phoneNumber).putBytes(bytes);
                db.collection("cmnd").document(phoneNumber).set(toObject()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Util.toast(getContext(), "Cập nhật CMND thành công");
                            dismiss();
                        } else {
                            Log.d(TAG, "Error add CMND: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getContext(), "Update user thất bại");
                        Log.e(TAG, e.toString());
                    }
                });;
                dismiss();
                break;
            case R.id.edtBirthday_dialog_cmnd:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edtStartDate_dialog_cmnd:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imvCMND:
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }
}
