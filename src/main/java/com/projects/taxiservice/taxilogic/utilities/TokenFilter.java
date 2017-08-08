package com.projects.taxiservice.taxilogic.utilities;

import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Driver;

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
        exec.scheduleAtFixedRate(userTask, 0, 1, TimeUnit.MINUTES);
        exec.scheduleAtFixedRate(driverTask, 0, 1, TimeUnit.MINUTES);
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

    public static int addUserSession(String key, User user){
        if(user == null) throw new IllegalArgumentException("null user");
        if(key == null || key.length() < 1) throw new IllegalArgumentException("null or empty key");

        if(isUserSession(key)) return 0;

        SessionInformation<User> info = new SessionInformation<>();
        info.setObject(user);
        info.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpireUserToken));

        userSessions.put(key, info);
        return 1;
    }

    public static int addDriverSession(String key, Driver driver){
        if(driver == null) throw new IllegalArgumentException("null driver");
        if(key == null || key.length() < 1) throw new IllegalArgumentException("null or empty key");

        if(isDriverSession(key)) return 0;

        SessionInformation<Driver> info = new SessionInformation<>();
        info.setObject(driver);
        info.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpireDriverToken));

        driverSessions.put(key, info);
        return 1;
    }

    public static boolean removeUserSession(String key){
        if(isUserSession(key)) userSessions.remove(key);
        else return false;

        return true;
    }

    public static boolean removeDriverSession(String key){
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
