package investwell.broker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class Broker_Client_Adapter extends RecyclerView.Adapter<Broker_Client_Adapter.MyViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private AppSession appSession;
    private BrokerActivity brokerActivity;

    public Broker_Client_Adapter(Context context, ArrayList<JSONObject> jsonObject) {
        this.mContext = context;
        this.mDataList = jsonObject;
        appSession = AppSession.getInstance(mContext);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.broker_client_adapter, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        brokerActivity = (BrokerActivity) mContext;
        final JSONObject jsonObject1 = mDataList.get(position);
        final Bundle bundle = new Bundle();
        bundle.putString("cid", jsonObject1.optString("Cid"));
        String name = Utils.convertStringFirstTextCaps(jsonObject1.optString("Name"));
        String image_path = jsonObject1.optString("ProfilePic");
        holder.name.setText(name);
        String pan = jsonObject1.optString("PANNO");
        String GroupHead ="  " +jsonObject1.optString("GroupHead");
        if (pan.equals("_")) {
            holder.tvPAN.setText(mContext.getResources().getString(R.string.broker_data_not_available_txt));

        } else {
            holder.tvPAN.setText(pan);
        }
        if (GroupHead.equals("_")) {
            holder.tvFamilyHead.setText(mContext.getResources().getString(R.string.broker_data_not_available_txt));
        } else {
            holder.tvFamilyHead.setText(GroupHead);
        }

        if (jsonObject1.optString("MobileNO").equalsIgnoreCase("_")) {
            holder.ivPhoneLavel.setVisibility(View.GONE);
            holder.tvMobile.setText(mContext.getResources().getString(R.string.broker_data_not_available_txt));
        } else {
            holder.ivPhoneLavel.setVisibility(View.VISIBLE);
            holder.tvMobile.setText(jsonObject1.optString("MobileNO"));
        }

        if (jsonObject1.optString("Email").isEmpty()) {
            holder.ivEmail.setVisibility(View.VISIBLE);
            holder.tvEmail.setText("  "+mContext.getResources().getString(R.string.broker_data_not_available_txt));
        } else {
            holder.ivEmail.setVisibility(View.VISIBLE);
            holder.tvEmail.setText("  "+jsonObject1.optString("Email"));
        }

        //   {"Name":"A Abdul Hakeem","Cid":"01C72715","PANNO":"AADPH3171Q","City":"MADURAI","MobileNO":"8220206449","UCC":"NA"}
        holder.ivPhoneLavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jsonObject1.optString("MobileNO")));
                mContext.startActivity(callIntent);
            }
        });

        holder.tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!holder.tvMobile.getText().equals(mContext.getResources().getString(R.string.broker_data_not_available_txt))) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"" + jsonObject1.optString("Email")});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.setType("email/rfc822");
                    if (intent != null) {
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        mContext.startActivity(Intent.createChooser(intent, ""));
                    }

                }
            }
        });

        holder.ivEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brokerActivity.displayViewOther(23, bundle);
            }
        });

        holder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("type","brokerActivity");

                Intent intent = new Intent(mContext, MainActivity.class);

               intent.putExtra("comingFromActivity", "brokerActivitySearch");
                intent.putExtra("cid", jsonObject1.optString("Cid"));
                intent.putExtra("ProfilePic",jsonObject1.optString("ProfilePic"));
                intent.putExtra("Name",jsonObject1.optString("Name"));
                intent.putExtra("MobileNO",jsonObject1.optString("MobileNO"));
                intent.putExtra("Email",jsonObject1.optString("Email"));
                mContext.startActivity(intent);

            }
        });
        try {

            Picasso.get().load(image_path).placeholder(R.mipmap.knowledge_area)
                    .error(R.mipmap.knowledge_area).into(holder.ivProfilePic);

        } catch (Exception e) {

        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppApplication.sClientDashboard = "";
                AppApplication.graph = "";
                AppApplication.portfolio_data = "";
                AppApplication.portfolio_detail_data = "";
                AppApplication.my_transaction = "";
                AppApplication.asset_allocation = "";
                AppApplication.category_allocation = "";
                AppApplication.applicant_allocation = "";
                AppApplication.fund_allocation = "";

                appSession.setAddToCartList("");
                appSession.setCID(jsonObject1.optString("Cid"));
                appSession.setUCC_CODE(jsonObject1.optString("UCC"));
                appSession.setFullName(jsonObject1.optString("Name"));
                appSession.setClientImage(jsonObject1.optString("ProfilePic"));
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(AppConstants.COME_FROM, "broker");
                mContext.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {

        return mDataList.size();
    }

    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, tvFamilyHead, phone, tvPAN, tvEmail, tvMobile;
        CardView cardView;

        ImageView ivProfilePic, ivPhoneLavel, ivUserProfile, ivEmail;

        public MyViewHolder(View view) {
            super(view);

            ivProfilePic = view.findViewById(R.id.ivProfile);
            ivUserProfile = view.findViewById(R.id.ivUserProfile);
            ivEmail = view.findViewById(R.id.ivEmail);
            name = view.findViewById(R.id.name);
            tvFamilyHead = view.findViewById(R.id.tvFamilyHead);
            phone = view.findViewById(R.id.phone);
            ivPhoneLavel = view.findViewById(R.id.ivPhoneLavel);
            tvPAN = view.findViewById(R.id.tvPAN);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvMobile = view.findViewById(R.id.tvMobile);
            cardView = view.findViewById(R.id.cardView);


        }
    }
}
