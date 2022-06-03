package investwell.client.fragment.fundpicks.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.fragment.fundpicks.fragments.FragFundScheme;

public class FundSchemeAdapter extends RecyclerView.Adapter<FundSchemeAdapter.ViewHolder> {
    public ArrayList<JSONObject> mItemData;
    private Context context;
    private FragFundScheme mfragFundScheme;


    public FundSchemeAdapter(ArrayList<JSONObject> ItemData, Context context, FragFundScheme fragFundScheme) {
        this.mItemData = ItemData;
        this.context = context;
        mfragFundScheme = fragFundScheme;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fundpick, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        JSONObject dataobject = mItemData.get(i);
        holder.schemecat.setText(dataobject.optString("SubCategory"));
        FundItemSchemeAdapter mFundItemAdapter = new FundItemSchemeAdapter(new ArrayList<JSONObject>(), context, mfragFundScheme);
        holder.mItemRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.mItemRecycler.setHasFixedSize(true);
        holder.mItemRecycler.setAdapter(mFundItemAdapter);

        try {
            JSONArray jsonArray = dataobject.getJSONArray("CategorySchemeList");
            ArrayList<JSONObject> newItemList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                newItemList.add(object);
            }
            mFundItemAdapter.updatelist(newItemList);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//View view = LayoutInflater.from(context).inflate(R.layout_gridview_type_two_a.item_fundscheme,false);
//        View view = inflater.inflate
//        mFundItemAdapter.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mItemData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView schemecat;
        RecyclerView mItemRecycler;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemRecycler = itemView.findViewById(R.id.rv_itemfundpick);
            schemecat = itemView.findViewById(R.id.tv_scheme_cat);
        }
    }

    public void updatelist(ArrayList<JSONObject> itemlist) {
        mItemData.clear();
        mItemData.addAll(itemlist);
        notifyDataSetChanged();
    }

}
