package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 CREATE TABLE calls (_id INTEGER PRIMARY KEY AUTOINCREMENT,number TEXT,
 date INTEGER,duration INTEGER,type INTEGER,new INTEGER,name TEXT,
 numbertype INTEGER,numberlabel TEXT,countryiso TEXT,voicemail_uri TEXT,
 is_read INTEGER,geocoded_location TEXT,lookup_uri TEXT,matched_number TEXT,
 normalized_number TEXT,photo_id INTEGER NOT NULL DEFAULT 0,formatted_number TEXT,
 _data TEXT,has_content INTEGER,mime_type TEXT,source_data TEXT,source_package TEXT,state INTEGER);

 3180|13818585388|1489887176037|12|2|0|蓝天师傅|2||CN|||中国|content://com.android.contacts/contacts/lookup/0r38-585472136D5747C7/36||+8613818585388|0|138 1858 5388||||||
 3210|15026443285|1491213253407|56|2|0|房    东|2||CN|||中国|content://com.android.contacts/contacts/lookup/0r246-45D64344/242||+8615026443285|0|150 2644 3285||||||
 3246|4006125955|1492315492918|0|2|0||0||CN|||中国||||0|400 612 5955||||||
 3247|4006125955|1492315583390|7|2|0||0||CN|||中国||||0|400 612 5955||||||
 */
@ClassVersion(version = 1)
public class Calls {

    @Primary
    public int id;
    public int _id;
    public String number;// 15026443285
    public int date;// 1491213253407
    public int duration;// 56
    public int type;// 2
    public int news;// 这里需要处理 0
    public String name;// 房    东
    public int numbertype;// 2
    public String numberlabel;//
    public String countryiso;// CN
    public String voicemail_uri;//
    public int is_read;//
    public String geocoded_location;// 中国
    public String lookup_uri;// content://com.android.contacts/contacts/lookup/0r246-45D64344/242
    public String matched_number;//
    public String normalized_number;// +8615026443285
    public int photo_id;// 0
    public String formatted_number;// 150 2644 3285
    public String _data;//
    public int has_content;//
    public String mime_type;//
    public String source_data;//
    public String source_package;//
    public int state;//

}
