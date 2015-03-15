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
    }

    public interface NOTIFICATION_ID
    {
        public static int FOREGROUND_SERVICE = 101;
    }
}
