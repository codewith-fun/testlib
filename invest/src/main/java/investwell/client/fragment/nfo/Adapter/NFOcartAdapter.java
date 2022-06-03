package investwell.client.fragment.nfo.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import investwell.utils.AppSession;

public class NFOcartAdapter extends RecyclerView.Adapter<NFOcartAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;

    public NFOcartAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @Override
    public NFOcartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_to_cart_list_adapter, viewGroup, false);
        return new NFOcartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NFOcartAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position);
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
                        appSession.setAddToNFOCartList(mDataList.toString());
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
        TextView basketname;


        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.delete);
            basketname = view.findViewById(R.id.colorBlue);
        }


        public void setItem(final int position) {
            JSONObject jsonObject = mDataList.get(position);
            basketname.setText(jsonObject.optString("SchName"));

            bucket_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDailog(position);
                }
            });


        }

    }


}

