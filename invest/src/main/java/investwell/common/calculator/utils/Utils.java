package investwell.common.calculator.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.util.DisplayMetrics;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class Utils {
    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public static String fmt(String s){

        StringBuilder formatted = new StringBuilder();
        if(s.length() > 1){
            formatted = new StringBuilder(s.substring(0, 1));
            s = s.substring(1);
        }

        while(s.length() > 3){
            formatted.append(",").append(s.substring(0, 2));
            s = s.substring(2);
        }
        return formatted + "," + s;
    }
    public static Uri saveImage(Bitmap image, Context context) {
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            System.out.println(uri);
        } catch (IOException e) {
            System.out.println("IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }


    public  static  void convertIntoCurrencyFormat(String currency, EditText editText) {
        String format = "";
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        format = nf.format(Double.parseDouble(currency)).trim();
        String[] resultAmount = format.split("\\.", 0);
        String vlaue = resultAmount[0];
        editText.setText(vlaue);
        editText.setSelection(editText.getText().toString().length());
        // return resultAmount[0];
    }

}
