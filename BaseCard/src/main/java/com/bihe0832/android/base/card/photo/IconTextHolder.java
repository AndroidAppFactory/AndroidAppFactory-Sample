package com.bihe0832.android.base.card.photo;


import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bihe0832.android.base.card.R;
import com.bihe0832.android.framework.ZixieContext;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;
import com.bihe0832.android.lib.ui.image.GlideExtKt;
import com.bihe0832.android.lib.utils.os.DisplayUtil;

/**
 * @author hardyshi code@bihe0832.com
 *         Created on 2019-11-21.
 *         Description: Description
 */

public class IconTextHolder extends CardBaseHolder {

    public TextView mTipsText;
    public ImageView mTipsIcon;

    public IconTextHolder(View view, Context context) {
        super(view, context);
    }

    @Override
    public void initView() {
        mTipsIcon = getView(R.id.scene_icon);
        mTipsText = getView(R.id.scene_desc);
    }

    @Override
    public void initData(CardBaseModule item) {
        IconTextData data = (IconTextData) item;
        mTipsText.setText(Html.fromHtml(data.mContentText));
        if (TextUtils.isEmpty(data.mContentResURL)) {
            GlideExtKt.loadCenterInsideImage(mTipsIcon, data.mContentResID);
        } else {
            GlideExtKt.loadFitCenterImage(mTipsIcon, data.mContentResURL, R.color.gray, R.color.gray);
        }

        ViewGroup.LayoutParams newParams = itemView.getLayoutParams();
        int itemHeight =
                ((ZixieContext.INSTANCE.getScreenHeight() - data.mVerticalFix) -
                        DisplayUtil.dip2px(ZixieContext.INSTANCE.getApplicationContext(), 32 * data.mVerticalNum))
                        / data.mVerticalNum;
        newParams.height = itemHeight;
        itemView.setLayoutParams(newParams);

        ViewGroup.LayoutParams imageParams = mTipsIcon.getLayoutParams();
        int itemWidth =
                ((ZixieContext.INSTANCE.getScreenWidth() - data.mHorizontalFix - DisplayUtil
                        .dip2px(ZixieContext.INSTANCE.getApplicationContext(), 32 * data.mHorizontalNum))
                        / data.mHorizontalNum)
                        - DisplayUtil.dip2px(ZixieContext.INSTANCE.getApplicationContext(), 64);
        imageParams.width = itemWidth;
        imageParams.height = itemWidth;
        mTipsIcon.setLayoutParams(imageParams);

    }
}
