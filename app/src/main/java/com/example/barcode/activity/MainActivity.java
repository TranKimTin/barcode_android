package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import com.example.barcode.R;
import com.example.barcode.object.User;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonScan, btnQuanLyThongTin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        btnQuanLyThongTin = (Button) findViewById(R.id.btnQuanLyThongTin);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

        btnQuanLyThongTin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                db.collection("user").whereEqualTo("id", id).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snap = task.getResult();
                            if(snap.isEmpty()){
                                Util.toast(getApplicationContext(), "Không tìm thấy user");
                            }
                            else{
                                User u = snap.getDocuments().get(0).toObject(User.class);
                                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                i.putExtra("type","view");
                                i.putExtra("user", u);
                                startActivity(i);
                            }

                        }
                        else {
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