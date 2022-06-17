package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class TaskDBHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "task.db";
    private static final int DB_VER = 1;
    private static final String TB_NAME = "TaskToDo";
    private static final String ID = "_id";
    public static final String TITLE = "title";
    private static final String DETAILS = "details";
    public static final String DATETIME = "dateTime";
    private static final String isDone = "isDone";
    private static final String IMAGE = "image";
    public SQLiteDatabase sqlDB;

    public TaskDBHelper(Context context){
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sCreate = String.format("CREATE TABLE %s" +
                "(%s integer primary key autoincrement,"+
                "%s text not null, %s text not null, %s text," +
                "%s integer not null default 0 check(%s in(0,1)),"+
                "%s BLOB);", TB_NAME, ID, TITLE, DETAILS, DATETIME, isDone, isDone, IMAGE);
        Log.d(DB_NAME,sCreate);
        db.execSQL(sCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public void open(){
        sqlDB = this.getWritableDatabase();
    }

    public void close(){
        sqlDB.close();
    }

    public long insertTask(Task task){
        ContentValues cvs = new ContentValues();
        cvs.put(TITLE, task.title);
        cvs.put(DETAILS, task.details);
        cvs.put(DATETIME, task.dateTime);
        cvs.put(isDone, task.isDone ? 1 : 0);
        cvs.put(IMAGE, task.image);
        long autoID = sqlDB.insert(TB_NAME, null, cvs);
        task.id = autoID;
        return autoID;
    }

    public void updateTaskDone(Task task){
        if(task.id<0)
        {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        }
        ContentValues cvs = new ContentValues();
        cvs.put(isDone, task.isDone);
        long result = sqlDB.update(TB_NAME, cvs, "_id="+task.id, null);
        if(result == -1){
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean deleteTask(Task task){
        return sqlDB.delete(TB_NAME, ID+"="+task.id, null)>0;
    }

    public Cursor getAllTask(){
        String[] sFields = new String[]{ID, TITLE, DETAILS, DATETIME, isDone, IMAGE};
        sqlDB = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqlDB != null){
            cursor = sqlDB.query(TB_NAME, sFields, null, null, null, null, null);
        }
        return cursor;
    }

    public Cursor readAllTask(){
        String query = "Select * from "+TB_NAME;
        sqlDB = this.getReadableDatabase();
        Cursor cursor = null;
        if(sqlDB != null){
            cursor = sqlDB.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getAllTask(long id)
    {
        String[] sFields = new String[]{ID, TITLE, DETAILS, DATETIME, isDone, IMAGE};
        Cursor cursor = sqlDB.query(TB_NAME, sFields, ID+"="+id, null, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean checkTable(){
        open();
        String query = "Select * from " + TB_NAME;
        Cursor cursor = sqlDB.rawQuery(query, null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
}
