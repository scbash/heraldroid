package com.crystalnebula.talkingsms;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

/**
 * Quoting the original tutorial, "... a helper class for the TTS engine.  This class is used to
 * avoid calling the TTS API directly from the Activity."
 *
 * Created by scbash on 2/22/15.
 */
public class Speaker implements TextToSpeech.OnInitListener
{
    private TextToSpeech tts;

    private boolean ready = false;

    private boolean allowed = false;

    public Speaker(Context context)
    {
        tts = new TextToSpeech(context, this);
    }

    @SuppressWarnings("unused")  // it's just good style to have getters for private members
    public boolean isAllowed()
    {
        return allowed;
    }

    public void allow(boolean allowed)
    {
        this.allowed = allowed;
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
           // Change this to match your locale
           tts.setLanguage(Locale.US);
           ready = true;
        }
        else
        {
            ready = false;
        }
    }

    /* This form of speak is deprecated in API 21, but it's replacement is also introduced in API
     * 21.  So for backwards compatibility with API 18 (desired functional API level for this
     * project), use the old method and suppress the warning.
     */
    @SuppressWarnings("deprecation")
    public void speak(String text)
    {
        // Speak only if TTS is ready and the user has allowed speech
        if (ready && allowed)
        {
            HashMap<String, String> hash = new HashMap<>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                     String.valueOf(AudioManager.STREAM_NOTIFICATION));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }

    /* This form of playSilence is deprecated in API 21, but it's replacement is also introduced
     * in API 21.  So for backwards compatibility with API 18 (desired functional API level for
     * this project), use the old method and suppress the warning.
     */
    @SuppressWarnings("deprecation")
    public void pause(int duration)
    {
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void destroy()
    {
        tts.shutdown();
    }
}
