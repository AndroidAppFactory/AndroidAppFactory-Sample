package com.bihe0832.android.base.card.m3u8;

import com.bihe0832.android.base.card.R;
import com.bihe0832.android.base.card.m3u8.M3U8ViewHolder.OnClickListener;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;

/**
 * @author hardyshi code@bihe0832.com
 *         Created on 2019-11-21.
 *         Description: Description
 */
public class M3U8ViewData extends CardBaseModule {

    public String m3u8URL = "";
    public String baseURl = "";
    public long downloadTime = 0;

    public String localpath = "";

    public OnClickListener mListener = null;

    public int getResID() {
        return R.layout.card_m3u8_list_item;
    }

    public Class<? extends CardBaseHolder> getViewHolderClass() {
        return M3U8ViewHolder.class;
    }

    public M3U8ViewData() {

    }
}