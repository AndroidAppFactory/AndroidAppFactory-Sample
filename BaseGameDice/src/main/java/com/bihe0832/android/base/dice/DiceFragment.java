package com.bihe0832.android.base.dice;

import android.app.Service;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bihe0832.android.base.game.ui.BaseShakeGameFragment;
import com.bihe0832.android.lib.log.ZLog;
import com.bihe0832.android.lib.text.TextFactoryUtils;
import com.bihe0832.android.lib.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;

public class DiceFragment extends BaseShakeGameFragment {

    private ArrayList<Integer> mDiceResultList = new ArrayList<>();
    private ArrayList<Integer> mDiceSortResultList = new ArrayList<>();

    private SeekBar mSeekBarDef;
    private TextView mLockDiceBtn;
    private TextView mSortDiceBtn;

    private boolean mIsSort = false;

    private boolean isGameStart = false;


    @Override
    protected int getLayoutID() {
        return R.layout.com_bihe0832_base_dice;
    }

    public void initGameUI() {
        setLocked(false);

        mSeekBarDef = (SeekBar) getView().findViewById(R.id.diceSeekBar);
        mLockDiceBtn = (TextView) getView().findViewById(R.id.diceBtnLock);
        mSortDiceBtn = (TextView) getView().findViewById(R.id.diceBtnSort);


        mSeekBarDef.setProgress(DiceConfig.INSTANCE.getDiceNum());
        mSeekBarDef.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateNum(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateNum(seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNum(seekBar.getProgress());
            }

        });
        updateNum(mSeekBarDef.getProgress());

        final CheckBox lockCheckbox = (CheckBox) getView().findViewById(R.id.diceCheckBoxLock);
        lockCheckbox.setChecked(DiceConfig.INSTANCE.isAutoLocked());
        lockCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean locked) {
                setLocked(locked);
                DiceConfig.INSTANCE.setAutoLocked(locked);
                showLockButton();
            }
        });
        mLockDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocked(!isLocked());
                showLockButton();
            }
        });

        final CheckBox sortCheckBox = (CheckBox) getView().findViewById(R.id.diceCheckBoxSort);
        sortCheckBox.setChecked(DiceConfig.INSTANCE.isAutoSorted());
        sortCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean sort) {
                DiceConfig.INSTANCE.setAutoSorted(sort);
                mIsSort = sort;
                showDiceResult();
                showSortButton();
            }
        });
        mSortDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsSort = !mIsSort;
                showDiceResult();
                showSortButton();
            }
        });

        resetGameUI();
    }

    private void updateNum(int process) {
        DiceConfig.INSTANCE.setDiceNum(process);
        TextView numTextView = (TextView) getView().findViewById(R.id.diceTextViewSeekBarNum);
        numTextView.setText(String.format(getString(R.string.dice_seekbar_changed), String.valueOf(process + 1)));
    }

    public void startGame() {
        if (!isLocked()) {
            ZLog.d("startGame start");
            // 获取骰子数量
            int num = mSeekBarDef.getProgress() + 1;

            //摇出骰子点数
            mDiceResultList = new ArrayList<>(num);
            for (int i = 0; i < num; i++) {
                mDiceResultList.add(MathUtils.getRandNumByLimit(1, 6));
            }

            mDiceSortResultList = new ArrayList<Integer>();
            for (int result : mDiceResultList) {
                mDiceSortResultList.add(result);
            }
            Collections.sort(mDiceSortResultList);
            startAnimation();
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{50L, 200L, 100L, 300L}, -1);

            ZLog.d("startGame end");
        }
    }

    private void startAnimation() {

        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）

        TranslateAnimation mAnimation = new TranslateAnimation(-30f * density, 30f * density, 0, 0);
        mAnimation.setDuration(100);//设置动画持续时间
        mAnimation.setRepeatCount(5);
        mAnimation.setRepeatMode(Animation.REVERSE);
        ImageView barrleView = (ImageView) getView().findViewById(R.id.diceBarrleImg);

        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ZLog.d("onAnimationStart");

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ZLog.d("onAnimationEnd");
                isGameStart = true;
                mIsSort = DiceConfig.INSTANCE.isAutoSorted();
                setLocked(DiceConfig.INSTANCE.isAutoLocked());
                //展示UI
                showDiceResult();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        barrleView.setAnimation(mAnimation);
        barrleView.startAnimation(mAnimation);
        barrleView.invalidate();
    }

    private void showDiceResult() {
        if (mDiceSortResultList.size() > 0 && mDiceResultList.size() > 0) {
            hideBarrleLayout();
            if (mIsSort) {
                showDice(mDiceSortResultList);
            } else {
                showDice(mDiceResultList);
            }
            showLockButton();
            showSortButton();
        }
    }

    private void hideBarrleLayout() {
        ImageView diceBackLayout = (ImageView) getView().findViewById(R.id.diceBarrleImg);
        diceBackLayout.setVisibility(View.GONE);
    }

    private void showBarrleLayout() {
        ImageView diceBackLayout = (ImageView) getView().findViewById(R.id.diceBarrleImg);
        diceBackLayout.setVisibility(View.VISIBLE);
    }

    private void showDice(ArrayList<Integer> resultList) {
        if (resultList != null) {
            if (resultList.size() > 4) {
                showMoreThanFourDice(resultList);
            } else {
                showLessThanFourDice(resultList);
            }
        }
    }

    @Override
    public void resetGameUI() {
        isGameStart = false;

        //清除上一次的结果
        LinearLayout diceTopLayout = (LinearLayout) getView().findViewById(R.id.diceImgLayoutTop);
        diceTopLayout.setVisibility(View.GONE);

        LinearLayout diceBottomLayout = (LinearLayout) getView().findViewById(R.id.diceImgLayoutBottom);
        diceBottomLayout.setVisibility(View.GONE);

        TextView numTextView = (TextView) getView().findViewById(R.id.diceTextViewDiceNumSum);
        numTextView.setText(getString(R.string.dice_tips_dice_result) + 0);
        numTextView.setVisibility(View.GONE);

        hideLockButton();
        hideSortButton();
        showBarrleLayout();
    }

    private void showLessThanFourDice(ArrayList<Integer> resultList) {
        if (resultList.size() > 4) {
            showMoreThanFourDice(resultList);
            return;
        }
        int diceWidth = getDiceWithAndPosition(resultList.size());
        int diceNumSum = 0;

        LinearLayout diceTopLayout = (LinearLayout) getView().findViewById(R.id.diceImgLayoutTop);
        diceTopLayout.removeAllViews();
        diceTopLayout.setVisibility(View.VISIBLE);
        for (int result : resultList) {
            ImageView img = new ImageView(getActivity());
            LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(diceWidth, diceWidth);
            img.setLayoutParams(tempParams);
            img.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/dice_" + result));
            diceTopLayout.addView(img);
            diceNumSum += result;
        }
        showTotalDice(diceNumSum);
    }

    private void showMoreThanFourDice(ArrayList<Integer> resultList) {
        if (resultList.size() < 5) {
            showLessThanFourDice(resultList);
            return;
        }
        int diceWidth = getDiceWithAndPosition(resultList.size());

        int diceNumSum = 0;
        int topNum = 0;
        LinearLayout diceTopLayout = (LinearLayout) getView().findViewById(R.id.diceImgLayoutTop);
        diceTopLayout.removeAllViews();
        diceTopLayout.setVisibility(View.VISIBLE);
        LinearLayout diceBottomLayout = (LinearLayout) getView().findViewById(R.id.diceImgLayoutBottom);
        diceBottomLayout.removeAllViews();
        diceBottomLayout.setVisibility(View.VISIBLE);
        for (int result : resultList) {
            if (topNum < resultList.size() / 2) {
                ImageView img = new ImageView(getActivity());
                LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(diceWidth, diceWidth);
                img.setLayoutParams(tempParams);
                img.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/dice_" + result));
                diceTopLayout.addView(img);
            } else {
                ImageView img = new ImageView(getActivity());
                LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(diceWidth, diceWidth);
                img.setLayoutParams(tempParams);
                img.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/drawable/dice_" + result));
                diceBottomLayout.addView(img);
            }
            topNum++;
            diceNumSum += result;
        }
        showTotalDice(diceNumSum);

    }

    private void showTotalDice(int diceNumSum) {
        TextView numTextView = (TextView) getView().findViewById(R.id.diceTextViewDiceNumSum);
        numTextView.setText(TextFactoryUtils.getSpannedTextByHtml(getString(R.string.dice_tips_dice_result) + TextFactoryUtils.getSpecialText(String.valueOf(diceNumSum), getContext().getColor(R.color.colorAccent))));
        numTextView.setVisibility(View.VISIBLE);
    }

    private void showLockButton() {
        if (isLocked()) {
            mLockDiceBtn.setText(getString(R.string.dice_btn_lock_unlock));
        } else {
            mLockDiceBtn.setText(getString(R.string.dice_btn_lock_lock));
        }
        if (isGameStart) {
            mLockDiceBtn.setVisibility(View.VISIBLE);
        } else {
            mLockDiceBtn.setVisibility(View.GONE);
        }
    }

    private void hideLockButton() {
        mLockDiceBtn.setVisibility(View.GONE);
    }

    private void showSortButton() {

        ZLog.d("mIsSort:" + mIsSort);
        if (mIsSort) {
            mSortDiceBtn.setText(getString(R.string.dice_btn_sort_unsort));
        } else {
            mSortDiceBtn.setText(getString(R.string.dice_btn_sort_sort));
        }
        if (isGameStart) {
            mSortDiceBtn.setVisibility(View.VISIBLE);
        } else {
            mSortDiceBtn.setVisibility(View.GONE);
        }
    }

    private void hideSortButton() {
        mSortDiceBtn.setVisibility(View.GONE);
    }

    private int getDiceWithAndPosition(int diceNum) {

        View tempView = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        Rect rect = new Rect();
        tempView.getDrawingRect(rect);
        int diceWidth = 0;
        if (diceNum > 4) {
            diceWidth = rect.width() / 6;
        } else {
            diceWidth = rect.width() / (diceNum + 2);
        }
        return diceWidth;
    }
}
