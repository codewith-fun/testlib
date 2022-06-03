package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.WebViewActivity;
import investwell.utils.AppSession;
import investwell.utils.CircleTransform;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<JSONObject> socialMediaJSONList;
    private SocialMediaClickListener socialMediaClickListener;
    private int rowIndex;
    private AppSession appSession;
    Intent intent;
    public SocialMediaAdapter(Context context, ArrayList<JSONObject> langLists) {
        this.socialMediaJSONList = langLists;
        mContext = context;
        appSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public SocialMediaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_social_media, parent, false);

        return new SocialMediaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SocialMediaAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject1 = socialMediaJSONList.get(position);
        holder.tvSocialMedia.setText(!TextUtils.isEmpty(jsonObject1.optString("Platform")) ? jsonObject1.optString("Platform") : "");
        Picasso.get().load(!TextUtils.isEmpty(jsonObject1.optString("ImageURL")) ? jsonObject1.optString("ImageURL") : "").error(R.mipmap.profileplaceholder).transform(new CircleTransform()).into(holder.ivSocialMedia);
        appSession.setSocialMediaLink(!TextUtils.isEmpty(jsonObject1.optString("SocialURL")) ? jsonObject1.optString("SocialURL") : "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowIndex = position;
                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("title", jsonObject1.optString("Platform"));
                intent.putExtra("url", jsonObject1.optString("SocialURL"));
                mContext.startActivity(intent);
                notifyDataSetChanged();
            }
        });


    }

    public void upDateLangList(List<JSONObject> list) {
        socialMediaJSONList.clear();
        socialMediaJSONList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return socialMediaJSONList.size();
    }

    public interface SocialMediaClickListener {
        void onSocialMediaClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSocialMedia;
        ImageView ivSocialMedia;
        LinearLayout llSocialMedia;


        public MyViewHolder(View view) {
            super(view);
            llSocialMedia = view.findViewById(R.id.ll_social_media_links);
            tvSocialMedia = view.findViewById(R.id.tv_social_media);
            ivSocialMedia = view.findViewById(R.id.iv_social_media);
        }
    }
}
