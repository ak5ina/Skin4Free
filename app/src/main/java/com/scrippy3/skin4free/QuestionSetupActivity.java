package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionSetupActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefToPush;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    Timer timer;

    private ArrayList<String> idOfWinners;

    private int questionNumber, questionNumberForAdmin;

    private Button btn_admin_startup, btn_admin_new_question, btn_admin_find_winner;
//    private EditText et_admin_question_type, et_admin_question, et_admin_answer;
    private ArrayList<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_setup);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        idOfWinners = new ArrayList<>();

        timer = new Timer();

        btn_admin_find_winner = findViewById(R.id.btn_admin_find_winner);
        btn_admin_startup = findViewById(R.id.btn_admin_start_up_quiz);
        btn_admin_new_question = findViewById(R.id.btn_admin_new_question);
//        et_admin_question_type = findViewById(R.id.edittext_admin_question_type);
//        et_admin_question = findViewById(R.id.edittext_admin_question);
//        et_admin_answer = findViewById(R.id.edittext_admin_answer);

        //HARD CODED QUESTIONS
        questionList = new ArrayList<>();
        questionList.add(new Question("a Cartoon","Rick and _____","morty"));
        questionList.add(new Question("Name missing Southpark character","Erik, stan, kenny and ____","kyle"));
        questionList.add(new Question("Write the name without titel","What character was representating trump?","Garrison"));
        questionList.add(new Question("Give a year","What year was the south park movie released?","1999"));
        questionList.add(new Question("South park knowledge","What country are kyles mom fighting in the movie?","canada"));



        btn_admin_startup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupNewQuestioneer();
            }
        });


        btn_admin_new_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupQuestion();
            }
        });

        btn_admin_find_winner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindWinner();
            }
        });



    }

    private void SetupAnswer() {
        myRef = database.getReference().child("question").child("participants");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snap : snapshot.getChildren()){

                    if(snap.child("answer").getValue() != null) {
                        if (!snap.child("answer").getValue().toString().toLowerCase().equals(questionList.get(questionNumber-1).getAnswer())) {
                            myRef.child(snap.getKey()).child("imalive").setValue(false);
                        } else {

                            myRef.child(snap.getKey()).child(Integer.toString(questionNumber)).setValue(true);
                        }
                    } else {
                        myRef.child(snap.getKey()).child("imalive").setValue(false);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FindWinner() {
        myRef = database.getReference().child("question").child("participants");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                idOfWinners.clear();

                for (DataSnapshot snap : snapshot.getChildren()){

                    if(snap.child("imalive").getValue() != null) {
                        boolean a = (boolean) snap.child("imalive").getValue();
                        if (a)
                            idOfWinners.add(snap.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for (int i = 0; i < idOfWinners.size(); i++) {
            System.out.println(idOfWinners.get(i));
        }
    }

    private void SetupQuestion() {
        questionNumber++;

        myRef = database.getReference().child("question");
        myRef.child("number").setValue(questionNumber);
        myRef.child("type").setValue(questionList.get(questionNumber-1).getType());

        myRef.child("isOver").setValue(false);
        myRef.child("question").setValue(questionList.get(questionNumber-1).getQuestion());

        StartClosingTimer();

    }

    private void StartClosingTimer() {
        myRef = database.getReference().child("question");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                myRef.child("isOver").setValue(true);
                SetupAnswer();
            }

        }, 1000*30 );
    }

    private void SetupNewQuestioneer() {

        myRef = database.getReference().child("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println(snapshot.getChildrenCount());

                for (DataSnapshot snap : snapshot.getChildren()){
                    if (snap.child("DiscordName").getValue() != null){
                        myRefToPush = database.getReference().child("question").child("participants").child(snap.getKey());
                        myRefToPush.child("imalive").setValue(true);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        questionNumber = 0;

    }
}