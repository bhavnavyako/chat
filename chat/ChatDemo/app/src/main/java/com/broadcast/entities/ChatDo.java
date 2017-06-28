package com.broadcast.entities;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class ChatDo implements Parcelable {
    public static final int MSG_STATUS_PENDING = 0;  //watch image
    public static final int MSG_STATUS_DELIVERED_TO_SERVER = 1; //single tick
    public static final int MSG_STATUS_DELIVERED_TO_CLIENT = 2; //double tick gray
    public static final int MSG_STATUS_READ = 3; //doublc tick blue

    public static final int MSG_TYPE_TEXT = 0;
    public static final int MSG_STICKER = 1;
    public static final int MSG__IMAGE = 2;
    public static final int MSG_VIDEO = 3;
    public static final int MSG_VOICE = 4;

    private int mId;
    private int msgType;
    private String mMessage;
    private String mMessageTime;
    private Bitmap cameraImage;
    public Boolean mIncomingMessage = true;
    private int mResStickerId;
    private String mVoiceMessage;
    private String mVoiceMessageDuration;
    private MediaStore.Video recordedVideo;

    public Uri getRecordedVideoUri() {
        return recordedVideoUri;
    }

    public void setRecordedVideoUri(Uri recordedVideoUri) {
        this.recordedVideoUri = recordedVideoUri;
    }

    private Uri recordedVideoUri;
    private boolean audioPlaying = false;

    public MediaStore.Video getRecordedVideo() {
        return recordedVideo;
    }

    public void setRecordedVideo(MediaStore.Video recordedVideo) {
        this.recordedVideo = recordedVideo;
    }

    public String getmVoiceStartAudio() {
        return mVoiceStartAudio;
    }

    public void setmVoiceStartAudio(String mVoiceStartAudio) {
        this.mVoiceStartAudio = mVoiceStartAudio;
    }

    private String mVoiceStartAudio;

    public String getmVoiceMessageDuration() {
        return mVoiceMessageDuration;
    }

    public void setmVoiceMessageDuration(String mVoiceMessageDuration) {
        this.mVoiceMessageDuration = mVoiceMessageDuration;
    }


    public String getmVoiceMessage() {
        return mVoiceMessage;
    }

    public void setmVoiceMessage(String mVoiceMessage) {
        this.mVoiceMessage = mVoiceMessage;
    }


    public ChatDo() {

    }

    protected ChatDo(Parcel in) {
        mId = in.readInt();
        msgType = in.readInt();
        mMessage = in.readString();
        mMessageTime = in.readString();
        cameraImage = in.readParcelable(Bitmap.class.getClassLoader());
        mResStickerId = in.readInt();
        mStatus = in.readInt();
    }

    public static final Creator<ChatDo> CREATOR = new Creator<ChatDo>() {
        @Override
        public ChatDo createFromParcel(Parcel in) {
            return new ChatDo(in);
        }

        @Override
        public ChatDo[] newArray(int size) {
            return new ChatDo[size];
        }
    };

    public Bitmap getCameraImage() {
        return cameraImage;
    }

    public void setCameraImage(Bitmap cameraImage) {
        this.cameraImage = cameraImage;
    }

    public int getmResStickerId() {
        return mResStickerId;
    }

    public void setmResStickerId(int mResStickerId) {
        this.mResStickerId = mResStickerId;
    }


    private int mStatus = MSG_STATUS_PENDING;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getMessageTime() {
        return mMessageTime;
    }

    public void setMessageTime(String mMessageTime) {
        this.mMessageTime = mMessageTime;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public Boolean isIncomingMsg() {
        return mIncomingMessage;
    }

    public void setmIncomingMessage(Boolean mIncomingMessage) {
        this.mIncomingMessage = mIncomingMessage;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public boolean isAudioPlaying() {
        return audioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        this.audioPlaying = audioPlaying;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(msgType);
        dest.writeString(mMessage);
        dest.writeString(mMessageTime);
        dest.writeParcelable(cameraImage, flags);
        dest.writeInt(mResStickerId);
        dest.writeInt(mStatus);
    }
}
