package investwell.client.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class SchemeCategoryAdapter extends RecyclerView.Adapter<SchemeCategoryAdapter.MyViewHolder> {
    public ArrayList<JSONObject> jsonObject;
    private Context mcontext;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private int rowIndex;
    private SchemeCategoryListener schemeCategoryListener;
    public SchemeCategoryAdapter(Context context, ArrayList<JSONObject> jsonObject,SchemeCategoryListener schemeCategoryListener1) {

        this.mcontext = context;
        this.jsonObject = jsonObject;
        this.schemeCategoryListener=schemeCategoryListener1;

    }

    @NonNull
    @Override
    public SchemeCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_scheme_category, parent, false);
        SchemeCategoryAdapter.MyViewHolder vh = new SchemeCategoryAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SchemeCategoryAdapter.MyViewHolder holder, final int position) {

        final JSONObject jsonObject1 = jsonObject.get(position);
        mActivity = (MainActivity) mcontext;
        mSession = AppSession.getInstance(mcontext);
        holder.btnSchemeCategory.setText(jsonObject1.optString("ItemName"));
        holder.btnSchemeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowIndex = position;
                schemeCategoryListener.onSchemeCategoryClick(position);
                notifyDataSetChanged();
            }
        });
        if (rowIndex == position) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.btnSchemeCategory.setBackground(mcontext.getResources().getDrawable(R.drawable.btn_primary_dark));

            }else{
                holder.btnSchemeCategory.setBackground(mcontext.getResources().getDrawable(R.drawable.btn_bg_primary));

            }
            holder.btnSchemeCategory.setTextColor(mcontext.getResources().getColor(R.color.colorWhite));

        } else {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.btnSchemeCategory.setBackground(mcontext.getResources().getDrawable(R.drawable.btn_tertiary_dark));

            }else{
                holder.btnSchemeCategory.setBackground(mcontext.getResources().getDrawable(R.drawable.btn_bg_tertiary));

            }
            holder.btnSchemeCategory.setTextColor(mcontext.getResources().getColor(R.color.lightPrimaryTextColor));

        }
    }

    @Override
    public int getItemCount() {
        return jsonObject.size();
    }

    public void updateList(List<JSONObject> list) {
        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button btnSchemeCategory;
        LinearLayout llSchemeCategory;

        public MyViewHolder(View view) {
            super(view);
            btnSchemeCategory = view.findViewById(R.id.btn_scheme_category);
            llSchemeCategory = view.findViewById(R.id.ll_scheme_category);


        }
    }
    public interface SchemeCategoryListener {
        void onSchemeCategoryClick(int position);
    }
}
