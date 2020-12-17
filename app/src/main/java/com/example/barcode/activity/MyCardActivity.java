package com.example.barcode.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.example.barcode.R;
import com.example.barcode.dialog.DialogCGV;
import com.example.barcode.dialog.DialogCMND;
import com.example.barcode.dialog.DialogDriverLicense;
import com.example.barcode.dialog.DialogStudentCard;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCardActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnCmnd, btnDriverLicense, btnStudentCard, btnCGV;
    private DialogCMND dialogCMND;
    private DialogDriverLicense dialogDriverLicense;
    private DialogCGV dialogCGV;
    private DialogStudentCard dialogStudentCard;
    private int indexBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        setView();
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
    }

    void setView() {
        btnCmnd = (Button) findViewById(R.id.btnCmnd);
        btnDriverLicense = (Button) findViewById(R.id.btnDriverLicense);
        btnCGV = (Button) findViewById(R.id.btnCGV);
        btnStudentCard = (Button) findViewById(R.id.btnStudentCard);

        btnCGV.setOnClickListener(this);
        btnDriverLicense.setOnClickListener(this);
        btnStudentCard.setOnClickListener(this);
        btnCmnd.setOnClickListener(this);
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

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
