package org.ttn.ecommerce.utils;

import java.util.HashSet;
import java.util.Set;

public class StringToSetParser {

    public static String toCommaSeparatedString(Set<String> valueSet){
        String values = "";
        if(valueSet.isEmpty())
            return values;

        for(String value : valueSet){
            values = values + value + ",";
        }
        values = values.substring(0, values.length() - 1);
        return values;
    }

    public static Set<String> toSetOfValues(String value){
        Set<String> values = new HashSet<>();
        String[] splitValues = value.split(",");

        for(String splitValue : splitValues){
            values.add(splitValue);
        }
        return values;
    }

}