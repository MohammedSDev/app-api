package com.digital.app;

import java.util.*;
//https://stackoverflow.com/questions/52985124/retrofit-multiple-query-parameters-of-same-name-where-name-is-set-dynamically
//https://github.com/square/retrofit/issues/1324#issuecomment-370323411
public class AppParamMap<K,V> extends HashMap<K,V> {


    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> originSet = super.entrySet();
        Set<Entry<K, V>> newSet = new HashSet<>();

        for (Entry<K, V> entry : originSet) {
            K entryKey = entry.getKey();
            if (entryKey == null) {
                throw new IllegalArgumentException("Query map contained null key.");
            }
            Object entryValue = entry.getValue();
            if (entryValue == null) {
                throw new IllegalArgumentException(
                        "Query map contained null value for key '" + entryKey + "'.");
            }
            else if(entryValue instanceof List) {
                for(Object arrayValue:(List)entryValue)  {
                    if (arrayValue != null) { // Skip null values
                        Map.Entry newEntry = new AbstractMap.SimpleEntry<>(entryKey, arrayValue);
                        newSet.add(newEntry);
                    }
                }
            }
            else {
                Map.Entry newEntry = new AbstractMap.SimpleEntry<>(entryKey, entryValue);
                newSet.add(newEntry);
            }
        }
        return newSet;
    }
   /* @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> originSet = super.entrySet();
        Set<Entry<String, Object>> newSet = new HashSet<>();

        for (Entry<String, Object> entry : originSet) {
            String entryKey = entry.getKey();
            if (entryKey == null) {
                throw new IllegalArgumentException("Query map contained null key.");
            }
            Object entryValue = entry.getValue();
            if (entryValue == null) {
                throw new IllegalArgumentException(
                        "Query map contained null value for key '" + entryKey + "'.");
            }
            else if(entryValue instanceof List) {
                for(Object arrayValue:(List)entryValue)  {
                    if (arrayValue != null) { // Skip null values
                        Map.Entry<String, Object> newEntry = new AbstractMap.SimpleEntry<>(entryKey, arrayValue);
                        newSet.add(newEntry);
                    }
                }
            }
            else {
                Map.Entry<String, Object> newEntry = new AbstractMap.SimpleEntry<>(entryKey, entryValue);
                newSet.add(newEntry);
            }
        }
        return newSet;
    }*/
}
