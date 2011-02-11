package org.nunux.droid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
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
import org.nunux.droid.command.AlarmCmd;
import org.nunux.droid.command.CallLogCmd;
import org.nunux.droid.command.CopyCmd;
import org.nunux.droid.command.HelpCmd;
import org.nunux.droid.command.IpCmd;
import org.nunux.droid.command.LocationCmd;
import org.nunux.droid.command.SmsCmd;
import org.nunux.droid.command.TextToSpeechCmd;
import org.nunux.droid.command.UrlCmd;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.CommandCLI;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.tools.CommandRegistrationHelper;

/**
 * Droid Service
 * @author Nicolas Carlier
 */
public class DroidService extends Service {
    public final static String TAG = "DROID";

    /** For showing and hiding our notification. */
    NotificationManager mNM;

    /** XMPP connection configuration */
    private ConnectionConfiguration mConnectionConfiguration = null;

    /** Command registration helper */
    private CommandRegistrationHelper mCommandRegistrationHelper = null;

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
    
    /** Location Service */
    private ILocationService locationService;

    /** Location Service connection */
    private LocationServiceConnection locationServiceConnection;

    class LocationServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            locationService = ILocationService.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
        }
    }

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

        Log.d(TAG, "Connecting to XMPP server [" + mConnectionConfiguration.getHost() + "]...");
        mConnection = new XMPPConnection(mConnectionConfiguration);
        try {
            mConnection.connect();
            Log.d(TAG, "Successfully connected to XMPP server [" + mConnectionConfiguration.getHost() + "].");
        } catch (XMPPException e) {
            Log.e(TAG, "Unable to connect to XMPP server [" + mConnectionConfiguration.getHost() + "].", e);
            Toast.makeText(getApplicationContext(), "Connection failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (!"DEFAULT".equals(mSaslMechanism)) {
            SASLAuthentication.supportSASLMechanism(mSaslMechanism, 0);
        }

        Log.d(TAG, "Login in...");
        try {
            mConnection.login(mLogin, mPassword);
            Log.d(TAG, "Successfully logged.");
        } catch (XMPPException e) {
            Log.e(TAG, "Login failed.", e);
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
        Log.d(TAG, "Ready to receive inbound messages.");
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
        Log.d(TAG, "Registering commands...");
//        try {
//            mCommandRegister = new CommandRegistrationHelper("org.nunux.droid.command", this);
            mCommandRegistrationHelper = new CommandRegistrationHelper(this,
                    AlarmCmd.class,
                    CallLogCmd.class,
                    CopyCmd.class,
                    IpCmd.class,
                    LocationCmd.class,
                    SmsCmd.class,
                    TextToSpeechCmd.class,
                    UrlCmd.class,
                    HelpCmd.class);
//        } catch (ClassNotFoundException ex) {
//            Log.e(TAG, "Unable to register commands.", ex);
//            Toast.makeText(getApplicationContext(), "Unable to register commands: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            return;
//        } catch (IOException ex) {
//            Log.e(TAG, "Unable to register commands.", ex);
//            Toast.makeText(getApplicationContext(), "Unable to register commands: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            return;
//        }
        Log.d(TAG, mCommandRegistrationHelper.getCommands().size() + " command(s) successfully registered.");
    }

    /** @retun regitered commands.*/
    public List<Command> getCommands() {
        return mCommandRegistrationHelper.getCommands();
    }

    /** handles commands */
    private void onCommandReceived(String commandLine) {
        Log.d(TAG, "Command line received: " + commandLine);
        try {
            new CommandCLI(getCommands()).execute(commandLine);
        } catch (InvalidSyntaxException e) {
            this.send("Unable to execute command line: " + commandLine);
            Log.e(TAG, "Unable to execute command line: " + commandLine, e);
        }
    }

    /**
     * Test if the XMPP connection is established.
     * @return <code>true</code> if connected, <code>false</code> else.
     */
    public boolean isConnected() {
        return (mConnection != null
                && mConnection.isConnected()
                && mConnection.isAuthenticated());
    }

    /**
     * Send XMPP message.
     * @param message the message
     */
    public void send(String message) {
        if (isConnected()) {
            Log.d(TAG, "Sending back message: " + message);
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
        Log.i(TAG, "Starting DroidService...");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // register commands
        registerCommands();

        // first, clean everything
        clearConnection();

        // then, re-import preferences
        importPreferences();

        // start location service
        locationServiceConnection = new LocationServiceConnection();
        boolean ret = bindService(new Intent(this, LocationService.class), locationServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Doid service bound with Location service:" + ret);

        // and finaly init connection
        initConnection();

        if (!isConnected()) {
            Log.e(TAG, "Unable to start DroidService. Abort.");
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

        Log.i(TAG, "DroidService started.");
    }

    /** Stop service. */
    private void handleStop() {
        Log.i(TAG, "Stopping DroidService...");

        // first, clean everything
        clearConnection();

        // stop location service
        unbindService(locationServiceConnection);
        locationServiceConnection = null;
        Log.d(TAG, "Location service unbound.");
        //this.stopService(new Intent(this, LocationService.class));

        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started);

        // Tell the user we stop the service.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "DroidService stopped.");
    }

    public ILocationService getLocationService() {
        return locationService;
    }
}
