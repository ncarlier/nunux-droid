package org.nunux.droid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.nunux.droid.Preferences;
import org.nunux.droid.R;
import org.nunux.droid.command.CopyCmd;
import org.nunux.droid.command.HelloWorldCmd;
import org.nunux.droid.command.HelpCmd;
import org.nunux.droid.command.TextToSpeechCmd;
import org.nunux.droid.command.LocationCmd;
import org.nunux.droid.command.SmsCmd;
import org.nunux.droid.command.UrlCmd;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.CommandCLI;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.tools.TextToSpeechHandle;

/**
 * Xmpp Service
 * @author Nicolas Carlier
 */
public class XmppService extends Service {
    /** For showing and hiding our notification. */
    NotificationManager mNM;

    /** XMPP connection configuration */
    private ConnectionConfiguration mConnectionConfiguration = null;

    /** Login parameter */
    private String mLogin;

    /** Password parameter */
    private String mPassword;

    /** SASL mechanism parameter */
    private String mSaslMechanism;

    /** External XMPP account parameter */
    private String mExternalXmppAcount;

    /** XMPP Connection */
    private XMPPConnection mConnection = null;

    /** Packet Listener */
    private PacketListener mPacketListener = null;

    /** Commands */
    private Set<Command> mCommands = new HashSet<Command>();

    // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {
        ProgressDialog dialog = ProgressDialog.show(getApplicationContext(), "", "Starting Droid Service...", true);
        handleStart(intent, startId);
        dialog.dismiss();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handleStop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Import preferences */
    private void importPreferences() {
        SharedPreferences prefs = getSharedPreferences(Preferences.SHARED_PREFERENCE_NAME, 0);
        String serverHost = prefs.getString(Preferences.KEY_SERVER_HOST_PREFERENCE, "im.apinc.org");
        int serverPort = Integer.parseInt(prefs.getString(Preferences.KEY_SERVER_PORT_PREFERENCE, "5222"));
        String serviceName = prefs.getString(Preferences.KEY_SERVICE_NAME_PREFERENCE, "im.apinc.org");
        mConnectionConfiguration = new ConnectionConfiguration(serverHost, serverPort, serviceName);
        mLogin = prefs.getString(Preferences.KEY_LOGIN_PREFERENCE, "");
        mPassword = prefs.getString(Preferences.KEY_PASSWORD_PREFERENCE, "");
        mExternalXmppAcount = prefs.getString(Preferences.KEY_EXT_XMPP_ACCOUNT_PREFERENCE, "");
        mSaslMechanism = prefs.getString(Preferences.KEY_SASL_MECHANISM_PREFERENCE, "DEFAULT");
    }

    /** Init the XMPP connection */
    public void initConnection() {
        if (mConnectionConfiguration == null) {
            importPreferences();
        }

        Log.d("Droid", "Connecting to XMPP server [" + mConnectionConfiguration.getHost() + "]...");
        mConnection = new XMPPConnection(mConnectionConfiguration);
        try {
            mConnection.connect();
            Log.d("Droid", "Successfully connected to XMPP server [" + mConnectionConfiguration.getHost() + "].");
        } catch (XMPPException e) {
            Log.e("Droid", "Unable to connect to XMPP server [" + mConnectionConfiguration.getHost() + "].", e);
            Toast.makeText(getApplicationContext(), "Connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (!"DEFAULT".equals(mSaslMechanism)) {
            SASLAuthentication.supportSASLMechanism(mSaslMechanism, 0);
        }

        Log.d("Droid", "Login in...");
        try {
            mConnection.login(mLogin, mPassword);
            Log.d("Droid", "Successfully logged.");
        } catch (XMPPException e) {
            Log.e("Droid", "Login failed.", e);
            Toast.makeText(getApplicationContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        mPacketListener = new PacketListener() {
            public void processPacket(Packet packet) {
                Message message = (Message) packet;

                // Filters self-messages and extrenal Xmpp account messages
                if (message.getFrom().toLowerCase().startsWith(mExternalXmppAcount.toLowerCase() + "/")
                        && !message.getFrom().equals(mConnection.getUser())) {
                    if (message.getBody() != null) {
                        onCommandReceived(message.getBody());
                    }
                }
            }
        };

        mConnection.addPacketListener(mPacketListener, filter);
        Log.d("Droid", "Ready to receive inbound messages.");
    }

    /** clears the XMPP connection */
    public void clearConnection() {
        if (mConnection != null) {
            if (mPacketListener != null) {
                mConnection.removePacketListener(mPacketListener);
            }
            // don't try to disconnect if already disconnected
            if (isConnected()) {
                mConnection.disconnect();
            }
        }
        mConnection = null;
        mPacketListener = null;
        mConnectionConfiguration = null;
    }

    /** Register commands. */
    private void registerCommands() {
        Log.d("Droid", "Registering commands...");
        try {
            mCommands.clear();
            // Register commands...
            mCommands.add(new HelloWorldCmd(this));
            mCommands.add(new LocationCmd(this));
            mCommands.add(new UrlCmd(this));
            mCommands.add(new CopyCmd(this));
            mCommands.add(new SmsCmd(this));
            mCommands.add(new TextToSpeechCmd(new TextToSpeechHandle(this)));
            mCommands.add(new HelpCmd(this));
            Log.d("Droid", "Commands successfully registered.");
        } catch (InvalidSyntaxException e) {
            Log.e("Droid", "Unable to register commands.", e);
            Toast.makeText(getApplicationContext(), "Unable to register commands: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
    }

    public Set<Command> getCommands() {
        return mCommands;
    }

    /** handles commands */
    private void onCommandReceived(String commandLine) {
        Log.d("Droid", "Command line received: " + commandLine);
        try {
            new CommandCLI(mCommands).execute(commandLine);
        } catch (InvalidSyntaxException e) {
            Log.e("Droid", "Unable to execute command line: " + commandLine, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected() {
        return (mConnection != null
                && mConnection.isConnected()
                && mConnection.isAuthenticated());
    }

    /**
     * {@inheritDoc}
     */
    public void send(String message) {
        if (isConnected()) {
            Log.d("Droid", "Sending back message: " + message);
            Message msg = new Message(mExternalXmppAcount, Message.Type.chat);
            msg.setBody(message);
            mConnection.sendPacket(msg);
        }
    }

    /**
     * Start the service.
     * @param intent intent
     * @param startId start id
     */
    private void handleStart(Intent intent, int startId) {
        Log.i("Droid", "Starting XmppService...");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // register commands
        registerCommands();

        // first, clean everything
        clearConnection();

        // then, re-import preferences
        importPreferences();

        // start location service
        this.startService(new Intent(this, LocationService.class));

        // and finaly init connection
        initConnection();

        if (!isConnected()) {
            Log.e("Droid", "Unable to activate XmppService. Abort.");
            onDestroy();
            return;
        }

        CharSequence text = getText(R.string.remote_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.notification_icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Preferences.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.remote_service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.remote_service_started, notification);

        // Tell the user we start the service.
        Toast.makeText(this, R.string.remote_service_started, Toast.LENGTH_SHORT).show();

        Log.i("Droid", "XmppService started.");
    }

    /** Stop service. */
    private void handleStop() {
        Log.i("Droid", "Stopping XmppService...");

        // first, clean everything
        clearConnection();

        // stop location service
        this.stopService(new Intent(this, LocationService.class));

        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started);

        // Tell the user we stop the service.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();

        Log.d("Droid", "XmppService stopped.");
    }
}
