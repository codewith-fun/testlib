package investwell.client.fragment.factsheet.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

public class SchemeComparisonAdapter extends RecyclerView.Adapter<SchemeComparisonAdapter.MyViewHolder> {
    private Context mContext;
    private String[] schemeAgeList;
    private SchemeComparisonListener schemeCompListener;
    int index = -1;
    long DURATION = 500;
    private boolean on_attach = true;
    private boolean clicked = false;

    public SchemeComparisonAdapter(Context context, String[] schemeAgeList, SchemeComparisonListener schemeCompListeners) {
        this.schemeAgeList = schemeAgeList;
        mContext = context;
        schemeCompListener = schemeCompListeners;
    }

    @NonNull
    @Override
    public SchemeComparisonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_scheme_age, parent, false);

        return new SchemeComparisonAdapter.MyViewHolder(itemView);
    }

    private void setAnimRightToLeft(View itemView, int i) {
        if (!on_attach) {
            i = -1;
        }
        boolean not_first_item = i == -1;
        i = i + 1;
        itemView.setTranslationX(itemView.getX() + 400);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getX() + 400, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1.f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION : (i * DURATION));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }

    @Override
    public void onBindViewHolder(@NonNull final SchemeComparisonAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setItem(position, schemeCompListener);

        if (clicked == true) {
        } else {
            holder.setColor(position);
        }
    }

    @Override
    public int getItemCount() {
        return schemeAgeList.length;
    }

    public interface SchemeComparisonListener {
        void onSchemeAgeClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSchemeAge;
        public RelativeLayout rlRowSchemeAge;

        public MyViewHolder(View view) {
            super(view);
            tvSchemeAge = view.findViewById(R.id.tv_scheme_age);
            rlRowSchemeAge = view.findViewById(R.id.rl_row_scheme_age);
        }

        public void setItem(final int position, final SchemeComparisonListener listener) {

            String value = schemeAgeList[position];
            tvSchemeAge.setText(value);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onSchemeAgeClick(position);
                    index = position;
                    setColor(index);
                    clicked = true;
                    notifyDataSetChanged();

                }
            });


            if (index == position) {
                tvSchemeAge.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                rlRowSchemeAge.setBackground(mContext.getResources().getDrawable(R.drawable.selected_scheme_age_bg));

            } else {
                tvSchemeAge.setTextColor(mContext.getResources().getColor(R.color.textColorSecondary));
                rlRowSchemeAge.setBackground(mContext.getResources().getDrawable(R.drawable.unselected_scheme_age_bg));

            }

        }


        public void setColor(int position) {
            for (int i = 0; i < schemeAgeList.length; i++) {
                if (position == 1) {
                    tvSchemeAge.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                    rlRowSchemeAge.setBackground(mContext.getResources().getDrawable(R.drawable.selected_scheme_age_bg));
                } else {
                    tvSchemeAge.setTextColor(mContext.getResources().getColor(R.color.textColorSecondary));
                    rlRowSchemeAge.setBackground(mContext.getResources().getDrawable(R.drawable.unselected_scheme_age_bg));
                }
            }
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }
}
