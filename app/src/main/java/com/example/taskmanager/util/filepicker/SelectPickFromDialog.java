package com.example.taskmanager.util.filepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SelectPickFromDialog extends BottomSheetDialogFragment {

    private View btnCamera;
    private View btnGallery;
    private View btnFileManager;

    private FilePicker.PickFrom currentPickFrom;
    private PickFromCallback callback;

    public SelectPickFromDialog(FilePicker.PickFrom currentPickFrom, @NonNull PickFromCallback callback) {
        this.currentPickFrom = currentPickFrom;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_select_pick_from, container, false);
        btnCamera = rootView.findViewById(R.id.btn_camera);
        btnGallery = rootView.findViewById(R.id.btn_gallery);
        btnFileManager = rootView.findViewById(R.id.btn_file_manager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPickFromSelected(FilePicker.PickFrom.CAMERA);
                dismiss();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPickFromSelected(FilePicker.PickFrom.GALLERY);
                dismiss();
            }
        });
        if (currentPickFrom == FilePicker.PickFrom.CAMERA_OR_GALLERY) {
            btnFileManager.setVisibility(View.GONE);
        } else {
            btnFileManager.setVisibility(View.VISIBLE);
            btnFileManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onPickFromSelected(FilePicker.PickFrom.FILE_MANAGER);
                    dismiss();
                }
            });
        }
    }

    interface PickFromCallback {
        void onPickFromSelected(@NonNull FilePicker.PickFrom pickFrom);
    }
}
