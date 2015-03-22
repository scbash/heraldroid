package com.crystalnebula.talkingnotifications;

/**
 * Simple container for action/intent strings.
 *
 * Created by scbash on 3/14/15.
 */
public class Constants
{
    public interface ACTION
    {
        public static String MAIN_ACTION = "com.crystalnebula.talkingnotifications.action.main";
        public static String MUTE_ACTION = "com.crystalnebula.talkingnotifications.action.mute";
        public static String UNMUTE_ACTION = "com.crystalnebula.talkingnotifications.action.unmute";

        public static String STARTFOREGROUND_ACTION = "com.crystalnebula.talkingnotifications.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.crystalnebula.talkingnotifications.action.stopforeground";

        public static String STARTEXAMINE_ACTION = "com.crystalnebula.talkingnotifications.action.startexamine";
        public static String REQUEST_LOCAL_BIND = "com.crystalnebula.talkingnotifciations.action.requestlocalbind";  // for activities to get notification list
    }

    public interface NOTIFICATION_ID
    {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface JSON
    {
        public static String APPNAME = "appname";
        public static String PACKAGE = "package";
        public static String TICKERTEXT = "tickertext";
        public static String CATEGORY = "category";
        public static String NUMBER = "number";
        public static String PRIORITY = "priority";
        public static String VISIBILITY = "visibility";
        public static String FLAGS = "flags";
    }
}
