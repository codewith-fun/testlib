package investwell.client.fragment.bank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleBankListIndiaAdapter extends RecyclerView.Adapter<RecycleBankListIndiaAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleBankListIndiaAdapter.OnItemClickListener listener;
    private String mType = "";

    public RecycleBankListIndiaAdapter(Context context, ArrayList<JSONObject> list, String type, RecycleBankListIndiaAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_banklist_india_adapter, viewGroup, false);
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
        TextView tvBankName;
        LinearLayout LinerProfileItems;

        public ViewHolder(View view) {
            super(view);
            tvBankName = view.findViewById(R.id.tvBankName);
            LinerProfileItems = view.findViewById(R.id.LinerProfileItems);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            tvBankName.setText(jsonObject.optString("BankName"));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });

        }

    }


}
