package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.MyViewHolder> {

    public HashMap<Integer, Integer> mHashMapForMarks;
    public ArrayList<JSONObject> primaryBotList;
    private ArrayList<JSONObject> questionsList = new ArrayList<>();
    Context context;


    public ChatBotAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.primaryBotList = jsonObject;
        mHashMapForMarks = new HashMap<>();
    }

    @NonNull
    @Override
    public ChatBotAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbot, parent, false);
        ChatBotAdapter.MyViewHolder vh = new ChatBotAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ChatBotAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (!primaryBotList.isEmpty()) {

            holder.question.setText(primaryBotList.get(position).optString("Question"));

        }

        Log.e("List of question", primaryBotList.toString());
    }

    @Override
    public int getItemCount() {

        return primaryBotList.size();

    }

    public void updateList(List<JSONObject> list) {

        primaryBotList.clear();
        primaryBotList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView question, answers;
        private RecyclerView rvOptions;


        public MyViewHolder(View view) {
            super(view);

            question = view.findViewById(R.id.tv_risk_questions);
            rvOptions = view.findViewById(R.id.rv_risk_answer_options);
            answers = view.findViewById(R.id.tv_risk_answer);
        }
    }


}

