package com.example.albumlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public final class DBMain extends SQLiteOpenHelper {
    private static DBMain instance;
    public static final String DBNAME = "albumlist.db";
    public static final String TABLENAME = "albums";
    public static final int VERSION = 1;
    public DBMain(@Nullable Context context){
        super(context, DBNAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE "+TABLENAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT, album_name TEXT, actor TEXT, picture BLOB, year INTEGER)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS "+TABLENAME+"";
        db.execSQL(query);

    }

    public static DBMain getInstance() {
        if (instance != null) {
            return instance;
        }return null;
    }

    public static void createInstance(@Nullable Context context) {
        instance = new DBMain(context);
    }

    public void addTestAlbums(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE "+TABLENAME+"");
        db.execSQL("CREATE TABLE "+TABLENAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT, album_name TEXT, actor TEXT, picture BLOB, year INTEGER)");
        //db.execSQL("INSERT INTO "+TABLENAME+"(album_name, actor, picture, year) VALUES ('Album 1', 'Autor 1', null, 1999)");

    }
    public Cursor selectAllAlbums(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+"", null);
        return cursor;
    }

    public void insertAlbum(Album album){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("album_name", album.getAlbumName());
        cv.put("actor", album.getActor());
        cv.put("picture", album.getPicture());
        cv.put("year", album.getYear());
        db.insert(TABLENAME, null, cv);
        //db.execSQL("INSERT INTO "+TABLENAME+"(album_name, actor, picture, year) VALUES ('"+album.getAlbumName()+"', '"+album.getActor()+"', '"+album.getPicture()+"', "+album.getYear()+")");
    }

    public Cursor findAlbumsByName(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE album_name LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbums(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE album_name LIKE '%"+query+"%' OR actor LIKE '%"+query+"%' OR year LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbumsByActor(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE actor LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbumsByYear(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE year LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbumsByNameAndActor(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE album_name LIKE '%"+query+"%' OR actor LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbumsByNameAndYear(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE album_name LIKE '%"+query+"%' OR year LIKE '%"+query+"%'", null);
        return cursor;
    }

    public Cursor findAlbumsByActorAndYear(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLENAME+" WHERE actor LIKE '%"+query+"%' OR year LIKE '%"+query+"%'", null);
        return cursor;
    }
}
