package com.example.taskmanager.util.filepicker;

import android.net.Uri;

public interface FileCallback {
    void onFileSelected(Uri fileUri);
}
