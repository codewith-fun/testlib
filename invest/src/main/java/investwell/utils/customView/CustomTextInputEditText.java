package investwell.utils.customView;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputEditText;
import android.util.AttributeSet;

public class CustomTextInputEditText extends TextInputEditText {
    public CustomTextInputEditText(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
        this.setTypeface(face);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
        this.setTypeface(face);
    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "Lato-Regular.ttf");
        this.setTypeface(face);
    }
}