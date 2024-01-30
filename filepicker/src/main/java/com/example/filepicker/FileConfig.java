package com.example.filepicker;

public class FileConfig {

    private FilePicker.PickObject pickObject = FilePicker.PickObject.ANY_THING;
    private FilePicker.PickFrom pickFrom = FilePicker.PickFrom.ANY_WHERE;
    private FileCallback fileCallback = null;
    private int quantity = 1;

    public FilePicker.PickObject getPickObject() {
        return pickObject;
    }

    public void setPickObject(FilePicker.PickObject pickObject) {
        this.pickObject = pickObject;
    }

    public FilePicker.PickFrom getPickFrom() {
        return pickFrom;
    }

    public void setPickFrom(FilePicker.PickFrom pickFrom) {
        this.pickFrom = pickFrom;
    }

    public FileCallback getFileCallback() {
        return fileCallback;
    }

    public void setFileCallback(FileCallback fileCallback) {
        this.fileCallback = fileCallback;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
