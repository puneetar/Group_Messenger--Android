package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ContentDataProvider extends ContentProvider {

	public static final Uri DB_URI = Uri.parse("content://edu.buffalo.cse.cse486586.groupmessenger.provider");
	public static Context context;
	
	SQLiteDatabase sql_db;
	public static Database data_base;
	
	@Override
	public boolean onCreate() {
		getContext().deleteDatabase(Database.DATABASE_NAME);
		System.out.println("Content provider is called");
		data_base = new Database(getContext());
		context=getContext();
		return false;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {

		return 0;
	}

	@Override
	public String getType(Uri arg0) {

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		sql_db=data_base.getWritableDatabase();
		
		sql_db.insert(Database.TABLE_NAME, null, values);
		
		
		//sql_db.close();
		
		getContext().getContentResolver().notifyChange(uri, null);
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String orderBy){
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(Database.TABLE_NAME);
		
		sql_db = data_base.getReadableDatabase();
		Cursor cursor = null;
			
		cursor = queryBuilder.query(sql_db, projection, Database.KEY + "=?", 
				new String[] {selection}, null, null, orderBy);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
		
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {

		return 0;
	}

}
