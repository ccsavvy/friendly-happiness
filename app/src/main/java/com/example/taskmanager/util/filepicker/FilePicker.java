package com.example.taskmanager.util.filepicker;

import static com.example.taskmanager.util.filepicker.FilePickerActivity.ACTION_PICKED_ITEM_FAILURE;
import static com.example.taskmanager.util.filepicker.FilePickerActivity.ACTION_PICKED_ITEM_SUCCESS;
import static com.example.taskmanager.util.filepicker.FilePickerActivity.EXTRA_PICKED_ITEM_URI;
import static com.example.taskmanager.util.filepicker.FilePickerActivity.EXTRA_PICK_FROM;
import static com.example.taskmanager.util.filepicker.FilePickerActivity.EXTRA_PICK_OBJECT;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.Objects;


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

    private BroadcastReceiver filePickerResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(filePickerResultReceiver);
            if (Objects.equals(intent.getAction(), ACTION_PICKED_ITEM_FAILURE)) {
                config.getFileCallback().onOperationCancelled();
            } else {
                try {
                    Uri pickedFileUri = (Uri) intent.getExtras().get(EXTRA_PICKED_ITEM_URI);
                    config.getFileCallback().onFileSelected(
                            pickedFileUri,
                            config.getPickObject()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    config.getFileCallback().onOperationCancelled();
                }
            }
        }
    };

    private void proceedFilePicking(WeakReference<AppCompatActivity> context) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PICKED_ITEM_SUCCESS);
        filter.addAction(ACTION_PICKED_ITEM_FAILURE);
        ContextCompat.registerReceiver(
                context.get(),
                filePickerResultReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );

        Intent filePickerIntent = new Intent(context.get(), FilePickerActivity.class);
        // filePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // filePickerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        filePickerIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        filePickerIntent.putExtra(EXTRA_PICK_OBJECT, config.getPickObject().ordinal());
        filePickerIntent.putExtra(EXTRA_PICK_FROM, config.getPickFrom().ordinal());
        context.get().startActivity(filePickerIntent);
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
