package com.akramhossain.quranulkarim.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.R;
import com.akramhossain.quranulkarim.TagActivity;
import com.akramhossain.quranulkarim.model.DuaZikr;
import com.akramhossain.quranulkarim.model.Tag;
import com.akramhossain.quranulkarim.util.Utils;

import java.util.ArrayList;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DuaZikrViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context c;
    ArrayList<DuaZikr> duaZikr;
    private Activity activity;
    Typeface font, fontUthmani, fontAlmajeed, fontAlQalam, fontNooreHidayat, fontSaleem, fontTahaNaskh, fontKitab;
    SharedPreferences mPrefs;

    public DuaZikrViewAdapter(Context c, ArrayList<DuaZikr> duaZikr, Activity activity) {
        this.c = c;
        this.duaZikr = duaZikr;
        this.activity = activity;
        mPrefs = c.getSharedPreferences(Utils.PREF_NAME, 0);
        font = Typeface.createFromAsset(c.getAssets(), "fonts/Siyamrupali.ttf");
        fontUthmani = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthmanic_Script_HAFS_Regular.ttf");
        fontAlmajeed = Typeface.createFromAsset(c.getAssets(),"fonts/AlMajeedQuranicFont_shiped.ttf");
        fontAlQalam = Typeface.createFromAsset(c.getAssets(),"fonts/AlQalamQuran.ttf");
        fontNooreHidayat = Typeface.createFromAsset(c.getAssets(),"fonts/noorehidayat.ttf");
        fontSaleem = Typeface.createFromAsset(c.getAssets(),"fonts/PDMS_Saleem_QuranFont.ttf");
        fontTahaNaskh = Typeface.createFromAsset(c.getAssets(),"fonts/KFGQPC_Uthman_Taha_Naskh_Regular.ttf");
        fontKitab = Typeface.createFromAsset(c.getAssets(),"fonts/kitab.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.dua_zikr_list,parent,false);
        RecyclerViewHolder rvHolder = new RecyclerViewHolder(v);
        rvHolder.tag_bn.setTypeface(font);
        return rvHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder rvHolder= (RecyclerViewHolder) holder;
        DuaZikr dz = duaZikr.get(position);
        rvHolder.dua_zikr_id.setText(dz.getDua_zikr_id());
        rvHolder.arabic.setText(dz.getArabic());
        rvHolder.transliteration_en.setText(Html.fromHtml(dz.getTransliteration_en(),Html.FROM_HTML_MODE_LEGACY));
        rvHolder.transliteration_bn.setText(Html.fromHtml(dz.getTransliteration_bn(),Html.FROM_HTML_MODE_LEGACY));
        rvHolder.translations_en.setText(dz.getTranslations_en());
        rvHolder.translations_bn.setText(dz.getTranslations_bn());
        rvHolder.reference_en.setText(dz.getReference_en());
        rvHolder.reference_bn.setText(dz.getReference_bn());
        rvHolder.repeat_times.setText(dz.getRepeat_times());
        rvHolder.when_to_en.setText(dz.getWhen_to_en());
        rvHolder.when_to_bn.setText(dz.getWhen_to_bn());
        rvHolder.name_en.setText(dz.getName_en());
        rvHolder.name_bn.setText(dz.getName_bn());
        rvHolder.tag_en.setText(dz.getTag_en());
        rvHolder.tag_bn.setText(dz.getTag_bn());

        rvHolder.tag_bn.setTypeface(font);
        rvHolder.reference_bn.setTypeface(font);
        rvHolder.name_bn.setTypeface(font);
        rvHolder.when_to_bn.setTypeface(font);
        rvHolder.translations_bn.setTypeface(font);
        rvHolder.transliteration_bn.setTypeface(font);

        rvHolder.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str1 = "";
                    if (dz.getArabic() != null && !dz.getArabic().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getArabic(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str1 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str2 = "";
                    if (dz.getTransliteration_en() != null && !dz.getTransliteration_en().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getTransliteration_en(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str2 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str3 = "";
                    if (dz.getTransliteration_bn() != null && !dz.getTransliteration_bn().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getTransliteration_bn(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str3 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str4 = "";
                    if (dz.getTranslations_en() != null && !dz.getTranslations_en().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getTranslations_en(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str4 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str5 = "";
                    if (dz.getTranslations_bn() != null && !dz.getTranslations_bn().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getTranslations_bn(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str5 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str6 = "";
                    if (dz.getReference_en() != null && !dz.getReference_en().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getReference_en(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str6 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String str7 = "";
                    if (dz.getReference_bn() != null && !dz.getReference_bn().equals("")) {
                        String text = HtmlCompat.fromHtml(dz.getReference_bn(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        str7 = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                    }

                    String fullHadith = str1+"\n\n"+str2+"\n\n"+str3+"\n\n"+str4+"\n\n"+str5+"\n\n"+str6+"\n\n"+str7;
                    String label = dz.getName_en()+", "+dz.getName_bn();

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(label, fullHadith);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(c.getApplicationContext(), "Copied.", Toast.LENGTH_LONG).show();

                }catch (Exception e) {
                    Log.e("copy error", e.getMessage());
                }
            }
        });

        rvHolder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder alert = new AlertDialog.Builder(c);
                    alert.setTitle("Reference");
                    alert.setMessage(dz.getReference_en() + "\n\n" + dz.getReference_bn());
                    alert.setPositiveButton(R.string.text_ok,null);
                    alert.show();
                }catch (Exception e) {
                    Log.e("info error", e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return duaZikr.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView dua_zikr_id;
        TextView arabic;
        TextView transliteration_en;
        TextView transliteration_bn;
        TextView translations_en;
        TextView translations_bn;
        TextView reference_en;
        TextView reference_bn;
        TextView repeat_times;
        TextView when_to_en;
        TextView when_to_bn;
        TextView name_en;
        TextView name_bn;
        TextView tag_en;
        TextView tag_bn;

        Button copyButton, infoButton;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            dua_zikr_id = (TextView) itemView.findViewById(R.id.dua_zikr_id);
            arabic = (TextView) itemView.findViewById(R.id.arabic);

            transliteration_en = (TextView) itemView.findViewById(R.id.transliteration_en);
            transliteration_bn = (TextView) itemView.findViewById(R.id.transliteration_bn);

            translations_en = (TextView) itemView.findViewById(R.id.translations_en);
            translations_bn = (TextView) itemView.findViewById(R.id.translations_bn);

            reference_bn = (TextView) itemView.findViewById(R.id.reference_bn);
            reference_en = (TextView) itemView.findViewById(R.id.reference_en);

            when_to_en = (TextView) itemView.findViewById(R.id.when_to_en);
            when_to_bn = (TextView) itemView.findViewById(R.id.when_to_bn);

            repeat_times = (TextView) itemView.findViewById(R.id.repeat_times);

            name_en = (TextView) itemView.findViewById(R.id.name_en);
            name_bn = (TextView) itemView.findViewById(R.id.name_bn);

            tag_en = (TextView) itemView.findViewById(R.id.tag_en);
            tag_bn = (TextView) itemView.findViewById(R.id.tag_bn);

            copyButton = itemView.findViewById(R.id.copyButton);
            infoButton = itemView.findViewById(R.id.infoButton);

            String mp_arabicFontFamily = mPrefs.getString("arabicFontFamily", "Noore Huda");
            String mp_enFz = mPrefs.getString("enFontSize", "15");
            String mp_bnFz = mPrefs.getString("bnFontSize", "15");
            String mp_arFz = mPrefs.getString("arFontSize", "30");

            if (!mp_enFz.equals("") && mp_enFz !=null) {
                try {
                    tag_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    transliteration_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    translations_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    reference_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    when_to_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                    //name_en.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }
            if (!mp_bnFz.equals("") && mp_bnFz !=null) {
                try {
                    tag_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    transliteration_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    translations_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    reference_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    when_to_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                    //name_bn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }

            if (!mp_arFz.equals("") && mp_arFz !=null) {
                try {
                    arabic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_arFz));
                }catch (NumberFormatException e) {
                    Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
                }
            }

            if(mp_arabicFontFamily.equals("Al Majeed Quranic Font")){
                arabic.setTypeface(fontAlmajeed);
            }
            if(mp_arabicFontFamily.equals("Al Qalam Quran")){
                arabic.setTypeface(fontAlQalam);
            }
            if(mp_arabicFontFamily.equals("Noore Huda")){
                arabic.setTypeface(fontUthmani);
            }
            if(mp_arabicFontFamily.equals("Noore Hidayat")){
                arabic.setTypeface(fontNooreHidayat);
            }
            if(mp_arabicFontFamily.equals("Saleem Quran")){
                arabic.setTypeface(fontSaleem);
            }
            if(mp_arabicFontFamily.equals("KFGQPC Uthman Taha Naskh")){
                arabic.setTypeface(fontTahaNaskh);
            }
            if(mp_arabicFontFamily.equals("Arabic Regular")){
                arabic.setTypeface(fontKitab);
            }

        }

    }

}
