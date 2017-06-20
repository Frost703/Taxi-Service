package com.projects.taxiservice.taxilogic.utilities;

import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by O'Neill on 6/8/2017.
 */
public final class TokenFilter {
    private static Map<String, SessionInformation<User>> userSessions = new HashMap<>();
    private static Map<String, SessionInformation<Driver>> driverSessions = new HashMap<>();

    private static ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
    static {
        Runnable userTask = () -> checkAndRemoveExpiredUserSessions(LocalDateTime.now());
        Runnable driverTask = () -> checkAndRemoveExpiredDriverSessions(LocalDateTime.now());
        exec.scheduleAtFixedRate(userTask, 0, 5, TimeUnit.SECONDS);
        exec.scheduleAtFixedRate(driverTask, 0, 5, TimeUnit.SECONDS);
    }

    private static final int hoursToExpireUserToken = 1;
    private static final int hoursToExpireDriverToken = 10;

    private static class SessionInformation <T>{
        T object;
        LocalDateTime expirationDate;

        private T getObject() {
            return object;
        }

        private void setObject(T object) {
            this.object = object;
        }

        private LocalDateTime getExpirationDate() {
            return expirationDate;
        }

        private void setExpirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
        }
    }

    public static boolean isUserSession(String key){
        if(key == null || key.length() < 1) return false;
        if(userSessions.containsKey(key)) return true;
        else return false;
    }

    public static boolean isDriverSession(String key){
        if(key == null || key.length() < 1) return false;
        if(driverSessions.containsKey(key)) return true;
        else return false;
    }

    public static void addUserSession(String key, User user){
        SessionInformation<User> info = new SessionInformation<>();
        info.setObject(user);
        info.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpireUserToken));

        userSessions.put(key, info);
    }

    public static void addDriverSession(String key, Driver driver){
        SessionInformation<Driver> info = new SessionInformation<>();
        info.setObject(driver);
        info.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpireDriverToken));

        driverSessions.put(key, info);
    }

    public static boolean removeDriverSession(String key){
        if(isUserSession(key)) userSessions.remove(key);
        else return false;

        return true;
    }

    public static boolean removeUserSession(String key){
        if(isDriverSession(key)) driverSessions.remove(key);
        else return false;

        return true;
    }

    public static User getUser(String key){
        if(!isUserSession(key)) return User.EMPTY;

        User user = userSessions.get(key).getObject();
        return user;
    }

    public static Driver getDriver(String key){
        if(!isDriverSession(key)) return Driver.EMPTY;
        else return driverSessions.get(key).getObject();
    }

    private static void checkAndRemoveExpiredUserSessions(LocalDateTime now){
        Set<String> expiredUserTokens = new HashSet<>();
        userSessions.forEach( (k,v) -> {
            if(v.getExpirationDate().isBefore(now)) {
                expiredUserTokens.add(k);
            }
        });

        if(expiredUserTokens.size() > 0) expiredUserTokens.forEach(userSessions::remove);
    }

    private static void checkAndRemoveExpiredDriverSessions(LocalDateTime now){
        Set<String> expiredDriverTokens = new HashSet<>();
        driverSessions.forEach((k,v) -> {
            if(v.getExpirationDate().isBefore(now)) expiredDriverTokens.add(k);
        });

        if(expiredDriverTokens.size() > 0) expiredDriverTokens.forEach(driverSessions::remove);
    }
}
