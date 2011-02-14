package org.nunux.droid.command;

import android.database.Cursor;
import java.util.Date;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.command.common.ICommandExecutor;
import org.nunux.droid.command.common.InvalidSyntaxException;
import org.nunux.droid.service.DroidService;

/**
 * Call log command.
 * @author Nicolas Carlier
 */
public class CallLogCmd extends Command {

    public CallLogCmd(final DroidService service) throws InvalidSyntaxException {
        super("^log (in|out|miss)$",
                "log <in|out|miss> : Get incoming, outgoing or missed calls.",
                new ICommandExecutor() {
                    public void execute(List<String> args) {
                        if(args.size() > 0) {
                            int type = android.provider.CallLog.Calls.MISSED_TYPE;
                            String typeLabel = "MISSED";
                            if ("in".equals(args.get(0))) {
                                type = android.provider.CallLog.Calls.INCOMING_TYPE;
                                typeLabel = "INCOMING";
                            }
                            else if ("out".equals(args.get(0))) {
                                type = android.provider.CallLog.Calls.OUTGOING_TYPE;
                                typeLabel = "OUTGOING";
                            }

                            Cursor c = service.getContentResolver().query(
                                android.provider.CallLog.Calls.CONTENT_URI,
                                null,
                                null,
                                null,
                                android.provider.CallLog.Calls.DATE+ " DESC");

                            int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                            int dateColumn = c.getColumnIndex(android.provider.CallLog.Calls.DATE);
                            int typeColumn = c.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                            int durationColumn = c.getColumnIndex(android.provider.CallLog.Calls.DURATION);

                            while (c.moveToNext()) {
                                int t = c.getInt(typeColumn);
                                if (t == type) {
                                    String number = c.getString(numberColumn);
                                    Date date = new Date(c.getLong(dateColumn));
                                    String duration = c.getString(durationColumn);
                                    StringBuilder output = new StringBuilder(typeLabel);
                                    output.append(" : ").append(number);
                                    output.append(" on ").append(date);
                                    if (type != android.provider.CallLog.Calls.MISSED_TYPE) {
                                        output.append(" during ").append(duration).append("s.");
                                    }
                                    service.send(output.toString());
                                }
                            }
                            c.close();
                        }
                    }
                });
    }
}
