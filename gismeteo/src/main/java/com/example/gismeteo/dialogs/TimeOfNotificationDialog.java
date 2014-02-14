package com.example.gismeteo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.example.gismeteo.R;
import com.example.gismeteo.constants.Constants;

import java.util.Calendar;

public class TimeOfNotificationDialog {

    public interface TimeNotifSetListener {
        public void onTimeNotifSet(long time, boolean activate);
    }

    public static void openTime(Context context, boolean active, boolean check, final TimeNotifSetListener callback) {
        final TimePicker tp ;
        final CheckBox activeBox;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View timeLayout = inflater.inflate(R.layout.time_dialog, null);
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(context.getString(R.string.time_of_notif));
        ad.setView(timeLayout);
        ad.setCancelable(true);
        tp = (TimePicker)timeLayout.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        activeBox = (CheckBox) timeLayout.findViewById(R.id.active);
        activeBox.setChecked(check);
        ad.setPositiveButton(context.getString(R.string.set), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                boolean activeIt = activeBox.isChecked();
                long time = tp.getCurrentHour() * Constants.HOUR + tp.getCurrentMinute() * Constants.MIN;
                callback.onTimeNotifSet(time, activeIt);
                dialog.cancel();
            }
        });
        ad.setNegativeButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        ad.create();
        if(active) {
            ad.show();
        }

    }
}
