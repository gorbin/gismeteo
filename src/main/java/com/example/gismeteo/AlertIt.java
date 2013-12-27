package com.example.gismeteo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertIt {
    public AlertIt(){ }

	public void gpsAlertBox(String mymessage, Context context) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);	
        ad.setMessage(mymessage);
        ad.setPositiveButton(context.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        ad.setNegativeButton(context.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
				context.startActivityForResult(new Intent(((Dialog) dialog).getContext(),RegionList.class),1);
				context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				return;
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
                return;
            }
        });
        ad.show();
    }
	public void alert(String message, Context context){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(message);
        ad.setCancelable(true);
        ad.setPositiveButton(context.getString(R.string.close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                }).create().show();
		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
                return;
            }
        });
        ad.show();
    }
}