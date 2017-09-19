package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;

/**
 * CREATE TABLE pdu (_id INTEGER PRIMARY KEY AUTOINCREMENT,thread_id INTEGER,date INTEGER,
 * date_sent INTEGER DEFAULT 0,msg_box INTEGER,read INTEGER DEFAULT 0,m_id TEXT,sub TEXT,sub_cs
 * INTEGER,ct_t TEXT,ct_l TEXT,exp INTEGER,m_cls TEXT,m_type INTEGER,v INTEGER,m_size INTEGER,
 * pri INTEGER,rr INTEGER,rpt_a INTEGER,resp_st INTEGER,st INTEGER,tr_id TEXT,retr_st INTEGER,
 * retr_txt TEXT,retr_txt_cs INTEGER,read_status INTEGER,ct_cls INTEGER,resp_txt TEXT,d_tm
 * INTEGER,d_rpt INTEGER,locked INTEGER DEFAULT 0,seen INTEGER DEFAULT 0,text_only INTEGER
 * DEFAULT 0);
 */
@ClassVersion(version = 1)
public class Pdu {
}
