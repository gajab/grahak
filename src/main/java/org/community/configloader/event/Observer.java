package org.community.configloader.event;

public interface Observer<T> {

	void onChange(T event);

}
