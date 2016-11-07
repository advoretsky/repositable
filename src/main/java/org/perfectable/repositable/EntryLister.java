package org.perfectable.repositable;

public interface EntryLister {
	interface Consumer {
		void entry(String name);
	}
	void list(Consumer consumer);
}
