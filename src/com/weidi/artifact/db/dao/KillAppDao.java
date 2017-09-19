package com.weidi.artifact.db.dao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;
import com.weidi.artifact.db.utils.BaseDaoImpl;
import com.weidi.artifact.db.utils.IDoDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KillAppDao extends BaseDaoImpl{

    private Context context;

    public KillAppDao(Context context) {
        super(context);
        this.context = context;
    }




}
