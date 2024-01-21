package com.example.lovequest;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lovequest.databinding.ActivityCallBinding;
import com.example.lovequest.repository.MainRepository;
import com.example.lovequest.utils.DataModelType;
import com.example.lovequest.utils.ErrorCallBack;
import com.example.lovequest.utils.FirebaseUtil;

public class CallActivity extends AppCompatActivity implements MainRepository.Listener {

    private ActivityCallBinding views;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        mainRepository = MainRepository.getInstance();

        String chatroomId = getIntent().getStringExtra("chatroomId");
        if (chatroomId != null) {
            // Split the chatroom ID to get individual user IDs
            String[] userIds = chatroomId.split("_");
            String currentUserID = FirebaseUtil.getCurrentUserId();
            String otherUserID = userIds[0].equals(currentUserID) ? userIds[1] : userIds[0];
            initiateCall(otherUserID);
        }

        setupCommonViews();
        setupSurfaceViews();
    }

    private void setupSurfaceViews() {
        // Initialize only if views are null to prevent re-initialization
        if (views.localView.getHolder().getSurface() == null) {
            mainRepository.initLocalView(views.localView);
        }
        if (views.remoteView.getHolder().getSurface() == null) {
            mainRepository.initRemoteView(views.remoteView);
        }
        mainRepository.listener = this;

        // Make SurfaceViews visible
        views.localView.setVisibility(View.VISIBLE);
        views.remoteView.setVisibility(View.VISIBLE);
    }

    private void initiateCall(String targetEmail) {
        mainRepository.sendCallRequest(targetEmail, new ErrorCallBack() {
            @Override
            public void onError() {
                Toast.makeText(CallActivity.this, "Couldn't find the target", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupCommonViews() {
        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    views.incomingNameTV.setText(data.getSender() + " is calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v -> {
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v -> views.incomingCallLayout.setVisibility(View.GONE));
                });
            }
        });

        setupControlButtons();
    }

    private void setupControlButtons() {
        views.switchCameraButton.setOnClickListener(v -> mainRepository.switchCamera());
        views.micButton.setOnClickListener(v -> toggleMic());
        views.videoButton.setOnClickListener(v -> toggleVideo());
        views.endCallButton.setOnClickListener(v -> {
            mainRepository.endCall();
            finish();
        });
    }

    private void toggleMic() {
        isMicrophoneMuted = !isMicrophoneMuted;
        mainRepository.toggleAudio(isMicrophoneMuted);
        int icon = isMicrophoneMuted ? R.drawable.ic_baseline_mic_off_24 : R.drawable.ic_baseline_mic_24;
        views.micButton.setBackgroundResource(icon);
    }

    private void toggleVideo() {
        isCameraMuted = !isCameraMuted;
        mainRepository.toggleVideo(isCameraMuted);
        int icon = isCameraMuted ? R.drawable.ic_baseline_videocam_off_24 : R.drawable.ic_baseline_videocam_24;
        views.videoButton.setBackgroundResource(icon);
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(() -> {
            views.incomingCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSurfaceViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Properly release resources, if required
        mainRepository.endCall();
    }
}