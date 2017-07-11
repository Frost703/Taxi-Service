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
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(senderName).append(" ");

        //set server time
        String hour = ""+now.getHour();
        String minute = ""+now.getMinute();

        messageBuilder.append(convertSingleDigitTime(hour)).append(":")
                .append(convertSingleDigitTime(minute)).append(System.lineSeparator());

        messageBuilder.append(message);
        return messageBuilder.toString();
    }

    private static String convertSingleDigitTime(String time){
        if(time.length() > 1) return time;
        else return "0"+time;
    }
}
