package com.example.android.sampletvinput;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.utils.SampleChannelListManager;
import com.google.android.media.tv.companionlibrary.utils.TvContractUtils;


/**
 * This is the Sample Activity that demo the usage of DeepLink. Referring to the
 * "Option 3" in the EpgSyncJobService.java. You should implement your own player here
 *
 * This activity is just used to demo 3p can received the tif_id and other information from
 * the intent
 *
 */
public class DemoPlayerActivity extends Activity {

    private static final String TAG = DemoPlayerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_player_activity);
        Toast toast=Toast.makeText(this,"Launch customized player activity",Toast.LENGTH_SHORT);
        toast.show();

        initializePlayerActivity(getIntent());
    }

    private void initializePlayerActivity(final Intent intent) {
        final int originalNetworkId =intent.getIntExtra(TvContractUtils.CHANNEL_DEEP_LINK_INTENT_PRIM_KEY,-1);

        final SampleChannelListManager channelManager = SampleChannelListManager.getInstance();

        Uri currentChannelUri = channelManager.getChannelUri(originalNetworkId);

        if(currentChannelUri == null){
            //If the channel uri can't be found in the cache, it means cache need to be updated
            channelManager.updateChannelList(getContentResolver(),
                    intent.getStringExtra(TvContractUtils.CHANNEL_DEEP_LINK_INTENT_SEC_KEY));

            currentChannelUri = channelManager.getChannelUri(originalNetworkId);
        }

        playChannel(currentChannelUri);
    }

    private void playChannel(final Uri currentChannelUri) {
        final Program program = TvContractUtils.getCurrentProgram(getContentResolver(),currentChannelUri);

        if(program == null) {
            Log.i(TAG,"Program is null");
            return;
        }

        final InternalProviderData internalProviderData = program.getInternalProviderData();

        if(internalProviderData == null) {
            Log.i(TAG,"InternalProviderData is null");
            return;
        }

        final Uri uri = Uri.parse(internalProviderData.getVideoUrl());
        Log.i(TAG,"ProgramUri is: " + uri);

        // Just set Video here for demo use, you could implement your own player here.
        final VideoView videoView =(VideoView)findViewById(R.id.player_view);
        final MediaController mediaController= new MediaController(this);

        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.start();
    }
}
