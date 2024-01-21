package com.example.lovequest.repository;

import android.content.Context;
import android.util.Log;

import com.example.lovequest.remote.FirebaseClient;
import com.example.lovequest.model.DataModel;
import com.example.lovequest.utils.DataModelType;
import com.example.lovequest.utils.ErrorCallBack;
import com.example.lovequest.utils.NewEventCallBack;
import com.example.lovequest.webrtc.MyPeerConnectionObserver;
import com.example.lovequest.webrtc.WebRTCClient;
import com.google.gson.Gson;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;

public class MainRepository implements WebRTCClient.Listener {

    public interface Listener {
        void webrtcConnected();
        void webrtcClosed();
    }

    public Listener listener;
    private final Gson gson = new Gson();
    private final FirebaseClient firebaseClient;
    private WebRTCClient webRTCClient;
    private String currentUsername;
    private SurfaceViewRenderer remoteView;
    private String target;

    private MainRepository() {
        this.firebaseClient = new FirebaseClient();
    }

    private static MainRepository instance;
    public static MainRepository getInstance() {
        if (instance == null) {
            instance = new MainRepository();
        }
        return instance;
    }

    public void initializeWebRTCClient(Context context, String email) {
        if (webRTCClient == null) {
            updateCurrentUsername(email); // Using email as the username
            this.webRTCClient = new WebRTCClient(context, new MyPeerConnectionObserver() {
                @Override
                public void onAddStream(MediaStream mediaStream) {
                    super.onAddStream(mediaStream);
                    try {
                        mediaStream.videoTracks.get(0).addSink(remoteView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
                    Log.d("TAG", "onConnectionChange: " + newState);
                    super.onConnectionChange(newState);
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED && listener != null) {
                        listener.webrtcConnected();
                    }

                    if (newState == PeerConnection.PeerConnectionState.CLOSED ||
                            newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
                        if (listener != null) {
                            listener.webrtcClosed();
                        }
                    }
                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {
                    super.onIceCandidate(iceCandidate);
                    webRTCClient.sendIceCandidate(iceCandidate, target);
                }
            }, currentUsername);
            webRTCClient.listener = this;
        }
    }

    public void initLocalView(SurfaceViewRenderer view) {
        webRTCClient.initLocalSurfaceView(view);
    }

    public void initRemoteView(SurfaceViewRenderer view) {
        webRTCClient.initRemoteSurfaceView(view);
        this.remoteView = view;
    }

    public void startCall(String target) {
        webRTCClient.call(target);
    }

    public void switchCamera() {
        webRTCClient.switchCamera();
    }

    public void toggleAudio(Boolean shouldBeMuted) {
        webRTCClient.toggleAudio(shouldBeMuted);
    }

    public void toggleVideo(Boolean shouldBeMuted) {
        webRTCClient.toggleVideo(shouldBeMuted);
    }

    public void sendCallRequest(String targetEmail, ErrorCallBack errorCallBack) {
        Log.d("MainRepository", "Sending call request to " + targetEmail);
        DataModel callRequestDataModel = new DataModel(targetEmail, currentUsername, null, DataModelType.StartCall);

        firebaseClient.sendMessageToOtherUser(callRequestDataModel, new ErrorCallBack() {
            @Override
            public void onError() {
                Log.e("MainRepository", "Failed to send call request to " + targetEmail);
                errorCallBack.onError();
            }
        });
    }

    public void endCall() {
        webRTCClient.closeConnection();
    }

    public void subscribeForLatestEvent(NewEventCallBack callBack) {
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()) {

                case Offer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(
                            SessionDescription.Type.OFFER, model.getData()
                    ));
                    webRTCClient.answer(model.getSender());
                    break;
                case Answer:
                    this.target = model.getSender();
                    webRTCClient.onRemoteSessionReceived(new SessionDescription(
                            SessionDescription.Type.ANSWER, model.getData()
                    ));
                    break;
                case IceCandidate:
                    try {
                        IceCandidate candidate = gson.fromJson(model.getData(), IceCandidate.class);
                        webRTCClient.addIceCandidate(candidate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case StartCall:
                    this.target = model.getSender();
                    callBack.onNewEventReceived(model);
                    break;
            }

        });
    }

    @Override
    public void onTransferDataToOtherPeer(DataModel model) {
        firebaseClient.sendMessageToOtherUser(model, () -> {});
    }

    private void updateCurrentUsername(String username) {
        this.currentUsername = username;
    }
}