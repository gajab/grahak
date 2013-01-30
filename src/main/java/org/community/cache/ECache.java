package org.community.cache;

public interface ECache {

	Object get(String key);

	void put(String key, Object value);

}
