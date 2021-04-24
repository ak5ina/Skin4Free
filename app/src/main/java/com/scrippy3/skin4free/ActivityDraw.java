package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scrippy3.skin4free.Admin.Roller;

public class ActivityDraw extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference myRef, pushRef;
    GoogleSignInAccount account;

    Roller roller;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private int tickets = 0;
    private int count = 0;
    private boolean didHeClaim;


    Button btnPrepareDraw, btnFindWinner, btnResetDraw, btnExtraTicketStart, btnExtraTicketEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnFindWinner = findViewById(R.id.btn_roll_draw);
        btnPrepareDraw = findViewById(R.id.btn_prepare_draw);
        btnResetDraw = findViewById(R.id.btn_reset_ticket);
        btnExtraTicketStart = findViewById(R.id.btn_extra_ticket_start);
        btnExtraTicketEnd = findViewById(R.id.btn_extra_ticket_end);

        roller = new Roller();

        btnPrepareDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roller.GetAllEntries();
            }
        });


        btnFindWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roller.FindWinner();
            }
        });


        btnResetDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roller.resetAllEntries();
            }
        });

        btnExtraTicketStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StartExtraTicker();

            }
        });

        btnExtraTicketEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EndExtraTicker();

            }
        });

    }

    private void EndExtraTicker() {
        myRef = database.getReference().child("Users");
        pushRef = database.getReference().child("Users");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Reset all.
                //all users
                for (DataSnapshot snap : snapshot.getChildren()){
                    // user childs
                    didHeClaim = false;
                    count = 0;
                    for (DataSnapshot snap2 : snap.getChildren()) {
                        if (snap2.getKey().contains("ExtraTickets")){
                            for (DataSnapshot snap3 : snap2.getChildren()) {
                                if (snap3.getKey().contains("Amount")) {
                                    count = Integer.parseInt(snap3.getValue().toString());
                                }
                                if (snap3.getKey().contains("LimitedTicket")) {

                                    System.out.println(snap3.getValue());
                                    if ((boolean) snap3.getValue()){
                                        didHeClaim = true;
                                    }
                                }
                            }
                            System.out.println(didHeClaim + " | " + count);
                            if (didHeClaim) {
                                pushRef.child(snap.getKey()).child("ExtraTickets").child("Amount").setValue(++count);
                                pushRef.child(snap.getKey()).child("ExtraTickets").child("LimitedTicket").setValue(false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void StartExtraTicker() {
        myRef = database.getReference().child("Users");
        pushRef = database.getReference().child("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Reset all.
                for (DataSnapshot snap : snapshot.getChildren()){
                    pushRef.child(snap.getKey()).child("ExtraTickets").child("LimitedTicket").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}