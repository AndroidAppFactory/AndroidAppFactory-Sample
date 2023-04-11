package com.bihe0832.android.base.puzzle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.bihe0832.android.base.puzzle.PuzzleGameMode;
import com.bihe0832.android.base.puzzle.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class ImagePiece {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EMPTY = 1;

    private int type = TYPE_NORMAL;
    private int index;
    private Bitmap bitmap;
    private ImageView imageView;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 传入一个bitmap 返回 一个picec集合
     *
     * @param bitmap
     * @param count
     * @return
     */
    public static List<ImagePiece> splitImage(Context context, Bitmap bitmap, int count, PuzzleGameMode gameMode) {

        List<ImagePiece> imagePieces = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int picWidth = Math.min(width, height) / count;

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * count);
                //为createBitmap 切割图片获取xy
                int x = j * picWidth;
                int y = i * picWidth;
                if (PuzzleGameMode.NORMAL == gameMode) {
                    if (i == count - 1 && j == count - 1) {
                        imagePiece.setType(ImagePiece.TYPE_EMPTY);
                        Bitmap emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
                        imagePiece.setBitmap(emptyBitmap);
                    } else {
                        imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picWidth));
                    }
                } else {
                    imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picWidth));
                }
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }
}
