package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;

/**
 * CREATE TABLE part (_id INTEGER PRIMARY KEY AUTOINCREMENT,mid INTEGER,seq INTEGER DEFAULT 0,
 * ct TEXT,name TEXT,chset INTEGER,cd TEXT,fn TEXT,cid TEXT,cl TEXT,ctt_s INTEGER,ctt_t TEXT,
 * _data TEXT,text TEXT);
 */
@ClassVersion(version = 1)
public class Part {
}
