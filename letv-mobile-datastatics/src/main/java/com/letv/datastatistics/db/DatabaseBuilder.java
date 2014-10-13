
package com.letv.datastatistics.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Database builder template - facilitates adding and getting
 * objects from SQLite database
 * 
 * @author Lukasz Wisniewski
 *
 * @param <T>
 */
public abstract class DatabaseBuilder<T> {
	
	/**
	 * Creates object out of cursor
	 * 
	 * @param c
	 * @return
	 */
	public abstract T build(Cursor c);
	
	/**
	 * Puts an object into a ContentValues instance
	 * 
	 * @param t
	 * @return
	 */
	public abstract ContentValues deconstruct(T t);

}
