package com.sonymobile.generativeartwork.language;

import android.os.Build;
import com.sonymobile.generativeartwork.gl.LetterStock;
import com.sonymobile.generativeartwork.utils.Utils;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public final class LanguageRules {
    private static final Character.UnicodeBlock ARABIC_SUPPLEMENT;
    private static final char CHAR_SPACE = ' ';
    private static final Character.UnicodeBlock CJK_STROKES;

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            CJK_STROKES = Character.UnicodeBlock.forName("CJK_STROKES");
            ARABIC_SUPPLEMENT = Character.UnicodeBlock.forName("ARABIC_SUPPLEMENT");
        } else {
            CJK_STROKES = Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
            ARABIC_SUPPLEMENT = Character.UnicodeBlock.ARABIC;
        }
    }

    public static void apply(LanguageData languageData) {
        languageData.isLanguageAllowed = true;
        languageData.numSkippedLetters = 0;
        for (char c : languageData.mCharacter) {
            languageData.isLanguageAllowed = (languageData.isLanguageAllowed && isSymbol(c)) || Character.isDigit(c) || (Character.isLetter(c) && (isLatin(c) || isCyrillic(c) || isGreek(c) || isArmenian(c) || isGeorgian(c) || isTamil(c) || isEastAsianSymbols(c) || isArabic(c) || isHebrew(c)));
        }
        if (languageData.isLanguageAllowed) {
            if (isEastAsianSymbols(languageData.mCharacter[0]) || isHebrew(languageData.mCharacter[0]) || isArabic(languageData.mCharacter[0])) {
                languageData.mCharacter[1] = CHAR_SPACE;
            }
            for (int i = 0; i < languageData.mCharacter.length; i++) {
                char c2 = languageData.mCharacter[i];
                if (isLatin(c2) || isCyrillic(c2) || isGreek(c2) || isArmenian(c2)) {
                    languageData.mCharacter[i] = String.valueOf(c2).toUpperCase(Locale.US).charAt(0);
                }
                if (isSymbol(c2)) {
                    languageData.mCharacter[i] = CHAR_SPACE;
                }
                if (languageData.mCharacter[i] == ' ') {
                    languageData.numSkippedLetters++;
                }
            }
            return;
        }
        languageData.numSkippedLetters = languageData.mCharacter.length;
        for (int i2 = 0; i2 < languageData.mCharacter.length; i2++) {
            languageData.mCharacter[i2] = CHAR_SPACE;
        }
    }

    public static int getFirstStockImageId(LanguageData languageData) {
        return Utils.generateRand(languageData.mOriginalChars[0], languageData.mOriginalChars[1], LetterStock.getNumImages());
    }

    public static int getSecondStockImageId(LanguageData languageData) {
        return Utils.generateRand(languageData.mOriginalChars[1], (char) (languageData.mOriginalChars[1] + languageData.mOriginalChars[0]), LetterStock.getNumImages());
    }

    public static int getColorPaletteId(LanguageData languageData, int i) {
        return Utils.generateRand(languageData.mOriginalChars[0], (char) (languageData.mOriginalChars[1] + languageData.mOriginalChars[0]), i);
    }

    private static boolean isEastAsianSymbols(char c) {
        Character.UnicodeBlock unicodeBlockOf = Character.UnicodeBlock.of(c);
        return unicodeBlockOf.equals(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT) || unicodeBlockOf.equals(Character.UnicodeBlock.KANGXI_RADICALS) || unicodeBlockOf.equals(Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS) || unicodeBlockOf.equals(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) || unicodeBlockOf.equals(Character.UnicodeBlock.HIRAGANA) || unicodeBlockOf.equals(Character.UnicodeBlock.KATAKANA) || unicodeBlockOf.equals(Character.UnicodeBlock.BOPOMOFO) || unicodeBlockOf.equals(Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) || unicodeBlockOf.equals(Character.UnicodeBlock.HANGUL_JAMO) || unicodeBlockOf.equals(Character.UnicodeBlock.KANBUN) || unicodeBlockOf.equals(Character.UnicodeBlock.BOPOMOFO_EXTENDED) || unicodeBlockOf.equals(CJK_STROKES) || unicodeBlockOf.equals(Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS) || unicodeBlockOf.equals(Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS) || unicodeBlockOf.equals(Character.UnicodeBlock.CJK_COMPATIBILITY) || unicodeBlockOf.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || unicodeBlockOf.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) || unicodeBlockOf.equals(Character.UnicodeBlock.YIJING_HEXAGRAM_SYMBOLS);
    }

    private static boolean isLatin(char c) {
        Character.UnicodeBlock unicodeBlockOf = Character.UnicodeBlock.of(c);
        return unicodeBlockOf.equals(Character.UnicodeBlock.BASIC_LATIN) || unicodeBlockOf.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) || unicodeBlockOf.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) || unicodeBlockOf.equals(Character.UnicodeBlock.LATIN_EXTENDED_B);
    }

    private static boolean isCyrillic(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.CYRILLIC);
    }

    private static boolean isGreek(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.GREEK);
    }

    private static boolean isArmenian(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.ARMENIAN);
    }

    private static boolean isGeorgian(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.GEORGIAN);
    }

    private static boolean isHebrew(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.HEBREW);
    }

    private static boolean isTamil(char c) {
        return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.TAMIL);
    }

    private static boolean isArabic(char c) {
        Character.UnicodeBlock unicodeBlockOf = Character.UnicodeBlock.of(c);
        return unicodeBlockOf.equals(Character.UnicodeBlock.ARABIC) || unicodeBlockOf.equals(ARABIC_SUPPLEMENT) || unicodeBlockOf.equals(Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A) || unicodeBlockOf.equals(Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B);
    }

    private static boolean isSymbol(char c) {
        return !Character.isLetterOrDigit(c) && isLatin(c);
    }
}
