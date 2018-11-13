package wanglihua.liveconsole.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import wanglihua.liveconsole.Utils.DBHelper;

/**
 * Created by Administrator on 2018/6/20.
 */

public class TitleRepo {
    private DBHelper dbHelper;

    public TitleRepo(Context context){
        dbHelper=new DBHelper(context);
    }

    public int insert(TitleInfo mInfo){
        //打开连接，写入数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(TitleInfo.KEY_TITLE,mInfo.mTitle);
        values.put(TitleInfo.KEY_REPORTER,mInfo.mReporter);
        //
        long mId=db.insert(TitleInfo.TABLE,null,values);
        db.close();
        return (int)mId;
    }

    public void delete(int student_Id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(TitleInfo.TABLE,TitleInfo.KEY_ID+"=?", new String[]{String.valueOf(student_Id)});
        db.close();
    }
//    public void update(TitleInfo mInfo){
//        SQLiteDatabase db=dbHelper.getWritableDatabase();
//        ContentValues values=new ContentValues();
//
//        values.put(Student.KEY_age,student.age);
//        values.put(Student.KEY_email,student.email);
//        values.put(Student.KEY_name,student.name);
//
//        db.update(Student.TABLE,values,Student.KEY_ID+"=?",new String[] { String.valueOf(student.student_ID) });
//        db.close();
//    }

    public ArrayList<TitleInfo> getList(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                TitleInfo.KEY_ID+","+
                TitleInfo.KEY_TITLE+","+
                TitleInfo.KEY_REPORTER+
                " FROM "+TitleInfo.TABLE;
        ArrayList<TitleInfo> mList=new ArrayList<TitleInfo>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                TitleInfo mInfo=new TitleInfo();
                mInfo.mTitle = cursor.getString(cursor.getColumnIndex(TitleInfo.KEY_TITLE));
                mInfo.mReporter = cursor.getString(cursor.getColumnIndex(TitleInfo.KEY_REPORTER));
                mList.add(mInfo);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mList;
    }

    public void delList(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(TitleInfo.TABLE,null,null);
        db.close();
    }

//    public Student getStudentById(int Id){
//        SQLiteDatabase db=dbHelper.getReadableDatabase();
//        String selectQuery="SELECT "+
//                Student.KEY_ID + "," +
//                Student.KEY_name + "," +
//                Student.KEY_email + "," +
//                Student.KEY_age +
//                " FROM " + Student.TABLE
//                + " WHERE " +
//                Student.KEY_ID + "=?";
//        int iCount=0;
//        Student student=new Student();
//        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
//        if(cursor.moveToFirst()){
//            do{
//                student.student_ID =cursor.getInt(cursor.getColumnIndex(Student.KEY_ID));
//                student.name =cursor.getString(cursor.getColumnIndex(Student.KEY_name));
//                student.email  =cursor.getString(cursor.getColumnIndex(Student.KEY_email));
//                student.age =cursor.getInt(cursor.getColumnIndex(Student.KEY_age));
//            }while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return student;
//    }
}