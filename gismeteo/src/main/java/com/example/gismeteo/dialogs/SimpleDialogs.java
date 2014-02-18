package com.example.gismeteo.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import com.example.gismeteo.R;
import com.example.gismeteo.RegionList;

public class SimpleDialogs {
    public static void alert(String message, Context context, boolean active){
        final Activity activity = (Activity) context;
        AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyOpaqueAlertDialog));
        ad.setMessage(message);
        ad.setCancelable(true);
        ad.setPositiveButton(context.getString(R.string.close),	new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                activity.finish();
            }
        });
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                activity.finish();
                return;
            }
        });
        ad.create();
        if(active) {
            ad.show();
        }
    }
    public static void gpsAlertBox(String mymessage, Context context, boolean active) {
        final Activity activity = (Activity) context;
//        AlertDialog.Builder ad;
//        ad = new AlertDialog.Builder(context);
        AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyOpaqueAlertDialog));
        ad.setMessage(mymessage);
        ad.setPositiveButton(context.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        ad.setNegativeButton(context.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                activity.startActivityForResult(new Intent(((Dialog) dialog).getContext(), RegionList.class), 1);
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return;
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                activity.finish();
                return;
            }
        });
        if(active) {
            ad.show();
        }
    }
}
