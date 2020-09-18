package com.example.barcode.activity;

import androidx.annotation.NonNull;
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
    private ListView lvListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        setView();

        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List listUser = new ArrayList();
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
                intent.putExtra("user", (Parcelable) adapterView.getItemAtPosition(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    private void setView(){
        lvListUser = (ListView) findViewById(R.id.lvListUser);

        registerForContextMenu(lvListUser);
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
        User user = (User) lvListUser.getAdapter().getItem(info.position);
        switch(item.getItemId()) {
            case R.id.view:
                Intent intentView = new Intent(getApplicationContext(), UserActivity.class);
                intentView.putExtra("type", "view");
                intentView.putExtra("user", user);
                startActivity(intentView);
                return true;
            case R.id.edit:
                Intent intentEdit = new Intent(getApplicationContext(), UserActivity.class);
                intentEdit.putExtra("type", "edit");
                intentEdit.putExtra("user", user);
                startActivity(intentEdit);
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
            case R.id.itemCancel:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}