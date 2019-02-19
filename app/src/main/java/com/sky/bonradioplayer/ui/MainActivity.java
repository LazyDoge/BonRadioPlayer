package com.sky.bonradioplayer.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sky.bonradioplayer.R;
import com.sky.bonradioplayer.service.RadioService;

public class MainActivity extends AppCompatActivity {

    private RadioService.PlayerServiceBinder playerServiceBinder;
    private MediaControllerCompat mediaController;
    private MediaControllerCompat.Callback callback;
    private ServiceConnection serviceConnection;

    //    private SimpleExoPlayer player;
//    private BandwidthMeter bandwidthMeter;
//    private ExtractorsFactory extractorsFactory;
//    private TrackSelection.Factory trackSelectionFactory;
//    private TrackSelector trackSelector;
//    private DefaultBandwidthMeter defaultBandwidthMeter;
//    private DataSource.Factory dataSourceFactory;
//    private MediaSource mediaSource;
    private boolean playing;
    static TextView textView;
    ProgressBar bar;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textTile);
        textView.setText(RadioService.title);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        if (playing) {
            bar.setVisibility(ProgressBar.VISIBLE);

        } else {
            bar.setVisibility(ProgressBar.INVISIBLE);

        }




//        final FloatingActionButton fab;
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playing) {

                    if (mediaController != null) {

                        mediaController.getTransportControls().play();
                        Snackbar.make(view, "Радио включено", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }


                } else {

                    if (mediaController != null) {

                        mediaController.getTransportControls().pause();
                        Snackbar.make(view, "Радио выключено", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                }


            }
        });

        callback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                if (state == null)
                    return;
                playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;
                if (playing) {
                    bar.setVisibility(ProgressBar.VISIBLE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.exo_controls_pause));
                } else {
                    bar.setVisibility(ProgressBar.INVISIBLE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.exo_controls_play));
                }



            }
        };

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                playerServiceBinder = (RadioService.PlayerServiceBinder) service;
                try {
                    mediaController = new MediaControllerCompat(MainActivity.this, playerServiceBinder.getMediaSessionToken());
                    mediaController.registerCallback(callback);
                    callback.onPlaybackStateChanged(mediaController.getPlaybackState());
                } catch (RemoteException e) {
                    mediaController = null;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerServiceBinder = null;
                if (mediaController != null) {
                    mediaController.unregisterCallback(callback);
                    mediaController = null;
                }
            }
        };

        bindService(new Intent(this, RadioService.class), serviceConnection, BIND_AUTO_CREATE);

    }

    private void setFabPlayingState(View view, FloatingActionButton fab, int visible, boolean b, int p, String s) {
        bar.setVisibility(visible);
        playing = b;
        fab.setImageDrawable(getResources().getDrawable(p));
        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerServiceBinder = null;
        if (mediaController != null) {
            mediaController.unregisterCallback(callback);
            mediaController = null;
        }
        unbindService(serviceConnection);
    }
}
