package com.akramhossain.quranulkarim.util;

import android.util.Log;
import android.view.View;

public class Utils {

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
}
