package com.projects.taxiservice.taxilogic.utilities;

import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Driver;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses Redis to send and receive messages between users and drivers
 */
public class DirectMessenger {
    private static Jedis jedis = new Jedis("localhost");

    /**
     * Sends message to specified driver
     *
     * @param driver object with id
     * @param message to be send to specified driver
     * @return 1 if operation was successful
     * @exception IllegalArgumentException if driver id < 1 or message is empty
     */
    public static int sendMessageToDriver(Driver driver, String message){
        if(driver == null || driver.getId() < 1) throw new IllegalArgumentException("driver id < 1");
        if(message == null || message.length() < 1) throw new IllegalArgumentException("message is empty");

        jedis.lpush("driver#"+driver.getId(), message);
        return 1;
    }

    /**
     * Sends message to specified user
     *
     * @param user object with id
     * @param message to be send to specified user
     * @return 1 if operation was successful
     * @exception IllegalArgumentException if user id < 1 or message is empty
     */
    public static int sendMessageToUser(User user, String message){
        if(user == null || user.getId() < 1) throw new IllegalArgumentException("user id < 1");
        if(message == null || message.length() < 1) throw new IllegalArgumentException("message is empty");

        jedis.lpush("user#"+user.getId(), message);
        return 1;
    }

    /**
     * Checks if specified driver has any messages
     *
     * @param driver object with id
     * @return true if there are messages to specified driver in Jedis
     * @exception IllegalArgumentException if driver is null
     */
    public static boolean hasDriverMessages(Driver driver){
        if(driver == null) throw new IllegalArgumentException("null driver");
        return jedis.exists("driver#"+driver.getId());
    }

    /**
     * Checks if specified user has any messages
     *
     * @param user object with id
     * @return true if there are messages to specified user in Jedis
     * @exception IllegalArgumentException if user is null
     */
    public static boolean hasUserMessages(User user){
        if(user == null) throw new IllegalArgumentException("null user");
        return jedis.exists("user#"+user.getId());
    }

    /**
     * Gets messages sent to specified driver
     *
     * @param driver object with id
     * @return list of messages from Jedis
     * @exception IllegalArgumentException if driver is null
     */
    public static List<String> getDriverMessages(Driver driver){
        if(driver == null) throw new IllegalArgumentException("null driver");
        ArrayList<String> messages = null;

        if(hasDriverMessages(driver)){
            messages = new ArrayList<>();
            String message;
            while((message = jedis.lpop("driver#"+driver.getId())) != null){
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * Gets messages sent to specified user
     *
     * @param user object with id
     * @return list of messages from Jedis
     * @exception IllegalArgumentException if user is null
     */
    public static List<String> getUserMessages(User user){
        if(user == null) throw new IllegalArgumentException("null user");
        ArrayList<String> messages = null;

        if(hasUserMessages(user)){
            messages = new ArrayList<>();
            String message;
            while((message = jedis.lpop("user#"+user.getId())) != null){
                messages.add(message);
            }
        }
        return messages;
    }
}
