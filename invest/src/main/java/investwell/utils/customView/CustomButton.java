package investwell.utils.customView;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

public class CustomButton extends AppCompatButton {


    private Context context;
    private AttributeSet attrs;
    private int defStyle;


    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //    init();
        this.setTypeface(setFont(context));
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(setFont(context));
    }

    public CustomButton(Context context) {
        super(context);
        this.setTypeface(setFont(context));

    }


    public Typeface setFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
    }

}