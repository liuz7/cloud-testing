package com.vmware.vchs.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The object utils use Java reflection
 */
public class ObjectUtils {

    public static <T> List<String> getProperty(Class<T> klazz) {
        List<String> result = Lists.newArrayList();
        Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isSynthetic()) {
                result.add(field.getName());
            }
        }
        return result;
    }

    public static <T> List<Object> getPropertyValue(T instance) {
        List<Object> result = Lists.newArrayList();
        for (String fieldName : getProperty(instance.getClass())) {
            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                result.add(field.get(instance));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T, S> T updateProperty(T instance, String name, S value) {
        try {
            BeanUtils.setProperty(instance, name, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static <T> String getProperty(T instance, String name) {
        String value = null;
        try {
            value = BeanUtils.getProperty(instance, name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static <T> void copyProperty(T destInstance, T originalInstance) {
        try {
            BeanUtils.copyProperties(destInstance, originalInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
