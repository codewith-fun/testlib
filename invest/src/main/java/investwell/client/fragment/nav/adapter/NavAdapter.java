package investwell.client.fragment.nav.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavAdapter extends RecyclerView.Adapter<NavAdapter.ViewHolder> {


    public ArrayList<JSONObject> mDataList;
    private MainActivity mActivity;
private AppApplication mApplication;
    private Context mContext;
    private AppSession mSession;

    public NavAdapter(Context mContext, ArrayList<JSONObject> mDataList) {
        this.mDataList = mDataList;
        this.mContext = mContext;
        mSession = AppSession.getInstance(mContext);
        mActivity = (MainActivity) mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_nav_adapter_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        final JSONObject object = mDataList.get(position);
        viewHolder.mSchemeName.setText(object.optString("AMCName"));
        final String url = object.optString("AMCLogo");
        if (url != null) {
            Picasso.get().load(url).placeholder(R.mipmap.tranparent)
                    .error(R.mipmap.tranparent).into(viewHolder.mLogo);
        } else
            viewHolder.mLogo.setImageResource(R.mipmap.tranparent);

        viewHolder.mSchemeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("schemename", object.optString("AMCName"));
                bundle.putString("schemecode", object.optString("AMCCode"));
                bundle.putString("schemeicon", url);
                mActivity.displayViewOther(67, bundle);
//                Toast.makeText(mContext, object.optString("AMCCode"), Toast.LENGTH_SHORT).show();
            }
        });


        /*if (position % 2 == 0) {
            viewHolder.mSchemeCard.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightest_gray));
        } else {
            viewHolder.mSchemeCard.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }*/


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public void updatelist(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mSchemeName;
        RelativeLayout mSchemeCard;
        ImageView mLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSchemeName = itemView.findViewById(R.id.tv_schemename);
            mSchemeCard = itemView.findViewById(R.id.cv_fund_picks);
            mLogo = itemView.findViewById(R.id.schemeLogo);
        }
    }
}
