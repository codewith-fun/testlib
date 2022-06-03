package investwell.client.flavourtypetwo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
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
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.GridSpacingItemDecoration;

public class SectionsTypeTwoAdapter extends RecyclerView.Adapter<SectionsTypeTwoAdapter.MyViewHolder> {


    public ArrayList<JSONObject> sectionArrayList;
    Context context;
    private JSONObject sectionJsonObject;
    private MultiViewAdapterFlavourTwo multiViewAdapterFlavourTwo;
    private JSONArray sectionTypeJSONArray;
    private ArrayList<JSONObject> sectionTypeJSONObjectList;
    private String appTypeCategory = "";
    private AppSession mSession;

    public SectionsTypeTwoAdapter(Context context, ArrayList<JSONObject> sectionArrayList) {

        this.context = context;
        this.sectionArrayList = sectionArrayList;
        mSession = AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_sections, parent, false);
        initializer();
        return new SectionsTypeTwoAdapter.MyViewHolder(v);
    }

    private void initializer() {
        multiViewAdapterFlavourTwo = new MultiViewAdapterFlavourTwo(context, new ArrayList<JSONObject>());
        sectionTypeJSONArray = new JSONArray();
        sectionTypeJSONObjectList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        sectionJsonObject = sectionArrayList.get(position);
        if (!TextUtils.isEmpty(sectionJsonObject.optString("Title"))) {
            holder.tvSectionHeaderName.setVisibility(View.VISIBLE);
        } else {
            holder.tvSectionHeaderName.setVisibility(View.GONE);

        }
        holder.tvSectionHeaderName.setText(!TextUtils.isEmpty(sectionJsonObject.optString("Title")) ? sectionJsonObject.optString("Title") : "");
        String viewType = "";

        if (!TextUtils.isEmpty(sectionJsonObject.optString("ViewType"))) {
            viewType = sectionJsonObject.optString("ViewType");
            mSession.setViewType(viewType);
        }

        if (viewType.equalsIgnoreCase("Slider")) {
            holder.rvSectionsType.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
            holder.rvSectionsType.setNestedScrollingEnabled(false);
            holder.rvSectionsType.setAdapter(multiViewAdapterFlavourTwo);
        } else if (viewType.equalsIgnoreCase("Card")) {
            holder.rvSectionsType.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
            holder.rvSectionsType.setNestedScrollingEnabled(false);
            holder.rvSectionsType.setAdapter(multiViewAdapterFlavourTwo);
        } else if (viewType.equalsIgnoreCase("Grid")) {
            if(appTypeCategory.equalsIgnoreCase("TYPE 2A")) {
                holder.rvSectionsType.setLayoutManager(new GridLayoutManager(context, 2));
                holder.rvSectionsType.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view); // item position
                        int spanCount = 2;
                        int spacing = 10;//spacing between views in grid

                        if (position >= 0) {
                            int column = position % spanCount; // item column

                            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                            if (position < spanCount) { // top edge
                                outRect.top = spacing;
                            }
                            outRect.bottom = spacing; // item bottom
                        } else {
                            outRect.left = 0;
                            outRect.right = 0;
                            outRect.top = 0;
                            outRect.bottom = 0;
                        }
                    }
                });
                holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
                holder.rvSectionsType.setNestedScrollingEnabled(false);
                holder.rvSectionsType.setAdapter(multiViewAdapterFlavourTwo);
            }else{
                holder.rvSectionsType.setLayoutManager(new GridLayoutManager(context, 3));
                holder.rvSectionsType.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
                holder.rvSectionsType.setItemAnimator(new DefaultItemAnimator());
                holder.rvSectionsType.setNestedScrollingEnabled(false);
                holder.rvSectionsType.setAdapter(multiViewAdapterFlavourTwo);
            }
        }

        if (sectionJsonObject.optJSONArray("SectionChildList").length() > 0) {

            sectionTypeJSONArray = sectionJsonObject.optJSONArray("SectionChildList");
            for (int i = 0; i < sectionTypeJSONArray.length(); i++) {
                JSONObject object = null;
                try {
                    object = sectionTypeJSONArray.getJSONObject(i);
                    sectionTypeJSONObjectList.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            multiViewAdapterFlavourTwo.updateSectionsTypeList(sectionTypeJSONObjectList, viewType);
        } else {
            sectionTypeJSONObjectList.clear();
            multiViewAdapterFlavourTwo.updateSectionsTypeList(new ArrayList<JSONObject>(), viewType);

        }


    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    public void updateSectionsList(List<JSONObject> list, String appType) {
        appTypeCategory = appType;
        sectionArrayList.clear();
        sectionArrayList.addAll(list);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSectionHeaderName;
        RecyclerView rvSectionsType;

        public MyViewHolder(View view) {
            super(view);
            tvSectionHeaderName = view.findViewById(R.id.tv_section_header);
            rvSectionsType = view.findViewById(R.id.rv_section_type_data);
        }

    }

    /****************************************Frag
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
