package com.broadcast.chatdemo;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.broadcast.adapter.ChatAdapter;
import com.broadcast.adapter.ChatStickersAdapter;
import com.broadcast.entities.ChatDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private static final int VIDEO_CAPTURE = 101;
    private static final int RESULT_LOAD_VIDEO = 1;
    private static final int REQUEST_CODE_FOR_TAKE_PICTURE = 2;
    private static final int REQUEST_CODE_FOR_CHOOSE_PICTURE_FROM_GALLERY = 3;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private boolean isRecording = false;
    private ProgressBar mprogressBar;
    private ListView mChatListView;
    private EditText mChatMessage;
    private RelativeLayout mRelativeLayout;
    private ImageView mImageView;
    private ImageView mStickerImageView;
    private GridView mGridView;
    private ImageView mCameraButton;
    private ImageView mRecordAudioImage;
    private RelativeLayout mVoiceMessageRelativeLayout;
    private Button mStartRecordingButton;
    private Button mStopRecordingButton;
    private Button mSendVoiceMessageButton;
    private ArrayList<ChatDo> mChatDoArrayList = new ArrayList<ChatDo>();
    private ChatAdapter mChatListAdapter = null;
    private ChatStickersAdapter mChatStickerAdapter = null;
    public Integer[] mStickerIds = {
            R.drawable.smile, R.drawable.unnamed,
            R.drawable.index, R.drawable.index1,
            R.drawable.index3, R.drawable.index4,
            R.drawable.index5, R.drawable.index6,
            R.drawable.index7, R.drawable.index8,
            R.drawable.images9, R.drawable.images10,
            R.drawable.images45, R.drawable.index1,
            R.drawable.smile, R.drawable.unnamed,
            R.drawable.index, R.drawable.img,
            R.drawable.index3, R.drawable.index4,
            R.drawable.index5, R.drawable.index6,
            R.drawable.index7, R.drawable.index8,
            R.drawable.images9, R.drawable.images10,
            R.drawable.images45, R.drawable.index1,
            R.drawable.smile, R.drawable.unnamed,
            R.drawable.index, R.drawable.index1,
            R.drawable.index3, R.drawable.index4,
            R.drawable.index5, R.drawable.index6,
            R.drawable.index7, R.drawable.index8,
            R.drawable.images9, R.drawable.images10,
            R.drawable.images45, R.drawable.index1,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mChatMessage.clearFocus();
        registerEvents();
        setChatListAdapter();
        setChatStickerAdapter();
        setSupportActionBar(toolbar);
        if (!hasMicrophone()) {
            mStopRecordingButton.setEnabled(false);
            mStartRecordingButton.setEnabled(false);
        } else {
            mStopRecordingButton.setEnabled(false);
        }
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.3gp";
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI..
                return true;

            case R.id.videoButton:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                onVideoBtnClick();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendTextMessageOrAudio() {
        String s1 = mChatMessage.getText().toString();

        if (s1.equals("")) {
            mImageView.setVisibility(View.GONE);
            mRecordAudioImage.setVisibility(View.VISIBLE);

        } else {
            mImageView.setVisibility(View.VISIBLE);
            mRecordAudioImage.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mGridView = (GridView) findViewById(R.id.gv__chat_sticker);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_chat_sticker);
        mChatMessage = (EditText) findViewById(R.id.et_message_text);
        mChatListView = (ListView) findViewById(R.id.lv_chat_item);
        mImageView = (ImageView) findViewById(R.id.iv_send_image);
        mStickerImageView = (ImageView) findViewById(R.id.iv_smiley_image);
        mCameraButton = (ImageView) findViewById(R.id.iv_camera_image);
        mRecordAudioImage = (ImageView) findViewById(R.id.iv_audio_image);
        mVoiceMessageRelativeLayout = (RelativeLayout) findViewById(R.id.rl_voice_message_recording);
        mStartRecordingButton = (Button) findViewById(R.id.start_recording_button);
        mStopRecordingButton = (Button) findViewById(R.id.stop_recording_button);
        mSendVoiceMessageButton = (Button) findViewById(R.id.send_recording_button);
    }

    private void registerEvents() {
        mChatMessage.setOnClickListener(this);
        mChatMessage.addTextChangedListener(mTextWatcher);
        mStickerImageView.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);
        mRecordAudioImage.setOnClickListener(this);
        mStartRecordingButton.setOnClickListener(this);
        mStopRecordingButton.setOnClickListener(this);
        mSendVoiceMessageButton.setOnClickListener(this);
    }

    private void sendTextMsg(String msgText, int msgType) {
        ChatDo chatDo = new ChatDo();
        chatDo.setMsgType(msgType);
        chatDo.setMessage(msgText);

        if (mChatDoArrayList.size() > 0) {
            int totalItems = mChatDoArrayList.size();
            boolean isPreviousOneIsIncoming = mChatDoArrayList.get(totalItems - 1).isIncomingMsg();
            if (isPreviousOneIsIncoming) {
                chatDo.setmIncomingMessage(false);
            } else {
                chatDo.setmIncomingMessage(true);
            }
        } else {
            chatDo.setmIncomingMessage(false);
        }

        long time = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFormat.format(cal1.getTime());
        String formattedDate = dateFormat.format(new Date());

        chatDo.setMessageTime(formattedDate);
        mChatDoArrayList.add(chatDo);
    }

    private void setChatListAdapter() {
        if (mChatListAdapter == null) {
            mChatListAdapter = new ChatAdapter(this, mChatDoArrayList);
            mChatListView.setAdapter(mChatListAdapter);
        } else {
            mChatListAdapter.refreshAdapter(mChatDoArrayList);
        }
    }

    private void setChatStickerAdapter() {
        mChatStickerAdapter = new ChatStickersAdapter(this, mStickerIds);
        mGridView.setAdapter(mChatStickerAdapter);
        onClickOfStickerItem();
    }

    private void onClickOfStickerItem() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mGridView.getItemIdAtPosition(position);
                ChatDo chatDo = new ChatDo();
                chatDo.setmResStickerId(mStickerIds[position]);
                chatDo.setMsgType(ChatDo.MSG_STICKER);

                if (mChatDoArrayList.size() > 0) {
                    int totalStickers = mChatDoArrayList.size();
                    boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.get(totalStickers
                            - 1).isIncomingMsg();
                    if (isPreviousOneIsIncomingStickerItem) {
                        chatDo.setmIncomingMessage(false);
                    } else {
                        chatDo.setmIncomingMessage(true);
                    }
                } else {
                    chatDo.setmIncomingMessage(false);
                }
                mChatDoArrayList.add(chatDo);
                setChatListAdapter();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_image:
                if (mChatMessage.getText().length() > 0) {
                    onSendBtnClick();
                }
                break;
            case R.id.iv_smiley_image:
                hideAndShowStickerView();
                break;
            case R.id.et_message_text:
                if (mRelativeLayout.getVisibility() == View.VISIBLE) {
                    mRelativeLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_camera_image:
                onCameraBtnClick();
                break;
            case R.id.iv_audio_image:
                onAudioBtnClick();
                break;
            case R.id.start_recording_button:
                try {
                    onClickOfStartRecording(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_recording_button:
                onClickOfStopRecording();
                break;
            case R.id.send_recording_button:
                onClickOfSendRecording();
                break;
        }

    }

    private void hideAndShowStickerView() {
        if (mRelativeLayout.getVisibility() == View.VISIBLE) {
            mRelativeLayout.setVisibility(View.GONE);
        } else {
            mRelativeLayout.setVisibility(View.VISIBLE);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void onSendBtnClick() {
        sendTextMsg(mChatMessage.getText().toString(), ChatDo.MSG_TYPE_TEXT);
        mChatMessage.setText("");
        mChatListAdapter.notifyDataSetChanged();
        mChatListView.setSelection(mChatListAdapter.getCount() - 1);
    }

    private void onCameraBtnClick() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(),
                            "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CODE_FOR_TAKE_PICTURE);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.
                            Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_FOR_CHOOSE_PICTURE_FROM_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_TAKE_PICTURE) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 8;
                    bitmapOptions.inScaled = true;
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    //mCameraImageView.setImageBitmap(bitmap);
                    ChatDo chatDo = new ChatDo();
                    chatDo.setCameraImage(bitmap);
                    chatDo.setMsgType(ChatDo.MSG__IMAGE);

                    if (mChatDoArrayList.size() > 0) {
                        int totalStickers = mChatDoArrayList.size();
                        boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.
                                get(totalStickers
                                        - 1).isIncomingMsg();
                        if (isPreviousOneIsIncomingStickerItem) {
                            chatDo.setmIncomingMessage(false);
                        } else {
                            chatDo.setmIncomingMessage(true);
                        }
                    } else {
                        chatDo.setmIncomingMessage(false);
                    }

                    long time = System.currentTimeMillis();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTimeInMillis(time);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    dateFormat.format(cal1.getTime());
                    String formattedDate = dateFormat.format(new Date());

                    chatDo.setMessageTime(formattedDate);

                    mChatDoArrayList.add(chatDo);
                    mChatListAdapter.notifyDataSetChanged();
                    mChatListView.setSelection(mChatListAdapter.getCount() - 1);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_FOR_CHOOSE_PICTURE_FROM_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 8;
                bitmapOptions.inScaled = true;
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath, bitmapOptions));
                //mCameraImageView.setImageBitmap(thumbnail);
                ChatDo chatDo = new ChatDo();
                chatDo.setCameraImage(thumbnail);
                chatDo.setMsgType(ChatDo.MSG__IMAGE);

                if (mChatDoArrayList.size() > 0) {
                    int totalStickers = mChatDoArrayList.size();
                    boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.get(totalStickers
                            - 1).isIncomingMsg();
                    if (isPreviousOneIsIncomingStickerItem) {
                        chatDo.setmIncomingMessage(false);
                    } else {
                        chatDo.setmIncomingMessage(true);
                    }
                } else {
                    chatDo.setmIncomingMessage(false);
                }

                long time = System.currentTimeMillis();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTimeInMillis(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                dateFormat.format(cal1.getTime());
                String formattedDate = dateFormat.format(new Date());

                chatDo.setMessageTime(formattedDate);
                mChatDoArrayList.add(chatDo);
                mChatListAdapter.notifyDataSetChanged();
                mChatListView.setSelection(mChatListAdapter.getCount() - 1);
            }
            else if (requestCode == VIDEO_CAPTURE) {
                if (resultCode == RESULT_OK) {
                    Uri videoUri = data.getData();
                    ChatDo chatDo = new ChatDo();
                    chatDo.setRecordedVideoUri(videoUri);
                    chatDo.setMsgType(ChatDo.MSG_VIDEO);

                    if (mChatDoArrayList.size() > 0) {
                        int totalStickers = mChatDoArrayList.size();
                        boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.get(totalStickers
                                - 1).isIncomingMsg();
                        if (isPreviousOneIsIncomingStickerItem) {
                            chatDo.setmIncomingMessage(false);
                        } else {
                            chatDo.setmIncomingMessage(true);
                        }
                    } else {
                        chatDo.setmIncomingMessage(false);
                    }

                    long time = System.currentTimeMillis();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTimeInMillis(time);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    dateFormat.format(cal1.getTime());
                    String formattedDate = dateFormat.format(new Date());

                    chatDo.setMessageTime(formattedDate);
                    mChatDoArrayList.add(chatDo);
                    mChatListAdapter.notifyDataSetChanged();
                    mChatListView.setSelection(mChatListAdapter.getCount() - 1);
                }
            } else if (requestCode == RESULT_LOAD_VIDEO) {
                Uri selectedVideo = data.getData();
                ChatDo chatDo = new ChatDo();
                chatDo.setRecordedVideoUri(selectedVideo);
                chatDo.setMsgType(ChatDo.MSG_VIDEO);
                if (mChatDoArrayList.size() > 0) {
                    int totalStickers = mChatDoArrayList.size();
                    boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.get(totalStickers
                            - 1).isIncomingMsg();
                    if (isPreviousOneIsIncomingStickerItem) {
                        chatDo.setmIncomingMessage(false);
                    } else {
                        chatDo.setmIncomingMessage(true);
                    }
                } else {
                    chatDo.setmIncomingMessage(false);
                }

                long time = System.currentTimeMillis();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTimeInMillis(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                dateFormat.format(cal1.getTime());
                String formattedDate = dateFormat.format(new Date());

                chatDo.setMessageTime(formattedDate);
                mChatDoArrayList.add(chatDo);
                mChatListAdapter.notifyDataSetChanged();
                mChatListView.setSelection(mChatListAdapter.getCount() - 1);
            }
        }
    }

    public void onClickOfChatItemImage(ChatDo chatDo) {
        Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
        intent.putExtra("myData", chatDo);
        startActivity(intent);
    }

    private void onAudioBtnClick() {
        mStartRecordingButton.setVisibility(View.VISIBLE);
        if (mVoiceMessageRelativeLayout.getVisibility() == View.VISIBLE) {
            mVoiceMessageRelativeLayout.setVisibility(View.GONE);
        } else {
            mVoiceMessageRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    // create a textWatcher member
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            sendTextMessageOrAudio();
        }
    };


    public void onClickOfStartRecording(View view) throws IOException {
        isRecording = true;
        mStopRecordingButton.setEnabled(true);
        mStartRecordingButton.setEnabled(true);
        if (mStartRecordingButton.getVisibility() == View.VISIBLE) {
            mStopRecordingButton.setVisibility(View.VISIBLE);
        } else if (mStopRecordingButton.getVisibility() == View.VISIBLE) {
            mStartRecordingButton.setVisibility(View.VISIBLE);
        } else {
            mStartRecordingButton.setVisibility(View.VISIBLE);
        }
        ObjectAnimator anim;
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progressbar);
        mprogressBar.setVisibility(View.VISIBLE);
        anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
        anim.setDuration(35000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    public void onClickOfStopRecording() {
        if (mStopRecordingButton.getVisibility() == View.VISIBLE) {
            mStopRecordingButton.setVisibility(View.GONE);
        } else {
            mStartRecordingButton.setVisibility(View.VISIBLE);
        }

        if (mprogressBar.getVisibility() == View.VISIBLE) {
            mprogressBar.setVisibility(View.GONE);
        } else {
            mprogressBar.setVisibility(View.VISIBLE);
        }

        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


    private void onClickOfSendRecording() {
        ChatDo chatDo = new ChatDo();
        chatDo.setmVoiceMessage(audioFilePath);
        chatDo.setMsgType(ChatDo.MSG_VOICE);
        if (mChatDoArrayList.size() > 0) {
            int totalStickers = mChatDoArrayList.size();
            boolean isPreviousOneIsIncomingStickerItem = mChatDoArrayList.get(totalStickers
                    - 1).isIncomingMsg();
            if (isPreviousOneIsIncomingStickerItem) {
                chatDo.setmIncomingMessage(false);
            } else {
                chatDo.setmIncomingMessage(true);
            }
        } else {
            chatDo.setmIncomingMessage(false);
        }

        long time = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFormat.format(cal1.getTime());
        String formattedDate = dateFormat.format(new Date());
        chatDo.setMessageTime(formattedDate);

        mChatDoArrayList.add(chatDo);
        mChatListAdapter.notifyDataSetChanged();
        mChatListView.setSelection(mChatListAdapter.getCount() - 1);
        if (mVoiceMessageRelativeLayout.getVisibility() == View.VISIBLE) {
            mVoiceMessageRelativeLayout.setVisibility(View.GONE);
        }
    }

    public void startRecordingVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        String fName = "VideoFileName.mp4";
        File f = new File(fName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private void onVideoBtnClick() {
        final CharSequence[] options = {"Take Video", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Video!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Video")) {
                    startRecordingVideo();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_VIDEO);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


}
