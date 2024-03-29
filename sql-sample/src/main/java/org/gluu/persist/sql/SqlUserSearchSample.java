/*
 * oxCore is available under the MIT License (2014). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.sql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.gluu.persist.model.base.CustomObjectAttribute;
import org.gluu.persist.sql.impl.SqlEntryManager;
import org.gluu.persist.sql.model.SimpleUser;
import org.gluu.persist.sql.model.UserRole;
import org.gluu.persist.sql.operation.impl.SqlConnectionProvider;
import org.gluu.persist.sql.persistence.SqlEntryManagerSample;
import org.gluu.search.filter.Filter;
import org.gluu.orm.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Movchan Date: 01/15/2020
 */
public final class SqlUserSearchSample {

    private static final Logger LOG = LoggerFactory.getLogger(SqlConnectionProvider.class);

    private static AtomicLong successResult = new AtomicLong(0) ;
    private static AtomicLong failedResult = new AtomicLong(0) ;
    private static AtomicLong errorResult = new AtomicLong(0) ;
    private static AtomicLong totalTime = new AtomicLong(0) ;
    private static AtomicLong activeCount = new AtomicLong(0) ;

    private SqlUserSearchSample() {
    }

    public static void main(String[] args) throws InterruptedException {
        // Prepare sample connection details
    	final SqlEntryManagerSample sqlEntryManagerSample = new SqlEntryManagerSample();
        final SqlEntryManager sqlEntryManager = sqlEntryManagerSample.createSqlEntryManager();

        Filter filter1 = Filter.createEqualityFilter("uid", "test_user");
        List<SimpleUser> users = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter1);
        System.out.println(users);
        
        int countUsers = 1000000;
        int threadCount = 200;
        int threadIterationCount = 200;

        Filter filter = Filter.createEqualityFilter(Filter.createLowercaseFilter("uid"), String.format("user%06d", countUsers));
        boolean foundUser = sqlEntryManager.contains("ou=people,o=gluu", SimpleUser.class, filter);
        if (!foundUser) {
        	addTestUsers(sqlEntryManager, countUsers);
        }

    	long totalStart = System.currentTimeMillis();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount, daemonThreadFactory());
            for (int i = 0; i < threadCount; i++) {
            	activeCount.incrementAndGet();
                final int count = i;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                    	long start = System.currentTimeMillis();
                        for (int j = 0; j < threadIterationCount; j++) {
	                    	long userUid = Math.round(Math.random() * countUsers);
	                    	String uid = "user" + userUid; /*String.format("user%06d", userUid);*/
	                        try {
	                        	Filter filter;
	                        	if (j % 2 == 0) {
	                        		filter = Filter.createEqualityFilter("birthdate", new Date());
	                        	} else {
	                        		filter = Filter.createEqualityFilter(Filter.createLowercaseFilter("uid"), StringHelper.toLowerCase(uid));
	                        	}
//		                        Filter filter = Filter.createEqualityFilter("uid", uid);
		                        List<SimpleUser> foundUsers = sqlEntryManager.findEntries("ou=people,o=gluu", SimpleUser.class, filter);
		                        if (foundUsers.size() > 0) {
		                        	successResult.incrementAndGet();
		                        } else {
		                        	LOG.warn("Failed to find user: " + uid);
		                        	failedResult.incrementAndGet();
		                        }
	                        } catch (Throwable e) {
	                        	errorResult.incrementAndGet();
	                            System.out.println("ERROR !!!, thread: " + count + ", uid: " + uid + ", error:" + e.getMessage());
	                            e.printStackTrace();
	                        }
                        }
                        
                        long end = System.currentTimeMillis();
                        long duration = end - start; 
                        LOG.info("Thread " + count + " execution time: " + duration);
                        totalTime.addAndGet(duration);
                    	activeCount.decrementAndGet();
                    }
                });
            }

            while (activeCount.get() != 0) {
            	Thread.sleep(1000L);
            }
        } finally {
            sqlEntryManager.destroy();
        }
        long totalEnd = System.currentTimeMillis();
        long duration = totalEnd - totalStart; 

        LOG.info("Total execution time: " + duration + " after execution: " + (threadCount * threadIterationCount));

        System.out.println(String.format("successResult: '%d', failedResult: '%d', errorResult: '%d'", successResult.get(), failedResult.get(), errorResult.get()));
    }

    private static void addTestUsers(SqlEntryManager sqlEntryManager, int countUsers) {
    	long startTime = System.currentTimeMillis();
        for (int j = 1; j <= countUsers; j++) {
        	String uid = "user" + j; /*String.format("user%06d", userUid);*/

        	SimpleUser newUser = new SimpleUser();
        	newUser.setInum("" + (startTime + j));

            newUser.setDn(String.format("inum=%s,ou=people,o=gluu", newUser.getInum()));
	        newUser.setUserId(uid);
	        newUser.setUserPassword("topsecret" + uid);
	        newUser.setUserRole(j % 2 == 0 ? UserRole.ADMIN : UserRole.USER);

	        newUser.setMemberOf(Arrays.asList("group_1", "group_2", "group_3"));

			newUser.setAttributeValue("givenName", "Agent Smith");
	        newUser.getCustomAttributes().add(new CustomObjectAttribute("address", Arrays.asList("London", "Texas", "Kiev")));
	        newUser.getCustomAttributes().add(new CustomObjectAttribute("transientId", "transientId"));

	        List<Object> oxExternalUid = Arrays.asList(1, 11);
	        if (j % 2 == 0) {
	        	oxExternalUid = Arrays.asList(1, 11, 2, 22);
	        } else if (j % 3 == 0) {
	        	oxExternalUid = Arrays.asList(2, 22, 3, 33);
	        } else if (j % 5 == 0) {
	        	oxExternalUid = Arrays.asList(1, 11, 2, 22, 3, 33, 4, 44);
	        }
	        newUser.getCustomAttributes().add(new CustomObjectAttribute("oxExternalUid", oxExternalUid));
			newUser.getCustomAttributes().add(new CustomObjectAttribute("birthdate", new Date()));
			newUser.getCustomAttributes().add(new CustomObjectAttribute("oxTrustActive", false));

			sqlEntryManager.persist(newUser);

			if (j % 1000 == 0) {
				LOG.info("Added: '{}'", j);
			}
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000L;

        LOG.info("Duration: '{}'", duration);
	}

	public static ThreadFactory daemonThreadFactory() {
        return new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        };
    }

}
