package com.midas.myvideo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;

/*
VideoPlayer Sample Project

참고URL : http://bcho.tistory.com/1056
 */
public class ActMain extends AppCompatActivity
{
    /******************************* Define *******************************/
    final static String SAMPLE_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    /******************************* Member *******************************/
    public Handler m_Handler = null;
    /******************************* Controller *******************************/
    public EditText m_edit_VideoUrl = null;
    public Button m_btn_Load = null;
    public Button m_btn_Play = null;
    public Button m_btn_Pause = null;
    public VideoView m_VideoView = null;
    public SeekBar m_SeekBar = null;
    /******************************* System Function  *******************************/
    //-----------------------------------------------------------------------------
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        initValue();
        recvIntentData();
        setInitLayout();
    }

    /******************************* User Function  *******************************/
    //-----------------------------------------------------------------------------
    //
    public void initValue()
    {

    }
    //-----------------------------------------------------------------------------
    //
    public void recvIntentData()
    {
        Intent pIntent = getIntent();
        if(pIntent == null)
            return;
    }
    //-----------------------------------------------------------------------------
    //
    public void setInitLayout()
    {
        m_Handler = new Handler();

        m_edit_VideoUrl = (EditText)findViewById(R.id.edit_VideoUrl);
        m_btn_Load = (Button)findViewById(R.id.btn_Load);
        m_btn_Play = (Button)findViewById(R.id.btn_Play);
        m_btn_Pause = (Button)findViewById(R.id.btn_Pause);
        m_VideoView = (VideoView)findViewById(R.id.videoView);
        m_SeekBar = (SeekBar)findViewById(R.id.seekBar);

        //event..
        m_btn_Load.setOnClickListener(onClickLoadVideo);
        m_btn_Play.setOnClickListener(onClickPlay);
        m_btn_Pause.setOnClickListener(onClickPause);

        //MediaController mc = new MediaController(this);
        //videoView.setMediaController(mc);
        settingView();
    }
    //-----------------------------------------------------------------------------
    //
    public void settingView()
    {
        m_edit_VideoUrl.setText(SAMPLE_VIDEO_URL);
    }
    //-----------------------------------------------------------------------------
    //
    public void loadVideo()
    {
        String url = m_edit_VideoUrl.getText().toString();

        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        m_VideoView.setVideoURI(Uri.parse(url));
        m_VideoView.requestFocus();

        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
        m_VideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra)
            {
                switch(what)
                {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        // Progress Diaglog 출력
                        Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        // Progress Dialog 삭제
                        Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                        m_VideoView.start();
                        break;
                }
                return false;
            }
        }

        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이를 한다.
        m_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                m_VideoView.start();
                long finalTime = m_VideoView.getDuration();
                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
                m_SeekBar.setMax((int) finalTime);
                m_SeekBar.setProgress(0);
                m_Handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //seekBar를 이동시키기 위한 쓰레드 객체 ,  100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.

                        long currentPosition = m_VideoView.getCurrentPosition();
                        m_SeekBar.setProgress((int) currentPosition);
                        m_Handler.postDelayed(this, 100);
                    }
                },100);

                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-----------------------------------------------------------------------------
    //
    public void playVideo()
    {
        if(m_VideoView == null)
            return;

        m_VideoView.requestFocus();
        m_VideoView.start();

    }
    //-----------------------------------------------------------------------------
    //
    public void pauseVideo()
    {
        if(m_VideoView == null)
            return;

        m_VideoView.pause();
    }

    /*********************************** listener ***********************************/
    //-----------------------------------------------------------------------------
    //
    View.OnClickListener onClickLoadVideo = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            loadVideo();
        }
    };
    //-----------------------------------------------------------------------------
    //
    View.OnClickListener onClickPlay = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            playVideo();
        }
    };
    //-----------------------------------------------------------------------------
    //
    View.OnClickListener onClickPause = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            pauseVideo();
        }
    };
}
