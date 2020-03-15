package com.github.kuro46.japanesechat;

import com.google.common.collect.ImmutableListMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public final class RomajiConverter {

    // Based on Mozc
    private static final ImmutableListMultimap<String, String> CONSONANT_MAPPINGS =
        new ImmutableListMultimap.Builder<String, String>()
            .putAll("", "あ", "い", "う", "え", "お")
            .putAll("k", "か", "き", "く", "け", "こ")
            .putAll("s", "さ", "し", "す", "せ", "そ")
            .putAll("t", "た", "ち", "つ", "て", "と")
            .putAll("n", "な", "に", "ぬ", "ね", "の")
            .putAll("h", "は", "ひ", "ふ", "へ", "ほ")
            .putAll("m", "ま", "み", "む", "め", "も")
            .putAll("y", "や", "い", "ゆ", "いぇ", "よ")
            .putAll("r", "ら", "り", "る", "れ", "ろ")
            .putAll("w", "わ", "うぃ", "う", "うぇ", "を")
            .putAll("g", "が", "ぎ", "ぐ", "げ", "ご")
            .putAll("z", "ざ", "じ", "ず", "ぜ", "ぞ")
            .putAll("d", "だ", "ぢ", "づ", "で", "ど")
            .putAll("b", "ば", "び", "ぶ", "べ", "ぼ")
            .putAll("j", "じゃ", "じ", "じゅ", "じぇ", "じょ")
            .putAll("p", "ぱ", "ぴ", "ぷ", "ぺ", "ぽ")
            // Y
            .putAll("ky", "きゃ", "きぃ", "きゅ", "きぇ", "きょ")
            .putAll("sy", "しゃ", "しぃ", "しゅ", "しぇ", "しょ")
            .putAll("ty", "ちゃ", "ちぃ", "ちゅ", "ちぇ", "ちょ")
            .putAll("ny", "にゃ", "にぃ", "にゅ", "にぇ", "にょ")
            .putAll("hy", "ひゃ", "ひぃ", "ひゅ", "ひぇ", "ひょ")
            .putAll("my", "みゃ", "みぃ", "みゅ", "みぇ", "みょ")
            .putAll("ry", "りゃ", "りぃ", "りゅ", "りぇ", "りょ")
            .putAll("gy", "ぎゃ", "ぎぃ", "ぎゅ", "ぎぇ", "ぎょ")
            .putAll("zy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ")
            .putAll("dy", "ぢゃ", "ぢぃ", "ぢゅ", "ぢぇ", "ぢょ")
            .putAll("by", "びゃ", "びぃ", "びゅ", "びぇ", "びょ")
            .putAll("jy", "じゃ", "じぃ", "じゅ", "じぇ", "じょ")
            .putAll("py", "ぴゃ", "ぴぃ", "ぴゅ", "ぴぇ", "ぴょ")
            .putAll("wy", "wya", "ゐ", "wyu", "ゑ", "wyo")
            // H
            .putAll("sh", "しゃ", "し", "しゅ", "しぇ", "しょ")
            .putAll("th", "てゃ", "てぃ", "てゅ", "てぇ", "てょ")
            .putAll("wh", "うぁ", "うぃ", "う", "うぇ", "うぉ")
            .putAll("ch", "ちゃ", "ち", "ちゅ", "ちぇ", "ちょ")
            // W
            .putAll("tw", "とぁ", "とぃ", "とぅ", "とぇ", "とぉ")
            // L
            .putAll("l", "ぁ", "ぃ", "ぅ", "ぇ", "ぉ")
            .putAll("lt", "lta", "lti", "っ", "lte", "lto")
            .putAll("ly", "ゃ", "ぃ", "ゅ", "ぇ", "ょ")
            // X
            .putAll("x", "ぁ", "ぃ", "ぅ", "ぇ", "ぉ")
            .putAll("xt", "xta", "xti", "っ", "xto")
            .putAll("xy", "ゃ", "ぃ", "ゅ", "ぇ", "ょ")
            .build();
//    private static final ImmutableMap<Character, String> OTHER_MAPPINGS = ImmutableMap.builder()
//        .put(
//        .build();
//
    private RomajiConverter() {
    }

    /**
     * ローマ字をひらがなに変換します。
     *
     * @param source 変換するローマ字
     * @return ひらがな
     */
    public static String toHiragana(@NonNull final String source) {
        return new HiraganaConverter(source).getDest();
    }

    /**
     * ローマ字をひらがなに変換し、可能な部分は漢字に置き換えます。
     *
     * @param source ローマ字
     * @return 変換後文字列
     */
    public static String toKanji(@NonNull final String source) throws IOException {
        final String hiragana = toHiragana(source);
        final String url = "http://www.google.com/transliterate?langpair=ja-Hira|ja&text="
            + URLEncoder.encode(hiragana, StandardCharsets.UTF_8.toString());
        final String json;
        try (final BufferedReader reader = openBufReader(new URL(url))) {
            json = reader.lines().collect(Collectors.joining());
        } catch (final IOException e) {
            throw new IOException("Failed to access to Google API!", e);
        }
        final StringBuilder msg = new StringBuilder();
        for (final JsonElement element : new Gson().fromJson(json, JsonArray.class)) {
            msg.append(((JsonArray) element).get(1).getAsJsonArray().get(0).getAsString());
        }
        return msg.toString();
    }

    private static BufferedReader openBufReader(@NonNull final URL url) throws IOException {
        return new BufferedReader(new InputStreamReader(
                    url.openStream(), StandardCharsets.UTF_8));
    }

    private static final class HiraganaConverter {

        private final StringBuilder buffer = new StringBuilder();
        private final StringBuilder dest = new StringBuilder();

        HiraganaConverter(@NonNull final String source) {
            final List<String> sources = Arrays.asList(source.split(" "));
            final List<HiraConvertResult> results = new ArrayList<>(sources.size());
            for (final char c : source.toCharArray()) {
                final String bufferStr = buffer.toString();
                if (c == 'n' && bufferStr.equals("n")) {
                    dest.append('ん');
                    clearBuf();
                    continue;
                }
                if (CONSONANT_MAPPINGS.containsKey(bufferStr)
                    && bufferStr.equals(Character.toString(c))) {
                    dest.append("っ");
                    clearBufAndInit(c);
                    continue;
                }
                final int vowelIndex = "aiueo".indexOf(c);
                if (vowelIndex != -1) {
                    if (!CONSONANT_MAPPINGS.containsKey(bufferStr)) {
                        flushBuf();
                        continue;
                    }
                    dest.append(CONSONANT_MAPPINGS.get(bufferStr).get(vowelIndex));
                    clearBuf();
                    continue;
                }
                // 'c' is not aiueoy
                if (c != 'y' && bufferStr.equals("n")) {
                    dest.append('ん');
                    clearBuf();
                    if (Character.isLowerCase(c)) {
                        buffer.append(c);
                    } else {
                        dest.append(c);
                    }
                    continue;
                }
                if (c == '-') {
                    flushBuf();
                    dest.append('ー');
                    continue;
                }
                if (Character.isLowerCase(c)) {
                    buffer.append(c);
                } else {
                    flushBuf();
                    dest.append(c);
                }
            }
            dest.append(buffer);
        }

        private void clearBuf() {
            buffer.setLength(0);
        }

        private void clearBufAndInit(final char c) {
            clearBuf();
            buffer.append(c);
        }

        private void flushBuf() {
            dest.append(buffer);
            clearBuf();
        }

        private String convert(@NonNull String sourceStrategy) {
            sourceStrategy = sourceStrategy.trim();
            if (sourceStrategy.isEmpty()) {
                return sourceStrategy;
            }
            if (Character.isUpperCase(sourceStrategy.charAt(0))) {
                return sourceStrategy;
            }

            for (final char c : sourceStrategy.toCharArray()) {
                final String bufferStr = buffer.toString();
                if (c == 'n' && bufferStr.equals("n")) {
                    dest.append('ん');
                    clearBuf();
                    continue;
                }
                if (CONSONANT_MAPPINGS.containsKey(bufferStr)
                    && bufferStr.equals(Character.toString(c))) {
                    dest.append("っ");
                    clearBufAndInit(c);
                    continue;
                }
                final int vowelIndex = "aiueo".indexOf(c);
                if (vowelIndex != -1) {
                    if (!CONSONANT_MAPPINGS.containsKey(bufferStr)) {
                        flushBuf();
                        continue;
                    }
                    dest.append(CONSONANT_MAPPINGS.get(bufferStr).get(vowelIndex));
                    clearBuf();
                    continue;
                }
                // 'c' is not aiueoy
                if (c != 'y' && bufferStr.equals("n")) {
                    dest.append('ん');
                    clearBuf();
                    if (Character.isLowerCase(c)) {
                        buffer.append(c);
                    } else {
                        dest.append(c);
                    }
                    continue;
                }
                if (c == '-') {
                    flushBuf();
                    dest.append('ー');
                    continue;
                }
                if (Character.isLowerCase(c)) {
                    buffer.append(c);
                } else {
                    flushBuf();
                    dest.append(c);
                }
            }
            dest.append(buffer);
            if (dest.charAt(dest.length() - 1) == 'n') {
                dest.replace(dest.length() - 1, dest.length(), "ん");
            }
            final String converted = dest.toString();
            clearBuf();
            dest.setLength(0);
            return converted;
        }

        String getDest() {
            return dest.toString();
        }
    }

    @AllArgsConstructor
    private static final class HiraConvertResult {
        @Getter
        private final String convertedStr;
        @Getter
        private final boolean doConvertToKanji;
    }
}
