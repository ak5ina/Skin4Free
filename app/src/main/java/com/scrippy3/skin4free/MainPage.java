package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    FirebaseDatabase database;
    DatabaseReference myRef;
    GoogleSignInAccount account;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private int tickets = 0;
    private int extraTickets = 0;

    private GiveAway thisWeek, lastWeek;
    private LoadingDialog loadingDialog;


    ImageView imageGa;
    TextView personal_ticket, tv_week_header, tv_week_price, tv_week_winner, tv_week_tickets, tv_extra;

    Button btnClaimTicker, btnThisWeek, btnLastWeek, btnJoinQuiz, btnClaimTimedTicket;
    ImageView btnSetSteamLink, btnInfo, day1, day2, day3, day4, day5, day6, day7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        getSupportActionBar().hide();

        imageGa = findViewById(R.id.image_week_price);
        personal_ticket = findViewById(R.id.text_tickets_privat);
        btnClaimTicker = findViewById(R.id.btn_claim_ticket);
        btnClaimTimedTicket = findViewById(R.id.btn_claim_limit_ticket);
        btnLastWeek = findViewById(R.id.btn_last_week);
        btnThisWeek = findViewById(R.id.btn_this_week);
        btnInfo = findViewById(R.id.btn_info);
        btnSetSteamLink = findViewById(R.id.btn_steamlink);
//        btnJoinQuiz = findViewById(R.id.btn_join_quiz);
        tv_week_price = findViewById(R.id.text_price_week);
        tv_week_header = findViewById(R.id.text_header_week);
        tv_week_tickets = findViewById(R.id.text_total_ticket_week);
        tv_week_winner = findViewById(R.id.text_winner_week);
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        day7 = findViewById(R.id.day7);
        tv_extra = findViewById(R.id.extra);

        database = FirebaseDatabase.getInstance();



        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(MainPage.this);

        GetPriceData();

        btnClaimTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference().child("Users").child(mAuth.getUid()).child("Tickets").child(getDayNumberOld());
                myRef.setValue(true);
                //ADMIN

//                startActivity(new Intent(MainPage.this, ActivityDraw.class));
//                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        btnSetSteamLink.setClickable(true);
        btnSetSteamLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, Settings.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        btnInfo.setClickable(true);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainPage.this)
                        .setTitle("Info")
                        .setMessage("In this application you can claim daily tickets to the giveaway every sunday." +
                                " \n Draw is showed on stream every sunday at 21:00 Paris time.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, null)
                        // A null listener allows the button to dismiss the dialog and take no further actioin
                        .show();
            }
        });

        btnLastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLastWeekGiveaway();
            }
        });

        btnThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowThisWeekGiveaway();
            }
        });

//        btnJoinQuiz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainPage.this, QuestionActivity.class));
//                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//            }
//        });

        //ADS

        btnClaimTimedTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference().child("Users").child(mAuth.getUid()).child("ExtraTickets").child("LimitedTicket");
                myRef.setValue(true);

            }
        });


        loadingDialog.startLoadingDialog();

    }

    private void ShowLastWeekGiveaway() {

        tv_week_winner.setText(lastWeek.getWinner());
        tv_week_tickets.setText("Tickets: " + lastWeek.getTickets());
        tv_week_header.setText("Last week:");
        tv_week_price.setText("Price: " + lastWeek.getPrice());
        tv_week_winner.setClickable(true);





        Glide.with(MainPage.this)
                .load(lastWeek.getPictureUrl())
                .into(imageGa);

    }

    private void ShowThisWeekGiveaway() {

        tv_week_winner.setText("");
        tv_week_tickets.setText("");
        tv_week_header.setText("This week:");
        tv_week_price.setText("Price: " + thisWeek.getPrice());
        tv_week_winner.setClickable(false);
        Glide.with(MainPage.this)
                .load(thisWeek.getPictureUrl())
                .into(imageGa);

    }



    private void GetPriceData() {

        myRef = database.getReference().child("giveaway");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.getChildrenCount());

                for (DataSnapshot snap : snapshot.getChildren()){
                    if (snap.getKey().contains("thisweek")){
                        thisWeek = new GiveAway();
                        for (DataSnapshot snap2 : snap.getChildren()){
                            if (snap2.getKey().contains("imageurl")){
                                thisWeek.setPictureUrl(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("price")){
                                thisWeek.setPrice(snap2.getValue().toString());
                            }
                        }

                        // Change picture
                        Glide.with(MainPage.this)
                                .load(thisWeek.getPictureUrl())
                                .into(imageGa);
                        // and text
                        tv_week_price.setText("Price: " + thisWeek.getPrice());


                    }

                    else if (snap.getKey().contains("lastweek")) {

                        lastWeek = new GiveAway();
                        for (DataSnapshot snap2 : snap.getChildren()){
                            if (snap2.getKey().contains("imageurl")){
                                lastWeek.setPictureUrl(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("price")){
                                lastWeek.setPrice(snap2.getValue().toString());
                            }  else if (snap2.getKey().contains("winner")){
                                lastWeek.setWinner(snap2.getValue().toString());
                            }  else if (snap2.getKey().contains("tickets")){
                                lastWeek.setTickets(Integer.parseInt(snap2.getValue().toString()));
                            }
                        }


                    }


                    else if (snap.getKey().contains("extra")) {
                        if ((boolean) snap.getValue())
                            btnClaimTimedTicket.setVisibility(View.VISIBLE);
                        else
                            btnClaimTimedTicket.setVisibility(View.GONE);

                    }
                }

                tv_week_winner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(lastWeek.getWinner());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });


                loadingDialog.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


    private void GetProfileData() {

        System.out.println("---------");
        System.out.println(mAuth.getUid());

        myRef = database.getReference().child("Users").child(mAuth.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tickets = 0;

                for (DataSnapshot snap : snapshot.getChildren()){
                    if (snap.getKey().equals("Tickets")) {
                        //Going into all the tickets
                        for (DataSnapshot snap2 : snap.getChildren()) {
                            System.out.println(snap2.getValue());
                            if ((boolean) snap2.getValue()) {
                                tickets++;
                            }


                            if (snap2.getKey().equals(getDayNumberOld())){
                                if ((boolean) snap2.getValue())
                                    btnClaimTicker.setVisibility(View.GONE);
                                else
                                    btnClaimTicker.setVisibility(View.VISIBLE);

                            }


                            ChangeColourOfCalender(snap2);
                        }
                    }
                    if (snap.getKey().contains("ExtraTickets")){
                        for (DataSnapshot snap2 : snap.getChildren()) {
                            if (snap2.getKey().contains("Amount")) {
                                System.out.println(snap2.getValue());
                                extraTickets = Integer.parseInt(snap2.getValue().toString());
                                tv_extra.setText("Extra: " + extraTickets);
                            }
                        }
                    }
                    System.out.println(snap.getKey());
                }

                tickets = tickets + extraTickets;

                personal_ticket.setText(Integer.toString(tickets));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ChangeColourOfCalender(DataSnapshot key) {
        System.out.println("-- -- -- ");
        System.out.println(key.getValue());
        System.out.println(key.getKey());

        if (key.getKey().equals("Monday")){
            if ((boolean)key.getValue())
                day1.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day1.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Tuesday")){
            if ((boolean)key.getValue())
                day2.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day2.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Wednesday")){
            if ((boolean)key.getValue())
                day3.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day3.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Thursday")){
            if ((boolean)key.getValue())
                day4.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day4.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Friday")){
            if ((boolean)key.getValue())
                day5.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day5.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Saturday")){
            if ((boolean)key.getValue())
                day6.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day6.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if (key.getKey().equals("Sunday")){
            if ((boolean)key.getValue())
                day7.setBackgroundColor(getResources().getColor(R.color.green));
            else
                day7.setBackgroundColor(getResources().getColor(R.color.red));
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if (mUser == null){
            GoBackToMainActivity();
        } else {
            GetProfileData();
        }
    }

    private void GoBackToMainActivity(){
        startActivity(new Intent(MainPage.this, MainActivity.class));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static String getDayNumberOld() {
        Calendar cal = Calendar.getInstance();
        String day;
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
            default:
                day = "Error";
                break;
        }

        return day;
    }

}
