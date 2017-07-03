package com.projects.taxiservice.taxilogic.utilities;

import java.time.LocalDateTime;

/**
 * Created by O'Neill on 7/3/2017.
 */
public class MessageStyler {
    public static String style(String message, String senderName){
        if(message == null || message.length() < 1) throw new IllegalArgumentException("Message is empty");
        if(senderName == null || senderName.length() < 1) throw new IllegalArgumentException("Sender is empty");

        LocalDateTime now = LocalDateTime.now();
        StringBuilder builder = new StringBuilder();
        //set server date
//        builder.append(now.getDayOfYear()).append(".")
//                .append(now.getMonth()).append(".")
//                .append(now.getDayOfMonth()).append(" ");

        builder.append(senderName).append(" ");

        //set server time
        builder.append(now.getHour()).append(":")
                .append(now.getMinute()).append(System.lineSeparator());

        builder.append(message);
        return builder.toString();
    }
}
