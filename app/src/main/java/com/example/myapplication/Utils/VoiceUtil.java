package com.example.myapplication.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;

import java.io.File;

public class VoiceUtil {

    private static File pcmFile;
    private static SpeechSynthesizer mTts;
    // 引擎类型
    private static String mEngineType = SpeechConstant.TYPE_CLOUD;

    // 默认发音人: xiaoyan,aisxping,aisjinger,aisbabyxu  aisjiuxu(男)
    private static String voicer = "aisbabyxu";

    private static final String TAG = "VoiceUtil";

    static {
        // 初始化合成对象，并初始化监听

        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(MainActivity.getContext(),"初始化失败,错误码："+ code,Toast.LENGTH_SHORT );
                } else {
                    // 初始化成功，之后可以调用startSpeaking方法
                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
                }
            }
        });
    }



    // 开始合成
    // 收到onCompleted 回调时，合成结束、生成合成音频
    // 合成的音频格式：只支持pcm格式
    public static void voice(Context context,String texts){
        pcmFile = new File(context.getExternalCacheDir().getAbsolutePath(), "tts_pcmFile.pcm");
        pcmFile.delete();
        // 设置参数
        setParam();
        // 合成并播放
        //int code = mTts.startSpeaking(texts, mTtsListener);
        int code = mTts.startSpeaking(texts, null);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//                String path = getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm";
//                //  synthesizeToUri 只保存音频不进行播放
//                int code = mTts.synthesizeToUri(texts, path, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(MainActivity.getContext(),"语音合成失败,错误码："+ code,Toast.LENGTH_SHORT );
        }
    }

    private static void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 支持实时音频返回，仅在 synthesizeToUri 条件下支持
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");

            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50");
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");

        }

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                MainActivity.getContext().getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm");
    }


}
