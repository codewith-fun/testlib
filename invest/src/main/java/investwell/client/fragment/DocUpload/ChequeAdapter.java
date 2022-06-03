package investwell.client.fragment.DocUpload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iw.acceleratordemo.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;

public class ChequeAdapter extends RecyclerView.Adapter<ChequeAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener listener;
private AppSession mSession;

    public ChequeAdapter(Context context, ArrayList<JSONObject> list, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_cheques, viewGroup, false);
        mSession=AppSession.getInstance(mContext);
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

    public interface OnItemClickListener {
        void onItemClick(int pos, JSONObject jsonObject);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvBankName, mTvBankBranch, mTvIfsCode, mTvAccNo;
        ImageView mIvCheck, mIvUpload;

        public ViewHolder(View view) {
            super(view);
            mTvBankName = view.findViewById(R.id.tv_bank_name);
            mTvBankBranch = view.findViewById(R.id.tv_branch);
            mTvIfsCode = view.findViewById(R.id.tv_ifsc);
            mTvAccNo = view.findViewById(R.id.tv_acc_no);
            mIvCheck = view.findViewById(R.id.iv_check);
            mIvUpload = view.findViewById(R.id.iv_upload_cheque);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            try {
             /*   final JSONObject jsonObject = mDataList.get(position);
                if (mSession.getExchangeNseBseMfu().equalsIgnoreCase(AppConstants.NSE_APP) ||
                        mSession.getExchangeNseBseMfu().equalsIgnoreCase(AppConstants.BSE_APP)){
                    mTvBankName.setText(Utils.setFirstLetterCapital(jsonObject.optString("bankName")));
                if (jsonObject.has("branchName") && (!jsonObject.optString("branchName").equalsIgnoreCase("null") &&
                        !TextUtils.isEmpty(jsonObject.optString("branchName")))) {
                    mTvBankBranch.setText(Utils.setFirstLetterCapital("Branch: " + jsonObject.optString("branchName")));
                    mTvBankBranch.setVisibility(View.VISIBLE);
                } else {
                    mTvBankBranch.setVisibility(View.GONE);
                }
                mTvAccNo.setText("A/C No. " + jsonObject.optString("accountNo"));

                if (jsonObject.optBoolean("isSubmitted")) {
                    mIvCheck.setImageResource(R.drawable.check_green);
                    mIvUpload.setImageResource(R.drawable.reload);
                } else {
                    mIvCheck.setImageResource(R.drawable.check_gray);
                    mIvUpload.setImageResource(R.drawable.upload);
                }
            }else{
                    mTvBankName.setText(Utils.setFirstLetterCapital(jsonObject.optString("documentType")));
                    mTvBankBranch.setText(Utils.setFirstLetterCapital(jsonObject.optString("name")));
                    mTvBankBranch.setVisibility(View.VISIBLE);
                    mTvAccNo.setVisibility(View.GONE);
                    if (jsonObject.optBoolean("isSubmitted")) {
                        mIvCheck.setImageResource(R.drawable.check_green);
                        mIvUpload.setImageResource(R.drawable.reload);
                    } else {
                        mIvCheck.setImageResource(R.drawable.check_gray);
                        mIvUpload.setImageResource(R.drawable.upload);
                    }
                }

                mIvUpload.setOnClickListener(view -> listener.onItemClick(position, jsonObject));


              */
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

}
