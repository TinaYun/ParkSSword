package codeeer.parkssword_android.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;

import static codeeer.parkssword_android.app.Functions.PasswordFormat;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String LogoutPassword="[N/A]";//注销时对密码字段的赋值
    private static final String DB_NAME = "UserPWDs.db"; //数据库名称
    private static final int version = 1; //数据库版本

    //private Context context;
    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, version);
        //this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        MainActivity.PassWord=null;

        //创建数据表
        db.execSQL("CREATE TABLE IF NOT EXISTS Setting(Key text primary key,Value text not null);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Recent(Domin text primary key,Times integer not null default 0);");
        db.execSQL("CREATE TABLE IF NOT EXISTS ManualPWDs(" +
                "Domin text primary key," +
                "PWDPool text not null," +
                "Length integer not null," +
                "MD5Times integer not null," +
                "LockCPU integer not null,LockHard integer not null,LockUSB integer not null);");

        //部署默认参数
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"TimeStamp", df.format(new Date())});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"Length_Default", 10});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"MD5Times_Default", 10});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"LockCPU_Default", true});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"LockHard_Default", true});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"LockUSB_Default", false});

        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"CPUID", java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,16)});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"HardID", java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,8)});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"USBID", java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,8)});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"UserName", ""});
        db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"PassWord", LogoutPassword});
        try {
            db.execSQL("insert into Setting(Key, Value) values(?,?)", new Object[]{"PassWord_APP", PasswordFormat(LogoutPassword)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        onCreate(db);
    }
}
