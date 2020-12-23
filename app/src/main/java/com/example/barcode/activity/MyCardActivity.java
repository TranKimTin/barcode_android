package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.example.barcode.R;
import com.example.barcode.dialog.DialogBigC;
import com.example.barcode.dialog.DialogCGV;
import com.example.barcode.dialog.DialogCMND;
import com.example.barcode.dialog.DialogDriverLicense;
import com.example.barcode.dialog.DialogStudentCard;
import com.example.barcode.object.User;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCardActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCmnd, btnDriverLicense, btnStudentCard, btnCGV, btnBigCCard;
    private DialogCMND dialogCMND;
    private DialogDriverLicense dialogDriverLicense;
    private DialogCGV dialogCGV;
    private DialogStudentCard dialogStudentCard;
    private DialogBigC dialogBigC;
    private int indexBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        setView();
        initDialog();
    }

    void initDialog(){
        dialogCMND = new DialogCMND(MyCardActivity.this, MyCardActivity.this);
        dialogCMND.show();
        dialogCMND.dismiss();
        dialogDriverLicense = new DialogDriverLicense(MyCardActivity.this, MyCardActivity.this);
        dialogDriverLicense.show();
        dialogDriverLicense.dismiss();
        dialogCGV = new DialogCGV(MyCardActivity.this, MyCardActivity.this);
        dialogCGV.show();
        dialogCGV.dismiss();
        dialogStudentCard = new DialogStudentCard(MyCardActivity.this, MyCardActivity.this);
        dialogStudentCard.show();
        dialogStudentCard.dismiss();
        dialogBigC = new DialogBigC(MyCardActivity.this, MyCardActivity.this);
        dialogBigC.show();
        dialogBigC.dismiss();
    }

    void setView() {
        btnCmnd = (Button) findViewById(R.id.btnCmnd);
        btnDriverLicense = (Button) findViewById(R.id.btnDriverLicense);
        btnCGV = (Button) findViewById(R.id.btnCGV);
        btnStudentCard = (Button) findViewById(R.id.btnStudentCard);
        btnBigCCard = (Button) findViewById(R.id.btnBigCCard);

        btnCGV.setOnClickListener(this);
        btnDriverLicense.setOnClickListener(this);
        btnStudentCard.setOnClickListener(this);
        btnCmnd.setOnClickListener(this);
        btnBigCCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCmnd:
                indexBytes = 0;
                dialogCMND.show();
                break;
            case R.id.btnDriverLicense:
                indexBytes = 1;
                dialogDriverLicense.show();
                break;
            case R.id.btnCGV:
                indexBytes = 2;
                dialogCGV.show();
                break;
            case R.id.btnStudentCard:
                indexBytes = 3;
                dialogStudentCard.show();
                break;
            case R.id.btnBigCCard:
                indexBytes=4;
                dialogBigC.show();
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
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //here you can choose quality factor in third parameter(ex. i choosen 25)
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);

                if (indexBytes == 0) {
                    dialogCMND.setUri(resultUri);
                    dialogCMND.setBytes(baos.toByteArray());
                }
                if (indexBytes == 1) {
                    dialogDriverLicense.setUri(resultUri);
                    dialogDriverLicense.setBytes(baos.toByteArray());
                }
                if (indexBytes == 2) {
                    dialogCGV.setUri(resultUri);
                    dialogCGV.setBytes(baos.toByteArray());
                }
                if (indexBytes == 3) {
                    dialogStudentCard.setUri(resultUri);
                    dialogStudentCard.setBytes(baos.toByteArray());
                }
                if (indexBytes == 4) {
                    dialogBigC.setUri(resultUri);
                    dialogBigC.setBytes(baos.toByteArray());
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else{
            //bar code
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                //if qrcode has nothing in it
                if (result.getContents() == null) {
                    Util.toast(this, "Không phát hiện mã");
                } else {
                    String code = result.getContents();
                    if(indexBytes != 0) Util.toast(getApplicationContext(), code);
                    if(indexBytes == 0){
                        dialogCMND.setCode(code);
                    }
                    if(indexBytes == 2){
                        dialogCGV.setCode(code);
                    }
                    if(indexBytes == 3){
                        dialogStudentCard.setCode(code);
                    }
                    if(indexBytes == 4){
                        dialogBigC.setCode(code);
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
