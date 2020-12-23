package com.example.barcode.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.barcode.activity.CaptureActivityPortrait;
import com.example.barcode.object.IdNumber;
import com.example.barcode.object.StudentCard;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static com.example.barcode.util.Util.createBarcodeBitmap;

public class DialogStudentCard extends Dialog implements View.OnClickListener {
    private Button btnSave, btnCancel, btnScanBarcode;
    private EditText edtStudentCode, edtName, edtDateOfBirth, edtMajor, edtClass, edtEndtDate;
    private Spinner spGender;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date2;
    private Calendar myCalendar2 = Calendar.getInstance();
    private ImageView imvStudentCard;
    private String TAG = "DIALOG_STUDENT_CARD";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+84", "0");
    private ImageView imvBarcode;

    public DialogStudentCard(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_student_card);
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

                edtEndtDate.setText(sdf.format(myCalendar2.getTime()));
            }
        };
    }

    void setView() {
        btnSave = (Button) findViewById(R.id.btnSaveStudentCard);
        spGender = (Spinner) findViewById(R.id.spGender_dialog_student_card);
        edtMajor = (EditText) findViewById(R.id.edtMajor_dialog_student_card);
        edtClass = (EditText) findViewById(R.id.edtClass_dialog_student_card);
        edtDateOfBirth = (EditText) findViewById(R.id.edtBirthday_dialog_student_card);
        edtName = (EditText) findViewById(R.id.edtName_dialog_student_card);
        edtEndtDate = (EditText) findViewById(R.id.edtEndDate_dialog_student_card);
        edtStudentCode = (EditText) findViewById(R.id.edtStudentCode_dialog_student_card);
        imvStudentCard = (ImageView) findViewById(R.id.imvStudentCard);
        btnCancel = (Button) findViewById(R.id.btnCancel_dialog_student_card);
        btnScanBarcode = (Button) findViewById(R.id.btnScanStudentBarcode);
        imvBarcode = (ImageView) findViewById(R.id.imgBarCodeStudentCard);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.90);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);


        String[] items = new String[]{"Nam", "Nữ"};
        // here you can use array or list
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, items);
        spGender.setAdapter(adapter);

        btnSave.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
        edtEndtDate.setOnClickListener(this);
        imvStudentCard.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
        imvStudentCard.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                dialog.show();
                return true;
            }
        });
        db.collection("student_card").document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        setObject(doc.toObject(StudentCard.class));
                    }
                } else {
                    Log.e(TAG, "lỗi get student card");
                }
            }
        });

    }

    public StudentCard toObject() {
        StudentCard studentCard = new StudentCard();
        if (edtStudentCode.getText() != null)
            studentCard.setStudentCode(edtStudentCode.getText().toString());
        if (edtName.getText() != null) studentCard.setName(edtName.getText().toString());
        if (myCalendar != null) studentCard.setDateOfBirth(myCalendar.getTime());
        if (myCalendar2 != null) studentCard.setEndtDate(myCalendar2.getTime());
        if (edtMajor.getText() != null) studentCard.setMajor(edtMajor.getText().toString());
        if (edtClass.getText() != null) studentCard.setClasses(edtClass.getText().toString());
        if (spGender.getSelectedItem() != null)
            studentCard.setGender(spGender.getSelectedItem().toString());
        studentCard.setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/student_card%2F" + phoneNumber + "?alt=media");
        return studentCard;
    }

    public void setObject(StudentCard obj) {
        if (obj == null) return;

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        int pos = obj.getGender().equals("Nam") ? 0 : 1;

        spGender.setSelection(pos);
        edtMajor.setText(obj.getMajor());
        edtClass.setText(obj.getClasses());
        edtStudentCode.setText(obj.getStudentCode());
        edtDateOfBirth.setText(sdf.format(obj.getDateOfBirth()));
        edtName.setText(obj.getName());
        edtEndtDate.setText(sdf.format(obj.getEndtDate()));

        if (obj.getUrl() != null) {
            Log.d(TAG, obj.getUrl());
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imvStudentCard);
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imv);
        }
        if(obj.getStudentCode() != null && !obj.getStudentCode().equals("")){
            try {
                imvBarcode.setImageBitmap(createBarcodeBitmap(obj.getStudentCode(), 1080, 210, BarcodeFormat.CODE_128));
            } catch (WriterException e) {
                Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
            }
        }
    }

    public void disableView() {
        spGender.setFocusable(false);
        edtMajor.setFocusable(false);
        edtClass.setFocusable(false);
        edtStudentCode.setFocusable(false);
        edtDateOfBirth.setFocusable(false);
        edtName.setFocusable(false);
        edtEndtDate.setFocusable(false);
        edtEndtDate.setOnClickListener(null);
        edtDateOfBirth.setOnClickListener(null);
        imvStudentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public void setUri(Uri uri) {
        imvStudentCard.setImageURI(uri);
        imv.setImageURI(uri);
    }

    public void setBytes(byte[] b) {
        bytes = b;
    }

    public void setCode(String code) {
        if(code == null || code.equals("")) return;
        edtStudentCode.setText(code);
        try {
            imvBarcode.setImageBitmap(createBarcodeBitmap(code, 1080, 210, BarcodeFormat.CODE_128));
        } catch (WriterException e) {
            Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveStudentCard:
                if (bytes != null)
                    storageRef.child("student_card").child(phoneNumber).putBytes(bytes);
                db.collection("student_card").document(phoneNumber).set(toObject()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Util.toast(getContext(), "Cập nhật thẻ sinh viên thành công");
                            dismiss();
                        } else {
                            Log.d(TAG, "Error add student card: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getContext(), "Cập nhật thẻ sinh viên thất bại");
                        Log.e(TAG, e.toString());
                    }
                });
                ;
                dismiss();
                break;
            case R.id.edtBirthday_dialog_student_card:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edtEndDate_dialog_student_card:
                // if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imvStudentCard:
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);
                break;
            case R.id.btnCancel_dialog_student_card:
                dismiss();
                break;
            case R.id.btnScanStudentBarcode:
                //initiating the qr code scan
                IntentIntegrator qrScan = new IntentIntegrator(activity);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt("Quét mã vạch thẻ sinh viên");
                qrScan.setOrientationLocked(false);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
                break;
        }
    }
}
