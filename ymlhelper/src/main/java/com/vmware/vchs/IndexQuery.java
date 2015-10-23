package com.vmware.vchs;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by liuda on 7/30/15.
 */
public class IndexQuery implements Querable {

    public int index;

    public IndexQuery(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        return helper
                .add("file", index)
                .toString();
    }

    @Override
    public List get(List items) {
        List returnList = Lists.newArrayList();
        for (Object item : items) {
            if (item instanceof List) {
                returnList.add(((List) item).get(index));
            }
        }
        return returnList;
    }

    @Override
    public void delete(List items) {
        for (Object item : items) {
            if (item instanceof List) {
                List itemList=((List) item);
                if(itemList.size()>index){
                    itemList.remove(index);
                }
            }
        }
    }
    @Override
    public void set(List items, String value)
    {
        for (Object item : items) {
            if (item instanceof List) {
                List itemList=((List) item);
                if(itemList.size()>index){
                    itemList.set(index,value);
                }
                else{
                    itemList.add(value);
                }
            }
        }
    }
}
