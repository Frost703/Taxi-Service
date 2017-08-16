package com.projects.taxiservice.taxilogic.utilities;

import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Driver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class to work with tokens and control sessions
 */
public final class TokenFilter {
    private static Map<String, SessionInformation<User>> userSessions = new HashMap<>();
    private static Map<String, SessionInformation<Driver>> driverSessions = new HashMap<>();

    //initialized a pool. Creates threads that check for expired tokens
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
        /**
         * Inner class to map User or Driver with expiration date
         */

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

    /**
     * Checks if specified token is in userSessions
     *
     * @param key token of user
     * @return true if userSessions contains specified token. Otherwise - false
     */
    public static boolean isUserSession(String key){
        if(key == null || key.length() < 1) return false;
        if(userSessions.containsKey(key)) return true;
        else return false;
    }

    /**
     * Checks if specified token is in driverSessions
     *
     * @param key token of driver
     * @return true if driverSessions contains specified token. Otherwise - false
     */
    public static boolean isDriverSession(String key){
        if(key == null || key.length() < 1) return false;
        if(driverSessions.containsKey(key)) return true;
        else return false;
    }

    /**
     * Adds a new user to userSessions and sets expiration date
     *
     * @param key token of user
     * @param user object that was authenticated
     * @return 1 or 0, when user is already in userSessions.
     * @exception IllegalArgumentException when user is null or key is empty
     */
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

    /**
     * Adds a new driver to driverSessions and sets expiration date
     *
     * @param key token of user
     * @param driver object that was authenticated
     * @return 1 or 0, when driver is already in driverSessions.
     * @exception IllegalArgumentException when driver is null or key is empty
     */
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

    /**
     * Removes a user from userSessions
     *
     * @param key token of user
     * @return true, when user is removed. Otherwise - false.
     */
    public static boolean removeUserSession(String key){
        if(isUserSession(key)) userSessions.remove(key);
        else return false;

        return true;
    }

    /**
     * Removes a driver from driverSessions
     *
     * @param key token of driver
     * @return true, when driver is removed. Otherwise - false.
     */
    public static boolean removeDriverSession(String key){
        if(isDriverSession(key)) driverSessions.remove(key);
        else return false;

        return true;
    }

    /**
     * Returns a user from userSessions
     *
     * @param key token of user
     * @return User object from sessions. If there isn't one - returns User.EMPTY
     */
    public static User getUser(String key){
        if(!isUserSession(key)) return User.EMPTY;
        else return userSessions.get(key).getObject();
    }

    /**
     * Returns a driver from driverSessions
     *
     * @param key token of driver
     * @return Driver object from sessions. If there isn't one - returns Driver.EMPTY
     */
    public static Driver getDriver(String key){
        if(!isDriverSession(key)) return Driver.EMPTY;
        else return driverSessions.get(key).getObject();
    }

    /**
     * Finds all users from userSessions with expired tokens and removes them
     *
     * @param now time to compare with expiration date
     */
    private static void checkAndRemoveExpiredUserSessions(LocalDateTime now){
        Set<String> expiredUserTokens = new HashSet<>();
        userSessions.forEach( (k,v) -> {
            if(v.getExpirationDate().isBefore(now)) {
                expiredUserTokens.add(k);
            }
        });

        if(expiredUserTokens.size() > 0) expiredUserTokens.forEach(userSessions::remove);
    }

    /**
     * Finds all drivers from driverSessions with expired tokens and removes them
     *
     * @param now time to compare with expiration date
     */
    private static void checkAndRemoveExpiredDriverSessions(LocalDateTime now){
        Set<String> expiredDriverTokens = new HashSet<>();
        driverSessions.forEach((k,v) -> {
            if(v.getExpirationDate().isBefore(now)) expiredDriverTokens.add(k);
        });

        if(expiredDriverTokens.size() > 0) expiredDriverTokens.forEach(driverSessions::remove);
    }
}
