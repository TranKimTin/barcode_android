package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.barcode.object.User;
import com.example.barcode.R;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar myCalendar = Calendar.getInstance();
    private EditText edtBirthDay, edtName, edtAdress, edtPhoneNumber, edtCMND;
    private DatePickerDialog.OnDateSetListener date;
    private Button btnAddUser;
    private final String TAG = "ADD_USER";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private String type;
    private ImageView imgBarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        type = getIntent().getStringExtra("type");
        if(type.equals("view")){
            user = (User) getIntent().getExtras().getParcelable("user");
            Util.toast(getApplicationContext(), user.getName());
        }
        else if(type.equals("edit")){
            user = (User) getIntent().getExtras().getParcelable("user");
        }
        else if(type.equals("add")){
            user = new User();
        }
        setView();

        myCalendar.set(2000,1,1);
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

        edtBirthDay.setOnClickListener(this);
        btnAddUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edtBirthday:
                if(type.equals("view")) break;
                // TODO Auto-generated method stub
                new DatePickerDialog(UserActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnAddUser:
                user.setName(edtName.getText().toString());
                user.setAdress(edtAdress.getText().toString());
                user.setCMND(edtCMND.getText().toString());
                user.setDateOfBirth(myCalendar.getTime());
                user.setPhoneNumber(edtPhoneNumber.getText().toString());

                Log.i(TAG,"id: " + user.getId());
//                DB.addUser(getApplicationContext(), user);
                db.collection("user").whereEqualTo("id", user.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot doc = task.getResult();
                            if(doc.isEmpty()){
                                db.collection("user").add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()) {
                                            Util.toast(getApplicationContext(), "Thêm user thành công");
                                            finish();
                                        }
                                        else{
                                            Log.d(TAG, "Error add user: ", task.getException());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Util.toast(getApplicationContext(), "Thêm thất bại");
                                    }
                                });
                            }
                            else{
                                Util.toast(getApplicationContext(), "Đã tồn tại user với số CMND " + user.getCMND());
                            }

                        }else{
                            Log.e(TAG,"lỗi get user");
                        }
                    }
                });
                break;
        }
    }

    private void setView(){
        edtName = (EditText) findViewById(R.id.edtName);
        edtBirthDay = (EditText) findViewById(R.id.edtBirthday);
        edtAdress = (EditText) findViewById(R.id.edtAdress);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtCMND = (EditText) findViewById(R.id.edtCMND);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        imgBarCode = (ImageView) findViewById(R.id.imgBarCode);

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtName.setText(user.getName());
        edtBirthDay.setText(sdf.format(user.getDateOfBirth().getTime()));
        edtAdress.setText(user.getAdress());
        edtPhoneNumber.setText(user.getPhoneNumber());
        edtCMND.setText(user.getCMND());

        if(type.equals("view")){
            try {
                imgBarCode.setImageBitmap(createBarcodeBitmap(user.getId(), 1080, 210));
            } catch (WriterException e) {
                Util.logE("Lỗi tạo mã " + e.getLocalizedMessage());
            }
            edtCMND.setFocusable(false);
            edtPhoneNumber.setFocusable(false);
            edtAdress.setFocusable(false);
            edtBirthDay.setFocusable(false);
            edtName.setFocusable(false);

        }
    }
    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
    }

}