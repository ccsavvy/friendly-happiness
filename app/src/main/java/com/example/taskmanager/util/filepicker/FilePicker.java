package com.example.taskmanager.util.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class FilePicker {

    private final WeakReference<AppCompatActivity> context;

    private ActivityResultLauncher<Intent> launcher;

    private FileConfig config;

    private FilePicker(Builder builder) {
        this.context = builder.context;
        this.config = builder.config;

        checkPickType(builder.context);

        this.launcher = builder.launcher;
    }

    private void checkPickType(WeakReference<AppCompatActivity> context) {
        if (config.getPickObject() == PickObject.ANY_THING) {
            new SelectPickObjectTypeDialog(pickObject -> {
                config.setPickObject(pickObject);
                checkPickFrom(context);
            }).show(context.get().getSupportFragmentManager(), "Select Pick Type");
        } else {
            checkPickFrom(context);
        }
    }

    private void checkPickFrom(WeakReference<AppCompatActivity> context) {
        if (
                config.getPickFrom() == PickFrom.ANY_WHERE ||
                        config.getPickFrom() == PickFrom.CAMERA_OR_GALLERY
        ) {
            new SelectPickFromDialog(
                    config.getPickFrom(),
                    pickFrom -> {
                        config.setPickFrom(pickFrom);
                        proceedFilePicking(context);
                    }).show(context.get().getSupportFragmentManager(), "Select Pick From");
        } else {
            proceedFilePicking(context);
        }
    }

    private void proceedFilePicking(WeakReference<AppCompatActivity> context) {
        switch (config.getPickFrom()) {
            case CAMERA:
                pickItemUsingCamera(context);
                break;
            case GALLERY:
                pickItemUsingGallery(context);
                break;
            case FILE_MANAGER:
                pickItemUsingFileManager(context);
                break;
        }
    }

    /**
     * ToDo : Implement the following methods.
     * Make sure they support from Android 10 to Android 14.
     * Use the minimum permissions possible. My guess is you
     * may need to create a new activity and
     * trigger from following functions because
     * usual way to get these files is using Intent with startActivityForResult(), but I
     * suggest you check the latest practice and
     * see if there are newer/better ways available.
     */

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "example",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //RegisterForActivityResult check out
    private void pickItemUsingCamera(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Intent captureIntent = new Intent();
        if (config.getPickObject().equals(PickObject.IMAGE))
            captureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        else if (config.getPickObject().equals(PickObject.VIDEO))
            captureIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(
                        context.get(),
                        "com.example.taskmanager.provider", photoFile
                );
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                launcher.launch(captureIntent);
            }
        //launcher.launch(captureIntent);
    }

    private void pickItemUsingGallery(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        galleryIntent.setType(MIME);
        launcher.launch(galleryIntent);
    }

    private void pickItemUsingFileManager(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileIntent.setType(MIME);
        launcher.launch(fileIntent);
    }

    private String getItemMIMEType(PickObject pickObject) {
        switch (pickObject) {
            case IMAGE:
                return "image/*";
            case VIDEO:
                return "video/*";
            case FILE:
                return "file/*";
        }
        return "*/*";
    }

    public FileConfig getConfig() {
        return config;
    }

    public static class Builder implements FilePickerBuilderBase {

        // Required params
        private final WeakReference<AppCompatActivity> context;
        private FileConfig config;

        private final ActivityResultLauncher<Intent> launcher;

        public Builder(AppCompatActivity context, ActivityResultLauncher<Intent> launcher) {
            this.context = new WeakReference<>(context);
            this.launcher = launcher;
            config = new FileConfig();
        }

        public Activity getContext() {
            return context.get();
        }

        @Override
        public Builder pick(int quantity) {
            config.setQuantity(quantity);
            return this;
        }

        @Override
        public Builder anything() {
            config.setPickObject(PickObject.ANY_THING);
            return this;
        }

        @Override
        public Builder image() {
            config.setPickObject(PickObject.IMAGE);
            return this;
        }

        @Override
        public Builder video() {
            config.setPickObject(PickObject.VIDEO);
            return this;
        }

        @Override
        public Builder file() {
            config.setPickObject(PickObject.FILE);
            return this;
        }

        @Override
        public Builder usingCamera() {
            config.setPickFrom(PickFrom.CAMERA);
            return this;
        }

        @Override
        public Builder fromGallery() {
            config.setPickFrom(PickFrom.GALLERY);
            return this;
        }

        @Override
        public Builder usingCameraOrGallery() {
            config.setPickFrom(PickFrom.CAMERA_OR_GALLERY);
            return this;
        }

        @Override
        public Builder fromFileManager() {
            config.setPickFrom(PickFrom.FILE_MANAGER);
            return this;
        }

        @Override
        public Builder fromAnywhere() {
            config.setPickFrom(PickFrom.ANY_WHERE);
            return this;
        }

        @Override
        public Builder and(FileCallback fileCallback) {
            config.setFileCallback(fileCallback);
            return this;
        }

        @Override
        public FilePicker now() {
            return new FilePicker(this);
        }
    }

    public enum PickObject {
        IMAGE(0), VIDEO(1), FILE(2), ANY_THING(2);
        private final int value;

        PickObject(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum PickFrom {

        CAMERA(0),
        GALLERY(1),
        CAMERA_OR_GALLERY(2),
        FILE_MANAGER(3),
        ANY_WHERE(4);

        private final int value;

        PickFrom(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
