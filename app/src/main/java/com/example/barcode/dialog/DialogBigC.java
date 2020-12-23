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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.barcode.R;
import com.example.barcode.activity.CaptureActivityPortrait;
import com.example.barcode.object.BigC;
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

public class DialogBigC extends Dialog implements View.OnClickListener {
    private Button btnSave, btnCancel, btnScanBarcode;
    private EditText edtNumberBigC, edtName;
    private ImageView imvBigC;
    private String TAG = "DIALOG_BigC";
    private Activity activity;
    private Dialog dialog;
    private ImageView imv;
    private byte bytes[];
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+84", "0");
    private ImageView imvBarcode;

    public DialogBigC(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_bigc);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.80);

        getWindow().setLayout(width, height);

        setView();
    }

    void setView() {
        btnSave = (Button) findViewById(R.id.btnSaveBigC);
        edtName = (EditText) findViewById(R.id.edtName_dialog_bigC);
        edtNumberBigC = (EditText) findViewById(R.id.edtNumber_BigC);
        imvBigC = (ImageView) findViewById(R.id.imvBigC);
        btnCancel = (Button) findViewById(R.id.btnCancelBigC);
        btnScanBarcode = (Button) findViewById(R.id.btnScanBigC);
        imvBarcode = (ImageView) findViewById(R.id.imvBarcodeBigC);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.imageview);
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 1);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.8);

        dialog.getWindow().setLayout(width, height);

        imv = (ImageView) dialog.findViewById(R.id.imv);

        btnSave.setOnClickListener(this);
        imvBigC.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
        imvBigC.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                dialog.show();
                return true;
            }
        });
        db.collection("bigc").document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        setObject(doc.toObject(BigC.class));
                    }
                } else {
                    Log.e(TAG, "lỗi get bigC card");
                }
            }
        });
    }

    public BigC toObject() {
        BigC bigC = new BigC();
        if (edtName.getText() != null) bigC.setName(edtName.getText().toString());
        if (edtNumberBigC.getText() != null) bigC.setNumberBigC(edtNumberBigC.getText().toString());
        bigC.setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/bigc%2F" + phoneNumber + "?alt=media");
        return bigC;
    }

    public void setObject(BigC obj) {
        if (obj == null) return;

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtName.setText(obj.getName());
        edtNumberBigC.setText(obj.getNumberBigC());

        if (obj.getUrl() != null) {
            Log.d(TAG, obj.getUrl());
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imvBigC);
            Glide.with(getContext())
                    .load(obj.getUrl())
                    .into(imv);
        }
        if(obj.getNumberBigC() != null && !obj.getNumberBigC().equals("")){
            try {
                imvBarcode.setImageBitmap(createBarcodeBitmap(obj.getNumberBigC(), 1080, 210, BarcodeFormat.EAN_13));
            } catch (WriterException e) {
                Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
            }
        }
    }

    public void disableView() {
        edtName.setFocusable(false);
        edtNumberBigC.setFocusable(false);
        imvBigC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    public void setUri(Uri uri) {
        imvBigC.setImageURI(uri);
        imv.setImageURI(uri);
    }

    public void setBytes(byte[] b) {
        bytes = b;
    }
    public void setCode(String code) {
        if(code == null || code.equals("")) return;
        edtNumberBigC.setText(code);
        try {
            imvBarcode.setImageBitmap(createBarcodeBitmap(code, 1080, 210, BarcodeFormat.EAN_13));
        } catch (WriterException e) {
            Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveBigC:
                if (bytes != null)
                    storageRef.child("bigc").child(phoneNumber).putBytes(bytes);
                db.collection("bigc").document(phoneNumber).set(toObject()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Util.toast(getContext(), "Cập nhật thẻ bigC thành công");
                            dismiss();
                        } else {
                            Log.d(TAG, "Error add bigC card: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getContext(), "Cập nhật thẻ bigC thất bại");
                        Log.e(TAG, e.toString());
                    }
                });
                ;
                dismiss();
            case R.id.imvBigC:
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);
                break;
            case R.id.btnCancelBigC:
                dismiss();
                break;
            case R.id.btnScanBigC:
                //initiating the qr code scan
                IntentIntegrator qrScan = new IntentIntegrator(activity);
//                qrScan.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                qrScan.setPrompt("Quét mã vạch thẻ bigC");
                qrScan.setOrientationLocked(false);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
                break;
        }
    }
}
