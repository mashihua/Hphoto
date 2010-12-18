package com.hphoto.server;

public interface Cache<K extends Comparable, V> {
	
	   V get(K obj);
	   
	   void put(K key, V obj);
	   
	   void put(K key, V obj, long validTime);
	   
	   void remove(K key);
	   
	   Pair[] getAll();
	   
	   int size();
	   
	}
