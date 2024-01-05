package com.example.taskmanager.util.filepicker;

public interface FilePickerBuilderBase {

    FilePicker.Builder pick(int quantity);

    FilePicker.Builder anything();

    FilePicker.Builder image();

    FilePicker.Builder video();

    FilePicker.Builder file();

    FilePicker.Builder fromAnywhere();

    FilePicker.Builder usingCamera();

    FilePicker.Builder fromGallery();

    FilePicker.Builder usingCameraOrGallery();

    FilePicker.Builder fromFileManager();

    FilePicker.Builder and(FileCallback fileCallback);

    FilePicker now();
}
