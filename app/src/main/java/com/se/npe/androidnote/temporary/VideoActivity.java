package com.se.npe.androidnote.temporary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.se.npe.androidnote.R;

import java.util.ArrayList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoActivity extends AppCompatActivity {
    JzvdStd jzvdStd;
    ArrayList<Media> select = new ArrayList<>();
    String mVideoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        jzvdStd = findViewById(R.id.videoplayer);

//        jzvdStd.thumbImageView.set
//        jzvdStd.thumbImageView.setImageURI("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
        Intent intent = new Intent(this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_VIDEO);
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, 188743680L);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 15);
        intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, select);
        startActivityForResult(intent, PickerConfig.PICKER_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PickerConfig.RESULT_CODE) {
            select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            switch (requestCode) {
                case PickerConfig.PICKER_VIDEO: {
                    Media media = select.get(0);
                    mVideoUrl = media.path;
                    jzvdStd.setUp(mVideoUrl, "", Jzvd.SCREEN_WINDOW_TINY);
//                    init();
                }
                break;
                default:
                    break;
            }
        }
    }

//    private void init() {
//
//        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
//        mNiceVideoPlayer.setUp(mVideoUrl, null);
//        Log.d("my", mVideoUrl);
//        TxVideoPlayerController controller = new TxVideoPlayerController(this);
////        controller.setTitle("I am a title");
////        controller.setLenght(98000);
//        mNiceVideoPlayer.setController(controller);
//    }
}
