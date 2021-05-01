package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private ArrayList<String> refCodes, idOfRefCodeHolder, referredPersons, valueOfReferredPerson;
    private boolean isTheRefCodeLegit;
    private TextView refAmount;
    private int refamountinint;
    //DATABASE
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2, myRef3, myRef4;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ref);

        //Assigning objects / variables.
        AssignObjects();

        //get all ref codes
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        GetRefCodeFromDatabase();
        GetPersonalReferedPerson();
        GetReferrals();

        //Save ref code
        btn_save_ref_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTheRefCodeLegit = false;
                myRef2 = database.getReference().child("Users").child(mAuth.getUid()).child("referedBy");

                for (int i = 0; i < refCodes.size(); i++){
                    if (et_ref_input.getText().toString().equals(refCodes.get(i))) {
                        myRef = database.getReference().child("refList").child(idOfRefCodeHolder.get(i)).child(mAuth.getUid());
                        myRef2.setValue(idOfRefCodeHolder.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myRef.setValue(true);
                                    Toast.makeText(RefActivity.this, "Ref code updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RefActivity.this, "Failed updating ref", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void GetReferrals() {
        myRef4 = database.getReference().child("refList").child(mAuth.getUid());
        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refamountinint = 0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    referredPersons.add(snap.getKey());
                    valueOfReferredPerson.add(snap.getValue().toString());
                    refamountinint++;
                }
                refAmount.setText(Integer.toString(refamountinint));
                customDecisionAdapter customDecisionAdapter = new customDecisionAdapter(RefActivity.this, referredPersons, valueOfReferredPerson);
                refListView.setAdapter(customDecisionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetPersonalReferedPerson() {
        myRef3 = database.getReference().child("Users").child(mAuth.getUid()).child("referedBy");
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    et_ref_input.setText(snapshot.getValue().toString());
                    et_ref_input.setEnabled(false);
//                    et_ref_input.setVisibility(View.GONE);
                    btn_save_ref_input.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetRefCodeFromDatabase() {

        myRef = database.getReference().child("refCodes");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getChildrenCount());
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    refCodes.add(snapshot1.getKey());
                    idOfRefCodeHolder.add(snapshot1.getValue().toString());
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
        refAmount = findViewById(R.id.text_referrals_amount);


        refCodes = new ArrayList<>();
        idOfRefCodeHolder = new ArrayList<>();
        valueOfReferredPerson = new ArrayList<>();
        referredPersons = new ArrayList<>();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}