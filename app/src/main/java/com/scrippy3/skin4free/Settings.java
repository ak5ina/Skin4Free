package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private DatabaseReference myRefTradeLink;
    private DatabaseReference myRefDiscordName;
    private DatabaseReference myRefCode;
    Button btnSettingsSave, btn_back;

    EditText etSteamTradeLink, etDiscordName, etRefCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        etSteamTradeLink = findViewById(R.id.edittext_settings_steamlink);
        etDiscordName = findViewById(R.id.edittext_settings_discordname);
        btnSettingsSave = findViewById(R.id.btn_settings_save);
        btn_back = findViewById(R.id.btn_back);
        etRefCode = findViewById(R.id.edittext_settings_refCode);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSettingsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference().child("Users").child(mAuth.getUid());
                myRef.child("SteamTradeLink").setValue(etSteamTradeLink.getText().toString());
                myRef.child("DiscordName").setValue(etDiscordName.getText().toString());

                if (etRefCode.isEnabled()) {
                    if (etRefCode.getText().toString().length() > 3) {
                        myRef2 = database.getReference().child("refCodes");
                        myRef2.child(etRefCode.getText().toString().toLowerCase()).setValue(mAuth.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    myRef.child("personalRefCode").setValue(etRefCode.getText().toString().toLowerCase());
                                else {
                                    Toast.makeText(Settings.this, "The ref code is already in use.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else
                        Toast.makeText(Settings.this, "Ref code length is minimum 4 letters.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        myRefTradeLink = database.getReference().child("Users").child(mAuth.getUid()).child("SteamTradeLink");

        myRefTradeLink.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    etSteamTradeLink.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRefDiscordName = database.getReference().child("Users").child(mAuth.getUid()).child("DiscordName");

        myRefDiscordName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    etDiscordName.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRefCode = database.getReference().child("Users").child(mAuth.getUid()).child("personalRefCode");

        myRefCode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    etRefCode.setText(snapshot.getValue().toString());
                    etRefCode.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}