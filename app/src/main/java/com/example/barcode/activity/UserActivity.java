package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barcode.dialog.DialogCGV;
import com.example.barcode.dialog.DialogCMND;
import com.example.barcode.dialog.DialogDriverLicense;
import com.example.barcode.dialog.DialogStudentCard;
import com.example.barcode.object.User;
import com.example.barcode.R;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.barcode.util.Util.createBarcodeBitmap;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar myCalendar = Calendar.getInstance();
    private EditText edtBirthDay, edtName, edtAdress, edtPhoneNumber, edtCMND;
    private DatePickerDialog.OnDateSetListener date;
    private Button btnSaveUser, btnCancel, btnCmnd, btnDriverLicense, btnStudentCard, btnCGV;
    private final String TAG = "ADD_USER";
    private User user;
    private String type, idFirestore;
    private ImageView imgBarCode;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<byte[]> bytes;
    private int indexBytes;
    private DialogCMND dialogCMND;
    private DialogDriverLicense dialogDriverLicense;
    private DialogCGV dialogCGV;
    private DialogStudentCard dialogStudentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bytes = new ArrayList<byte[]>();
        for (int i = 0; i < 4; i++) bytes.add(null);

        storageRef = FirebaseStorage.getInstance().getReference();
        dialogCMND = new DialogCMND(UserActivity.this, UserActivity.this);
        dialogCMND.show();
        dialogCMND.dismiss();
        dialogDriverLicense = new DialogDriverLicense(UserActivity.this, UserActivity.this);
        dialogDriverLicense.show();
        dialogDriverLicense.dismiss();
        dialogCGV = new DialogCGV(UserActivity.this, UserActivity.this);
        dialogCGV.show();
        dialogCGV.dismiss();
        dialogStudentCard = new DialogStudentCard(UserActivity.this, UserActivity.this);
        dialogStudentCard.show();
        dialogStudentCard.dismiss();

        type = getIntent().getStringExtra("type");
        if (type.equals("view")) {
            user = (User) getIntent().getExtras().getParcelable("user");
//            Util.toast(getApplicationContext(), user.getName());
        } else if (type.equals("edit")) {
            user = (User) getIntent().getExtras().getParcelable("user");
            idFirestore = getIntent().getStringExtra("id_firestore");
        } else if (type.equals("add")) {
            user = new User();
        }
        setView();

        if (type.equals("add")) myCalendar.set(2000, 1, 1);
        else myCalendar.setTime(user.getDateOfBirth());
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

                edtBirthDay.setText(sdf.format(myCalendar.getTime()));
            }
        };

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtBirthday:
                if (type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(UserActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnSaveUser:
                user.setName(edtName.getText().toString());
                user.setAdress(edtAdress.getText().toString());
                user.setCMND(edtCMND.getText().toString());
                user.setDateOfBirth(myCalendar.getTime());
                user.setPhoneNumber(edtPhoneNumber.getText().toString());
                user.setIdNumber(dialogCMND.toObject());
                user.setDriverLicense(dialogDriverLicense.toObject());
                user.setCgv(dialogCGV.toObject());
                user.setStudentCard(dialogStudentCard.toObject());
                user.getIdNumber().setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/cmnd%2F" + user.getCMND() + "?alt=media");
                user.getDriverLicense().setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/driver_license%2F" + user.getCMND() + "?alt=media");
                user.getCgv().setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/cgv%2F" + user.getCMND() + "?alt=media");
                user.getStudentCard().setUrl("https://firebasestorage.googleapis.com/v0/b/barcode-57c5e.appspot.com/o/student_card%2F" + user.getCMND() + "?alt=media");

                if (user.getCMND().trim().equals("")) {
                    Util.toast(getApplicationContext(), "Số CMND không được để trống");
                    return;
                }
                Log.i(TAG, "id: " + user.getId());
                db.collection("user").whereEqualTo("id", user.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot doc = task.getResult();
                            if (type.equals("add")) {
                                if (doc.isEmpty()) {
                                    if (bytes.get(0) != null) {
                                        storageRef.child("cmnd").child(user.getCMND()).putBytes(bytes.get(0));
                                    }
                                    if (bytes.get(1) != null) {
                                        storageRef.child("driver_license").child(user.getCMND()).putBytes(bytes.get(1));
                                    }
                                    if (bytes.get(2) != null) {
                                        storageRef.child("cgv").child(user.getCMND()).putBytes(bytes.get(2));
                                    }
                                    if (bytes.get(3) != null) {
                                        storageRef.child("student_card").child(user.getCMND()).putBytes(bytes.get(3));
                                    }
                                    db.collection("user").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Util.toast(getApplicationContext(), "Thêm user thành công");
                                                finish();
                                            } else {
                                                Log.d(TAG, "Error add user: ", task.getException());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Util.toast(getApplicationContext(), "Thêm thất bại");
                                        }
                                    });
                                } else {
                                    Util.toast(getApplicationContext(), "Đã tồn tại user với số CMND " + user.getCMND());
                                }
                            } else if (type.equals("edit")) {
                                if (doc.isEmpty() || doc.getDocuments().get(0).getId().equals(idFirestore)) {
                                    if (bytes.get(0) != null) {
                                        storageRef.child("cmnd").child(user.getCMND()).putBytes(bytes.get(0));
                                    }
                                    if (bytes.get(1) != null) {
                                        storageRef.child("driver_license").child(user.getCMND()).putBytes(bytes.get(1));
                                    }
                                    if (bytes.get(2) != null) {
                                        storageRef.child("cgv").child(user.getCMND()).putBytes(bytes.get(2));
                                    }
                                    if (bytes.get(3) != null) {
                                        storageRef.child("student_card").child(user.getCMND()).putBytes(bytes.get(3));
                                    }
                                    db.collection("user").document(idFirestore).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Util.toast(getApplicationContext(), "Sửa thành công");
                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Util.toast(getApplicationContext(), "Có lỗi xảy ra khi sửa user");
                                                }
                                            });
                                } else {
                                    Util.toast(getApplicationContext(), "Đã tồn tại user khác với số CMND " + user.getCMND());
                                }
                            }


                        } else {
                            Log.e(TAG, "lỗi get user");
                        }
                    }
                });
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnCmnd:
                indexBytes = 0;
                //   Toast.makeText(this,"asd",Toast.LENGTH_LONG).show();
                dialogCMND.show();
                if (type.equals("view") || type.equals("edit")) {
                    dialogCMND.setObject(user.getIdNumber());
                }
                if (type.equals("view")) {
                    dialogCMND.disableView();
                }
                dialogCMND.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        user.setIdNumber(dialogCMND.toObject());
                    }
                });
                break;
            case R.id.btnDriverLicense:
                indexBytes = 1;
                dialogDriverLicense.show();
                if (type.equals("view") || type.equals("edit")) {
                    dialogDriverLicense.setObject(user.getDriverLicense());
                }
                if (type.equals("view")) {
                    dialogDriverLicense.disableView();
                }
                dialogDriverLicense.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        user.setDriverLicense(dialogDriverLicense.toObject());
                    }
                });
                break;
            case R.id.btnCGV:
                indexBytes = 2;
                dialogCGV.show();
                if (type.equals("view") || type.equals("edit")) {
                    dialogCGV.setObject(user.getCgv());
                }
                if (type.equals("view")) {
                    dialogCGV.disableView();
                }
                dialogCGV.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        user.setCgv(dialogCGV.toObject());
                    }
                });
                break;
            case R.id.btnStudentCard:
                indexBytes = 3;
                dialogStudentCard.show();
                if (type.equals("view") || type.equals("edit")) {
                    dialogStudentCard.setObject(user.getStudentCard());
                }
                if (type.equals("view")) {
                    dialogStudentCard.disableView();
                }
                dialogStudentCard.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        user.setStudentCard(dialogStudentCard.toObject());
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if (indexBytes == 0) dialogCMND.setUri(resultUri);
                if (indexBytes == 1) dialogDriverLicense.setUri(resultUri);
                if (indexBytes == 2) dialogCGV.setUri(resultUri);
                if (indexBytes == 3) dialogStudentCard.setUri(resultUri);
                ////////////////////////////
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //here you can choose quality factor in third parameter(ex. i choosen 25)
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                bytes.set(indexBytes, baos.toByteArray());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setView() {
        //find view
        edtName = (EditText) findViewById(R.id.edtName);
        edtBirthDay = (EditText) findViewById(R.id.edtBirthday);
        edtAdress = (EditText) findViewById(R.id.edtAdress);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtCMND = (EditText) findViewById(R.id.edtCMND);
        btnSaveUser = (Button) findViewById(R.id.btnSaveUser);
        imgBarCode = (ImageView) findViewById(R.id.imgBarCode);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCmnd = (Button) findViewById(R.id.btnCmnd);
        btnCGV = (Button) findViewById(R.id.btnCGV);
        btnDriverLicense = (Button) findViewById(R.id.btnDriverLicense);
        btnStudentCard = (Button) findViewById(R.id.btnStudentCard);

        //set click button
        edtBirthDay.setOnClickListener(this);
        btnSaveUser.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCmnd.setOnClickListener(this);
        btnStudentCard.setOnClickListener(this);
        btnDriverLicense.setOnClickListener(this);
        btnCGV.setOnClickListener(this);

        //set text to view
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtName.setText(user.getName());
        edtBirthDay.setText(sdf.format(user.getDateOfBirth().getTime()));
        edtAdress.setText(user.getAdress());
        edtPhoneNumber.setText(user.getPhoneNumber());
        edtCMND.setText(user.getCMND());

        if (type.equals("view")) {
            try {
                imgBarCode.setImageBitmap(createBarcodeBitmap(user.getId(), 1080, 210, BarcodeFormat.CODE_128));
            } catch (WriterException e) {
                Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
            }
            edtCMND.setFocusable(false);
            edtPhoneNumber.setFocusable(false);
            edtAdress.setFocusable(false);
            edtBirthDay.setFocusable(false);
            edtName.setFocusable(false);
            btnSaveUser.setVisibility(View.GONE);

        }
        if (type.equals("edit")) {
            btnSaveUser.setText("Sửa");
        }
    }

}