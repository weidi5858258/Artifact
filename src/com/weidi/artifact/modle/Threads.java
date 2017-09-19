package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;

/**
 * CREATE TABLE threads (_id INTEGER PRIMARY KEY AUTOINCREMENT,date INTEGER DEFAULT 0,
 * message_count INTEGER DEFAULT 0,recipient_ids TEXT,snippet TEXT,snippet_cs INTEGER DEFAULT
 * 0,read INTEGER DEFAULT 1,type INTEGER DEFAULT 0,error INTEGER DEFAULT 0,has_attachment
 * INTEGER DEFAULT 0);
 */
@ClassVersion(version = 1)
public class Threads {
}
