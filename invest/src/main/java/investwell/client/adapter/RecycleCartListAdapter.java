package investwell.client.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.iw.acceleratordemo.R;
import investwell.utils.AppSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleCartListAdapter extends RecyclerView.Adapter<RecycleCartListAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleCartListAdapter.OnItemClickListener listener;
    private LinearLayout mLiner;
    private RelativeLayout mRelative;

    public RecycleCartListAdapter(Context context, RelativeLayout relativeLayout, LinearLayout linearLayout, ArrayList<JSONObject> list, RecycleCartListAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mRelative = relativeLayout;
        mLiner = linearLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_to_cart_list_adapter, viewGroup, false);
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

    public void confirmDailog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.alert_dialog_delete_scheme_txt));
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        mDataList.remove(position);
                        AppSession appSession = AppSession.getInstance(mContext);
                        appSession.setAddToCartList(mDataList.toString());
                        if (mDataList.size()==0){
                            mLiner.setVisibility(View.VISIBLE);
                            mRelative.setVisibility(View.GONE);
                        }else{
                            mLiner.setVisibility(View.GONE);
                            mRelative.setVisibility(View.VISIBLE);
                        }
                        notifyDataSetChanged();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bucket_icon;
        TextView basketname,folioNo;


        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.delete);
            basketname = view.findViewById(R.id.colorBlue);
            folioNo = view.findViewById(R.id.folioNo);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            JSONObject jsonObject = mDataList.get(position);
            basketname.setText(jsonObject.optString("SchName"));
            if (!jsonObject.optString("FolioNo").isEmpty()){
                folioNo.setVisibility(View.VISIBLE);
                folioNo.setText(jsonObject.optString("FolioNo"));
            }else{
                folioNo.setVisibility(View.GONE);
            }

            bucket_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDailog(position);
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
