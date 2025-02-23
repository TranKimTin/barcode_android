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
import com.example.barcode.activity.CaptureActivityPortrait;
import com.example.barcode.object.CGV;
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
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.barcode.util.Util.createBarcodeBitmap;

public class DialogCGV extends Dialog implements View.OnClickListener {
    private Button btnSave, btnCancel, btnScanBarcode;
    private EditText edtCMND, edtNumberCGV, edtName, edtDateOfBirth;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar = Calendar.getInstance();
    private ImageView imvCGV;
    private String TAG = "DIALOG_CGV";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+84", "0");
    private ImageView imvBarcode;

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
        btnCancel = (Button) findViewById(R.id.btnCancelCgv);
        btnScanBarcode = (Button) findViewById(R.id.btnScanCgv);
        imvBarcode = (ImageView) findViewById(R.id.imvBarcodeCgv);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);

        btnSave.setOnClickListener(this);
        edtDateOfBirth.setOnClickListener(this);
        imvCGV.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
        imvCGV.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                dialog.show();
                return true;
            }
        });
        db.collection("cgv").document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        setObject(doc.toObject(CGV.class));
                    }
                } else {
                    Log.e(TAG, "lỗi get cgv card");
                }
            }
        });
    }

    public CGV toObject() {
        CGV cgv = new CGV();
        if (edtCMND.getText() != null) cgv.setIdNUmber(edtCMND.getText().toString());
        if (edtName.getText() != null) cgv.setName(edtName.getText().toString());
        if (myCalendar != null) cgv.setDateOfBirth(myCalendar.getTime());
        if (edtNumberCGV.getText() != null) cgv.setNumberCGV(edtNumberCGV.getText().toString());
        cgv.setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/cgv%2F" + phoneNumber + "?alt=media");
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
        if(obj.getNumberCGV() != null && !obj.getNumberCGV().equals("")){
            try {
                imvBarcode.setImageBitmap(createBarcodeBitmap(obj.getNumberCGV(), 1080, 210, BarcodeFormat.CODE_128));
            } catch (WriterException e) {
                Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
            }
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

    public void setCode(String code) {
        if (code == null || code.equals("")) return;
        edtNumberCGV.setText(code);
        try {
            imvBarcode.setImageBitmap(createBarcodeBitmap(code, 1080, 210, BarcodeFormat.CODE_128));
        } catch (WriterException e) {
            Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveCGV:
                if (bytes != null)
                    storageRef.child("cgv").child(phoneNumber).putBytes(bytes);
                db.collection("cgv").document(phoneNumber).set(toObject()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Util.toast(getContext(), "Cập nhật thẻ CGV thành công");
                            dismiss();
                        } else {
                            Log.d(TAG, "Error add cgv card: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getContext(), "Cập nhật thẻ CGV thất bại");
                        Log.e(TAG, e.toString());
                    }
                });
                ;
                dismiss();
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
            case R.id.btnCancelCgv:
                dismiss();
                break;
            case R.id.btnScanCgv:
                //initiating the qr code scan
                IntentIntegrator qrScan = new IntentIntegrator(activity);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt("Quét mã vạch thẻ CGV");
                qrScan.setOrientationLocked(false);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
                break;
        }
    }
}
