package investwell.utils.customView;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;


public class CustomTextViewBold extends AppCompatTextView {
    public CustomTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
          this.setTypeface(setFont(context));
    }

    public CustomTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
          this.setTypeface(setFont(context));
    }

    public CustomTextViewBold(Context context) {
        super(context);
        this.setTypeface(setFont(context));

    }

    public Typeface setFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");

    }




    @Override
    public boolean isInEditMode() {
        return super.isInEditMode();
    }

}