package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.object.User;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonScan, btnQuanLyThongTin, btnMyCard, btnLogout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        setview();
    }

    void setview() {
        buttonScan = (Button) findViewById(R.id.buttonScan);
        btnQuanLyThongTin = (Button) findViewById(R.id.btnQuanLyThongTin);
        btnMyCard = (Button) findViewById(R.id.btnMyCard);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogout.setText("Logout (" + phoneNumber.replace("+84","0") + ")");

        buttonScan.setOnClickListener(this);
        btnMyCard.setOnClickListener(this);
        btnQuanLyThongTin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonScan:
                //initiating the qr code scan
                IntentIntegrator qrScan = new IntentIntegrator(this);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
                qrScan.setPrompt("Quét mã vạch");
                qrScan.setOrientationLocked(false);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
                break;
            case R.id.btnQuanLyThongTin:
                startActivity(new Intent(MainActivity.this, ManagementActivity.class));
                break;
            case R.id.btnMyCard:
                startActivity(new Intent(MainActivity.this, MyCardActivity.class));
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Util.toast(this, "Không phát hiện mã");
            } else {
                String id = result.getContents();
                Util.toast(getApplicationContext(), id);
                db.collection("user").whereEqualTo("id", id).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snap = task.getResult();
                            if (snap.isEmpty()) {
                                Util.toast(getApplicationContext(), "Không tìm thấy user");
                            } else {
                                User u = snap.getDocuments().get(0).toObject(User.class);
                                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                i.putExtra("type", "view");
                                i.putExtra("user", u);
                                startActivity(i);
                            }

                        } else {
                            Util.toast(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại");
                        }
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}