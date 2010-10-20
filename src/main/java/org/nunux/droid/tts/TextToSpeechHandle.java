/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nunux.droid.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

/**
 *
 * @author fr23972
 */
public class TextToSpeechHandle {

    private TextToSpeech mTts;

    public TextToSpeechHandle(Context context) {
        mTts = new TextToSpeech(context,
                new TextToSpeech.OnInitListener() {

                    public void onInit(int status) {
                        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
                        if (status == TextToSpeech.SUCCESS) {
                            // Set preferred language to US english.
                            // Note that a language may not be available, and the result will indicate this.
                            int result = mTts.setLanguage(Locale.FRANCE);
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                // Lanuage data is missing or the language is not supported.
                                Log.e("Droid", "Language is not available.");
                            } else {
                                Log.i("Droid", "TextToSpeech initialized.");
                            }
                        } else {
                            // Initialization failed.
                            Log.e("Droid", "Could not initialize TextToSpeech.");
                        }
                    }
                });
    }

    public void tell(String text) {
        mTts.speak(text,
            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
            null);
    }
}
