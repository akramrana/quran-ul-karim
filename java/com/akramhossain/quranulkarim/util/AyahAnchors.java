package com.akramhossain.quranulkarim.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AyahAnchors {

    public static class AyahBand {
        public final int surah;
        public final int ayah;
        public final float[] box; // normalized: [x, y, w, h]

        public AyahBand(int surah, int ayah, float[] box) {
            this.surah = surah;
            this.ayah = ayah;
            this.box = box;
        }
    }

    private static class Entry {
        int surah, ayah, x, y;

        Entry(int surah, int ayah, int x, int y) {
            this.surah = surah;
            this.ayah = ayah;
            this.x = x;
            this.y = y;
        }
    }

    private static class HlMeta {
        int height;
        int mgwidth;
        int twidth;
        int ofwidth;
        int ofheight;
        int faselSura;
        int pageTop;
        int pageSuraTop;

        int memHeight;
        int memOfheight;

        int fpHeight;
        int fpMgwidth;
        int fpTwidth;
        int fpOfwidth;
        int fpOfheight;

        HlMeta(int height,
               int mgwidth,
               int twidth,
               int ofwidth,
               int ofheight,
               int faselSura,
               int pageTop,
               int pageSuraTop,
               int memHeight,
               int memOfheight,
               int fpHeight,
               int fpMgwidth,
               int fpTwidth,
               int fpOfwidth,
               int fpOfheight) {

            this.height = height;
            this.mgwidth = mgwidth;
            this.twidth = twidth;
            this.ofwidth = ofwidth;
            this.ofheight = ofheight;
            this.faselSura = faselSura;
            this.pageTop = pageTop;
            this.pageSuraTop = pageSuraTop;
            this.memHeight = memHeight;
            this.memOfheight = memOfheight;
            this.fpHeight = fpHeight;
            this.fpMgwidth = fpMgwidth;
            this.fpTwidth = fpTwidth;
            this.fpOfwidth = fpOfwidth;
            this.fpOfheight = fpOfheight;
        }
    }

    private static final int IMG_W = 456;
    private static final int IMG_H = 672;

    public enum MushafType {
        HAFS,
        WARSH,
        TAJWEED
    }

    private static final HlMeta HAFS_META = new HlMeta(
            30, 40, 416, 10, 15,
            110, 37, 80,
            45, 24,
            20, 80, 376, 5, 10
    );

    private static final HlMeta WARSH_META = new HlMeta(
            40, 25, 427, 17, 20,
            140, 30, 80,
            48, 22,
            20, 80, 376, 5, 10
    );

    private static final HlMeta TAJWEED_META = new HlMeta(
            40, 25, 427, 17, 20,
            140, 30, 80,
            48, 22,
            30, 100, 350, 10, 15
    );

    public static List<AyahBand> loadPageBands(Context context, int page) throws Exception {
        return loadPageBands(context, page, MushafType.HAFS, false);
    }

    public static List<AyahBand> loadPageBands(Context context, int page, MushafType mushafType) throws Exception {
        return loadPageBands(context, page, mushafType, false);
    }

    public static List<AyahBand> loadPageBands(Context context, int page, MushafType mushafType, boolean memorize) throws Exception {
        String path = "quran_pages_map/" + page + ".json";
        //String json = readAsset(context, path);
        String json;
        try {
            json = readAsset(context, path);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        JSONObject root = new JSONObject(json);
        JSONObject pageObj = root.getJSONObject(String.valueOf(page));

        List<Entry> entries = new ArrayList<>();
        Iterator<String> keys = pageObj.keys();

        while (keys.hasNext()) {
            String key = keys.next(); // e.g. "2_6"
            JSONArray arr = pageObj.getJSONArray(key);

            int x = arr.getInt(0);
            int y = arr.getInt(1);

            String[] parts = key.split("_");
            if (parts.length != 2) continue;

            int surah = Integer.parseInt(parts[0]);
            int ayah = Integer.parseInt(parts[1]);

            entries.add(new Entry(surah, ayah, x, y));
        }

        if (entries.isEmpty()) {
            return new ArrayList<>();
        }

        // Important: JS logic depends on ayah order, not visual y/x sort.
        entries.sort((a, b) -> {
            if (a.surah != b.surah) return Integer.compare(a.surah, b.surah);
            return Integer.compare(a.ayah, b.ayah);
        });

        HlMeta meta = getMeta(mushafType);
        return buildBandsJsStyle(entries, page, meta, memorize);
    }

    private static HlMeta getMeta(MushafType mushafType) {
        switch (mushafType) {
            case WARSH:
                return WARSH_META;
            case TAJWEED:
                return TAJWEED_META;
            case HAFS:
            default:
                return HAFS_META;
        }
    }

    private static List<AyahBand> buildBandsJsStyle(List<Entry> entries, int page, HlMeta meta, boolean memorize) {
        List<AyahBand> bands = new ArrayList<>();
        if (entries.isEmpty()) return bands;

        int height = meta.height;
        int mgwidth = meta.mgwidth;
        int twidth = meta.twidth;
        int ofwidth = meta.ofwidth;
        int ofheight = meta.ofheight;
        int faselSura = meta.faselSura;
        int pageTop = meta.pageTop;
        int pageSuraTop = meta.pageSuraTop;

        if (memorize) {
            height = meta.memHeight;
            ofheight = meta.memOfheight;
        }

        if (page == 1 || page == 2) {
            height = meta.fpHeight;
            mgwidth = meta.fpMgwidth;
            twidth = meta.fpTwidth;
            ofwidth = meta.fpOfwidth;
            ofheight = meta.fpOfheight;
        }

        Integer prevTop = null;
        Integer prevLeft = null;
        int count = 1;

        for (Entry e : entries) {
            int top = e.y - ofheight;
            int left = e.x - ofwidth;

            if (count == 1) {
                prevLeft = twidth;

                if (page == 1 || page == 2) {
                    prevTop = 270;
                } else {
                    prevTop = (e.ayah == 1) ? pageSuraTop : pageTop;
                }
            } else {
                if (e.ayah == 1) {
                    prevTop += faselSura;
                    prevLeft = twidth;
                }
            }

            int diff = top - prevTop;

            if (diff > (height * 1.6f)) {
                // hilite_1
                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        mgwidth,
                        prevTop,
                        prevLeft - mgwidth,
                        height
                );

                // hilite_2
                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        left,
                        top,
                        twidth - left,
                        height
                );

                // hilite_3
                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        mgwidth,
                        prevTop + height,
                        twidth - mgwidth,
                        diff - height
                );
            } else if (diff > (height * 0.6f)) {
                // hilite_1
                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        mgwidth,
                        prevTop,
                        prevLeft - mgwidth,
                        height
                );

                // hilite_2
                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        left,
                        top,
                        twidth - left,
                        height
                );
            } else {
                int width = prevLeft - left;

                addBandPx(
                        bands,
                        e.surah,
                        e.ayah,
                        left,
                        top,
                        width,
                        height
                );
            }

            prevTop = top;
            prevLeft = left;
            count++;
        }

        return bands;
    }

    private static void addBandPx(List<AyahBand> out, int surah, int ayah,
                                  int left, int top, int width, int height) {
        if (width < 0) {
            left = left + width;
            width = -width;
        }

        int right = left + width;
        int bottom = top + height;

        left = clamp(left, 0, IMG_W);
        right = clamp(right, 0, IMG_W);
        top = clamp(top, 0, IMG_H);
        bottom = clamp(bottom, 0, IMG_H);

        // Keep same padding feel as your previous version
        int padX = 4;
        int padY = 2;

        left += padX;
        right -= padX;
        top += padY;
        bottom -= padY;

        left = clamp(left, 0, IMG_W);
        right = clamp(right, 0, IMG_W);
        top = clamp(top, 0, IMG_H);
        bottom = clamp(bottom, 0, IMG_H);

        if (right <= left || bottom <= top) return;

        float nx = left / (float) IMG_W;
        float ny = top / (float) IMG_H;
        float nw = (right - left) / (float) IMG_W;
        float nh = (bottom - top) / (float) IMG_H;

        out.add(new AyahBand(surah, ayah, new float[]{nx, ny, nw, nh}));
    }

    public static AyahBand hitTest(List<AyahBand> bands, float nx, float ny) {
        float slop = 0.015f;

        for (AyahBand band : bands) {
            float[] b = band.box;
            if (nx >= b[0] - slop && nx <= b[0] + b[2] + slop &&
                    ny >= b[1] - slop && ny <= b[1] + b[3] + slop) {
                return band;
            }
        }
        return null;
    }

    public static List<float[]> getAllBoxesForAyah(List<AyahBand> bands, int surah, int ayah) {
        List<float[]> result = new ArrayList<>();
        for (AyahBand band : bands) {
            if (band.surah == surah && band.ayah == ayah) {
                result.add(band.box);
            }
        }
        return result;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String readAsset(Context context, String path) throws Exception {
        try (InputStream is = context.getAssets().open(path);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) > 0) {
                bos.write(buffer, 0, n);
            }
            return bos.toString(StandardCharsets.UTF_8.name());
        }
    }
}