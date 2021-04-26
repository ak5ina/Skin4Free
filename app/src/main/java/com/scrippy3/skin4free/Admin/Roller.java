package com.scrippy3.skin4free.Admin;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class Roller {

    //TEST
    ArrayList<String> checkerTest;
    ArrayList<String> checkerTest2;

    //KEEP
    private ArrayList<String> drawingList;
    private ArrayList<String> steamTradeLinks;
    private ArrayList<String> idOfSteamTradeLinks;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    int timeInLoop;

    public Roller(){
        drawingList = new ArrayList<>();
        steamTradeLinks = new ArrayList<>();
        idOfSteamTradeLinks = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        checkerTest  = new ArrayList<>();
        checkerTest2 = new ArrayList<>();
    }

    public void GetAllEntries(){
        myRef = database.getReference().child("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    for (DataSnapshot snap2 : snap.getChildren()){
                        if (snap2.getKey().equals("Tickets")) {
                                for (DataSnapshot snap3 : snap2.getChildren()) {
                                    if ((boolean) snap3.getValue()) {
                                        drawingList.add(snap.getKey().toString());
                                    }
                                }
                        } else if (snap2.getKey().contains("SteamTradeLink")){
                            if (snap2.getValue().toString() != null) {
                                steamTradeLinks.add(snap2.getValue().toString());
                                idOfSteamTradeLinks.add(snap.getKey());
                            } else {
                                steamTradeLinks.add("NO STEAM TRADE LINK INSERTED!");
                            }
                        }  else if (snap2.getKey().contains("ExtraTickets")){
                            for (DataSnapshot snap3 : snap2.getChildren()){
                                if(snap3.getKey().equals("Amount")){
                                    for (int i = 0; i < Integer.parseInt(snap3.getValue().toString()); i++){
                                        drawingList.add(snap.getKey().toString());

                                    }
                                }
                            }



                        }
                    }
                }

                System.out.println("Total amount of tickets: " + drawingList.size());
                for (int i = 0 ; i < drawingList.size(); i++){
                    System.out.println("Tickets number " + (i+1) + ": " + drawingList.get(i));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void resetAllEntries(){
        myRef = database.getReference().child("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    myRef.child(snap.getKey()).child("Tickets").child("Monday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Tuesday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Wednesday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Thursday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Friday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Saturday").setValue(false);
                    myRef.child(snap.getKey()).child("Tickets").child("Sunday").setValue(false);
                    myRef.child(snap.getKey()).child("ExtraTickets").child("Amount").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CheckForMultipleAccounts(){

        myRef = database.getReference().child("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snap : snapshot.getChildren()){
                    if (snap.child("SteamTradeLink").getValue() != null && !snap.child("SteamTradeLink").getValue().equals("")) {
                        checkerTest.add(snap.child("SteamTradeLink").getValue().toString());
                        checkerTest2.add(snap.getKey());
                    }

                }// ENDING for snap

                timeInLoop = 0;

                for (String test : checkerTest){
                    int i = 0;
                    for (int u = 0; u < checkerTest.size(); u++){
                        if (checkerTest.get(u).equals(test))
                            i++;
                    } // checking for amount

                    if (i > 1){
                        System.out.println("-----");
                        System.out.println("Multiple accounts found on:");
                        System.out.println("Tradelink: " + checkerTest.get(timeInLoop));
                        System.out.println("Id in DB: " + checkerTest2.get(timeInLoop));
                    }
                    timeInLoop++;
                } //ENDING for test

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void FindWinner(){
        Random ran = new Random();
        int winnerTicket =  ran.nextInt(drawingList.size());

        System.out.println("The winner number is: " + (winnerTicket+1));
        System.out.println("The winner id: " + drawingList.get(winnerTicket));

        for (int i = 0; i < steamTradeLinks.size(); i++){
            if (idOfSteamTradeLinks.get(i).contains(drawingList.get(winnerTicket))){
                System.out.println(steamTradeLinks.get(i));
            }
        }


        CheckForMultipleAccounts();



    }





}
