package com.scrippy3.skin4free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PreviousGiveawaysActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;
    private ArrayList<String> listDrawUrl, listGiftToWin, listImageUrl, listPriceValue;
    private ListView listView;
    private TextView tvValueOfGA;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_giveaways);
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(PreviousGiveawaysActivity.this);

        listDrawUrl = new ArrayList<>();
        listGiftToWin = new ArrayList<>();
        listImageUrl = new ArrayList<>();
        listPriceValue = new ArrayList<>();

        listView = findViewById(R.id.previousga_listview);
        tvValueOfGA = findViewById(R.id.text_giveaway_combined_value);
        btn_back = findViewById(R.id.btn_back2);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        GetAllGiveaways();



    }

    private void GetAllGiveaways() {
        loadingDialog.startLoadingDialog();

        myRef = database.getReference().child("previousGifts");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    for (DataSnapshot snap : snapshot1.getChildren()) {
                        if (snap.getKey().equals("draw")) {
                            listDrawUrl.add(snap.getValue().toString());
                        } else if (snap.getKey().equals("gift")) {
                            listGiftToWin.add(snap.getValue().toString());
                        } else if (snap.getKey().equals("imageurl")) {
                            listImageUrl.add(snap.getValue().toString());
                        } else if (snap.getKey().equals("price")) {
                            listPriceValue.add(snap.getValue().toString());
                        }
                    }
                }


                System.out.println(listDrawUrl.size());

                UpdateListview();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateListview() {


        customDecisionAdapterGA customDecisionAdapter = new customDecisionAdapterGA(PreviousGiveawaysActivity.this, listGiftToWin, listPriceValue, listDrawUrl, listImageUrl);
        listView.setAdapter(customDecisionAdapter);

        int counter = 0;
        for (String h : listPriceValue)
            counter = counter + Integer.parseInt(h);

        tvValueOfGA.setText(Integer.toString(counter) + "$");

        loadingDialog.dismissDialog();
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}