package com.se.npe.androidnote.models;

import com.se.npe.androidnote.util.Logger;

import java.lang.reflect.Field;

public class SingletonResetter {

    /**
     * reset Singleton instance of class
     *
     * @param clazz     the name of class
     * @param fieldName the field name of Singleton instance
     */
    static void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            org.robolectric.util.Logger.error("Singleton reset failed in SingletonResetter.resetSingleton().", e);
        }
    }

    static void resetMySQLiteOpenHelperSingleton() {
        // "helper" is the static variable name which holds the singleton MySQLiteOpenHelper instance
        resetSingleton(MySQLiteOpenHelper.class, "helper");
    }

    static void resetDBManagerSingleton() {
        // "manager" is the static variable name which holds the singleton DBManager instance
        resetSingleton(DBManager.class, "manager");
        // Delegate to reset MySQLiteOpenHelper singleton
        resetMySQLiteOpenHelperSingleton();
    }

    /**
     * Reset TableOperate Singleton instance
     * Used in the test outside the package
     */
    public static void resetTableOperateSingleton() {
        // "tableOperate" is the static variable name which holds the singleton TableOperate instance
        resetSingleton(TableOperate.class, "tableOperate");
        // Delegate to reset DBManager singleton
        resetDBManagerSingleton(); // This function then delegate to reset MySQLiteOpenHelper singleton
    }
}