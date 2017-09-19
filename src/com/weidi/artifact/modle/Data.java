package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.contacts/databases/contacts2.db

 CREATE TABLE data (_id INTEGER PRIMARY KEY AUTOINCREMENT,
 package_id INTEGER REFERENCES package(_id),
 mimetype_id INTEGER REFERENCES mimetype(_id) NOT NULL,
 raw_contact_id INTEGER REFERENCES raw_contacts(_id) NOT NULL,
 is_read_only INTEGER NOT NULL DEFAULT 0,is_primary INTEGER NOT NULL DEFAULT 0,
 is_super_primary INTEGER NOT NULL DEFAULT 0,data_version INTEGER NOT NULL DEFAULT 0,
 data1 TEXT,data2 TEXT,data3 TEXT,data4 TEXT,data5 TEXT,
 data6 TEXT,data7 TEXT,data8 TEXT,data9 TEXT,data10 TEXT,
 data11 TEXT,data12 TEXT,data13 TEXT,data14 TEXT,data15 TEXT,
 data_sync1 TEXT, data_sync2 TEXT, data_sync3 TEXT, data_sync4 TEXT );

 CREATE INDEX data_mimetype_data1_index ON data (mimetype_id,data1);
 CREATE INDEX data_raw_contact_id ON data (raw_contact_id);
 CREATE TRIGGER data_deleted BEFORE DELETE ON data BEGIN    UPDATE raw_contacts     SET version=version+1      WHERE _id=OLD.raw_contact_id;   DELETE FROM phone_lookup     WHERE data_id=OLD._id;   DELETE FROM status_updates     WHERE status_update_data_id=OLD._id;   DELETE FROM name_lookup     WHERE data_id=OLD._id; END;
 CREATE TRIGGER data_updated AFTER UPDATE ON data BEGIN    UPDATE data     SET data_version=OLD.data_version+1      WHERE _id=OLD._id;   UPDATE raw_contacts     SET version=version+1      WHERE _id=OLD.raw_contact_id; END;

 807||7|306|0|0|0|0|张小诺|诺|张||小|||||3|0||||||||
 808||14|306|0|0|0|0|13958270632|微信|发送消息|13021699d0fa2aebd89bd6c6513e7b55|||||||||||||||
 809||15|306|0|0|0|0|13958270632|微信|免费视频聊天|13021699d0fa2aebd89bd6c6513e7b55|||||||||||||||
 810||16|306|0|0|0|0|13958270632|微信|查看朋友圈|13021699d0fa2aebd89bd6c6513e7b55|||||||||||||||
 811||17|306|0|0|0|0|13958270632|微信||13021699d0fa2aebd89bd6c6513e7b55|||||||||||||||

 1128||5|619|0|0|0|0|152 1689 5399|2||+8615216895399|||||||||||||||
 1129||7|619|0|0|0|0|刚    哥|哥|刚|||||||2|0||||||||
 1183||5|673|0|0|0|0|01056859184|3||+861056859184|||||||||||||||
 1184||7|673|0|0|0|0|文思海辉王佳|文思海辉王佳||||||||2|0||||||||
 */
@ClassVersion(version = 1)
public class Data {

    @Primary
    public int id;
    public int _id;// 1184 808
    public int package_id;//
    public int mimetype_id;// 7 14(这个数字是多少,那么对应MimeTypes表中是什么类型,则data1对应的是什么值)
    public int raw_contact_id;// 673 306
    public int is_read_only;// 0 0
    public int is_primary;// 0 0
    public int is_super_primary;// 0 0
    public int data_version;// 0 0
    public String data1;// 文思海辉王佳 (13958270632) (137-6193-3405) (152 1689 5399)
    public String data2;// 文思海辉王佳 微信
    public String data3;//            发送消息
    public String data4;//            13021699d0fa2aebd89bd6c6513e7b55
    public String data5;//
    public String data6;//
    public String data7;//
    public String data8;//
    public String data9;//
    public String data10;// 2
    public String data11;// 0
    public String data12;//
    public String data13;//
    public String data14;//
    public String data15;//
    public String data_sync1;//
    public String data_sync2;//
    public String data_sync3;//
    public String data_sync4;//

}
