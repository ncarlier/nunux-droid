package org.nunux.droid.command;

import android.database.Cursor;
import android.net.Uri;
import java.util.Date;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * SMS log command.
 * @author Nicolas Carlier
 */
public class SmsLogCmd extends Command {

    public SmsLogCmd(final DroidService service) throws InvalidSyntaxException {
        super("^sms (in|out)$",
            "sms <in|out> : Get incoming or outgoing or SMS.",
            new ICommandExecutor() {
                public void execute(List<String> args) {
                    if(args.size() > 0) {
                        Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
                        String typeLabel = "IN";
                        if ("out".equals(args.get(0))) {
                            mSmsQueryUri = Uri.parse("content://sms/sent");
                            typeLabel = "OUT";
                        }

                        String columns[] = new String[] { "address", "body", "date", "status"};

                        Cursor c = service.getContentResolver().query(
                            mSmsQueryUri,
                            columns,
                            null,
                            null,
                            null);

                        if (c.getCount() > 0) {
                            for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                                Date date = new Date(c.getLong(c.getColumnIndex("date")));
                                //String person = c.getString(c.getColumnIndex("person"));
                                String address = c.getString(c.getColumnIndex("address"));
                                String body = c.getString(c.getColumnIndex("body"));

                                StringBuilder output = new StringBuilder(typeLabel);
                                output.append(" : ").append(date);
                                //output.append(" : ").append(person);
                                output.append(" : ").append(address);
                                output.append(" : ").append(body);
                                service.send(output.toString());
                            }
                        }
                        c.close();
                    }
                }
            }
        );
    }
}
