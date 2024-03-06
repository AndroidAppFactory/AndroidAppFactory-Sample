package com.bihe0832.android.base.card.m3u8;


import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.bihe0832.android.base.card.R;
import com.bihe0832.android.lib.adapter.CardBaseHolder;
import com.bihe0832.android.lib.adapter.CardBaseModule;
import com.bihe0832.android.lib.utils.encrypt.messagedigest.MD5;
import com.bihe0832.android.lib.utils.time.DateUtil;

/**
 * @author zixie code@bihe0832.com
 *         Created on 2019-11-21.
 *         Description: Description
 */

public class M3U8ViewHolder extends CardBaseHolder {

    public TextView title_info;
    public TextView download_url;
    public TextView local_path;
    public TextView download_delete;
    public TextView video_name;


    public interface OnClickListener {

        void onClick();

        void onDelete();

    }

    public M3U8ViewHolder(View view, Context context) {
        super(view, context);
    }

    @Override
    public void initView() {
        title_info = getView(R.id.title_info);
        download_url = getView(R.id.download_url);
        local_path = getView(R.id.local_path);
        video_name = getView(R.id.video_name);
        download_delete = getView(R.id.download_delete);
    }

    @Override
    public void initData(CardBaseModule item) {
        final M3U8ViewData data = (M3U8ViewData) item;
        title_info.setText(DateUtil.getDateEN(data.downloadTime, "yyyy-MM-dd") + " 下载视频");
        download_url.setText("下载地址：" + Html.fromHtml(data.m3u8URL));
        local_path.setText("本地路径：" + data.localpath);
        video_name.setText("视频名称：" + MD5.getMd5(data.m3u8URL) + ".mp4");
        if (null != data.mListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.mListener.onClick();
                }
            });

            download_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.mListener.onDelete();
                }
            });
        }else {
            itemView.setOnClickListener(null);
            download_delete.setOnClickListener(null);
        }
    }
}
