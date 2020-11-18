package com.bihe0832.android.base.card.tips;

import android.view.View;

import com.bihe0832.android.base.card.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */
public class TipsData extends CardBaseModule {
    public String mContentText = "";
    public View.OnClickListener mListener = null;

    public int getResID() {
        return R.layout.card_tips;
    }

    public Class<? extends CardBaseHolder> getViewHolderClass() {
        return TipsHolder.class;
    }

    public TipsData(){

    }
    public TipsData(String tips) {
        mContentText = tips;
    }

    public TipsData(String netType, View.OnClickListener listener) {
        mContentText = netType;
        mListener = listener;
    }
}