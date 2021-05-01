package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainPage extends AppCompatActivity implements OnUserEarnedRewardListener {

    private RewardedInterstitialAd rewardedInterstitialAd;

    FirebaseDatabase database;
    DatabaseReference myRef;
    GoogleSignInAccount account;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private int tickets = 0;
    private int extraTickets = 0;
    private boolean isItMainTicket;

    private GiveAway thisWeek, lastWeek, nextWeek;
    private LoadingDialog loadingDialog;


    ImageView imageGa;
    TextView personal_ticket, tv_week_price, tv_week_skin_type, tv_this_week, tv_last_week, tv_future;

    Button btnClaimTicker, btnClaimTimedTicket;

    private BottomNavigationItemView btnRef, btnSetSteamLink, btnInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        getSupportActionBar().hide();

        imageGa = findViewById(R.id.image_week_price);
        personal_ticket = findViewById(R.id.text_tickets_privat);
        btnClaimTicker = findViewById(R.id.button);
        tv_week_price = findViewById(R.id.text_prize_gun_type);
        tv_week_skin_type = findViewById(R.id.text_prize_skin_type);
        btnInfo = findViewById(R.id.btn_menu_info);
        btnSetSteamLink = findViewById(R.id.btn_menu_settings);
        tv_this_week = findViewById(R.id.btn_this_week);
        tv_last_week = findViewById(R.id.btn_last_week);
        tv_future = findViewById(R.id.btn_future);
        btnRef = findViewById(R.id.btn_menu_home);

        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(MainPage.this);

        GetPriceData();

        //ADS
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                System.out.println("YOU CAN USE ADS NOW");
            }
        });


        btnClaimTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isItMainTicket = true;
                LoadAds();
                loadingDialog.startLoadingDialog();

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
                                " \nDraw is showed on stream every sunday at 21:00 Paris time." +
                                " \nYou can find the channel by searching for nudecoder on Youtube.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, null)
                        // A null listener allows the button to dismiss the dialog and take no further actioin
                        .show();
            }
        });

        tv_this_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowThisWeekGiveaway();
                tv_this_week.setBackground(getResources().getDrawable(R.drawable.shadowbox));
                tv_last_week.setBackground(null);
                tv_future.setBackground(null);
            }
        });

        tv_last_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLastWeekGiveaway();
                tv_last_week.setBackground(getResources().getDrawable(R.drawable.shadowbox));
                tv_this_week.setBackground(null);
                tv_future.setBackground(null);
            }
        });



        tv_future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNextWeekGiveaway();
                tv_future.setBackground(getResources().getDrawable(R.drawable.shadowbox));
                tv_this_week.setBackground(null);
                tv_last_week.setBackground(null);
            }
        });



        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, RefActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        loadingDialog.startLoadingDialog();

    }


    private void LoadAds() {
        RewardedInterstitialAd.load(MainPage.this, getResources().getString(R.string.google_ad),
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        System.out.println("Ad loaded");
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                System.out.println("onAdFailedToShowFullScreenContent");
                            }

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                System.out.println("onAdShowedFullScreenContent");
                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                System.out.println("onAdDismissedFullScreenContent");
                            }
                        });
                        loadingDialog.dismissDialog();
                        rewardedInterstitialAd.show(MainPage.this, MainPage.this::onUserEarnedReward);
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
//                        Toast.makeText(MainPage.this, loadAdError.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("Error : " + loadAdError);
                        loadingDialog.dismissDialog();
                        Toast.makeText(MainPage.this, "Error on ad loading! try again in 10 min.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void ShowLastWeekGiveaway() {
        tv_week_price.setText(lastWeek.getGunType());
        tv_week_skin_type.setText(lastWeek.getSkinType());
        Glide.with(MainPage.this)
                .load(lastWeek.getPictureUrl())
                .into(imageGa);

    }

    private void ShowNextWeekGiveaway() {
        tv_week_price.setText(nextWeek.getGunType());
        tv_week_skin_type.setText(nextWeek.getSkinType());
        Glide.with(MainPage.this)
                .load(nextWeek.getPictureUrl())
                .into(imageGa);

    }

    private void ShowThisWeekGiveaway() {
        tv_week_price.setText(thisWeek.getGunType());
        tv_week_skin_type.setText(thisWeek.getSkinType());
        Glide.with(MainPage.this)
                .load(thisWeek.getPictureUrl())
                .into(imageGa);

    }


    private void GetPriceData() {

        myRef = database.getReference().child("GA");
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
                            } else if (snap2.getKey().contains("guntype")){
                                thisWeek.setGunType(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("skintype")){
                                thisWeek.setSkinType(snap2.getValue().toString());
                            }
                        }

                        // Change picture
                        Glide.with(MainPage.this)
                                .load(thisWeek.getPictureUrl())
                                .into(imageGa);
                        // and text
                        tv_week_price.setText(thisWeek.getGunType());
                        tv_week_skin_type.setText(thisWeek.getSkinType());


                    }

                    else if (snap.getKey().contains("lastweek")) {

                        lastWeek = new GiveAway();
                        for (DataSnapshot snap2 : snap.getChildren()){
                            if (snap2.getKey().contains("imageurl")){
                                lastWeek.setPictureUrl(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("guntype")){
                                lastWeek.setGunType(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("skintype")){
                                lastWeek.setSkinType(snap2.getValue().toString());
                            }

//                              else if (snap2.getKey().contains("winner")){
//                                lastWeek.setWinner(snap2.getValue().toString());
//                            }  else if (snap2.getKey().contains("tickets")){
//                                lastWeek.setTickets(Integer.parseInt(snap2.getValue().toString()));
//                            }
                        }


                    }

                    else if (snap.getKey().contains("nextweek")) {

                        nextWeek = new GiveAway();
                        for (DataSnapshot snap2 : snap.getChildren()){
                            if (snap2.getKey().contains("imageurl")){
                                nextWeek.setPictureUrl(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("guntype")){
                                nextWeek.setGunType(snap2.getValue().toString());
                            } else if (snap2.getKey().contains("skintype")){
                                nextWeek.setSkinType(snap2.getValue().toString());
                            }
                        }


                    }

                }


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
                                if ((boolean) snap2.getValue()) {
                                    btnClaimTicker.setText("Ticket claimed!");
                                    btnClaimTicker.setBackground(getResources().getDrawable(R.drawable.greenshadowbox));
                                    btnClaimTicker.setClickable(false);
                                }
                                else {
                                    btnClaimTicker.setText("Claim ticket");
                                    btnClaimTicker.setBackground(getResources().getDrawable(R.drawable.shadowbox));
                                }
                            }

                        }
                    }
                    if (snap.getKey().contains("ExtraTickets")){
                        for (DataSnapshot snap2 : snap.getChildren()) {
                            if (snap2.getKey().contains("Amount")) {
                                System.out.println(snap2.getValue());
                                extraTickets = Integer.parseInt(snap2.getValue().toString());
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

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        System.out.println(rewardItem.getType() + " | " + rewardItem.getAmount());
        if (isItMainTicket) {
            myRef = database.getReference().child("Users").child(mAuth.getUid()).child("Tickets").child(getDayNumberOld());
            myRef.setValue(true);
        }
        else {
            myRef = database.getReference().child("Users").child(mAuth.getUid()).child("ExtraTickets").child("LimitedTicket");
            myRef.setValue(true);
        }
    }
}
