package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.telephony/databases/blacklist.db

 CREATE TABLE blacklist(_id INTEGER PRIMARY KEY,number TEXT,normalized_number TEXT UNIQUE,
 is_regex INTEGER,phone INTEGER DEFAULT 0,message INTEGER DEFAULT 0);

 1|+10155|+10155|0|1|0
 */
@ClassVersion(version = 1)
public class Blacklist {

    @Primary
    public int id;
    public int _id;
    public String number;
    public String normalized_number;
    public int is_regex;
    public int phone;
    public int message;

}
