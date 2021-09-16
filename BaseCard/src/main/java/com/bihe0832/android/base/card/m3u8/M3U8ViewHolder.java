package com.bihe0832.android.base.card.m3u8;


import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bihe0832.android.base.card.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;
import com.bihe0832.android.lib.utils.DateUtil;
import com.bihe0832.android.lib.utils.encrypt.MD5;

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */

public class M3U8ViewHolder extends CardBaseHolder {
    public TextView title_info;
    public TextView download_url;
    public TextView local_path;

    public TextView video_name;

    public M3U8ViewHolder(View view, Context context) {
        super(view, context);
    }

    @Override
    public void initView() {
        title_info = getView(R.id.title_info);
        download_url = getView(R.id.download_url);
        local_path = getView(R.id.local_path);
        video_name = getView(R.id.video_name);
    }

    @Override
    public void initData(CardBaseModule item) {
        M3U8ViewData data = (M3U8ViewData) item;
        title_info.setText(DateUtil.getDateEN(data.downloadTime, "yyyy-MM-dd") + " 下载视频");
        download_url.setText("下载地址："+Html.fromHtml(data.m3u8URL));
        local_path.setText("本地路径："+data.localpath);
        video_name.setText("视频名称："+ MD5.getMd5(data.m3u8URL) + ".mp4");
        if (null != data.mListener) {
            itemView.setOnClickListener(data.mListener);
        }
    }
}
