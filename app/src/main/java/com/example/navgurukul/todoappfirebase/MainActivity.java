package com.example.navgurukul.todoappfirebase;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.navgurukul.todoappfirebase.Adapter.listItemAdapter;
import com.example.navgurukul.todoappfirebase.Model.Todo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    List<Todo>todoList=new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager ;
    FloatingActionButton fab;

    public MaterialEditText titleText,noteText;
    public boolean isUpdate=false;
    public String idUpdate="";

    listItemAdapter adapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db=FirebaseFirestore.getInstance();
        dialog=new SpotsDialog(this);

        titleText=findViewById(R.id.title);
        noteText=findViewById(R.id.note);

        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isUpdate){
                    setData(titleText.getText().toString(),noteText.getText().toString());
                }else {
                    updateData(titleText.getText().toString(),noteText.getText().toString());
                    isUpdate= !isUpdate;
                }

            }
        });

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        loadData();
    }

    private void updateData(String title, String notes) {
        db.collection("Todo App").document(idUpdate)
                .update("title",title,"note",notes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("Todo App").document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void setData(String title, String notes) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> todo=new HashMap<>();
        todo.put("id",id);
        todo.put("title",title);
        todo.put("note",notes);

        db.collection("Todo App").document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Delete"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int order) {
        db.collection("Todo App").document(todoList.get(order).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });

    }

    private void loadData() {
        dialog.show();
        db.collection("Todo App")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot child:task.getResult()){
                            Todo todo=new Todo(child.getString("id"),
                                    child.getString("title"),child.getString("note"));
                             todoList.add(todo);
                        }
                        adapter=new listItemAdapter(MainActivity.this,todoList);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
