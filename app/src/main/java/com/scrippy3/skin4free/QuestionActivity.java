package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;

public class QuestionActivity extends AppCompatActivity {



    private FirebaseDatabase database;
    private DatabaseReference myRef, dataUpdater;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private boolean imAlive;
    private CountDownTimer countDownTimer;
    private long time;

    private Button btn_answer_question;
    private TextView tv_question_type, tv_question_question, tv_question_number, tv_question_live, tv_question_timer;
    private EditText et_answer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btn_answer_question = findViewById(R.id.btn_question_answer);
        tv_question_question = findViewById(R.id.text_question_question);
        tv_question_type = findViewById(R.id.text_question_type);
        tv_question_number = findViewById(R.id.text_question_number);
        tv_question_live = findViewById(R.id.text_question_live);
        tv_question_timer = findViewById(R.id.text_question_timer);
        et_answer = findViewById(R.id.et_question_answer);


        imAlive = false;
        GetQuestionData();

        btn_answer_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(et_answer.getText().toString());
                System.out.println(mAuth.getUid());
                myRef = database.getReference().child("question").child("participants").child(mAuth.getUid());
                myRef.child("answer").setValue(et_answer.getText().toString());
            }
        });


    }

    private void GetQuestionData() {

        dataUpdater = database.getReference().child("question");
        dataUpdater.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println(snapshot.getChildrenCount());
                //Getting the kids: question, number and type
                for (DataSnapshot snap : snapshot.getChildren()){
                    if(snap.getKey().contains("number")){
                        tv_question_number.setText("Question nr: " + snap.getValue().toString());
                    } else if(snap.getKey().contains("isOver")){

                    } else if(snap.getKey().contains("question")){

                        if (snap.getValue().equals("")){
                            tv_question_question.setText("");
                        } else {
                            tv_question_question.setText(snap.getValue().toString());

                            start30SecTimer();


                        }

                    } else if(snap.getKey().contains("type")){
                        if (snap.getValue().equals("")){
                            tv_question_type.setText("WAITING FOR NEXT QUESTION");
                        } else {
                            tv_question_type.setText(snap.getValue().toString());
                        }
                    } else if(snap.getKey().contains("participants")){
                        if (snap.child(mAuth.getUid()).child("imalive").getValue() != null) {
                            imAlive = (boolean) snap.child(mAuth.getUid()).child("imalive").getValue();
                            if (imAlive) {
                                    tv_question_live.setText("Your still in the quiz!");
                            } else {
                                tv_question_live.setText("Sorry you lost");
                            }
                        } else {
                            tv_question_live.setText("No quiz is active");
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void start30SecTimer() {
        time = 30*1000;
        countDownTimer = new CountDownTimer(30*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished;
                UpdateTimer();
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
            }
        }.start();
    }

    private void UpdateTimer() {
        int timeLeft = (int) time / 1000;
        tv_question_timer.setText(" : " + Integer.toString(timeLeft));
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}