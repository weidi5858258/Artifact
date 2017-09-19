package com.weidi.artifact.db.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-7-31.
 */

public class BaseDaoImpl extends ABaseDao {

    private Context context;
    private MySQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private String tableName;

    public BaseDaoImpl(Context context) {
        this.context = context;
        helper = new MySQLiteOpenHelper(context);
    }

    public MySQLiteOpenHelper getHelper(){
        if(context == null){
            throw new IllegalArgumentException("BaseDaoImpl-context is null.");
        }
        if(helper == null){
            helper = new MySQLiteOpenHelper(context);
        }
        return helper;
    }

    public SQLiteDatabase getReadableDb(){
        return getHelper().getReadableDb();
    }

    public SQLiteDatabase getWritableDb(){
        return getHelper().getWritableDb();
    }

    public void closeDb(){
        getHelper().closeDb();
    }

    /**
     * OK
     *
     * @param clazz
     * @param values
     * @return 新添加的id号(id就是主键)
     */
    @Override
    public long add(Class<?> clazz, ContentValues values) {
        long index = -1;
        db = helper.getWritableDb();
        db.beginTransaction();
        try {
            if (clazz == null) {
                return index;
            }
            tableName = clazz.getSimpleName();
            /**
             * INSERT INTO table_name( column1, column2....columnN)
             * VALUES ( value1, value2....valueN);
             */
            //            StringBuilder sb = new StringBuilder();
            //            sb.append("INSERT INTO ");
            //            sb.append(tableName);
            //            sb.append(" ( ");
            //            Field fields[] = clazz.getDeclaredFields();
            //            if (fields == null) {
            //                return index;
            //            }
            //            int fields_count = fields.length;
            //            String fieldName = null;
            //            LinkedList<String> list = new LinkedList<>();
            //            for (int i = 0; i < fields_count; ++i) {
            //                //            System.out.println("--------------->"+fields[i].getName() +
            //                //                    " "+fields[i].getType().getSimpleName());
            //                fieldName = fields[i].getName();
            //                if (fieldName.contains("$")) {
            //                    continue;
            //                }
            //                sb.append(fieldName);
            //                list.add(fieldName);
            //                if(i<fields_count - 1){
            //                    sb.append(", ");
            //                }else{
            //                    sb.append(" )");
            //                }
            //            }
            //            sb.append("VALUES ( ");
            //            int count = list.size();
            //            String key = null;
            //            String value = null;
            //            for(int i=0;i<count;++i){
            //                key = list.get(i);
            //                if(!map.containsKey(key)){
            //                    continue;
            //                }
            //                value = map.get(key);
            //                sb.append("\'");
            //                sb.append(value);
            //                if(i<count-1){
            //                    sb.append("\', ");
            //                }else{
            //                    sb.append("\' );");
            //                }
            //            }
            //            System.out.println(sb.toString());
            //            db.execSQL(sb.toString());
            index = db.insert(tableName, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return index;
    }

    /**
     * OK
     *
     * @param clazz
     * @param id
     * @return 删除的条数
     */
    @Override
    public int delete(Class<?> clazz, int id) {
        int index = -1;
        db = helper.getWritableDb();
        db.beginTransaction();
        try {
            if (clazz == null) {
                return index;
            }
            tableName = clazz.getSimpleName();
            index = db.delete(tableName, "id=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return index;
    }

    /**
     * OK
     *
     * @param clazz
     * @param map
     * @return 删除的条数
     */
    @Override
    public int delete(Class<?> clazz, Map<String, String> map) {
        int index = -1;
        db = helper.getWritableDb();
        db.beginTransaction();
        try {
            if (clazz == null) {
                return index;
            }
            tableName = clazz.getSimpleName();
            StringBuilder sb = new StringBuilder();
            int count = map.size();
            int i = 0;
            int j = i;

            String values[] = new String[count];
            for (Map.Entry<String, String> entry : map.entrySet()) {
                j = i++;
                if (j < count - 1) {
                    sb.append(entry.getKey());
                    sb.append("=?");
                    sb.append(" and ");
                } else {
                    sb.append(entry.getKey());
                    sb.append("=?");
                }
                values[j] = entry.getValue();
            }
            //            Iterator iterator=map.entrySet().iterator();
            //            while(iterator.hasNext()){
            //                Map.Entry<String, String> entry= (Entry<String, String>) iterator.next();
            //                System.out.println("key:"+entry.getKey()+" value"+entry.getValue());
            //            }

            // db.delete(tableName, "number=? and flag=?", new String[]{"12345", "5"})
            index = db.delete(tableName, sb.toString(), values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return index;
    }

    /**
     * OK
     *
     * @param clazz
     * @param values 需要更新的的内容
     * @param map 更新哪一条的条件
     *eturn 更新的条数
     */
    @Override
    public int update(Class<?> clazz, ContentValues values, Map<String, String> map) {
        int index = -1;
        db = helper.getWritableDb();
        db.beginTransaction();
        try {
            if (clazz == null) {
                return index;
            }
            tableName = clazz.getSimpleName();
            StringBuilder sb = new StringBuilder();
            int count = map.size();
            int i = 0;
            int j = i;

            String whereArgs[] = new String[count];
            for (Map.Entry<String, String> entry : map.entrySet()) {
                j = i++;
                if (j < count - 1) {
                    sb.append(entry.getKey());
                    sb.append("=?");
                    sb.append(" and ");
                } else {
                    sb.append(entry.getKey());
                    sb.append("=?");
                }
                whereArgs[j] = entry.getValue();
            }
            // update(String table, ContentValues values, String whereClause, String[] whereArgs)
            index = db.update(tableName, values, sb.toString(), whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return index;
    }

    /**
     * OK
     *
     * @param clazz
     * @param id
     * @return 单个对象
     */
    @Override
    public Object querySingle(Class<?> clazz, int id) {
        db = helper.getReadableDb();
        db.beginTransaction();
        Object object = null;
        try {
            if (clazz == null) {
                return object;
            }
            /**
             * 要求创建的java bean有无参的构造方法
             */
            object = clazz.newInstance();
            tableName = clazz.getSimpleName();
            Cursor cursor = db.query(tableName, null, "id=?", new String[]{String.valueOf(id)},
                    null, null, null);

            object = internalQuerySingle(clazz, object, cursor);
            if (object == null) {
                return object;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return object;
    }

    /**
     * OK
     *
     * @param clazz
     * @param map
     * @return 单个对象
     */
    @Override
    public Object querySingle(Class<?> clazz, Map<String, String> map) {
        db = helper.getReadableDb();
        db.beginTransaction();
        Object object = null;
        try {
            if (clazz == null) {
                return object;
            }
            /**
             * 要求创建的java bean有无参的构造方法
             */
            object = clazz.newInstance();
            tableName = clazz.getSimpleName();
            StringBuilder sb = new StringBuilder();
            int count = map.size();
            int i = 0;
            int j = i;

            String values[] = new String[count];
            for (Map.Entry<String, String> entry : map.entrySet()) {
                j = i++;
                if (j < count - 1) {
                    sb.append(entry.getKey());
                    sb.append("=?");
                    sb.append(" and ");
                } else {
                    sb.append(entry.getKey());
                    sb.append("=?");
                }
                values[j] = entry.getValue();
            }
            /**
             * query(String table, String[] columns,
             * String selection, String[] selectionArgs,
             * String groupBy, String having, String orderBy)
             */
            Cursor cursor = db.query(tableName, null, sb.toString(), values, null, null, null);

            object = internalQuerySingle(clazz, object, cursor);
            if (object == null) {
                return object;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return object;
    }

    /**
     * OK
     *
     * @param clazz
     * @param map
     * @return
     */
    @Override
    public List queryMore(Class<?> clazz, Map<String, String> map) {
        db = helper.getReadableDb();
        db.beginTransaction();
        List mList = null;
        Cursor cursor = null;
        try {
            if (clazz == null) {
                return mList;
            }
            tableName = clazz.getSimpleName();
            if(map != null && !map.isEmpty()){
                StringBuilder sb = new StringBuilder();
                int count = map.size();
                int i = 0;
                int j = i;
                String values[] = new String[count];
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    j = i++;
                    if (j < count - 1) {
                        sb.append(entry.getKey());
                        sb.append("=?");
                        sb.append(" and ");
                    } else {
                        sb.append(entry.getKey());
                        sb.append("=?");
                    }
                    values[j] = entry.getValue();
                }
                cursor = db.query(tableName, null, sb.toString(), values, null, null, null);
            }else{
                cursor = db.query(tableName, null, null, null, null, null, null);
            }

            mList = internalQueryMore(clazz, cursor);
            if (mList == null) {
                return mList;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return mList;
        } finally {
            db.endTransaction();
            db.close();
        }
        return mList;
    }

    private Object internalQuerySingle(Class<?> clazz, Object object, Cursor cursor) {
        try {
            int columnCount = cursor.getColumnCount();
            String temp = null;
            String columnName = null;
            int i = 0;
            Map<String, String> map = new HashMap<String, String>();
            if (cursor != null && cursor.moveToNext()) {
                for (i = 0; i < columnCount; ++i) {
                    temp = cursor.getString(i);
                    columnName = cursor.getColumnName(i);
                    //                System.out.println("------------>" + columnName + " " + temp);
                    map.put(columnName, temp);
                }
            }
            cursor.close();

            Field fields[] = clazz.getDeclaredFields();
            if (fields == null) {
                return null;
            }
            int fields_count = fields.length;
            for (i = 0; i < fields_count; ++i) {
                if (fields[i].getName().contains("$")) {
                    continue;
                }
                Field field = fields[i];
                field.setAccessible(true);
                String fieldName = field.getName();
                String fieldTypeName = field.getType().getSimpleName();
                if (map.containsKey(fieldName)) {
                    String value = map.get(fieldName);
                    if (TextUtils.isEmpty(value) || "null".equals(value)) {
                        continue;
                    }
                    if (fieldTypeName.equals(String.class.getSimpleName())) {
                        field.set(object, value);
                    } else if (fieldTypeName.equals(long.class.getSimpleName()) ||
                            fieldTypeName.equals(Long.class.getSimpleName())) {
                        field.setLong(object, Long.parseLong(value));
                    } else if (fieldTypeName.equals(short.class.getSimpleName()) ||
                            fieldTypeName.equals(Short.class.getSimpleName())) {
                        field.setShort(object, Short.parseShort(value));
                    } else if (fieldTypeName.equals(int.class.getSimpleName()) ||
                            fieldTypeName.equals(Integer.class.getSimpleName())) {
                        field.setInt(object, Integer.parseInt(value));
                    } else if (fieldTypeName.equals(double.class.getSimpleName()) ||
                            fieldTypeName.equals(Double.class.getSimpleName())) {
                        field.setDouble(object, Double.parseDouble(value));
                    } else if (fieldTypeName.equals(float.class.getSimpleName()) ||
                            fieldTypeName.equals(Float.class.getSimpleName())) {
                        field.setFloat(object, Float.parseFloat(value));
                    } else if (fieldTypeName.equals(boolean.class.getSimpleName()) ||
                            fieldTypeName.equals(Boolean.class.getSimpleName())) {
                        field.setBoolean(object, Boolean.parseBoolean(value));
                    } else if (fieldTypeName.equals(char.class.getSimpleName()) ||
                            fieldTypeName.equals(Character.class.getSimpleName())) {
                        field.setChar(object, value.charAt(0));
                    } else if (fieldTypeName.equals(byte.class.getSimpleName()) ||
                            fieldTypeName.equals(Byte.class.getSimpleName())) {
                        field.setByte(object, Byte.parseByte(value));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    private List<Object> internalQueryMore(Class<?> clazz, Cursor cursor) {
        List<Object> mList = null;
        try {
            int columnCount = cursor.getColumnCount();
            String temp = null;
            String columnName = null;
            int i = 0;
            int j = 0;
            List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();
            Map<String, String> map = null;
            while (cursor != null && cursor.moveToNext()) {
                map = new HashMap<String, String>();
                for (i = 0; i < columnCount; ++i) {
                    temp = cursor.getString(i);
                    columnName = cursor.getColumnName(i);
                    map.put(columnName, temp);
                }
//                System.out.println("------------->"+map);
                tempList.add(map);
            }
            cursor.close();

            Field fields[] = clazz.getDeclaredFields();
            if (fields == null) {
                return null;
            }
            int fields_count = fields.length;
            mList = new ArrayList<Object>();

            int tempListCount = tempList.size();
            for(i=0;i<tempListCount;++i){
                map = tempList.get(i);
                /**
                 * 要求创建的java bean有无参的构造方法
                 */
                Object object = clazz.newInstance();
                for (j = 0; j < fields_count; ++j) {
                    if (fields[j].getName().contains("$")) {
                        continue;
                    }
                    Field field = fields[j];
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    String fieldTypeName = field.getType().getSimpleName();
                    if (map.containsKey(fieldName)) {
                        String value = map.get(fieldName);
                        if (TextUtils.isEmpty(value) || "null".equals(value)) {
                            continue;
                        }
                        if (fieldTypeName.equals(String.class.getSimpleName())) {
                            field.set(object, value);
                        } else if (fieldTypeName.equals(long.class.getSimpleName()) ||
                                fieldTypeName.equals(Long.class.getSimpleName())) {
                            field.setLong(object, Long.parseLong(value));
                        } else if (fieldTypeName.equals(short.class.getSimpleName()) ||
                                fieldTypeName.equals(Short.class.getSimpleName())) {
                            field.setShort(object, Short.parseShort(value));
                        } else if (fieldTypeName.equals(int.class.getSimpleName()) ||
                                fieldTypeName.equals(Integer.class.getSimpleName())) {
                            field.setInt(object, Integer.parseInt(value));
                        } else if (fieldTypeName.equals(double.class.getSimpleName()) ||
                                fieldTypeName.equals(Double.class.getSimpleName())) {
                            field.setDouble(object, Double.parseDouble(value));
                        } else if (fieldTypeName.equals(float.class.getSimpleName()) ||
                                fieldTypeName.equals(Float.class.getSimpleName())) {
                            field.setFloat(object, Float.parseFloat(value));
                        } else if (fieldTypeName.equals(boolean.class.getSimpleName()) ||
                                fieldTypeName.equals(Boolean.class.getSimpleName())) {
                            field.setBoolean(object, Boolean.parseBoolean(value));
                        } else if (fieldTypeName.equals(char.class.getSimpleName()) ||
                                fieldTypeName.equals(Character.class.getSimpleName())) {
                            field.setChar(object, value.charAt(0));
                        } else if (fieldTypeName.equals(byte.class.getSimpleName()) ||
                                fieldTypeName.equals(Byte.class.getSimpleName())) {
                            field.setByte(object, Byte.parseByte(value));
                        }
                    }
                }
                mList.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mList;
    }

    /**
     DbUtils.getDefault(getApplicationContext())
     .createOrUpdateDBWithVersion(getApplicationContext(), Student.class);
     BlacklistPhone info = (BlacklistPhone)
     (new BaseDaoImpl(getApplicationContext()).querySingle(BlacklistPhone.class, 1));
     //        System.out.println(info.toString());
     // String number, String address, long date, long duration, String time, int type, int news, int flag
     ContentValues values = new ContentValues();
     values.put("number", "1234567890");
     values.put("address", "天津");
     values.put("date", 1234567890);
     values.put("duration", 3000);
     values.put("time", "2016");
     values.put("type", 3);
     values.put("news", 2);
     values.put("flag", 1);
     //        long index = new BaseDaoImpl(getApplicationContext()).add(BlacklistPhone.class, values);
     //        System.out.println("------------------------->index = "+index);

     //                values = new ContentValues();
     //                values.put("number", "1234567890");
     //                info = (BlacklistPhone)
     //                        (new BaseDaoImpl(getApplicationContext()).querySingle(BlacklistPhone.class, values));
     //                System.out.println(info.toString());

     Map<String, String> map = new HashMap<>();
     map.put("number", "1234567890");
     //        map.put("address", "天津");
     //        map.put("flag", "2");
     List<BlacklistPhone> list = new BaseDaoImpl(getApplicationContext()).queryMore(BlacklistPhone.class, null);
     if(list != null){
     for(BlacklistPhone in : list){
     System.out.println(in.toString());
     }
     }

     values = new ContentValues();
     values.put("number", "1234567890");
     values.put("address", "抚背");
     values.put("date", 123456789);
     values.put("duration", 6000);
     values.put("time", "2015");
     values.put("type", 40);
     values.put("news", 50);
     values.put("flag", 30);
     map = new HashMap<>();
     map.put("number", "1234567890");
     //        map.put("flag", "5");
     int index = new BaseDaoImpl(getApplicationContext()).update(BlacklistPhone.class, values, map);
     System.out.println("------------------------->index = "+index);
     */

}
