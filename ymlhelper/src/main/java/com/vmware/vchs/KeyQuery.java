package com.vmware.vchs;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuda on 7/30/15.
 */
public class KeyQuery implements Querable {

    public String key;

    public KeyQuery(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        return helper
                .add("file", key)
                .toString();
    }

    private void walk_through(List items,BiConsumer<Map,String> function){
        for (Object item : items) {
            if (item instanceof Map) {
                Iterator iterator=((Map) item).keySet().iterator();
                while(iterator.hasNext()) {
                    String key=iterator.next().toString();
                    String regex = this.key.replace("*", ".*");
                    Pattern keyValuePattern = Pattern.compile(regex);
                    Matcher m = keyValuePattern.matcher(key);
                    if (m.find()) {
                        function.accept(((Map) item),key);
//                        returnList.add(((Map) item).get(key));
                    }
                }
            }

        }
    }

    @Override
    public List get(List items) {
        List returnList = Lists.newArrayList();
        BiConsumer<Map,String> function = (x,y) -> {returnList.add(x.get(y));};
        walk_through(items,function);


        return returnList;
    }

    @Override
    public void delete(List items) {

        BiConsumer<Map,String> function = (x,y) -> {x.remove(y);};
        walk_through(items,function);
//        for (Object item : items) {
//            if (item instanceof Map) {
//                Iterator iterator=((Map) item).keySet().iterator();
//                while(iterator.hasNext()) {
//                    String key=iterator.next().toString();
//                    String regex = this.key.replace("*", ".*");
//                    Pattern keyValuePattern = Pattern.compile(regex);
//                    Matcher m = keyValuePattern.matcher(key);
//                    if (m.find()) {
//                        function.accept(((Map) item),key);
////                        ((Map) item).remove(key);
//                    }
//                }
//            }
//
//        }
    }
    @Override
    public void set(List items, String value) {
        BiConsumer<Map,String> function = (x,y) -> {x.put(y, value);};
        walk_through(items,function);
//        for (Object item : items) {
//            if (item instanceof Map) {
//                Iterator iterator=((Map) item).keySet().iterator();
//                while(iterator.hasNext()) {
//                    String key=iterator.next().toString();
//                    String regex = this.key.replace("*", ".*");
//                    Pattern keyValuePattern = Pattern.compile(regex);
//                    Matcher m = keyValuePattern.matcher(key);
//                    if (m.find()) {
//                        function.accept(((Map) item),key);
////                        ((Map) item).put(key, value);
//                    }
//                }
//            }
//
//        }
    }
}
