package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;

/**
 * CREATE VIRTUAL TABLE words USING FTS3 (_id INTEGER PRIMARY KEY, index_text TEXT, source_id
 * INTEGER, table_to_use INTEGER);
 */
@ClassVersion(version = 1)
public class Words {
}
