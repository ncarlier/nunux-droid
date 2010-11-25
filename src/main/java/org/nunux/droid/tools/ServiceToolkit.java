package org.nunux.droid.tools;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import java.util.List;

/**
 * Toolkit.
 * @author Nicolas Carlier
 */
public class ServiceToolkit {

    private ServiceToolkit() {
    }

    public static ComponentName isServiceExisted(Context context, String className) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> serviceList =
                activityManager.getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return null;
        }

        for(int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if(serviceName.getClassName().equals(className)) {
                return serviceName;
            }
        }
        return null;
    }

}
