package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    private boolean error;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefTradeLink;
    private DatabaseReference myRefDiscordName;
    Button btnSettingsSave;

    EditText etSteamTradeLink, etDiscordName;

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

        btnSettingsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error = false;
                myRef = database.getReference().child("Users").child(mAuth.getUid());
                myRef.child("DiscordName").setValue(etDiscordName.getText().toString());
                if (etSteamTradeLink.getText().toString().contains("https://steamcommunity.com/traderoffer/new")){
                    myRef.child("SteamTradeLink").setValue(etSteamTradeLink.getText().toString());
                    finish();
                } else {
                    Toast.makeText(Settings.this, "Please enter a valid steam trade link", Toast.LENGTH_SHORT).show();
                }
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

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}