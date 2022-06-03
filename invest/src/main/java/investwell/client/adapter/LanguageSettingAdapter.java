package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;
import com.yariksoffice.lingver.Lingver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.model.LanguageBean;

public class LanguageSettingAdapter extends RecyclerView.Adapter<LanguageSettingAdapter.MyViewHolder> {
    private Context mContext;
    private List<LanguageBean> langList;
    private ArrayList<JSONObject> langJSONList;
    private LanguageSupportAdapter.LanguageClickListener langClickListener;
    private int rowIndex;
    private AppSession appSession;

    public LanguageSettingAdapter(Context context, ArrayList<JSONObject> langLists) {
        this.langJSONList = langLists;
        mContext = context;

        appSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public LanguageSettingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_language_support, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LanguageSettingAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject1 = langJSONList.get(position);
        holder.tvLanguageName.setText(!TextUtils.isEmpty(jsonObject1.optString("Language")) ? jsonObject1.optString("Language") : "");
        if (!TextUtils.isEmpty(jsonObject1.optString("ActualLanguage"))) {
            holder.tvLanguageAlternateName.setText(jsonObject1.optString("ActualLanguage"));
            holder.tvLanguageAlternateName.setVisibility(View.VISIBLE);
        } else {
            holder.tvLanguageAlternateName.setVisibility(View.GONE);
        }
        holder.llLanguageSupport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rowIndex = position;
                if (jsonObject1.optString("Language").equalsIgnoreCase("English")) {
                    Lingver.getInstance().setLocale(mContext, "en");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_english) + "(" + mContext.getResources().getString(R.string.app_language_english_inverse) + ")");
                    appSession.setSelectedAppLang("English");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Hindi")) {
                    Lingver.getInstance().setLocale(mContext, "hi");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_hindi) + "(" + mContext.getResources().getString(R.string.app_language_hindi_inverse) + ")");
                    appSession.setSelectedAppLang("Hindi");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Gujarati")) {
                    Lingver.getInstance().setLocale(mContext, "gu");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_gujrati) + "(" + mContext.getResources().getString(R.string.app_language_gujrati_inverse) + ")");
                    appSession.setSelectedAppLang("Gujarati");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Marathi")) {
                    Lingver.getInstance().setLocale(mContext, "mr");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_marathi) + "(" + mContext.getResources().getString(R.string.app_language_marathi_inverse) + ")");
                    appSession.setSelectedAppLang("Marathi");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Bengali")) {
                    Lingver.getInstance().setLocale(mContext, "bn");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_bengali) + "(" + mContext.getResources().getString(R.string.app_language_bengali_inverse) + ")");
                    appSession.setSelectedAppLang("Bangla");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Telugu")) {
                    Lingver.getInstance().setLocale(mContext, "ta");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_tamil) + "(" + mContext.getResources().getString(R.string.app_language_tamil_inverse) + ")");
                    appSession.setSelectedAppLang("Tamil");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Tamil")) {
                    Lingver.getInstance().setLocale(mContext, "te");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_telgu) + "(" + mContext.getResources().getString(R.string.app_language_telgu_inverse) + ")");
                    appSession.setSelectedAppLang("Telegu");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Kannad")) {
                    Lingver.getInstance().setLocale(mContext, "kn");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_kannada) + "(" + mContext.getResources().getString(R.string.app_language_kannada_inverse) + ")");
                    appSession.setSelectedAppLang("Kannad");
                    appSession.setSavedLangPos(position);

                }
                if (jsonObject1.optString("Language").equalsIgnoreCase("Punjabi")) {
                    Lingver.getInstance().setLocale(mContext, "kn");
                    appSession.setDefaultAppLang(mContext.getResources().getString(R.string.app_language_kannada) + "(" + mContext.getResources().getString(R.string.app_language_kannada_inverse) + ")");
                    appSession.setSelectedAppLang("Kannad");
                    appSession.setSavedLangPos(position);

                }

                notifyDataSetChanged();
            }
        });

        if (appSession.getSavedLangPos() == position) {
            holder.ivChosenLanguage.setVisibility(View.VISIBLE);
            holder.borderView.setVisibility(View.VISIBLE);
            holder.llLanguageSupport.setBackgroundColor(mContext.getResources().getColor(R.color.colorGrey_100));
        } else {
            holder.ivChosenLanguage.setVisibility(View.GONE);
            holder.borderView.setVisibility(View.GONE);
            holder.llLanguageSupport.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        }

    }

    public void upDateLangList(List<JSONObject> list) {
        langJSONList.clear();
        langJSONList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return langJSONList.size();
    }

    public interface LanguageClickListener {
        void onLanguageSelected(View view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguageName, tvLanguageAlternateName;
        ImageView ivChosenLanguage;
        LinearLayout llLanguageSupport;
        View borderView;

        public MyViewHolder(View view) {
            super(view);
            llLanguageSupport = view.findViewById(R.id.ll_language_support);
            tvLanguageName = view.findViewById(R.id.tv_language);
            tvLanguageAlternateName = view.findViewById(R.id.tv_alternate_language);
            ivChosenLanguage = view.findViewById(R.id.iv_checked_lang);
            borderView = view.findViewById(R.id.v_border);
        }
    }
}
