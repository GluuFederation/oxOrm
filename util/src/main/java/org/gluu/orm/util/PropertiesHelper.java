/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.orm.util;

import java.util.Iterator;
import java.util.Properties;


/**
 * @author Yuriy Movchan Date: 05/11/2019
 */
public final class PropertiesHelper {

    public static Properties filterProperties(Properties conf, String splitter) {
    	Properties resultConf = new Properties();

    	Iterator<?> keys = conf.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            
            int index = key.indexOf(splitter);
            if ((index > 0) && (index < key.length() - 1)) {
        		String resultKey = key.substring(index + 1);
                String value = (String) conf.getProperty(key);

                resultConf.put(resultKey, value);
            }
        }
        
        return resultConf;
    }

    public static Properties findProperties(Properties conf, String prefix, String splitter) {
    	String findKey = prefix + splitter;

    	Properties resultConf = new Properties();

    	Iterator<?> keys = conf.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            
            if (key.startsWith(findKey)) {
                String value = (String) conf.getProperty(key);

                resultConf.put(key, value);
            }
        }
        
        return resultConf;
    }

    public static Properties appendPrefix(Properties conf, String prefix, String splitter) {
    	String appendToKey = prefix + splitter;

    	Properties resultConf = new Properties();

    	Iterator<?> keys = conf.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            
            resultConf.put(appendToKey + key, conf.getProperty(key));
        }
        
        return resultConf;
    }
}
