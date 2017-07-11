package com.projects.taxiservice.taxilogic.utilities;

import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import redis.clients.jedis.Jedis;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by O'Neill on 6/29/2017.
 */
public class DirectMessenger {
    private static Jedis jedis = new Jedis("localhost");

    public static int sendMessageToDriver(Driver driver, String message){
        if(driver == null || driver.getId() < 1) throw new IllegalArgumentException("driver id < 1");
        if(message == null || message.length() < 1) throw new IllegalArgumentException("message is empty");

        jedis.lpush("driver#"+driver.getId(), message);
        return 1;
    }

    public static int sendMessageToUser(User user, String message){
        if(user == null || user.getId() < 1) throw new IllegalArgumentException("user id < 1");
        if(message == null || message.length() < 1) throw new IllegalArgumentException("message is empty");

        jedis.lpush("user#"+user.getId(), message);
        return 1;
    }

    public static boolean hasDriverMessages(Driver driver){
        if(driver == null) throw new IllegalArgumentException("null driver");
        return jedis.exists("driver#"+driver.getId());
    }

    public static boolean hasUserMessages(User user){
        if(user == null) throw new IllegalArgumentException("null user");
        return jedis.exists("user#"+user.getId());
    }

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
