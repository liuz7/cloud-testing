package com.vmware.vchs;

import java.util.List;
import java.util.Map;

/**
 * Created by liuda on 9/21/15.
 */
public interface Querable {
    List get(List items);
    void delete(List items);
    void set(List items,String value);
}
