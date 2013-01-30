package org.community.configloader.event;

public interface Observable<T> {
	
	void registerObserver(Observer<T> observer);

}
