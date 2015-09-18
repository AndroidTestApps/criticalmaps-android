package de.stephanlindauer.criticalmaps.handler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import de.stephanlindauer.criticalmaps.R;
import de.stephanlindauer.criticalmaps.service.ServerSyncService;
import de.stephanlindauer.criticalmaps.utils.ApplicationCloser;

public class ApplicationCloseHandler {

    private final Activity activity;

    public ApplicationCloseHandler(Activity activity) {
        this.activity = activity;
    }

    public void execute() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ApplicationCloser.close(activity);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.close).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }
}
