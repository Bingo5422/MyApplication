package com.example.myapplication.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;

import java.io.File;

public abstract class VoiceUtil {

    private static File pcmFile;
    private static SpeechSynthesizer mTts;
    // engine type
    private static String mEngineType = SpeechConstant.TYPE_CLOUD;

    // default speaker: xiaoyan,aisxping,aisjinger,aisbabyxu  aisjiuxu(man)

    
    private static String voicer;
    private static final String TAG = "VoiceUtil";




    static {
        // Initialize the composite object and initialize the listener

        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(MainActivity.getContext(),"Initialization failed, error code:"+ code,Toast.LENGTH_SHORT );
                } else {
                    // The initialization is successful, and then the startSpeaking method can be called
                    // Note: Some developers call startSpeaking to synthesize immediately after creating the synthetic object in the onCreate method.
                    // The correct way is to move the startSpeaking call in onCreate here
                }
            }
        });
    }




    // start compositing
    // When the onCompleted callback is received, the synthesis ends and the synthesized audio is generated
    // Synthesized audio format: only pcm format is supported
    public  static void voice(Context context, String texts,String voicer){
        pcmFile = new File(context.getExternalCacheDir().getAbsolutePath(), "tts_pcmFile.pcm");
        pcmFile.delete();
        // Setting parameters
        setParam(voicer);
        // Compose and play
        //int code = mTts.startSpeaking(texts, mTtsListener);
        int code = mTts.startSpeaking(texts, null);
//			/**
//			* Only save the audio without playing the interface, please comment the startSpeaking interface to call this interface
//          * text: the text to be synthesized, uri: the full path of the audio to be saved, listener: the callback interface
//			*/
//                String path = getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm";
//                //  synthesizeToUri Only save the audio without playing it
//                int code = mTts.synthesizeToUri(texts, path, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(MainActivity.getContext(),"Speech synthesis failed, error code："+ code,Toast.LENGTH_SHORT );
        }
    }


    private static void setParam(String voicer) {
        // clear parameters
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // Set the corresponding parameters according to the synthesis engine
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // Support real-time audio return, only supported under synthesizeToUri condition
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");

            // Set up an online synthetic speaker
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //Set synthetic speech rate
            mTts.setParameter(SpeechConstant.SPEED, "50");
            //Set Synth Pitch
            mTts.setParameter(SpeechConstant.PITCH, "50");
            //Set composition volume
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");

        }

        //Set the player audio stream type
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // Set to play synthetic audio to interrupt music playback, the default is true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

        // Set the audio save path, the save audio format supports pcm, wav,
        // set the path to sd card, please pay attention to the WRITE_EXTERNAL_STORAGE permission
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                MainActivity.getContext().getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm");
    }



}
