package com.projects.taxiservice.taxilogic.utilities;

import java.time.LocalDateTime;

/**
 * Decorates messages
 */
public class MessageStyler {

    /**
     * Styles message. Adds sender name and date
     *
     * @param message to be decorated
     * @param senderName to display
     * @return a new decorated message
     * @exception IllegalArgumentException if message is null or empty
     * @exception IllegalArgumentException if senderName is null or empty
     */
    public static String style(String message, String senderName){
        if(message == null || message.length() < 1) throw new IllegalArgumentException("Message is empty");
        if(senderName == null || senderName.length() < 1) throw new IllegalArgumentException("Sender is empty");

        LocalDateTime now = LocalDateTime.now();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(senderName).append(" ");

        //set server time
        String hour = ""+now.getHour();
        String minute = ":"+now.getMinute();

        messageBuilder.append(convertSingleDigitTime(hour))
                .append(convertSingleDigitTime(minute)).append(System.lineSeparator());

        messageBuilder.append(message);
        return messageBuilder.toString();
    }

    /**
     * Converts single digit time. I.E. (string)"9" -> (string)"09".
     * Purpose - to display all dates in chat with a single format
     *
     * @param time string
     * @return converted string
     */
    private static String convertSingleDigitTime(String time){
        if(time.length() > 1) return time;
        else return "0"+time;
    }
}
