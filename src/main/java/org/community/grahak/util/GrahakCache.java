package org.community.grahak.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.community.cache.ECache;
import org.community.cache.ECacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class GrahakCache {
	
    private static Logger log = LoggerFactory.getLogger(GrahakCache.class);

	private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
    //private ECacheManager  cacheManager;
	//private ECache cache;
	
	public void init()
	{
		if(log.isDebugEnabled())
			log.debug("initializing service access cache ");
		//cache = cacheManager.getCache("serviceProxy");
	}

	public void setCacheManager(ECacheManager manger) {
		//this.cacheManager = manger;
	}

	public Object get(String key)
	{ 
		return cache.get(key);
	}
	
	
	public void add(String key, Object value)
	{
		cache.put(key, value);
	}
}
