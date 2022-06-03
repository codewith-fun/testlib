package investwell.utils.piechart.others;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.utils.customView.CustomTextViewLight;
import investwell.utils.customView.CustomTextViewRegular;
import investwell.utils.piechart.data.IPieInfo;
import investwell.utils.Config;


/**
 * Created by 大灯泡 on 2018/9/26.
 * <p>
 * 图例
 */
public class DefaultPieLegendsView extends BasePieLegendsView {

    private View viewTag;
    private TextView tvDesc;
    private static final float SCALE_RATIO = 0.4f;
    private static final float SCALE_MAX = 0.8f;

    public static DefaultPieLegendsView newInstance(Context context) {
        return new DefaultPieLegendsView(context);
    }

    private DefaultPieLegendsView(Context context) {
        this(context, null);
    }

    private DefaultPieLegendsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private DefaultPieLegendsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.widget_default_pie_legends);
        viewTag = findViewById(R.id.view_tag);
        tvDesc = findViewById(R.id.tv_desc);

        reset();
    }

    private void reset() {
        viewTag.setScaleX(0f);
        viewTag.setScaleY(0f);
        viewTag.setAlpha(0);
        tvDesc.setAlpha(0);
        tvDesc.setScaleX(0f);
        tvDesc.setScaleY(0f);

        viewTag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewTag.setPivotX(viewTag.getWidth() / 2);
                viewTag.setPivotY(viewTag.getHeight() / 2);
                viewTag.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        tvDesc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvDesc.setPivotX(tvDesc.getWidth() / 2);
                tvDesc.setPivotY(tvDesc.getHeight() / 2);
                tvDesc.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

    }

    @Override
    public void onPieDrawStart(@NonNull IPieInfo pie) {
        viewTag.setBackgroundColor(pie.getColor());
        boolean mIsValueMatched = false;

        // tvDesc.setText(pie.getDesc());

        for (int i = 0; i < Config.mGraphValue.size() ; i++) {
            String firstKey = (String) Config.mGraphValue.keySet().toArray()[i];
            double value = Double.valueOf(Config.mGraphValue.get(firstKey));
            double graphAmount = pie.getValue();
           // DecimalFormat twoDForm = new DecimalFormat("#.00");
            double newGraphData = Double.valueOf(graphAmount);
            if ((int)value == (int)newGraphData) {
                mIsValueMatched = true;
                tvDesc.setText(firstKey);
                break;
            }
        }

       /* try {

            JSONObject mDataList = new JSONObject(AppApplication.graph);
            String Status = mDataList.optString("Status");
            if (Status.equalsIgnoreCase("True")) {
                JSONArray jsonArray = mDataList.optJSONArray("PortfolioAllocationDetail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Double Amount = Double.valueOf(jsonObject1.optString("Amount"));
                    Double graphAmount = pie.getValue();
                    if (Amount.equals(graphAmount)) {
                        mIsValueMatched = true;
                        tvDesc.setText(jsonObject1.optString("Asset"));
                        break;
                    }
                }

            }
        } catch (Exception e) {
            System.out.println();
        } finally {
            if (!mIsValueMatched) {
                String[] graph_data = Pref.getString(getContext(), "graph_data").split("\\|");
                for (int i = 0; i < graph_data.length; i++) {
                    Double Amount = Double.valueOf(graph_data[i]);
                    Double graphAmount = pie.getValue();
                    DecimalFormat twoDForm = new DecimalFormat("#.00");
                    double newGraphData = Double.valueOf(twoDForm.format(graphAmount));

                    if (Amount.equals(newGraphData)) {
                        if (i == 0) {
                            tvDesc.setText("Equity");
                        } else if (i == 1) {
                            tvDesc.setText("Debt");
                        } else if (i == 2) {
                            tvDesc.setText("Other");
                        }
                        break;
                    }

                }
            }
        }*/

        reset();
    }

    @Override
    public void onPieDrawing(@NonNull IPieInfo pie, float progress) {
        viewTag.setAlpha(progress);
        viewTag.setScaleX(SCALE_MAX * progress);
        viewTag.setScaleY(SCALE_MAX * progress);

        tvDesc.setAlpha(progress);
        tvDesc.setScaleX(SCALE_MAX * progress);
        tvDesc.setScaleY(SCALE_MAX * progress);
    }

    @Override
    public void onPieDrawFinish(@NonNull IPieInfo pie) {
        viewTag.setAlpha(1f);
        viewTag.setScaleX(SCALE_MAX);
        viewTag.setScaleY(SCALE_MAX);

        tvDesc.setAlpha(1f);
        tvDesc.setScaleX(SCALE_MAX);
        tvDesc.setScaleY(SCALE_MAX);
    }

    public void onPieFloatUp(@NonNull IPieInfo pie, float timeSet) {
        float scale = SCALE_RATIO * timeSet;

        viewTag.setScaleX(SCALE_MAX + scale);
        viewTag.setScaleY(SCALE_MAX + scale);

        tvDesc.setScaleX(SCALE_MAX + scale);
        tvDesc.setScaleY(SCALE_MAX + scale);
    }

    public void onPieFloatDown(@NonNull IPieInfo pie, float timeSet) {
        float scale = SCALE_RATIO * timeSet;

        viewTag.setScaleX(SCALE_MAX + scale);
        viewTag.setScaleY(SCALE_MAX + scale);

        tvDesc.setScaleX(SCALE_MAX + scale);
        tvDesc.setScaleY(SCALE_MAX + scale);
    }
}
