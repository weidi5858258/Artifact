package com.weidi.artifact.db.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.constant.Constant;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * Created by root on 16-7-30.
 * 后期目标:
 * 1.根据Cursor得到其字段,然后创建表进行复制操作
 * 2.在java bean中加上主键
 */

public class DbUtils {

    private volatile static DbUtils mDbUtils;
    private MySQLiteOpenHelper helper;

    private DbUtils(){}

    public static DbUtils getInstance(Context context){
        if (mDbUtils == null) {
            synchronized (DbUtils.class) {
                if (mDbUtils == null) {
                    mDbUtils = new DbUtils();
                }
            }
        }
        return mDbUtils;
    }

    public MySQLiteOpenHelper getHelper(Context context){
        if (helper == null) {
            helper = new MySQLiteOpenHelper(context);
        }
        return helper;
    }

    /**
     * 得到到java bean类，然后得到注解的值
     *
     * @param context
     */
    public void createOrUpdateDBWithVersion(Context context, Class<?> clazz) {
        try {
            if (clazz == null) {
                return;
            }
            String object = clazz.getSimpleName();
            DbVersion annotation = clazz.getAnnotation(DbVersion.class);
            if (annotation == null) {
                return;
            }
            int version_new = annotation.version();
            SharedPreferences sp = context.getSharedPreferences(
                    Constant.SHAREDPREFERENCES,
                    Context.MODE_PRIVATE);
            int version_old = sp.getInt(object, 0);
            String path = "/data/data/" + context.getPackageName() + "/databases";
            File file = new File(path, Constant.DB_NAME);
            if (file == null || !file.exists() || !file.isFile()) {
                return;
            }
            if (sp.contains(object)) {
                if (version_new == version_old) {
                    return;
                }
                updateDB(clazz, context);
            } else {
                createDB(clazz, context);
            }
            saveJavaBeanVersion(sp, object, version_new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDB(Class<?> clazz, Context context) {
//        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
//        System.out.println("createDBWithVersion:" + helper.getDatabaseName());
        String table_name = clazz.getSimpleName();
        SQLiteDatabase db = getHelper(context).getWritableDb();
        db.beginTransaction();

        Field fields[] = clazz.getDeclaredFields();
        if (fields == null) {
            return;
        }
        int fields_count = fields.length;
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(table_name);
        sb.append(" (");
        sb.append(" id integer primary key");
        int i = 0;
        for (i = 0; i < fields_count; ++i) {
//            System.out.println("--------------->" + fields[i].getName() +
//                    " " + fields[i].getType().getSimpleName());
            String fieldName = fields[i].getName();
            String fieldTypeName = fields[i].getType().getSimpleName();
            if (fieldName.contains("$")) {
                continue;
            }

            if (fieldTypeName.equals(String.class.getSimpleName()) ||
                    fieldTypeName.equals(long.class.getSimpleName()) ||
                    fieldTypeName.equals(short.class.getSimpleName()) ||
                    fieldTypeName.equals(int.class.getSimpleName()) ||
                    fieldTypeName.equals(double.class.getSimpleName()) ||
                    fieldTypeName.equals(float.class.getSimpleName()) ||
                    fieldTypeName.equals(boolean.class.getSimpleName()) ||
                    fieldTypeName.equals(char.class.getSimpleName()) ||
                    fieldTypeName.equals(byte.class.getSimpleName()) ||
                    fieldTypeName.equals(Long.class.getSimpleName()) ||
                    fieldTypeName.equals(Short.class.getSimpleName()) ||
                    fieldTypeName.equals(Integer.class.getSimpleName()) ||
                    fieldTypeName.equals(Double.class.getSimpleName()) ||
                    fieldTypeName.equals(Float.class.getSimpleName()) ||
                    fieldTypeName.equals(Boolean.class.getSimpleName()) ||
                    fieldTypeName.equals(Character.class.getSimpleName()) ||
                    fieldTypeName.equals(Byte.class.getSimpleName())) {
                sb.append(",");
                sb.append(" ");
                sb.append(fieldName);
                sb.append(" ");
                if (i < fields_count) {
                    sb.append("varchar");
                }
            }
        }
        sb.append(" );");
//        System.out.println("----------------->" + sb.toString());
        // 新建数据库
        db.execSQL(sb.toString());

        db.setTransactionSuccessful();
        db.endTransaction();
        getHelper(context).closeDb();

    }

    /**
     * db.execSQL("create table if not exists hero_info("
     * + "id integer primary key,"
     * + "name varchar,"
     * + "level integer)");
     * create table if not exists BlacklistPhone
     * ( id integer primary key, address varchar,
     * time varchar, number varchar,
     * flag varchar, date varchar,
     * duration varchar, news varchar,
     * type varchar, id varchar );
     *
     * @param clazz
     */
    private void updateDB(Class<?> clazz, Context context) {
        // ALTER TABLE table_name RENAME TO new_table_name;
//        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
//        System.out.println("------------->" + helper.getDatabaseName());
        String table_name = clazz.getSimpleName();
        String table_name_temp = table_name + "_temp";
        SQLiteDatabase db = getHelper(context).getWritableDb();
        db.beginTransaction();

        //        String drop_table_sql = "DROP TABLE "+table_name+"2;";
        //        System.out.println(drop_table_sql);
        //        db.execSQL(drop_table_sql);
        //        drop_table_sql = "DROP TABLE "+table_name+"3;";
        //        System.out.println(drop_table_sql);
        //        db.execSQL(drop_table_sql);

        String sql = "ALTER TABLE " + table_name + " RENAME TO " + table_name_temp + ";";
        db.execSQL(sql);

        ArrayList<String> fieldList = new ArrayList<String>();
        sql = "select * from " + table_name_temp + ";";
        Cursor cursor = getHelper(context).getReadableDb().rawQuery(sql, null);
        int columnCount = cursor.getColumnCount();
        int i = 0;

        Field fields[] = clazz.getDeclaredFields();
        if (fields == null) {
            return;
        }
        int fields_count = fields.length;
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(table_name);
        sb.append(" (");
        sb.append(" id integer primary key");
        fieldList.add("id");
        for (i = 0; i < fields_count; ++i) {
            //            System.out.println("--------------->"+fields[i].getName() +
            //                    " "+fields[i].getType().getSimpleName());
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getName().contains("$")) {
                continue;
            }
            sb.append(",");
            sb.append(" ");
            sb.append(field.getName());
            fieldList.add(field.getName());
            sb.append(" ");
            if (i < fields_count) {
                sb.append("varchar");
            }
        }
        for (i = 0; i < columnCount; ++i) {
            String columnName = cursor.getColumnName(i);
            if (!fieldList.contains(columnName)) {
                sb.append(",");
                sb.append(" ");
                sb.append(columnName);
                sb.append(" ");
                sb.append("varchar");
                fieldList.add(columnName);
            }
        }
        sb.append(" );");
        //        System.out.println("----------------->"+sb.toString());
        // 新建数据库
        db.execSQL(sb.toString());

        String temp = null;
        String columnName = null;
        while (cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            for (i = 0; i < columnCount; ++i) {
                temp = cursor.getString(i);
                columnName = cursor.getColumnName(i);
//                System.out.println("------------>" + columnName + " " + temp);
                values.put(columnName, temp);
            }
            db.insert(table_name, columnName, values);
        }
        cursor.close();

//        System.out.println("------------------------------start");
//        sql = "ALTER TABLE " + table_name + " RENAME TO " + table_name_temp + ";";
//        db.execSQL(sql);
//        System.out.println("------------------------------end");

        // DROP TABLE database_name.table_name;
        sql = "DROP TABLE " + table_name_temp + ";";
//        System.out.println(sql);
        db.execSQL(sql);

        db.setTransactionSuccessful();
        db.endTransaction();

        getHelper(context).closeDb();
    }

    private static void saveJavaBeanVersion(SharedPreferences sp, String clazz, int version) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(clazz, version);
        edit.commit();
    }

}
