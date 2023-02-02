package com.bihe0832.android.base.card.photo;

import com.bihe0832.android.base.card.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;

/**
 * @author zixie code@bihe0832.com
 *         Created on 2019-11-21.
 *         Description: Description
 */
public class IconTextData extends CardBaseModule {

    public int mIconID = 0;
    public String mContentText = "";
    public String mContentResURL = "";
    public int mContentResID = -1;
    public int mHorizontalNum = 2;
    public int mVerticalNum = 2;
    public int mHorizontalFix = 0;
    public int mVerticalFix = 600;

    public int getResID() {
        return R.layout.card_puzzle_item;
    }

    public Class<? extends CardBaseHolder> getViewHolderClass() {
        return IconTextHolder.class;
    }

    public IconTextData() {

    }

    public IconTextData(String tips) {
        mContentText = tips;
    }

    public IconTextData(String netType, int resID) {
        mContentText = netType;
        mContentResID = resID;
    }

    public IconTextData(String netType, String url) {
        mContentText = netType;
        mContentResURL = url;
    }
}