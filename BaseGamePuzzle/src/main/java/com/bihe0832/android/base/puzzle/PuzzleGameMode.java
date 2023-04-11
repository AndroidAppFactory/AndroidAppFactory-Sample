package com.bihe0832.android.base.puzzle;

/**
 * @author zixie code@bihe0832.com Created on 1/6/21.
 */

public enum PuzzleGameMode {

    BAD(0), NORMAL(1), EXCHANGE(2);

    private int val = 0;

    PuzzleGameMode(int i) {
        val = i;
    }

    public static PuzzleGameMode getGameMode(int i) {
        PuzzleGameMode platform = PuzzleGameMode.BAD;
        switch (i) {
            case 0:
                platform = PuzzleGameMode.BAD;
                break;
            case 1:
                platform = PuzzleGameMode.NORMAL;
                break;
            case 2:
                platform = PuzzleGameMode.EXCHANGE;
                break;
        }
        return platform;
    }

    public String getDesc() {
        switch (val) {
            case 1:
                return "普通模式";
            case 2:
                return "交换模式";
            default:
                return "未知模式";
        }

    }
}
