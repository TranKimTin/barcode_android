package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.barcode.object.User;
import com.example.barcode.R;
import com.example.barcode.util.DB;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar myCalendar = Calendar.getInstance();
    private EditText edtBirthDay, edtName, edtAdress, edtPhoneNumber, edtCMND;
    private DatePickerDialog.OnDateSetListener date;
    private Button btnAddUser;
    private String TAG = "ADD_USER";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        edtName = (EditText) findViewById(R.id.edtName);
        edtBirthDay = (EditText) findViewById(R.id.edtBirthday);
        edtAdress = (EditText) findViewById(R.id.edtAdress);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtCMND = (EditText) findViewById(R.id.edtCMND);
        btnAddUser = (Button) findViewById(R.id.btnAddUser);

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
                // TODO Auto-generated method stub
                new DatePickerDialog(AddUserActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnAddUser:
                final User u = new User();
                u.setName(edtName.getText().toString());
                u.setAdress(edtAdress.getText().toString());
                u.setCMND(edtCMND.getText().toString());
                u.setDateOfBirth(myCalendar.getTime());
                u.setPhoneNumber(edtPhoneNumber.getText().toString());

                Log.i(TAG,"id: " + u.getId());
//                DB.addUser(getApplicationContext(), user);
                db.collection("user").whereEqualTo("id", u.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot doc = task.getResult();
                            if(doc.isEmpty()){
                                db.collection("user").add(u).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
                                Util.toast(getApplicationContext(), "Đã tồn tại user với số CMND " + u.getCMND());
                            }

                        }else{
                            Log.e(TAG,"lỗi get user");
                        }
                    }
                });
                break;
        }
    }
}