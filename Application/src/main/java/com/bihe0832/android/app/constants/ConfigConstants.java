package com.bihe0832.android.app.constants;

import com.bihe0832.android.lib.utils.encrypt.messagedigest.MD5;

/**
 * @author zixie code@bihe0832.com
 * Created on 2023/6/16.
 * Description: Description
 */
public interface ConfigConstants {

    public interface APK {
        String KEY_SIGNATURE_TYPE = "signatureType";
        String VALUE_DEFAULT_SIGNATURE_TYPE = MD5.MESSAGE_DIGEST_TYPE_MD5;
    }
}
