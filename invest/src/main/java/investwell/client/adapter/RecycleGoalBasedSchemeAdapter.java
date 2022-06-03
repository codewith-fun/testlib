package investwell.client.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextViewBold;
import investwell.utils.customView.CustomTextViewRegular;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleGoalBasedSchemeAdapter extends RecyclerView.Adapter<RecycleGoalBasedSchemeAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleGoalBasedSchemeAdapter.OnItemClickListener listener;
    private AppSession mSession;

    public RecycleGoalBasedSchemeAdapter(Context context, ArrayList<JSONObject> list, RecycleGoalBasedSchemeAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_bucket_adapter, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bucket_icon,ivAddToCart;
        CustomTextViewRegular schemeNmae;
        CustomButton tvAddLog;
        TextView tvPercentage;
        CustomTextViewBold yearOne, yearTwo, yearThree;
        private RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.iv_scheme_logo);
            schemeNmae = view.findViewById(R.id.tv_scheme_name);
            yearOne = view.findViewById(R.id.yearOne);
            yearTwo = view.findViewById(R.id.yearTwo);
            ivAddToCart=view.findViewById(R.id.iv_add_to_cart);
            yearThree = view.findViewById(R.id.yearThree);
            ratingBar = view.findViewById(R.id.ratingBar);
            tvPercentage=view.findViewById(R.id.tv_percentage);
            ivAddToCart.setVisibility(View.INVISIBLE);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);

            try {
                String image_path = jsonObject.optString("AMCLogo");
                schemeNmae.setText(jsonObject.optString("SchName"));

                yearOne.setText(String.format("%.2f", jsonObject.optDouble("Performance1Year")));
                yearTwo.setText(String.format("%.2f", jsonObject.optDouble("Performance3Year")));
                yearThree.setText(String.format("%.2f", jsonObject.optDouble("Performance5Year")));
tvPercentage.setText(jsonObject.optString("AllocatePercentage")+" %");
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });

        /*        String rating = jsonObject.optString("FundRating");
                if (!rating.contains("NA"))
                    ratingBar.setRating(Float.parseFloat(rating));
                else
                    ratingBar.setRating(Float.parseFloat("0.0"));*/

                Picasso.get().load(image_path).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.knowledge_area).into(bucket_icon);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


}
