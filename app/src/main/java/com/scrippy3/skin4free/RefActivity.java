package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RefActivity extends AppCompatActivity {

    private ListView refListView;
    private EditText et_ref_input;
    private Button btn_save_ref_input;
    private ArrayList<String> refCodes, idOfRefCodeHolder;
    //DATABASE
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ref);

        //Assigning objects / variables.
        AssignObjects();

        //get all ref codes
        GetRefCodeFromDatabase();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Save ref code
        btn_save_ref_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void GetRefCodeFromDatabase() {

        myRef = database.getReference().child("refCodes");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void AssignObjects() {
        refListView = findViewById(R.id.ref_listview);
        et_ref_input = findViewById(R.id.edittext_ref_code_input);
        btn_save_ref_input = findViewById(R.id.ref_btn_save_refcode_input);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}