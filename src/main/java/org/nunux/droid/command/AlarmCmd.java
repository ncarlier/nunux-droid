/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nunux.droid.command;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.XmppService;

/**
 *
 * @author Nicolas Carlier
 */
public class AlarmCmd extends Command {

    MediaPlayer mMediaPlayer = null;

    public AlarmCmd(final XmppService service) throws InvalidSyntaxException {
        super("^alarm (start|stop)$",
            "alarm <start|stop>\nStart or stop ringing the phone.",
            null);

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(service, Settings.System.DEFAULT_RINGTONE_URI);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e("Droid", "Unable to initiate MediaPlayer. Alarm command will not run.", e);
            mMediaPlayer = null;
        }

        setCommandExecutor(
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if (mMediaPlayer == null) {
                        service.send("Sorry. Unable to run this command. Check device logs.");
                    }
                    else if(args.size() > 0) {
                        mMediaPlayer.isPlaying();
                        if ("start".equals(args.get(0)) && !mMediaPlayer.isPlaying()) {
                            try {
                                mMediaPlayer.prepare();
                            } catch (Exception e) {
                                Log.e("Droid", "Unable to prepare MediaPlayer. Alarm command will not run.", e);
                                service.send("Sorry. Unable to run this command. Check device logs.");
                            }
                            mMediaPlayer.start();
                        }
                        else if ("stop".equals(args.get(0)) && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }

                        service.send("Phone " + (mMediaPlayer.isPlaying() ? "" : "not") + " ringing.");
                    }
                }
            }
        );
    }
}
