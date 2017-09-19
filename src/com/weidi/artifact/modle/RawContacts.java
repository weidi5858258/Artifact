package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 CREATE TABLE raw_contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT,
 account_id INTEGER REFERENCES accounts(_id),sourceid TEXT,
 raw_contact_is_read_only INTEGER NOT NULL DEFAULT 0,
 version INTEGER NOT NULL DEFAULT 1,dirty INTEGER NOT NULL DEFAULT 0,
 deleted INTEGER NOT NULL DEFAULT 0,contact_id INTEGER REFERENCES contacts(_id),
 aggregation_mode INTEGER NOT NULL DEFAULT 0,aggregation_needed INTEGER NOT NULL DEFAULT 1,
 custom_ringtone TEXT,send_to_voicemail INTEGER NOT NULL DEFAULT 0,
 times_contacted INTEGER NOT NULL DEFAULT 0,last_time_contacted INTEGER,
 starred INTEGER NOT NULL DEFAULT 0,display_name TEXT,display_name_alt TEXT,
 display_name_source INTEGER NOT NULL DEFAULT 0,phonetic_name TEXT,
 phonetic_name_style TEXT,sort_key TEXT COLLATE PHONEBOOK,
 sort_key_alt TEXT COLLATE PHONEBOOK,name_verified INTEGER NOT NULL DEFAULT 0,
 sync1 TEXT, sync2 TEXT, sync3 TEXT, sync4 TEXT );

 CREATE INDEX raw_contact_sort_key1_index ON raw_contacts (sort_key);
 CREATE INDEX raw_contact_sort_key2_index ON raw_contacts (sort_key_alt);
 CREATE INDEX raw_contacts_contact_id_index ON raw_contacts (contact_id);
 CREATE INDEX raw_contacts_source_id_account_id_index ON raw_contacts (sourceid, account_id);
 CREATE TRIGGER raw_contacts_deleted    BEFORE DELETE ON raw_contacts BEGIN    DELETE FROM data
 WHERE raw_contact_id=OLD._id;   DELETE FROM agg_exceptions     WHERE raw_contact_id1=OLD._id
 OR raw_contact_id2=OLD._id;   DELETE FROM visible_contacts     WHERE _id=OLD.contact_id
 AND (SELECT COUNT(*) FROM raw_contacts            WHERE contact_id=OLD.contact_id           )=1;
 DELETE FROM default_directory     WHERE _id=OLD.contact_id       AND (SELECT COUNT(*) FROM
 raw_contacts            WHERE contact_id=OLD.contact_id           )=1;   DELETE FROM contacts
 WHERE _id=OLD.contact_id       AND (SELECT COUNT(*) FROM raw_contacts            WHERE
 contact_id=OLD.contact_id           )=1; END;
 CREATE TRIGGER raw_contacts_marked_deleted    AFTER UPDATE ON raw_contacts BEGIN    UPDATE
 raw_contacts     SET version=OLD.version+1      WHERE _id=OLD._id       AND NEW.deleted!= OLD
 .deleted; END;

 619|1||0|2|1|0|620|0|0||0|0||0|刚哥|刚哥|40||0|GANG 刚 GE 哥|GANG 刚 GE 哥|0||||
 673|1||0|2|1|0|674|0|0||0|0||0|文思海辉王佳|文思海辉王佳|40||0|WEN 文 SI 思 HAI 海 HUI 辉 WANG 王 JIA 佳|
 WEN 文 SI 思 HAI 海 HUI 辉 WANG 王 JIA 佳|0||||
 */
@ClassVersion(version = 1)
public class RawContacts {

    @Primary
    public int id;
    public int _id;// 673
    public int account_id;// 1
    public String sourceid;//
    public int raw_contact_is_read_only;// 0
    public int version;// 2(版本号，用于监听变化)
    public int dirty;// 1
    public int deleted;// 0(删除标志， 0为默认,1表示这行数据已经删除 )
    public int contact_id;// 674
    public int aggregation_mode;// 0
    public int aggregation_needed;// 0
    public String custom_ringtone;//
    public int send_to_voicemail;// 0
    public int times_contacted;// 0
    public int last_time_contacted;// (最后联系的时间)
    public int starred;// 0
    public String display_name;// 文思海辉王佳(联系人名称)
    public String display_name_alt;// 文思海辉王佳
    public int display_name_source;// 40
    public String phonetic_name;//
    public String phonetic_name_style;// 0
    public String sort_key;// WEN 文 SI 思 HAI 海 HUI 辉 WANG 王 JIA 佳
    public String sort_key_alt;// WEN 文 SI 思 HAI 海 HUI 辉 WANG 王 JIA 佳
    public int name_verified;// 0
    public String sync1;//
    public String sync2;//
    public String sync3;//
    public String sync4;//

}
