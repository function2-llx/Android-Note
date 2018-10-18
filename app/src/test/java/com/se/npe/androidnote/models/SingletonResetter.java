package com.se.npe.androidnote.models;

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
            throw new RuntimeException();
        }
    }
}