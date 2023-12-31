package com.example.taskmanager.util.filepicker;

import android.app.Activity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class FilePicker {

    private final WeakReference<AppCompatActivity> context;
    private FileConfig config;

    private FilePicker(Builder builder) {
        this.context = builder.context;
        this.config = builder.config;

        checkPickType(builder.context);
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
     * ToDo : Implement the following methods. Make sure they support from Android 10 to
     * Android 14. Use the minimum permissions possible. My guess is you may need to create
     * a new activity and trigger from following functions because usual way to get these files
     * is using Intent with startActivityForResult(), but I suggest you check the latest practice
     * and see if there are newer/better ways available.
     */

    private void pickItemUsingCamera(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Toast.makeText(context.get(), "To Do", Toast.LENGTH_SHORT).show();
    }

    private void pickItemUsingGallery(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Toast.makeText(context.get(), "To Do", Toast.LENGTH_SHORT).show();
    }

    private void pickItemUsingFileManager(WeakReference<AppCompatActivity> context) {
        String MIME = getItemMIMEType(config.getPickObject());
        int quantity = config.getQuantity();
        Toast.makeText(context.get(), "To Do", Toast.LENGTH_SHORT).show();
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

        public Builder(AppCompatActivity context) {
            this.context = new WeakReference<>(context);
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
