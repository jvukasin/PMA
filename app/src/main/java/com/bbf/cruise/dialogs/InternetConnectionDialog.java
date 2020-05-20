package com.bbf.cruise.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class InternetConnectionDialog extends AlertDialog.Builder {

    public InternetConnectionDialog(Context context) {
        super(context);
        setUpDialog();
    }

    private void setUpDialog(){
        setMessage("Your internet connection seems to be disabled, please enable it so you can use the app.");
        setCancelable(false);

        setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getContext().startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
                dialog.dismiss();
            }
        });
    }

    public AlertDialog prepareDialog(){
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
