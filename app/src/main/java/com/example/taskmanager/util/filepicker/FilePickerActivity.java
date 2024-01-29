package com.example.taskmanager.util.filepicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FilePickerActivity extends AppCompatActivity {

    private FilePicker.PickObject pickObject = null;
    private FilePicker.PickFrom pickFrom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        pickObject = FilePicker.PickObject.values()[intent.getIntExtra(EXTRA_PICK_OBJECT, FilePicker.PickObject.IMAGE.ordinal())];
        pickFrom = FilePicker.PickFrom.values()[intent.getIntExtra(EXTRA_PICK_FROM, FilePicker.PickFrom.CAMERA.ordinal())];

        startPickingOperation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPickingOperation();
            } else {
                handleOperationCancelled();
            }
        }
    }

    // start picking operation

    private void startPickingOperation() {
        if (pickObject != null) {
            switch (pickObject) {
                case IMAGE:
                    if (pickFrom != null) {
                        switch (pickFrom) {
                            case CAMERA:
                                pickImageFromCamera();
                                break;
                            case GALLERY:
                                pickImageFromGallery();
                                break;
                            case FILE_MANAGER:
                                pickImageFromDocumentPicker();
                                break;
                            case CAMERA_OR_GALLERY:
                            case ANY_WHERE:
                            default:
                                handleOperationCancelled();
                                break;
                        }
                    } else {
                        handleOperationCancelled();
                    }
                    break;

                case VIDEO:
                    if (pickFrom != null) {
                        switch (pickFrom) {
                            case CAMERA:
                                pickVideoFromCamera();
                                break;
                            case GALLERY:
                                pickVideoFromGallery();
                                break;
                            case FILE_MANAGER:
                                pickVideoFromDocumentPicker();
                                break;
                            case CAMERA_OR_GALLERY:
                            case ANY_WHERE:
                            default:
                                handleOperationCancelled();
                                break;
                        }
                    } else {
                        handleOperationCancelled();
                    }
                    break;

                case FILE:
                    pickPDFFromDocumentPicker();
                    break;

                case ANY_THING:
                default:
                    handleOperationCancelled();
                    break;
            }
        } else {
            handleOperationCancelled();
        }
    }

    // endregion

    // region pick image/video from gallery

    private ActivityResultLauncher<PickVisualMediaRequest> pickImage = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    handleImageResult(uri);
                } else {
                    handleOperationCancelled();
                }
            });

    private ActivityResultLauncher<PickVisualMediaRequest> pickVideo = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    handleVideoResult(uri);
                } else {
                    handleOperationCancelled();
                }
            });

    private void pickImageFromGallery() {
        pickImage.launch(
                new PickVisualMediaRequest
                        .Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void pickVideoFromGallery() {
        pickVideo.launch(
                new PickVisualMediaRequest
                        .Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                        .build()
        );
    }

    // endregion

    // region pick image/video using document picker

    private ActivityResultLauncher<Intent> openDocumentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri documentUri = result.getData() != null ? result.getData().getData() : null;
                        if (documentUri != null) {
                            String documentType = getContentResolver().getType(documentUri);
                            if (documentType == null)
                                documentType = "application/pdf";

                            if (documentType.startsWith("image")) {
                                handleImageResult(documentUri);
                            } else if (documentType.startsWith("video")) {
                                handleVideoResult(documentUri);
                            } else {
                                handleFileResult(documentUri);
                            }
                        }
                    } else {
                        handleOperationCancelled();
                    }
                }
            }
    );

    private void pickImageFromDocumentPicker() {
        openDocumentPicker("image/*");
    }

    private void pickVideoFromDocumentPicker() {
        openDocumentPicker("video/*");
    }

    private void pickPDFFromDocumentPicker() {
        openDocumentPicker("application/pdf");
    }

    private void openDocumentPicker(String documentType) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(documentType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        openDocumentLauncher.launch(intent);
    }

    // endregion

    // region pick image/video from camera

    private String emptyFilePath;

    private ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            MediaStore.Images.Media.insertImage(getContentResolver(), emptyFilePath, "FILE_" + timeStamp, "Picture from Camera");
                        } catch (FileNotFoundException e) {
                            handleOperationCancelled();
                        }
                        handleImageResult(Uri.fromFile(new File(emptyFilePath)));
                    } else {
                        handleOperationCancelled();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> takeVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        File file = new File(emptyFilePath);
                        handleVideoResult(Uri.fromFile(file));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            addVideoToGalleryAPI29(file,"FILE_"+System.currentTimeMillis());
                        }

                    } else {
                        handleOperationCancelled();
                    }
                }
            }
    );

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void addVideoToGalleryAPI29(
            File file,
            String displayName
    ) {
        String savePath = Environment.DIRECTORY_DCIM + File.separator + file.getName();
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + new File(savePath).getName());
        cv.put(MediaStore.Video.Media.TITLE, displayName + ".mp4");
        cv.put(MediaStore.Video.Media.DISPLAY_NAME, displayName + ".mp4");
        cv.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        cv.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        cv.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        cv.put(MediaStore.Video.Media.IS_PENDING, 1);

        ContentResolver resolver = getContentResolver();
        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedVideo = resolver.insert(collection, cv);

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(uriSavedVideo, "w")) {
            if (pfd != null) {
                try {
                    FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    inputStream.close();
                    cv.clear();
                    cv.put(MediaStore.Video.Media.IS_PENDING, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resolver.update(uriSavedVideo, cv, null, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void pickVideoFromCamera() {
        if (!hasCameraPermission()) {
            requestCameraPermission();
            return;
        }

        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                try {
                    return createAnEmptyFile(".mp4");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File videoFile) {
                if (videoFile != null) {
                    Uri videoURI = FileProvider.getUriForFile(
                            FilePickerActivity.this,
                            getPackageName() + ".provider",
                            videoFile
                    );

                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                    takeVideoLauncher.launch(
                            Intent.createChooser(takeVideoIntent, "Select Camera")
                    );
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void pickImageFromCamera() {
        if (!hasCameraPermission()) {
            requestCameraPermission();
            return;
        }

        new AsyncTask<Void, Void, File>() {
            @Override
            protected File doInBackground(Void... voids) {
                try {
                    return createAnEmptyFile(".jpg");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(File photoFile) {
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(
                            FilePickerActivity.this,
                            getPackageName() + ".provider",
                            photoFile
                    );

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureLauncher.launch(
                            Intent.createChooser(takePictureIntent, "Select Camera")
                    );
                }
            }
        }.execute();
    }


    private File createAnEmptyFile(String extension) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = File.createTempFile(
                "FILE_" + timeStamp + "_",  // prefix
                extension,  // suffix
                getFilesDir()  // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        emptyFilePath = file.getAbsolutePath();

        return file;
    }


    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    // endregion

    // region handle success and failure results

    private void handleImageResult(Uri imageUri) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PICKED_ITEM_SUCCESS);
        intent.putExtra(EXTRA_PICKED_ITEM_URI, imageUri);
        sendBroadcast(intent);
        finish();
    }

    private void handleVideoResult(Uri videoUri) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PICKED_ITEM_SUCCESS);
        intent.putExtra(EXTRA_PICKED_ITEM_URI, videoUri);
        sendBroadcast(intent);
        finish();
    }

    private void handleFileResult(Uri fileUri) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PICKED_ITEM_SUCCESS);
        intent.putExtra(EXTRA_PICKED_ITEM_URI, fileUri);
        sendBroadcast(intent);
        finish();
    }

    private void handleOperationCancelled() {
        Intent intent = new Intent();
        intent.setAction(ACTION_PICKED_ITEM_FAILURE);
        sendBroadcast(intent);
        finish();
    }


    // endregion

    // Static constants
    private static final String TAG = "FilePickerActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 12345;

    public static final String EXTRA_PICK_OBJECT = "PickObject";
    public static final String EXTRA_PICK_FROM = "PickFrom";
    public static final String EXTRA_PICKED_ITEM_URI = "EXTRA_PICKED_ITEM_URI";
    public static final String ACTION_PICKED_ITEM_SUCCESS = "ACTION_PICKED_ITEM_SUCCESS";
    public static final String ACTION_PICKED_ITEM_FAILURE = "ACTION_PICKED_ITEM_FAILURE";
}
