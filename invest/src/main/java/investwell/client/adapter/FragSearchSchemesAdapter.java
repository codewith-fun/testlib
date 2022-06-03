package investwell.client.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;


/**
 * Created by shruti on 8/5/18.
 */

public class FragSearchSchemesAdapter extends RecyclerView.Adapter<FragSearchSchemesAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;


    public FragSearchSchemesAdapter(Context context, ArrayList<JSONObject> list,  OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_search_scheme_adapter, viewGroup, false);
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

        TextView mSchemeName, tv_SchemType;


        public ViewHolder(View view) {
            super(view);
            mSchemeName = itemView.findViewById(R.id.tv_schemename);
            tv_SchemType = itemView.findViewById(R.id.tv_SchemType);
        }

        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);

           mSchemeName.setText(jsonObject.optString("SchemeName"));
            tv_SchemType.setText(jsonObject.optString("Category"));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });


        }

    }


}