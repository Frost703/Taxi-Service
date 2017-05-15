package com.projects.taxiservice.TaxiLogic;

import java.io.*;

import java.util.HashSet;

/**
 * Created by O'Neill on 7/14/2016.
 */
public class MyLogger {
    private static HashSet<String> loggingInformation = new HashSet<String>();
    private static boolean isConsoleLogEnabled = true;
    private static File logingFile = new File("loggingInformation.txt");

    public static void addToLog(String s){
        loggingInformation.add(s);
        saveToLogFile();
        if(isConsoleLogEnabled) System.out.println(s);
    }

    public static HashSet<String> getLogInf(){
        return loggingInformation;
    }

    public static void enableConsoleLog(){
        isConsoleLogEnabled = true;
    }

    public static void disableConsoleLog(){
        isConsoleLogEnabled = false;
    }

    private static void saveToLogFile(){
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(logingFile), "utf-8"));
            for(String s : loggingInformation)
            writer.write(s + "\n");
        } catch (IOException ex) {
            // report
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }
}
