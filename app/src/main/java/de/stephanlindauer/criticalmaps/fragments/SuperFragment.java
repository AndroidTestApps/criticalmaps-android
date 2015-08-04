package de.stephanlindauer.criticalmaps.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.stephanlindauer.criticalmaps.R;
import de.stephanlindauer.criticalmaps.events.NewOverlayConfigEvent;
import de.stephanlindauer.criticalmaps.helper.clientinfo.BuildInfo;
import de.stephanlindauer.criticalmaps.helper.clientinfo.DeviceInformation;
import de.stephanlindauer.criticalmaps.model.OwnLocationModel;
import de.stephanlindauer.criticalmaps.notifications.trackinginfo.TrackingInfoNotificationSetter;
import de.stephanlindauer.criticalmaps.service.EventService;
import de.stephanlindauer.criticalmaps.service.GPSMananger;
import de.stephanlindauer.criticalmaps.utils.ImageHelper;

//import de.stephanlindauer.criticalmaps.service.S3Service;

public class SuperFragment extends Fragment {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 12345;

    protected MenuItem trackingToggleButton;
    protected Button noTrackingOverlay;

    private File photoFile;

    protected boolean shouldShowSternfahrtRoutes = false;

    private final EventService eventService = EventService.getInstance();
//    private final S3Service s3Service = S3Service.getInstance();

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
                startCamera();
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

        if (resultCode == Activity.RESULT_OK || requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(), options);

            bitmap = ImageHelper.rotateBitmap(bitmap, photoFile);
            showConfirmUploadDialog(bitmap, photoFile);

            return;
        } else {
            Toast.makeText(getActivity(), R.string.camera_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmUploadDialog(final Bitmap bitmap, final File photoFile) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = factory.inflate(R.layout.picture_upload, null);

        ImageView image = (ImageView) view.findViewById(R.id.picture_preview);

        image.setImageBitmap(bitmap);

        TextView text;
        text = (TextView) view.findViewById(R.id.picture_confirm_text);
        text.setLinksClickable(true);
        text.setText(Html.fromHtml(getResources().getString(R.string.camera_comfirm_image_upload)));

        builder.setView(view);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        uploadImage(photoFile);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //do nothing + let dialog close
                        break;
                }
            }
        };

        builder.setPositiveButton(R.string.camera_upload, dialogClickListener);
        builder.setNegativeButton(R.string.camera_discard, dialogClickListener);
        builder.setCancelable(false);
        builder.show();
    }

    private void uploadImage(File file) {


//        new UploadImageToS3(file, getActivity()).execute();


//        RequestParams params = new RequestParams();
//        params.put("key", "value");
//        params.put("more", "data");
//        try {
//            params.put("uploaded_file", file);
//        } catch (FileNotFoundException e) {
//            System.out.println();
//        }
//
//        client.post("http://api.criticalmaps.net/gallery/pic.php", params, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onSuccess(int i, Header[] headers, String s) {
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onProgress(int bytesWritten, int totalSize) {
//                progressDialog.setMax(totalSize);
//                progressDialog.setProgress(bytesWritten);
//            }
//        });


//        new SnapshotUploadTask(file, progressDialog, getActivity()).execute();
    }

    private void startCamera() {
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(getActivity(), R.string.no_camera, Toast.LENGTH_SHORT).show();
            return;
        }

        File path = new File(Environment.getExternalStorageDirectory(), "foo/bar");
        if (!path.exists()) path.mkdirs();
        photoFile = ImageHelper.getNewOutputImageFile();

        Uri mImageCaptureUri1 = Uri.fromFile(photoFile);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri1);
//        cameraIntent.putExtra("return-data", true);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    private void startFeedbackIntent() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"stephan.lindauer@gmail.com"});
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
}

