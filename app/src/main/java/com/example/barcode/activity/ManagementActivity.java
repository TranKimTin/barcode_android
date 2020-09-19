package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.adapter.AdapterUser;
import com.example.barcode.object.User;
import com.example.barcode.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ManagementActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView lvListUser;
    private ListenerRegistration listenerUserChange;
    private Button btnSearch, btnPrevios, btnNext;
    private EditText edtSearch, edtPageIndex;
    private int pageSize = 25, pageIndex = 1, count = 0;
    private String textSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        setView();

        search();

        lvListUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("type","view");
                intent.putExtra("user", (Parcelable) adapterView.getItemAtPosition(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSearch:
                textSearch = edtSearch.getText().toString().trim();
                search();
                break;
            case R.id.btnPrevios:
                pageIndex --;
                if(pageIndex<=0) pageIndex = 1;
                edtPageIndex.setText(pageIndex+"");
                search();
                break;
            case R.id.btnNext:
                pageIndex++;
                edtPageIndex.setText(pageIndex+"");
                search();
                break;
        }
    }

    private void setView(){
        lvListUser = (ListView) findViewById(R.id.lvListUser);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        btnPrevios = (Button) findViewById(R.id.btnPrevios);
        btnNext = (Button) findViewById(R.id.btnNext);
        edtPageIndex = (EditText) findViewById(R.id.edtPageIndex);

        registerForContextMenu(lvListUser);
        btnSearch.setOnClickListener(this);
        btnPrevios.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lvListUser) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_long_click_user, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final User user = (User) lvListUser.getAdapter().getItem(info.position);
        switch(item.getItemId()) {
            case R.id.view:
                Intent intentView = new Intent(getApplicationContext(), UserActivity.class);
                intentView.putExtra("type", "view");
                intentView.putExtra("user", user);
                startActivity(intentView);
                return true;
            case R.id.edit:
                db.collection("user").whereEqualTo("id", user.getId()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                Intent intentEdit = new Intent(getApplicationContext(), UserActivity.class);
                                intentEdit.putExtra("type", "edit");
                                intentEdit.putExtra("user", user);
                                intentEdit.putExtra("id_firestore", task.getResult().getDocuments().get(0).getId());
                                startActivity(intentEdit);
                            }
                            else{
                                Util.toast(getApplicationContext(), "User không tồn tại");
                            }

                        }else{
                            Util.toast(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại");
                        }
                    }
                });
                return true;
            case R.id.delete:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_magagement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemAddUser:
                Intent i = new Intent(ManagementActivity.this, UserActivity.class);
                i.putExtra("type","add");
                startActivity(i);
                return true;
            case R.id.itemScanBarcode:
                IntentIntegrator qrScan = new IntentIntegrator(this);
                qrScan.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
                qrScan.setPrompt("Quét mã vạch");
                qrScan.setOrientationLocked(false);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
                return true;
            case R.id.itemCancel:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                                i.putExtra("type","edit");
                                i.putExtra("user", u);
                                i.putExtra("id_firestore", snap.getDocuments().get(0).getId());
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

    private void search(){
        textSearch = textSearch.toLowerCase();
        if(listenerUserChange != null) listenerUserChange.remove();
        listenerUserChange = db.collection("user").whereArrayContains("subName", textSearch).orderBy("name").limit(pageSize).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Util.logE("Listen failed.", error);
                    Util.toast(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại");
                    return;
                }
                List<User> listUser = new ArrayList<User>();
                for(DocumentSnapshot doc : value){
                    User u = doc.toObject(User.class);
                    listUser.add(u);
                }
                AdapterUser adapterUser = new AdapterUser(getApplicationContext(),R.layout.item_user, listUser);
                lvListUser.setAdapter(adapterUser);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerUserChange.remove();
//        listenerUserChange = null;
    }
}