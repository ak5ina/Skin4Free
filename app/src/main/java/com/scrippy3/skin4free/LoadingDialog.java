package com.scrippy3.skin4free;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity activity){
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();

    }

    void dismissDialog(){
        alertDialog.dismiss();
    }

}
