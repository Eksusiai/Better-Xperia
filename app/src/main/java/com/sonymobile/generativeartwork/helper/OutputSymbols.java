package com.sonymobile.generativeartwork.helper;

import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class OutputSymbols {
    public static final int NUM_LETTERS = 2;
    public char[] Symbols;

    public OutputSymbols() {
        char[] cArr = new char[2];
        this.Symbols = cArr;
        Arrays.fill(cArr, ' ');
    }
}
