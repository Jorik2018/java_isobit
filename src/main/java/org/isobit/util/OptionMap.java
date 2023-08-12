package org.isobit.util;

import java.util.HashMap;

public class OptionMap extends HashMap{

    @Override
    public Object get(Object key) {
        Object o=super.get(key); 
        return o!=null?o:key;
    }
    
}
