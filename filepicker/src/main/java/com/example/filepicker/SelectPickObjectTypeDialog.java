package com.example.filepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SelectPickObjectTypeDialog extends BottomSheetDialogFragment {

    private View btnImage;
    private View btnVideo;
    private View btnFile;

    private PickObjectTypeCallback callback;

    public SelectPickObjectTypeDialog(@NonNull PickObjectTypeCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_select_pick_object_type, container, false);
        btnImage = rootView.findViewById(R.id.btn_image);
        btnVideo = rootView.findViewById(R.id.btn_video);
        btnFile = rootView.findViewById(R.id.btn_file);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onObjectTypeSelected(FilePicker.PickObject.IMAGE);
                dismiss();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onObjectTypeSelected(FilePicker.PickObject.VIDEO);
                dismiss();
            }
        });
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onObjectTypeSelected(FilePicker.PickObject.FILE);
                dismiss();
            }
        });
    }

    interface PickObjectTypeCallback {
        void onObjectTypeSelected(@NonNull FilePicker.PickObject pickObject);
    }
}
