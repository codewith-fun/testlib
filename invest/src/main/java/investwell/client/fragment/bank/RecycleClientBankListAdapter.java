package investwell.client.fragment.bank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleClientBankListAdapter extends RecyclerView.Adapter<RecycleClientBankListAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleClientBankListAdapter.OnItemClickListener listener;
    private String mType = "";
    private String mUCC ="";

    public RecycleClientBankListAdapter(Context context, ArrayList<JSONObject> list, String type, RecycleClientBankListAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_client_bank_list_adapter, viewGroup, false);
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
        TextView tvIFSC, tv_account_type, tvAccountNo, tvBankName;
        ImageView ivUpload;
        LinearLayout LinerProfileItems;

        public ViewHolder(View view) {
            super(view);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvAccountNo = view.findViewById(R.id.tvAccountNo);
            tv_account_type = view.findViewById(R.id.tv_account_type);
            tvIFSC = view.findViewById(R.id.tvIFSC);
            ivUpload = view.findViewById(R.id.ivUpload);
            LinerProfileItems = view.findViewById(R.id.LinerProfileItems);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            tvBankName.setText(jsonObject.optString("BankCode"));
            tvAccountNo.setText(jsonObject.optString("AccountNo"));
            tvIFSC.setText(jsonObject.optString("IFSCCode"));
            tv_account_type.setText(jsonObject.optString("AccountType"));

            if (jsonObject.optString("BankStatus").equalsIgnoreCase("Not Activated")){
                ivUpload.setVisibility(View.VISIBLE);
            }else{
                ivUpload.setVisibility(View.GONE);
            }



            ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.mBankObjectOnBankList = jsonObject;
                    mainActivity.pickPhoto();

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });

        }

    }


}
