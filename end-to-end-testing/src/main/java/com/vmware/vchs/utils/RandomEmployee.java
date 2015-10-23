package com.vmware.vchs.utils;

import com.vmware.vchs.test.client.db.model.Employee;

import java.util.Random;

/**
 * Created by liuda on 6/11/15.
 */
public class RandomEmployee {
    private static Random rand = new Random();
    private static int index = 0;

    public static Employee getRandomEmployee() {
        index++;
        int key = index * 10000 + rand.nextInt() % 10000;
        return new Employee(key, "name_" + key, "role_" + key);
    }

}
