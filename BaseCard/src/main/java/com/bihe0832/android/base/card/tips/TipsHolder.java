package com.bihe0832.android.base.card.tips;


import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bihe0832.android.base.card.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;

/**
 * @author zixie code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */

public class TipsHolder extends CardBaseHolder {

    public TextView mTipsText;


    public TipsHolder(View view, Context context) {
        super(view, context);
    }

    @Override
    public void initView() {
        mTipsText = getView(R.id.test_title);
    }

    @Override
    public void initData(CardBaseModule item) {
        TipsData data = (TipsData) item;
        mTipsText.setText(Html.fromHtml(data.mContentText));
        if (null != data.mListener) {
            itemView.setOnClickListener(data.mListener);
        }
    }
}
