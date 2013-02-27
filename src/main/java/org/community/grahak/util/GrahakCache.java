package org.community.grahak.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class GrahakCache {
    private static Logger log = LoggerFactory.getLogger(GrahakCache.class);
	private Map<String, Object> cache =  null;

	public void init()
	{
	    log.debug("{} ","[initializing service access caches]");
        cache = new ConcurrentHashMap<String, Object>();
	}

    /** TODO implement cache
	public void setCacheManager(ECacheManager manger) {
		//this.cacheManager = manger;
	}
     **/

	public Object get(String key)
	{ 
		return cache.get(key);
	}
	
	
	public void add(String key, Object value)
	{
		cache.put(key, value);
	}
}
