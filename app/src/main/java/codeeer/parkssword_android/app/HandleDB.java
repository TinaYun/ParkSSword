package codeeer.parkssword_android.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import static codeeer.parkssword_android.app.Functions.PasswordFormat;

public class HandleDB {
    //查找设定中的键值
    public static String findValueByKey(SqliteHelper sh, String Key){
        SQLiteDatabase db = sh.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Setting WHERE Key=?", new String[]{Key});

        String result = null;
        if (cursor.moveToFirst()){
            if (cursor.getCount() != 0) {
                result = cursor.getString(cursor.getColumnIndex("Value"));
            }
        }

        db.close();
        cursor.close();
        return result;
    }

    //加载手动列表
    public static List<Functions.ManualItems> LoadManualItems(SqliteHelper sh){
        SQLiteDatabase db = sh.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ManualPWDs", null);
        List<Functions.ManualItems> data = new ArrayList<Functions.ManualItems>();

        while(cursor.moveToNext()){

            int dominIndex = cursor.getColumnIndex("Domin");
            int pwdpoolIndex = cursor.getColumnIndex("PWDPool");
            int lengthIndex = cursor.getColumnIndex("Length");
            int md5Index = cursor.getColumnIndex("MD5Times");
            int cpuIndex = cursor.getColumnIndex("LockCPU");
            int hardIndex = cursor.getColumnIndex("LockHard");
            int usbIndex = cursor.getColumnIndex("LockUSB");

            data.add(new Functions.ManualItems(
                    cursor.getString(dominIndex),
                    cursor.getString(pwdpoolIndex),
                    cursor.getInt(lengthIndex),
                    cursor.getInt(md5Index),
                    cursor.getInt(cpuIndex)==1,
                    cursor.getInt(hardIndex)==1,
                    cursor.getInt(usbIndex)==1));
        }

        db.close();

        return data;
    }

    //登记用户使用记录
    public static class Record{
        public static void Add(SqliteHelper sh,String Domin){
            if(Domin.trim().equals(""))return;

            SQLiteDatabase db = sh.getWritableDatabase();

            db.execSQL("INSERT OR IGNORE INTO Recent (Domin) VALUES('" + Domin + "')");
            db.execSQL("UPDATE Recent SET Times = Times + 1 WHERE Domin = '" + Domin + "'");

            db.close();
        }

        public static List<String> Read(SqliteHelper sh){
            SQLiteDatabase db = sh.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM Recent ORDER BY Times DESC", null);
            List<String> data = new ArrayList<String>();

            int dominIndex = cursor.getColumnIndex("Domin");
            while(cursor.moveToNext()){
                data.add(cursor.getString(dominIndex));
            }

            db.close();

            return data;
        }
    }

    //账号操作
    public static class Account{

        public enum PassWordType {LoginAccount,LoginApplication}

        public static class Set{

            public static void Password(SqliteHelper sh,PassWordType LoginType,String Value){

                SQLiteDatabase db = sh.getWritableDatabase();

                String TypeStr = null;

                switch (LoginType){
                    case LoginAccount:TypeStr = "PassWord";break;
                    case LoginApplication:TypeStr = "PassWord_APP";break;
                }

                db.execSQL("UPDATE Setting SET Value = '"+Value+"' WHERE Key = '"+TypeStr+"'");
                db.close();
            }
        }

        public static class Get{

            //获取登录密码
            public static String Password(SqliteHelper sh,PassWordType LoginType)
            {
                SQLiteDatabase db = sh.getReadableDatabase();

                String RequestStr=null;

                switch (LoginType){
                    case LoginAccount:RequestStr = "SELECT * FROM Setting WHERE Key='PassWord'";break;
                    case LoginApplication:RequestStr = "SELECT * FROM Setting WHERE Key='PassWord_APP'";break;
                }

                Cursor cursor = db.rawQuery(RequestStr,null);

                String PWD = SqliteHelper.LogoutPassword;
                if (cursor.moveToFirst()){
                    if (cursor.getCount() != 0){
                        PWD = cursor.getString(cursor.getColumnIndex("Value"));
                    }
                }

                db.close();

                return PWD;
            }

            //获取用户名
            public static String UserName(SqliteHelper sh)
            {
                SQLiteDatabase db = sh.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM Setting WHERE Key='UserName'",null);

                String UserName = null;
                if (cursor.moveToFirst()){
                    if (cursor.getCount() != 0){
                        UserName = cursor.getString(cursor.getColumnIndex("Value"));
                    }
                }

                db.close();

                return UserName;
            }
        }

        //注销登录
        public static void Logout(SqliteHelper sh) throws Exception {
            SQLiteDatabase db = sh.getWritableDatabase();
            db.execSQL("DELETE FROM ManualPWDs");
            db.execSQL("UPDATE Setting SET Value = '"+SqliteHelper.LogoutPassword+"' WHERE Key = 'PassWord'");
            db.execSQL("UPDATE Setting SET Value = '"+PasswordFormat(SqliteHelper.LogoutPassword)+"' WHERE Key = 'PassWord_APP'");
            db.execSQL("UPDATE Setting SET Value = '10' WHERE Key = 'Length_Default'");
            db.execSQL("UPDATE Setting SET Value = '10' WHERE Key = 'MD5Times_Default'");
            db.execSQL("UPDATE Setting SET Value = 'true' WHERE Key = 'LockCPU_Default'");
            db.execSQL("UPDATE Setting SET Value = 'true' WHERE Key = 'LockHard_Default'");
            db.execSQL("UPDATE Setting SET Value = 'false' WHERE Key = 'LockUSB_Default'");
            db.execSQL("UPDATE Setting SET Value = '"+ java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,16)+"' WHERE Key = 'CPUID'");
            db.execSQL("UPDATE Setting SET Value = '"+java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,8)+"' WHERE Key = 'HardID'");
            db.execSQL("UPDATE Setting SET Value = '"+java.util.UUID.randomUUID().toString().toUpperCase().replace("-","").substring(0,8)+"' WHERE Key = 'USBID'");
            db.close();
        }
    }
}
