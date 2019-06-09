package com.magdy.travelli.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.annotations.NotNull;
import com.magdy.travelli.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public abstract class ImageAttachmentManager<T extends Activity, Image extends ImageView, F extends Fragment> {
    /*
     * This Abstract class to help you to get images from the mobile internal
     * by asking for the permission to access your image gallery or camera
     * and send the chosen image to crop tool using an external Library
     * find examples in TODO: OFFERS ADD and EDIT for Activities example and Profile Fragment in Home Activity
     * */
    private T activity;
    private F fragment;
    private View contentView;
    private Image imageView;
    private Dialog Options;
    public final static int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri photoURI;
    private File destination;
    private Cursor cursor;

    public ImageAttachmentManager(T activity, Image imageView) {
        this.activity = activity;
        this.imageView = imageView;
        setUpViews();
    }

    public ImageAttachmentManager(F fragment, View view, Image imageView) {
        this.fragment = fragment;
        this.contentView = view;
        this.imageView = imageView;
        setUpViews();
    }


    void setUpViews() {
        Options = new Dialog(activity);
        Options.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Options.setCancelable(false);
        Options.setContentView(R.layout.options_linear);
        if (Options.getWindow() != null)
            Options.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView Gallary = findViewById(R.id.galleryOption);
        final TextView Camera = findViewById(R.id.cameraOption);
        final TextView Cancel = findViewById(R.id.cancelOption);
        Gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.dismiss();
            }
        });
    }

    private void galleryIntent() {
        //// Send an intent to system to get images from gallery and selecting a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        if (activity != null)
            activity.startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        else if (fragment != null)
            fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        photoURI = null;
        //// Send an intent to system to get images from camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getBaseContext()),
                        StaticMembers.AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (activity != null)
                    activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                else if (fragment != null)
                    fragment.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private String createImagePath() {
        //create image with date name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_hh_mm_a", Locale.US).format(System.currentTimeMillis());
        return "JPEG_" + timeStamp + "_";
    }

    private File createImageFile() throws IOException {
        //save image in a directory
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                createImagePath(),
                ".jpg",
                storageDir
        );
    }

    private void onSelectFromGalleryResult(Intent data) {
        //Action after selecting image from Galley
        if (data != null) {
            try {
                Uri tempUri = getImageUri(getBaseContext(), MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData()));
                int x, y;
                if (imageView != null) {
                    x = imageView.getHeight();
                    y = imageView.getWidth();
                    if (x <= 0)
                        x = 100;
                    if (y <= 0)
                        y = 100;
                } else {
                    x = 100;
                    y = 100;
                }
                //Send bitmap to CropImage Activity to crop it and edit it
                CropImage.activity(tempUri).setAspectRatio(x, y).setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
                if (cursor != null)
                    cursor.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        int x, y;
        if (imageView != null) {
            x = imageView.getHeight();
            y = imageView.getWidth();
        } else {
            x = 100;
            y = 100;
        }
        if (photoURI != null)
            CropImage.activity(photoURI).setAspectRatio(x, y).setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, createImagePath(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE:
                    onSelectFromGalleryResult(data);
                    break;
                case REQUEST_CAMERA:
                    onCaptureImageResult(data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = null;
                    if (result != null) {
                        resultUri = result.getUri();
                        if (resultUri != null && imageView != null) {
                            imageView.setImageURI(resultUri);
                        } else if (photoURI != null && imageView != null) {
                            imageView.setImageURI(photoURI);
                        }
                        assert resultUri != null;
                        File f = new File(resultUri.getPath());
                        Toast.makeText(getBaseContext(), "Selected", Toast.LENGTH_SHORT).show();
                        FileOutputStream fo;
                        try {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            if (result.getBitmap() != null) {
                                result.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
                            }
                            if (f.createNewFile()) {
                                fo = new FileOutputStream(f);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        destination = f;
                        onFileReady(f);
                    }
                    break;
            }
        }
        hideAttachmentOptions();
    }

    public abstract void onFileReady(@NotNull File file);


    public File getDestination() {
        return destination;
    }

    public void showAttachmentOptions() {
        Options.show();
    }

    public void hideAttachmentOptions() {
        Options.dismiss();
    }

    public Image getImageView() {
        return imageView;
    }

    public void setImageView(Image imageView) {
        this.imageView = imageView;
    }

    Activity getActivity() {
        if (activity != null)
            return activity;
        else if (fragment != null && fragment.getActivity() != null) {
            return fragment.getActivity();
        } else return null;
    }

    Context getBaseContext() {
        if (activity != null)
            return activity.getBaseContext();
        else if (fragment != null)
            return fragment.getContext();
        else return null;
    }

    <E extends View> E findViewById(int id) {
        if (activity != null)
            return activity.findViewById(id);
        else if (fragment != null)
            return contentView.findViewById(id);
        else return null;
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Options.show();
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                // Fragment uses requestPermissions directly and Activities use ActivityCompat.requestPermission
                if (activity != null)
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
                else if (fragment != null)
                    fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
                return false;
            }
        } else {
            Options.show();
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SELECT_FILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Options.show();
                } else {
                    Toast.makeText(getBaseContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

}