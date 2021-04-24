package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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

    private RewardedAd mRewardedAd;

    FirebaseDatabase database;
    DatabaseReference myRef;
    GoogleSignInAccount account;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private int tickets = 0;
    private int extraTickets = 0;
    private boolean isItThisWeek;
    private boolean isTheExtraTicketActive;

    private GiveAway thisWeek, lastWeek;
    private LoadingDialog loadingDialog;


    ImageView imageGa;
    TextView personal_ticket, tv_week_header, tv_week_price, tv_week_winner, tv_week_tickets, tv_extra;

    Button btnClaimTicker, btnChangeWeek, btnJoinQuiz, btnClaimTimedTicket;
    ImageView btnSetSteamLink, btnInfo, day1, day2, day3, day4, day5, day6, day7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        isItThisWeek = true;
        isTheExtraTicketActive = false;

        getSupportActionBar().hide();

        imageGa = findViewById(R.id.image_week_price);
        personal_ticket = findViewById(R.id.text_tickets_privat);
        btnClaimTicker = findViewById(R.id.btn_claim_ticket);
        btnClaimTimedTicket = findViewById(R.id.btn_claim_limit_ticket);
        btnChangeWeek = findViewById(R.id.btn_change_week);
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


        //ADS
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });


        btnClaimTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RewardedAd.load(MainPage.this, getResources().getString(R.string.google_ad), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        super.onAdLoaded(rewardedAd);
                        mRewardedAd = rewardedAd;
                        System.out.println("On ad loaded");

                        ShowAd();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mRewardedAd = null;
                        System.out.println("Error: " + loadAdError);
                    }
                });

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

        btnChangeWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickOnWeeklyBtn();
            }
        });

        btnClaimTimedTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef = database.getReference().child("Users").child(mAuth.getUid()).child("ExtraTickets").child("LimitedTicket");
                myRef.setValue(true);


            }
        });


        loadingDialog.startLoadingDialog();

    }

    private void ShowAd() {

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                System.out.println("ad failed to show full screen");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                System.out.println("ad showed full screen");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                System.out.println("ad dismissed full screen");
            }
        });

        if (mRewardedAd != null){
            Activity activityContext = MainPage.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    System.out.println("YEAS");
                    int reward = rewardItem.getAmount();
                    String type = rewardItem.getType();
                    System.out.println(reward + " | " + type);



                    myRef = database.getReference().child("Users").child(mAuth.getUid()).child("Tickets").child(getDayNumberOld());
                    myRef.setValue(true);
                }
            });
        }
    }

    private void ShowLastWeekGiveaway() {

        isItThisWeek = false;

        btnChangeWeek.setText("This week");
        tv_week_winner.setVisibility(View.VISIBLE);
        tv_week_tickets.setVisibility(View.VISIBLE);
        tv_week_winner.setText(lastWeek.getWinner());
        tv_week_tickets.setText("Tickets: " + lastWeek.getTickets());
        tv_week_header.setText("Last week:");
        tv_week_price.setText("Prize: " + lastWeek.getPrice());
        tv_week_winner.setClickable(true);





        Glide.with(MainPage.this)
                .load(lastWeek.getPictureUrl())
                .into(imageGa);

    }

    private void ShowThisWeekGiveaway() {
        isItThisWeek = true;

        btnChangeWeek.setText("Last week");


        tv_week_winner.setVisibility(View.GONE);
        tv_week_tickets.setVisibility(View.GONE);
        tv_week_header.setText("This week:");
        tv_week_price.setText("Prize: " + thisWeek.getPrice());
        tv_week_winner.setClickable(false);
        Glide.with(MainPage.this)
                .load(thisWeek.getPictureUrl())
                .into(imageGa);

    }

    private void clickOnWeeklyBtn(){

        if (isItThisWeek)
            ShowLastWeekGiveaway();
        else
            ShowThisWeekGiveaway();

    }


    private void GetPriceData() {
        tv_week_winner.setVisibility(View.GONE);
        tv_week_tickets.setVisibility(View.GONE);

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

                        isTheExtraTicketActive = (boolean) snap.getValue();

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


        myRef = database.getReference().child("Users").child(mAuth.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tickets = 0;

                for (DataSnapshot snap : snapshot.getChildren()){
                    if (snap.getKey().equals("Tickets")) {
                        //Going into all the tickets
                        for (DataSnapshot snap2 : snap.getChildren()) {
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
                            }
                            else if (snap2.getKey().equals("LimitedTicket")){
                                if ((boolean) snap2.getValue() && isTheExtraTicketActive){
                                    extraTickets++;
                                }
                            }

                            tv_extra.setText("Extra: " + extraTickets);


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
