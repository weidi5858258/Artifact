package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;

/**
 * CREATE TABLE pending_msgs (_id INTEGER PRIMARY KEY,proto_type INTEGER,msg_id INTEGER,
 * msg_type INTEGER,err_type INTEGER,err_code INTEGER,retry_index INTEGER NOT NULL DEFAULT 0,
 * due_time INTEGER,last_try INTEGER);
 */
@ClassVersion(version = 1)
public class PendingMsgs {
}
