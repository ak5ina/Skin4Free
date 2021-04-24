package com.scrippy3.skin4free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etPassword, etEmail;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Hide action bar
        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.register_email);
        etPassword = findViewById(R.id.register_password);


        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button btn_create = findViewById(R.id.createacc_btn_create);
        Button btn_back = findViewById(R.id.createacc_btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               CreateAnAccount();
            }
        });


    }

    private void CreateAnAccount() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    myRef = database.getReference().child("Users").child(mAuth.getUid());
                    myRef.child("Tickets").child("Monday").setValue(false);
                    myRef.child("Tickets").child("Tuesday").setValue(false);
                    myRef.child("Tickets").child("Wednesday").setValue(false);
                    myRef.child("Tickets").child("Thursday").setValue(false);
                    myRef.child("Tickets").child("Friday").setValue(false);
                    myRef.child("Tickets").child("Saturday").setValue(false);
                    myRef.child("Tickets").child("Sunday").setValue(false);

                    Login();

                }
                //no account in the name so creating one
                else{
                    Toast.makeText(CreateAccount.this,"Error, contact support", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void Login() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(getApplicationContext(), "Login succeed", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), user.getUid(), Toast.LENGTH_LONG).show();


                            Intent intent = new Intent(CreateAccount.this, MainPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}