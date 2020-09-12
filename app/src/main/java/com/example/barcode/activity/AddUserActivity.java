package com.example.barcode.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.barcode.object.User;
import com.example.barcode.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar myCalendar = Calendar.getInstance();
    private EditText edtBirthDay, edtName, edtAdress, edtPhoneNumber, edtCMND;
    private DatePickerDialog.OnDateSetListener date;
    private Button btnAddUser;
    private String TAG = "ADD_USER";

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
                User user = new User();
                user.setName(edtName.getText().toString());
                user.setAdress(edtAdress.getText().toString());
                user.setCMND(edtCMND.getText().toString());
                user.setDateOfBirth(myCalendar.getTime());
                user.setPhoneNumber(edtPhoneNumber.getText().toString());

                Log.i(TAG,"id: " + user.getId());


                break;
        }
    }
}