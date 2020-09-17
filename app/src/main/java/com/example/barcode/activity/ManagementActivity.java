package com.example.barcode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.barcode.R;
import com.example.barcode.adapter.AdapterUser;
import com.example.barcode.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManagementActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnAddUser;
    private List listUser;
    private ListView lvListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        lvListUser = (ListView) findViewById(R.id.lvListUser);
        btnAddUser.setOnClickListener(this);

        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listUser = new ArrayList();
                QuerySnapshot snap = task.getResult();
                for(DocumentSnapshot doc : snap){
                    User u = doc.toObject(User.class);
                    listUser.add(u);
                }
                AdapterUser adapterUser = new AdapterUser(getApplicationContext(),R.layout.item_user, listUser);
                lvListUser.setAdapter(adapterUser);
            }
        });

        lvListUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("type","view");
                intent.putExtra("user", (Parcelable) adapterView.getAdapter().getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddUser:
                Intent i = new Intent(ManagementActivity.this, UserActivity.class);
                i.putExtra("type","add");
                startActivity(i);
                break;
        }
    }
}