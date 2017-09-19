package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/***
 /data/data/com.android.providers.contacts/databases/contacts2.db

 CREATE TABLE mimetypes (_id INTEGER PRIMARY KEY AUTOINCREMENT,mimetype TEXT NOT NULL);
 CREATE UNIQUE INDEX mime_type ON mimetypes (mimetype);

 1|vnd.android.cursor.item/email_v2
 2|vnd.android.cursor.item/im
 3|vnd.android.cursor.item/nickname
 4|vnd.android.cursor.item/organization
 5|vnd.android.cursor.item/phone_v2
 6|vnd.android.cursor.item/sip_address
 7|vnd.android.cursor.item/name
 8|vnd.android.cursor.item/postal-address_v2
 9|vnd.android.cursor.item/identity
 10|vnd.android.cursor.item/photo
 11|vnd.android.cursor.item/group_membership
 12|vnd.android.cursor.item/note
 13|vnd.android.cursor.item/website
 14|vnd.android.cursor.item/vnd.com.tencent.mm.chatting.profile
 15|vnd.android.cursor.item/vnd.com.tencent.mm.chatting.voip.video
 16|vnd.android.cursor.item/vnd.com.tencent.mm.plugin.sns.timeline
 17|vnd.android.cursor.item/vnd.com.tencent.mm.chatting.voiceaction

 */
@ClassVersion(version = 1)
public class MimeTypes {

    @Primary
    public int id;
    public int _id;// 1
    public String mimetype;// vnd.android.cursor.item/email_v2

}
