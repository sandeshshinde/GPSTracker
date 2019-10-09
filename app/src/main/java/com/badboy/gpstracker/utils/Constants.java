package com.badboy.gpstracker.utils;

/**
 * Created by Bad Boy on 2/4/2017.
 */

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "com.badboy.gpstracker.action.main";
        String PREV_ACTION = "com.badboy.gpstracker.action.prev";
        String PLAY_ACTION = "com.badboy.gpstracker.action.play";
        String NEXT_ACTION = "com.badboy.gpstracker.action.next";
        String STARTFOREGROUND_ACTION = "com.badboy.gpstracker.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.badboy.gpstracker.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
