package com.vmware.vchs;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuda on 7/30/15.
 */
public class AttributeQuery implements Querable {

    public String key;
    public String value;
    public String attribute;

    public AttributeQuery(String attribute) throws Exception {
        String tmp = attribute.substring(1, attribute.length() - 1);
        String[] splitList = tmp.split("=");
        if (splitList != null && splitList.length == 2) {
            this.key = splitList[0];
            this.value = splitList[1];
            this.attribute = tmp;
        } else if (splitList != null && splitList.length == 1) {
            this.key = splitList[0];
        } else {
            throw new Exception("wrong parameter in AttributeQuery");
        }
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        return helper
                .add("key", key)
                .add("value", value)
                .toString();
    }


//    private void operate(List addList, Object item, OptionHandler.Operation operation, List removeList, String mapKey, int listIndex, String setValue) {
//        switch (operation) {
//            case GET:
//                addList.add(item);
//                break;
//            case DELETE:
//                if (item instanceof Map) {
//                    ((Map) item).remove(mapKey);
//                } else if (listIndex >= 0) {
//                    removeList.remove(listIndex);
//                }
//                break;
//            case PUT:
//                if (item instanceof Map) {
//                    ((Map) item).put(mapKey, setValue);
//                } else if (listIndex >= 0) {
//                    removeList.set(listIndex, setValue);
//                }
//                break;
//        }
//    }

    private List walk_through(List items, BiConsumer function) {
        List returnList = Lists.newArrayList();
        for (Object item : items) {
            if (item instanceof List) {
                List tmpItem = ((List) item);
                for (int index = 0; index < tmpItem.size(); index++) {
                    if (value == null && key.equalsIgnoreCase("*")) {
                        function.accept(tmpItem,index);
                    } else if (tmpItem.get(index) instanceof Map) {
                        Iterator iterator = ((Map) tmpItem.get(index)).entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            String mapKey = key.replace("*", ".*");
                            Pattern keyValuePattern = Pattern.compile(mapKey);
                            Matcher m = keyValuePattern.matcher(entry.getKey().toString());
                            if (m.find()) {
                                String mapValue = value.replace("*", ".*");
                                keyValuePattern = Pattern.compile(mapValue);
                                m = keyValuePattern.matcher(entry.getValue().toString());
                                if (m.find()) {
                                    function.accept(tmpItem,index);
//                                    function.accept(tmpItem.get(index), index);
//                                    operate(returnList, tmpItem.get(index), operation, tmpItem, this.key, index, setValue);
                                }
                            }
                        }
                    } else if (tmpItem.get(index) instanceof String) {
                        String regex = this.attribute.replace("*", ".*");
                         regex = regex.replace("{", "\\{");  //FIXME other special character
                        Pattern keyValuePattern = Pattern.compile(regex);
                        Matcher m = keyValuePattern.matcher(((String) tmpItem.get(index)));
                        if (m.find()) {
                            function.accept(tmpItem,index);
//                            function.accept(tmpItem.get(index),index);
//                            operate(returnList, tmpItem.get(index), operation, tmpItem, this.key, index, setValue);
                        }
                    }
                }
            }
        }

        return returnList;
    }

    @Override
    public List get(List items) {
        List retList=Lists.newArrayList();
        BiConsumer<List,Integer> function = (x,y) -> {retList.add(x.get(y));};
        walk_through(items,function);
        return retList;
//        List returnList= Lists.newArrayList();
//        for(Object item:items){
//            if(item instanceof List){
//                for(Object detailItem:((List) item)){
//                    if(detailItem instanceof Map){
//                        String originalValue=((Map) detailItem).get(key).toString();
//                        if(originalValue.equalsIgnoreCase(this.value)){
//                            returnList.add(detailItem);
//                        }
//                        else if(this.value.equalsIgnoreCase("*")&&originalValue!=null){
//                            returnList.add(detailItem);
//                        }
//                        else if(this.value.contains("*")){
//                            String regex=this.value.replace("*",".*");
//                            Pattern  keyValuePattern = Pattern.compile("\\["+regex+"\\]");
//                            Matcher m = keyValuePattern.matcher(originalValue);
//                            if (m.find()) {
//                                returnList.add(detailItem);
//                            }
//                        }
//                    }
//                    else if(detailItem instanceof String){
//                        if(((String) detailItem).equalsIgnoreCase(this.attribute)){
//                            returnList.add(detailItem);
//                        }
//                    }
//                }
//            }
//            else if(item instanceof Map){
//                String value=((Map) item).get(key).toString();
//                //FIXME: *
//                if(value.equalsIgnoreCase(this.value)){
//                    returnList.add(item);
//                }
//            }
//            else if(item instanceof String){
//                if(((String) item).equalsIgnoreCase(this.attribute)){
//                    returnList.add(item);
//                }
//            }
//        }
//        return returnList;

    }

    @Override
    public void delete(List items) {
        BiConsumer<List,Integer> function = (x,y) -> {x.remove(y);};
        walk_through(items, function);
//        for(Object item:items) {
//            if (item instanceof List) {
//                List tmpItem = ((List) item);
//                for (int index = 0; index < tmpItem.size(); index++) {
//                    if (tmpItem.get(index) instanceof Map) {
//                        String originalValue = ((Map) tmpItem.get(index)).get(key).toString();
//                        //FIXME: *
//                        if (originalValue.equalsIgnoreCase(this.value)) {
//                            ((Map) tmpItem.get(index)).remove(this.key);
//                        }
//                    } else if (tmpItem.get(index) instanceof String) {
//                        if (((String) tmpItem.get(index)).equalsIgnoreCase(this.attribute)) {
//                            tmpItem.remove(index);
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void set(List items, String value) {
        BiConsumer<List,Integer> function = (x,y) -> {x.set(y, value);};
        walk_through(items,function);


//        for(Object item:items){
//            if(item instanceof List){
//                List tmpItem=((List) item);
//                for(int index=0;index<tmpItem.size();index++){
//                    if(tmpItem.get(index) instanceof Map){
//                        String originalValue=((Map) tmpItem.get(index)).get(key).toString();
//                        //FIXME: *
//                        if(originalValue.equalsIgnoreCase(this.value)){
//                            ((Map) tmpItem.get(index)).put(key,value);
//                        }
//                    }
//                    else if(tmpItem.get(index) instanceof String){
//                        if(((String) tmpItem.get(index)).equalsIgnoreCase(this.attribute)){
//                            tmpItem.set(index,value);
//                        }
//                    }
//                }
//            }
//            else if(item instanceof Map){
//                String originalValue=((Map) item).get(key).toString();
//                //FIXME: *
//                if(originalValue.equalsIgnoreCase(this.value)){
//                    ((Map) item).put(key, value);
//                }
//            }
//            else if(item instanceof String){
//                if(((String) item).equalsIgnoreCase(this.attribute)){
//                    returnList.add(item);
//                }
//            }
    }
}
