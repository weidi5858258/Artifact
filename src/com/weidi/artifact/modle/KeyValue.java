package com.weidi.artifact.modle;

import com.weidi.dbutil.ClassVersion;
import com.weidi.dbutil.Primary;

/**
 * Created by root on 17-5-11.
 */
@ClassVersion(version = 1)
public class KeyValue {

    @Primary
    public int _id;
    public String key;
    public String value;

}
