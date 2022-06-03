package investwell.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import investwell.client.activity.MainActivity;

/**
 * Created by gigabyte on 21-09-2016.
 */
public class DocumentSwipeAdapter extends RecyclerView.Adapter<DocumentSwipeAdapter.MyViewHolder> {
    public List<JSONObject> mDataListRycleView;
    private Context mContaxt;
    private HashMap<Integer, Boolean> mHashSelection = new HashMap<>();

    public DocumentSwipeAdapter(Context context, ArrayList<JSONObject> horizontalList) {
        this.mDataListRycleView = horizontalList;
        mContaxt = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.document_swipe_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final JSONObject object = mDataListRycleView.get(position);
        holder.mRelLayout.setVisibility(View.VISIBLE);
        holder.linerAllDocuments.setVisibility(View.GONE);
        holder.tvName.setText(object.optString("DocName"));
        try {
            String path = object.optString("DocPath");
            // path = path.replace("co.in", "in");
            if (path != null && !path.equals("")) {
                Picasso.get().load(path).placeholder(R.mipmap.card_blank)
                        .error(R.mipmap.card_blank).into(holder.ivImage);
            }
        } catch (Exception e) {

        }

        holder.mRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "singleView");
                bundle.putString("data", object.toString());
                Intent intent = new Intent(mContaxt, MainActivity.class);
                intent.putExtra("Flavour", "TYPE 2");
                intent.putExtra("position", "100");
                intent.putExtras(bundle);
                mContaxt.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataListRycleView.size();

    }

    public void updateList(List<JSONObject> list) {
        mDataListRycleView.clear();
        mDataListRycleView.addAll(list);
        mHashSelection.clear();

        for (int i = 0; i < mDataListRycleView.size(); i++) {
            mHashSelection.put(i, true);
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView ivImage;
        private RelativeLayout mRelLayout;
        private LinearLayout linerAllDocuments;


        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.textView1);
            ivImage = view.findViewById(R.id.ivImage);
            mRelLayout = view.findViewById(R.id.relMain);
            linerAllDocuments = view.findViewById(R.id.linerAllDocuments);
        }
    }
}