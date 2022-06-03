package investwell.client.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewLight;



public class YearRecyclieviewAdapter extends RecyclerView.Adapter<YearRecyclieviewAdapter.ViewHolder> {

    private String[] mYear;
    private LayoutInflater mInflater;
    private YearRecyclieviewAdapter.OnItemClickListener listener;
    private Context mContext;
    int index = -1;
    private boolean cliked = false;
    private AppSession mSession;

    // data is passed into the constructor
    public YearRecyclieviewAdapter(Context context, String[] year, YearRecyclieviewAdapter.OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mYear = year;
        this.listener = listener;
        this.mContext = context;
        mSession=AppSession.getInstance(context);
    }

    // inflates the row layout_gridview_type_two_a from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.year_view_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setItem(position, listener);

        if (cliked==true){
        }else {
            holder.setColor(position);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mYear.length;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.years);
        }

        public void setItem(final int position, final OnItemClickListener listener) {

            String value = mYear[position];
            myTextView.setText(value);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(position);
                    index = position;
                    setColor(index);
                    cliked=true;
                    notifyDataSetChanged();

                }
            });


            if (index == position) {


                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                    myTextView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.darkPrimaryBtnBg));
                }else{
                    myTextView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.lightPrimaryBtnBg));

                }

            } else {
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                    myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.darkDialogBackground));
                    myTextView.setTextColor(Color.WHITE);
                }else{
                    myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.lightDialogBackground));
                    myTextView.setTextColor(Color.BLACK);
                }

            }

        }


        public void setColor(int position) {
            for (int i = 0; i < mYear.length; i++) {
                if (position == 7) {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                        myTextView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                        myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.darkPrimaryBtnBg));
                    }else{
                        myTextView.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                        myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.lightPrimaryBtnBg));

                    }

                } else {
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                        myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.darkDialogBackground));
                        myTextView.setTextColor(Color.WHITE);
                    }else{
                        myTextView.setBackgroundColor(mContext.getResources().getColor(R.color.lightDialogBackground));
                        myTextView.setTextColor(Color.BLACK);
                    }
                }
            }
        }
    }


}
