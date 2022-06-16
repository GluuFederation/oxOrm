/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.orm.couchbase;

import org.gluu.orm.couchbase.impl.CouchbaseEntryManager;
import org.gluu.orm.couchbase.model.GluuStatus;
import org.gluu.orm.couchbase.model.SimpleCache;
import org.gluu.orm.couchbase.model.SimpleUser;
import org.gluu.orm.couchbase.operation.impl.CouchbaseConnectionProvider;
import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.persist.model.base.DeletableEntity;
import org.gluu.search.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Yuriy Movchan Date: 11/03/2016
 */
public final class CouchbaseDeleteSample {

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseConnectionProvider.class);

    private CouchbaseDeleteSample() {
    }

    public static void main(String[] args) {
        // Prepare sample connection details
        CouchbaseEntryManagerSample couchbaseEntryManagerSample = new CouchbaseEntryManagerSample();

        // Create Couchbase entry manager
        CouchbaseEntryManager couchbaseEntryManager = couchbaseEntryManagerSample.createCouchbaseEntryManager();

        Date now = new GregorianCalendar(TimeZone.getTimeZone("UTC")).getTime();
        Calendar pastCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        pastCalendar.add(Calendar.SECOND, -60);
        Date past = pastCalendar.getTime();

        if (true) {
	        for (int i = 0; i < 1000; i++) {
	            String id = String.format("cache_%06d", i);
	            String dn = String.format("id=%s,ou=cache,o=gluu", id);
	
	            SimpleCache newCache = new SimpleCache();
	            newCache.setDn(dn);
	            newCache.setId(id);
	            newCache.setData("{'sample_data': 'sample_data_value'}");
	            newCache.setExpirationDate(past);
	            newCache.setDeletable(true);
	
	    		try {
	                couchbaseEntryManager.persist(newCache);
	            } catch (Throwable e) {
	                e.printStackTrace();
	            }
	        }
        }

        String baseDn = "ou=cache,o=gluu";
		Filter filter = Filter.createANDFilter(
		        Filter.createEqualityFilter("del", true),
				Filter.createLessOrEqualFilter("exp", couchbaseEntryManager.encodeTime(baseDn, now))
        );

        int result = couchbaseEntryManager.remove(baseDn, SimpleCache.class, filter, 2000);
        System.out.println(result);
    }

}
