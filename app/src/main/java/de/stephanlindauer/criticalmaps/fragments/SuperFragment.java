package de.stephanlindauer.criticalmaps.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import de.stephanlindauer.criticalmaps.R;
import de.stephanlindauer.criticalmaps.events.NewOverlayConfigEvent;
import de.stephanlindauer.criticalmaps.handler.ProcessCameraResultHandler;
import de.stephanlindauer.criticalmaps.handler.StartCameraHandler;
import de.stephanlindauer.criticalmaps.helper.clientinfo.BuildInfo;
import de.stephanlindauer.criticalmaps.helper.clientinfo.DeviceInformation;
import de.stephanlindauer.criticalmaps.model.OwnLocationModel;
import de.stephanlindauer.criticalmaps.notifications.trackinginfo.TrackingInfoNotificationSetter;
import de.stephanlindauer.criticalmaps.service.EventService;
import de.stephanlindauer.criticalmaps.service.GPSMananger;
import de.stephanlindauer.criticalmaps.vo.chat.RequestCodes;

public class SuperFragment extends Fragment {

    protected MenuItem trackingToggleButton;
    protected Button noTrackingOverlay;

    private File newCameraOutputFile;

    protected boolean shouldShowSternfahrtRoutes = false;

    private final EventService eventService = EventService.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_buttons, menu);

        trackingToggleButton = menu.findItem(R.id.settings_tracking_toggle);
        trackingToggleButton.setChecked(OwnLocationModel.getInstance().isListeningForLocation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_close:
                handleCloseRequested();
                break;
            case R.id.take_picture:
                new StartCameraHandler(this).execute();
                break;
            case R.id.settings_tracking_toggle:
                handleTrackingToggled(item);
                break;
            case R.id.show_sternfahrt:
                handleShowSternfahrt(item);
                break;
            case R.id.settings_feedback:
                startFeedbackIntent();
                break;
            case R.id.settings_datenschutz:
                startDatenschutzIntent();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            return;
        }

        if (requestCode == RequestCodes.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            new ProcessCameraResultHandler(getActivity(), newCameraOutputFile).execute();
        }
    }

    private void startFeedbackIntent() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"stephanlindauer@posteo.de"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "feedback critical maps");
        Email.putExtra(Intent.EXTRA_TEXT, DeviceInformation.getString() + BuildInfo.getString(getActivity().getPackageManager(), getActivity().getPackageName()));
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

    private void startDatenschutzIntent() {
        String url = "http://criticalmaps.net/datenschutzerklaerung.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void handleTrackingToggled(MenuItem item) {
        item.setChecked(!item.isChecked());
        if (item.isChecked()) {
            GPSMananger.getInstance().setTrackingUserLocation(true);
            showNoTrackingOverlay(false);
        } else {
            GPSMananger.getInstance().setTrackingUserLocation(false);
            showNoTrackingOverlay(true);
        }
    }

    private void showNoTrackingOverlay(boolean shouldShow) {
        if (noTrackingOverlay != null)
            noTrackingOverlay.setVisibility(shouldShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void handleShowSternfahrt(MenuItem item) {
        item.setChecked(!item.isChecked());
        shouldShowSternfahrtRoutes = item.isChecked();
        eventService.post(new NewOverlayConfigEvent());
    }


    public void handleCloseRequested() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        TrackingInfoNotificationSetter.getInstance().cancel();
                        getActivity().finish();
                        System.exit(0);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.close).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public void setNewCameraOutputFile(File newCameraOutputFile) {
        this.newCameraOutputFile = newCameraOutputFile;
    }
}

