package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.contacts/databases/contacts2.db

 CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT,
 name_raw_contact_id INTEGER REFERENCES raw_contacts(_id),
 photo_id INTEGER REFERENCES data(_id),photo_file_id INTEGER REFERENCES photo_files(_id),
 custom_ringtone TEXT,send_to_voicemail INTEGER NOT NULL DEFAULT 0,
 times_contacted INTEGER NOT NULL DEFAULT 0,last_time_contacted INTEGER,
 starred INTEGER NOT NULL DEFAULT 0,has_phone_number INTEGER NOT NULL DEFAULT 0,
 lookup TEXT,status_update_id INTEGER REFERENCES data(_id));

 CREATE INDEX contacts_has_phone_index ON contacts (has_phone_number);
 CREATE INDEX contacts_name_raw_contact_id_index ON contacts (name_raw_contact_id);

 620|619||||0|0|0|0|1|0r619-484B48A6|
 674|673||||0|0|0|0|1|0r673-756D6EF24B294E6D7474508D|
 */
@ClassVersion(version = 1)
public class Contacts {

    @Primary
    public int id;
    public int _id;// 674(主要用于其它表通过contacts 表中的ID可以查到相应的数据。 )
    public int name_raw_contact_id;// 673
    public int photo_id;// (头像的ID，如果没有设置联系人头像，这个字段就为空 )
    public int photo_file_id;//
    public String custom_ringtone;//
    public int send_to_voicemail;// 0
    public int times_contacted;// 0(通话记录的次数)
    public int last_time_contacted;// 0(最后的通话时间)
    public int starred;// 0
    public int has_phone_number;// 1
    // 0r673-756D6EF24B294E6D7474508D(是一个持久化的储存，因为用户可能会改名，但是它改不了lookup)
    public String lookup;
    public int status_update_id;//
    // ContactsContract.ContactOptionsColumns
    // ContactsColumns

}
