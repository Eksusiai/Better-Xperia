package com.sonymobile.generativeartwork.language;

/* JADX INFO: loaded from: classes2.dex */
public class LanguageData {
    private static final int NUM_CHARS = 2;
    public final char[] mCharacter;
    public final char[] mOriginalChars;
    public boolean isLanguageAllowed = true;
    public int numSkippedLetters = 0;

    public LanguageData(char c, char c2) {
        char[] cArr = {c, c2};
        this.mCharacter = cArr;
        char[] cArr2 = new char[2];
        this.mOriginalChars = cArr2;
        System.arraycopy(cArr, 0, cArr2, 0, cArr.length);
        LanguageRules.apply(this);
    }

    public String toString() {
        return "CharacterData [mCharacter1=" + this.mCharacter[0] + ", mCharacter2=" + this.mCharacter[1] + ", isLanguageAllowed=" + this.isLanguageAllowed + "]";
    }
}
