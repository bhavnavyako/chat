package com.broadcast.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.broadcast.chatdemo.MainActivity;
import com.broadcast.chatdemo.MediaManager;
import com.broadcast.chatdemo.R;
import com.broadcast.entities.ChatDo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 1/19/2017.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatDo> mChatDoArrayList;

    private MediaManager mMediaManager;


    public ChatAdapter(Context context, ArrayList<ChatDo> chatDoArrayList) {

        this.mContext = context;
        this.mChatDoArrayList = chatDoArrayList;
        mMediaManager = MediaManager.getInstance();
    }


    @Override
    public int getCount() {
        return mChatDoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatDo chatDo = mChatDoArrayList.get(position);
        convertView = getContentViewAsPerMsgType(convertView, parent, chatDo);
        feedInfoAsPerMsgType(convertView, chatDo);
        return convertView;
    }

    private void feedInfoAsPerMsgType(final View convertView, final ChatDo chatDo) {
        TextView messageTextView = null;
        TextView dateAndTime = null;
        ImageView stickerImageView = null;
        ImageView cameraAndGalleryImage = null;
        ImageView playRecording = null;
        VideoView videoView = null;


        switch (chatDo.getMsgType()) {
            case ChatDo.MSG_TYPE_TEXT:
                messageTextView = (TextView) convertView.findViewById(R.id.tv_message_text);
                dateAndTime = (TextView) convertView.findViewById(R.id.tv_message_time);
                messageTextView.setText(chatDo.getMessage());
                dateAndTime.setText(chatDo.getMessageTime());
                break;
            case ChatDo.MSG_STICKER:
                stickerImageView = (ImageView) convertView.findViewById(R.id.iv_sticker_image);
                int stickerId = chatDo.getmResStickerId();
                stickerImageView.setImageResource(stickerId);
                break;
            case ChatDo.MSG__IMAGE:
                cameraAndGalleryImage = (ImageView) convertView.findViewById(R.id.iv_camera_chat_item_image);
                Bitmap bitmapCameraImage = chatDo.getCameraImage();
                cameraAndGalleryImage.setImageBitmap(bitmapCameraImage);

                cameraAndGalleryImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) mContext).onClickOfChatItemImage(chatDo);
                    }
                });
                dateAndTime = (TextView) convertView.findViewById(R.id.tv_message_time);
                dateAndTime.setText(chatDo.getMessageTime());
                break;
            case ChatDo.MSG_VOICE:
                playRecording = (ImageView) convertView.findViewById(R.id.iv_play_audio);
                dateAndTime = (TextView) convertView.findViewById(R.id.tv_message_time);

                playRecording.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chatDo.isAudioPlaying()) {
                            chatDo.setAudioPlaying(false);
                            mMediaManager.pauseMedia();
                        } else {

                            if (mMediaManager.getmChatDo() == null || mMediaManager.getmChatDo() != chatDo) {
                                //nothing player so far
                                mMediaManager.prepareMediaPlayer(mContext, chatDo, convertView);
                            } else if (mMediaManager.getmChatDo() == chatDo) {
                                mMediaManager.playMedia();
                            }
                            chatDo.setAudioPlaying(true);

                        }
                    }
                });

                dateAndTime.setText(chatDo.getMessageTime());
                break;
            case ChatDo.MSG_VIDEO:
                videoView = (VideoView) convertView.findViewById(R.id.vv_camera_chat_item_video);
                dateAndTime = (TextView) convertView.findViewById(R.id.tv_message_time);
                final ImageButton playButton = (ImageButton)
                        convertView.findViewById(R.id.play_video_button);
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playButton.setVisibility(View.VISIBLE);
                    }
                });
                final VideoView finalVideoView = videoView;
                playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri videoRecordedUri = chatDo.getRecordedVideoUri();
                        finalVideoView.setVideoURI(videoRecordedUri);
                        finalVideoView.start();
                        playButton.setVisibility(View.GONE);

                    }

                });

                dateAndTime.setText(chatDo.getMessageTime());
                break;

        }
    }

    private View getContentViewAsPerMsgType(View convertView, ViewGroup parent, ChatDo chatDo) {
        if (chatDo.isIncomingMsg()) {
            switch (chatDo.getMsgType()) {
                case ChatDo.MSG_TYPE_TEXT:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_item_incoming_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_item_incoming_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_item_incoming_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_item_incoming_layout);
                    }
                    break;
                case ChatDo.MSG_STICKER:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_chat_item_sticker_incoming_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_chat_item_sticker_incoming_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_chat_item_sticker_incoming_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_chat_item_sticker_incoming_layout);
                    }
                    break;
                case ChatDo.MSG__IMAGE:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_chat_item_incoming_image_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_chat_item_incoming_image_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_chat_item_incoming_image_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_chat_item_incoming_image_layout);
                    }
                    break;
                case ChatDo.MSG_VOICE:
                    if (convertView == null || convertView.getTag(R.layout.activity_chat_item_audio_incoming_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chat_item_audio_incoming_layout, parent, false);
                        convertView.setTag(R.layout.activity_chat_item_audio_incoming_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_chat_item_audio_incoming_layout);
                    }
                    break;
                case ChatDo.MSG_VIDEO:
                    if (convertView == null || convertView.getTag(R.layout.activity_chat_item_video_incoming_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chat_item_video_incoming_layout, parent, false);
                        convertView.setTag(R.layout.activity_chat_item_video_incoming_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_chat_item_video_incoming_layout);
                    }
                    break;
            }

        } else {
            switch (chatDo.getMsgType()) {
                case ChatDo.MSG_TYPE_TEXT:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_item_outgoing_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_item_outgoing_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_item_outgoing_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_item_outgoing_layout);
                    }
                    break;
                case ChatDo.MSG_STICKER:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_chat_item_outgoing_sticker_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_chat_item_outgoing_sticker_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_chat_item_outgoing_sticker_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_chat_item_outgoing_sticker_layout);
                    }
                    break;
                case ChatDo.MSG__IMAGE:
                    if (convertView == null || convertView.getTag(R.layout.activity_list_chat_item_outgoing_image_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_list_chat_item_outgoing_image_layout, parent, false);
                        convertView.setTag(R.layout.activity_list_chat_item_outgoing_image_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_list_chat_item_outgoing_image_layout);
                    }
                    break;
                case ChatDo.MSG_VOICE:
                    if (convertView == null || convertView.getTag(R.layout.activity_chat_item_audio_outgoing_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chat_item_audio_outgoing_layout, parent, false);
                        convertView.setTag(R.layout.activity_chat_item_audio_outgoing_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_chat_item_audio_outgoing_layout);
                    }
                    break;
                case ChatDo.MSG_VIDEO:
                    if (convertView == null || convertView.getTag(R.layout.activity_chat_item_video_outgoing_layout) == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_chat_item_video_outgoing_layout, parent, false);
                        convertView.setTag(R.layout.activity_chat_item_video_outgoing_layout, convertView);
                    } else {
                        convertView = (View) convertView.getTag(R.layout.activity_chat_item_video_outgoing_layout);
                    }
                    break;
            }

        }
        return convertView;
    }


    /**
     * Refresh the adapter*
     */
    public void refreshAdapter(ArrayList<ChatDo> chatDoArrayList) {
        this.mChatDoArrayList = chatDoArrayList;
        notifyDataSetChanged();
    }

}


