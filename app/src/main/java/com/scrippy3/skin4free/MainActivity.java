package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText textviewEmail, textviewPassword, btcinputadress;
    private Button btn_login, btn_createAccount, btn_MyOrders, btn_MyIntrest, btn_btcadress;
    private FirebaseAuth mAuth;
    private TextView overViewUserMail, overViewUserBank;
    private ListView listView;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide action bar
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        textviewEmail = findViewById(R.id.login_email);
        textviewPassword = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.login_btn_login);
        btn_createAccount = findViewById(R.id.login_btn_createaccount);

        //Making btn functions:
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textviewEmail.getText().length() > 0 && textviewPassword.getText().length() > 0) {
                    SignIn();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter both email and password", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });



    }

    private void SignIn() {
        mAuth.signInWithEmailAndPassword(textviewEmail.getText().toString(), textviewPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(getApplicationContext(), "Login succeed", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), user.getUid(), Toast.LENGTH_LONG).show();

                            GetDateToUser(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void GetDateToUser(FirebaseUser user){
        //Changing UI to the logged in UI.
        firebaseUser = user;
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            GetDateToUser(currentUser);
        }
    }






}