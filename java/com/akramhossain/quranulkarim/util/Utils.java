package com.akramhossain.quranulkarim.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

public class Utils {

    public static final String PREF_NAME = "quran_ul_karim_pref";
    public static final int PRIVATE_MODE = 0;

    public Utils() {
    }

    public static void preventTwoClick(final View view, long miliSec){
        Log.d("Delay time in milisec",String.valueOf(miliSec));
        view.setEnabled(false);
        view.postDelayed(
                ()-> view.setEnabled(true),
                miliSec
        );
    }

    public static String tajweedCss(String fontFamily,String fontSize,String bodyBgColor,String bodyTxtColor, String appTheme){
        Log.d("App Theme",appTheme);
        if (appTheme.equals("1")) {
            return "<style>@font-face{font-family:fontUthmani;src:url(file:///android_asset/fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf)}@font-face{font-family:fontAlmajeed;src:url(file:///android_asset/fonts/AlMajeedQuranicFont_shiped.ttf)}@font-face{font-family:fontAlQalam;src:url(file:///android_asset/fonts/AlQalamQuran.ttf)}@font-face{font-family:fontNooreHidayat;src:url(file:///android_asset/fonts/noorehidayat.ttf)}@font-face{font-family:fontSaleem;src:url(file:///android_asset/fonts/PDMS_Saleem_QuranFont.ttf)}@font-face{font-family:fontTahaNaskh;src:url(file:///android_asset/fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf)}@font-face{font-family:fontKitab;src:url(file:///android_asset/fonts/kitab.ttf)}body{font-family:" + fontFamily + ";font-size:" + fontSize + ";text-align:justify;background:" + bodyBgColor + ";color:" + bodyTxtColor + ";direction:rtl;margin-bottom:10px;overflow:hidden;}.hamza_wasl,.laam_shamsiyah,.silent, slnt{color:#aaaaaa}.madda_normal{color:#537fff}.madda_permissible{color:#4050ff}.madda_necessary{color:#4169e1}.qalaqah{color:#ee4b2b}.madda_obligatory{color:#48acf0}.ikhafa_shafawi{color:#d500b7}.ikhafa{color:#ca2c92}.idgham_shafawi{color:#58b800}.iqlab{color:#26bffd}.idgham_ghunnah{color:#50c878}.idgham_wo_ghunnah{color:#14CE10}.idgham_mutajanisayn,.idgham_mutaqaribayn{color:#a1a1a1}.ghunnah{color:#ff7e1e}.end{color:#82f200;font-size:20px;margin-left:15px}</style>";
        }else if(appTheme.equals("0")) {
            return "<style>@font-face{font-family:fontUthmani;src:url(file:///android_asset/fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf)}@font-face{font-family:fontAlmajeed;src:url(file:///android_asset/fonts/AlMajeedQuranicFont_shiped.ttf)}@font-face{font-family:fontAlQalam;src:url(file:///android_asset/fonts/AlQalamQuran.ttf)}@font-face{font-family:fontNooreHidayat;src:url(file:///android_asset/fonts/noorehidayat.ttf)}@font-face{font-family:fontSaleem;src:url(file:///android_asset/fonts/PDMS_Saleem_QuranFont.ttf)}@font-face{font-family:fontTahaNaskh;src:url(file:///android_asset/fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf)}@font-face{font-family:fontKitab;src:url(file:///android_asset/fonts/kitab.ttf)}body{font-family:" + fontFamily + ";font-size:" + fontSize + ";text-align:justify;background:" + bodyBgColor + ";color:" + bodyTxtColor + ";direction:rtl;margin-bottom:10px;overflow:hidden;}.hamza_wasl,.laam_shamsiyah,.silent, slnt{color:#aaaaaa}.madda_normal{color:#537fff}.madda_permissible{color:#4050ff}.madda_necessary{color:#000ebc}.qalaqah{color:#dd0008}.madda_obligatory{color:#2144c1}.ikhafa_shafawi{color:#d500b7}.ikhafa{color:#9400a8}.idgham_shafawi{color:#58b800}.iqlab{color:#26bffd}.idgham_ghunnah{color:#169777}.idgham_wo_ghunnah{color:#169200}.idgham_mutajanisayn,.idgham_mutaqaribayn{color:#a1a1a1}.ghunnah{color:#ff7e1e}.end{color:#82f200;font-size:20px;margin-left:15px}</style>";
        }else{
            return "<style>@font-face{font-family:fontUthmani;src:url(file:///android_asset/fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf)}@font-face{font-family:fontAlmajeed;src:url(file:///android_asset/fonts/AlMajeedQuranicFont_shiped.ttf)}@font-face{font-family:fontAlQalam;src:url(file:///android_asset/fonts/AlQalamQuran.ttf)}@font-face{font-family:fontNooreHidayat;src:url(file:///android_asset/fonts/noorehidayat.ttf)}@font-face{font-family:fontSaleem;src:url(file:///android_asset/fonts/PDMS_Saleem_QuranFont.ttf)}@font-face{font-family:fontTahaNaskh;src:url(file:///android_asset/fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf)}@font-face{font-family:fontKitab;src:url(file:///android_asset/fonts/kitab.ttf)}body{font-family:" + fontFamily + ";font-size:" + fontSize + ";text-align:justify;background:" + bodyBgColor + ";color:" + bodyTxtColor + ";direction:rtl;margin-bottom:10px;overflow:hidden;}.hamza_wasl,.laam_shamsiyah,.silent, slnt{color:#aaaaaa}.madda_normal{color:#537fff}.madda_permissible{color:#4050ff}.madda_necessary{color:#4169e1}.qalaqah{color:#ee4b2b}.madda_obligatory{color:#48acf0}.ikhafa_shafawi{color:#d500b7}.ikhafa{color:#ca2c92}.idgham_shafawi{color:#58b800}.iqlab{color:#26bffd}.idgham_ghunnah{color:#50c878}.idgham_wo_ghunnah{color:#14CE10}.idgham_mutajanisayn,.idgham_mutaqaribayn{color:#a1a1a1}.ghunnah{color:#ff7e1e}.end{color:#82f200;font-size:20px;margin-left:15px}</style>";
        }
    }

    public static void saveLocation(Context ctx, double lat, double lon, double tz) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putLong("pr_lat_bits", Double.doubleToLongBits(lat))
                .putLong("pr_lon_bits", Double.doubleToLongBits(lon))
                .putFloat("pr_tz", (float) tz)
                .putBoolean("pr_has_location", true)
                .apply();
    }

    public static double getLat(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(sp.getLong("pr_lat_bits", Double.doubleToLongBits(0)));
    }

    public static double getLon(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(sp.getLong("pr_lon_bits", Double.doubleToLongBits(0)));
    }

    public static double getTz(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getFloat("pr_tz", 6.0f);
    }

    public static void saveMethods(Context ctx, int calcMethod, int asrMethod) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putInt("pr_calc_method", calcMethod)
                .putInt("pr_asr_method", asrMethod)
                .apply();
    }

    public static int getCalcMethod(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getInt("pr_calc_method", 0);
    }

    public static int getAsrMethod(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getInt("pr_asr_method", 0);
    }
}
