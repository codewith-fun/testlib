package investwell.client.flavourtypefour.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.GridSpacingItemDecoration;

public class SectionTypeFourPlanAdapter extends RecyclerView.Adapter<SectionTypeFourPlanAdapter.MyViewHolder> {


    public ArrayList<JSONObject> sectionArrayList;
    Context context;
    private JSONObject jsonObject1;
    private MultiViewAdapterFlavourFour multiViewTypeAdapterFlavourOne;
    private JSONArray sectionTypeJSONArray;
    private ArrayList<JSONObject> sectionTypeJSONObjectList;

    private View view;
    private AppSession mSession;

    public SectionTypeFourPlanAdapter(Context context, ArrayList<JSONObject> sectionArrayList) {

        this.context = context;
        this.sectionArrayList = sectionArrayList;
        mSession = AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public SectionTypeFourPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_dashboard_flavour_four, parent, false);


        initializer();
        return new SectionTypeFourPlanAdapter.MyViewHolder(view);
    }

    private void initializer() {
        multiViewTypeAdapterFlavourOne = new MultiViewAdapterFlavourFour(context, new ArrayList<JSONObject>());
        sectionTypeJSONArray = new JSONArray();
        sectionTypeJSONObjectList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(SectionTypeFourPlanAdapter.MyViewHolder holder, final int position) {

        jsonObject1 = sectionArrayList.get(position);
        if(!TextUtils.isEmpty(jsonObject1.optString("Title")) ){
            holder.rlTitleContainer.setVisibility(View.VISIBLE);
        }else {
            holder.rlTitleContainer.setVisibility(View.GONE);

        }
        holder.rlTitleContainer.setVisibility(View.GONE);
        holder.tvSectionHeaderName.setText(!TextUtils.isEmpty(jsonObject1.optString("Title")) ? jsonObject1.optString("Title") : "");
        String viewType = "";

        if (!TextUtils.isEmpty(jsonObject1.optString("ViewType"))) {
            viewType = jsonObject1.optString("ViewType");
        }
        if (viewType.equalsIgnoreCase("Slider")) {
            holder.rvSectionsType.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
            holder.rvSectionsType.setNestedScrollingEnabled(false);
            holder.rvSectionsType.setAdapter(multiViewTypeAdapterFlavourOne);
        } else if (viewType.equalsIgnoreCase("Card")) {
            holder.rvSectionsType.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
            holder.rvSectionsType.setNestedScrollingEnabled(false);
            holder.rvSectionsType.setAdapter(multiViewTypeAdapterFlavourOne);
        } else if (viewType.equalsIgnoreCase("Grid")) {
            holder.rvSectionsType.setLayoutManager(new GridLayoutManager(context, 3));
            holder.rvSectionsType.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
            holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
            holder.rvSectionsType.setNestedScrollingEnabled(false);
            holder.rvSectionsType.setAdapter(multiViewTypeAdapterFlavourOne);
        }
        if (jsonObject1.optJSONArray("SectionChildList").length() > 0) {
            sectionTypeJSONArray = jsonObject1.optJSONArray("SectionChildList");
            for (int i = 0; i < sectionTypeJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = sectionTypeJSONArray.getJSONObject(i);
                    sectionTypeJSONObjectList.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            multiViewTypeAdapterFlavourOne.updateSectionsTypeList(sectionTypeJSONObjectList,viewType);
        } else {
            sectionTypeJSONObjectList.clear();
            multiViewTypeAdapterFlavourOne.updateSectionsTypeList(new ArrayList<JSONObject>(),viewType);
        }

    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    public void updateSectionsList(List<JSONObject> list) {

        sectionArrayList.clear();
        sectionArrayList.addAll(list);
        notifyDataSetChanged();
    }



    /****************************************Frag
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSectionHeaderName;
        RecyclerView rvSectionsType;
        RelativeLayout rlTitleContainer;
        public MyViewHolder(View view) {
            super(view);
            tvSectionHeaderName = view.findViewById(R.id.tv_dashboard_items_title);
            rvSectionsType = view.findViewById(R.id.rv_dashboard);
            rlTitleContainer = view.findViewById(R.id.rl_investment_route_title_container);
        }


    }

}

