
package investwell.utils.piechart.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import investwell.client.activity.AppApplication;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvDate ,tvInvestment, tvCurrent;
    String mType = "";

    public MyMarkerView(Context context, int layoutResource, String type) {
        super(context, layoutResource);

        tvDate = findViewById(R.id.tvDate);
        tvCurrent = findViewById(R.id.tvCurrent);
        tvInvestment = findViewById(R.id.tvInvestment);
        mType = type;
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        try {
            if (e instanceof CandleEntry) {

                CandleEntry ce = (CandleEntry) e;

                //tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            } else {
                int xValue = (int) (Math.round(e.getX()));
                ;
                Entry investment = AppApplication.GInvestmentList.get(xValue);
                Entry currentValue = AppApplication.GCurrentValueList.get(xValue);
                JSONObject jsonObject = AppApplication.dataList.get(xValue);

                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(0);

                tvCurrent.setText(getResources().getString(R.string.factsheet_line_graph_vale1)+": "+format.format(currentValue.getY()));
                tvInvestment.setText(getResources().getString(R.string.factsheet_line_graph_vale2)+": " + format.format(investment.getY()));
                tvDate.setText(getResources().getString(R.string.txt_date)+": " + jsonObject.optString("DateSeries"));


                //tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
            }

            //Entry, x: 13.0 y: 2452628.8
//Highlight, x: 13.0, y: 2452628.8, dataSetIndex: 1, stackIndex (only stacked barentry): -1
        }catch (Exception ea){

        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
