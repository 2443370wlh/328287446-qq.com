package wanglihua.liveconsole.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import wanglihua.liveconsole.Model.TitleInfo;

/**
 * Created by Administrator on 2018/6/20.
 */

public class DBHelper extends SQLiteOpenHelper {
    //数据库版本号
    private static final int DATABASE_VERSION=1;

    //数据库名称
    private static final String DATABASE_NAME="Title.db";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String CREATE_TABLE_STUDENT="CREATE TABLE "+ TitleInfo.TABLE+"("
                +TitleInfo.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +TitleInfo.KEY_TITLE+" TEXT, "
                +TitleInfo.KEY_REPORTER+" TEXT)";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果旧表存在，删除，所以数据将会消失
        db.execSQL("DROP TABLE IF EXISTS "+ TitleInfo.TABLE);

        //再次创建表
        onCreate(db);
    }
}