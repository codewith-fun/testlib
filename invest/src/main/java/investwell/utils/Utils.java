package investwell.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.client.activity.MainActivity;

public class Utils {

    private String s;
public static  String tabText="";
public static int tabPosition=0;
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
    public static String getTodayDateString() {
        String todayDate = "";
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            todayDate = outputFormat.format(new Date());
        } catch (Exception e) {

        }
        return todayDate;
    }
    public static Date getTodayDate(String date) {
        Date afterFormat = null;
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            afterFormat = outputFormat.parse(date);
        } catch (Exception e) {

        }
        return afterFormat;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isTablet(Activity activity) {
        return (activity.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void generateFinalKey(String u, String p) {
        calcLength(u, p);
    }

    //Step 1:
    private static void calcLength(String s, String u) {
        int sLength = s.length();
        int pLength = u.length();
        Log.e("USERNAME LENGTH", String.valueOf(sLength));
        Log.e("PASSWORD LENGTH", String.valueOf(pLength));
        reverse(s, u, sLength, pLength);
    }

    //Step 2:
    private static void reverse(String s, String u, int sLength, int pLength) {
        StringBuilder sb = new StringBuilder(s);
        StringBuilder sp = new StringBuilder(u);

        String randomString = getAlphaNumericString(20);
        Log.e("RANDOM STRING", randomString);
        divideStrings(randomString, sb.reverse().toString(), sp.reverse().toString(), sLength, pLength);
        Log.e("REVERSE USERNAME LENGTH", sb.reverse().toString());
        Log.e("REVERSE PASSWORD LENGTH", sp.reverse().toString());
    }

    //Step 3:
    // function to generate a random string of length 20
    private static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();

    }

    //Step 4 :
    private static void divideStrings(String dividedString, String insertedOne, String insertedTwo, int sLength, int pLength) {
        //Stores the length of the string
        int len = dividedString.length();
        //n determines the variable that divide the string in 'n' equal parts
        int n = 2;
        int temp = 0, chars = len / n;
        //Stores the array of string
        String[] equalStr = new String[n];
        //Check whether a string can be divided into n equal parts
        if (len % n != 0) {
            System.out.println("Sorry this string cannot be divided into " + n + " equal parts.");
        } else {
            for (int i = 0; i < len; i = i + chars) {
                //Dividing string in n equal part using substring()
                String part = dividedString.substring(i, i + chars);
                equalStr[temp] = part;
                temp++;
            }
            System.out.println(n + " equal parts of given string are ");

            for (int i = 0; i < equalStr.length; i++) {
                System.out.println(equalStr[i]);

                Log.e("Strins are here", equalStr[i]);
            }
            String s1 = equalStr[0];
            String s2 = equalStr[1];
            Log.e("RANDOM STRING  1", s1 + "Length" + s1.length());
            Log.e("RANDOM STRING 2", s2 + "Lenghth" + s2.length());

            generateRandomIndex(s1.length(), s2.length(), s1, s2, insertedOne, insertedTwo, sLength, pLength);
        }
    }

    //Step 5 :
    private static void generateRandomIndex(int u, int p, String originalStringOne, String originalStringTwo, String insertedOne, String inserteTwo, int sLength, int pLength) {
        Random generator = new Random();
        int i = 0, j = 0;
        i = generator.nextInt(u);
        j = generator.nextInt(p);
        Log.e("RANDOM INDEXES", String.valueOf(i));
        Log.e("RANDOM INDEXES", String.valueOf(j));
        String modifiedString1= insertString(originalStringOne, insertedOne, i);
        String modifiedString2=  insertStringTwo(originalStringTwo, inserteTwo, j);
        String resultantString=modifiedString1+modifiedString2;
        sequenceNumbers(i, sLength, u - i, j, pLength,resultantString);
    }

    //STEP 6: Inserted String 1
    public static String insertString(
            String originalString,
            String stringToBeInserted,
            int index) {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        Log.e("STRING MODIFIED 1", newString);
        return newString;
    }

    //STEP 6: Inserted String 2
    public static String insertStringTwo(
            String originalString,
            String stringToBeInserted,
            int index) {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        Log.e("STRING MODIFIED 2", newString);
        return newString;
    }

    //Step 7:
    private static void sequenceNumbers(int randomIndexOne, int userNameLength, int subtractedIndexValueOne,
                                        int randomIndexTwo, int pwdLength, String resultantString) {
        String randomAlpha = generateAlphaString(4);
        Log.e("randomAlpha", randomAlpha);
        char randomAlpha1 = randomAlpha.charAt(0);
        char randomAlpha2 = randomAlpha.charAt(1);
        char randomAlpha3 = randomAlpha.charAt(2);
        char randomAlpha4 = randomAlpha.charAt(3);
        String s1 = "";
        if (randomIndexOne < 10) {
            s1 = "0" + randomIndexOne;
        } else {
            s1 = String.valueOf(randomIndexOne);
        }
        int combinedLengthOne = randomIndexOne + userNameLength;
        int combinedLengthTwo = combinedLengthOne + subtractedIndexValueOne + randomIndexTwo;
        int combinedLengthThree = combinedLengthTwo + pwdLength;

        String sequence = s1 + combinedLengthOne + combinedLengthTwo + combinedLengthThree;
        String d2 = String.valueOf(combinedLengthOne);
        String d3 = String.valueOf(combinedLengthTwo);
        String d4 = String.valueOf(combinedLengthThree);
        StringBuffer sbf1 = new StringBuffer(s1);
        sbf1.append(randomAlpha1);
        StringBuffer sbf2 = new StringBuffer(d2);
        sbf2.append(randomAlpha2);
        StringBuffer sbf3 = new StringBuffer(d3);
        sbf3.append(randomAlpha3);
        StringBuffer sbf4 = new StringBuffer(d4);
        sbf4.append(randomAlpha4);

        Log.e("PRE FINAL SEQUENCE", sequence);
        String randomSequenceKey=sbf1.toString() + sbf2.toString() + sbf3.toString() + sbf4.toString();
       /* Log.e("RANDOM FINAL SEQUENCE", sbf1.toString() + sbf2.toString() + sbf3.toString() + sbf4.toString());*/
        Random r = new Random();
        int low = 10;
        int high = 30;
        int result = r.nextInt(high - low) + low;
        String random30 = getAlphaNumericString(result);

finalSecretKey(resultantString,random30,randomSequenceKey);
    }

    //Step 8:
    // function to generate a random string of length 4
    private static String generateAlphaString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();

    }
private static void finalSecretKey(String resultantString,String randomString,String randomKey){
String resultCipher=resultantString+randomString+randomKey;
Log.e("FINAL SECRET KEY",resultCipher);
}
   /* public Boolean isDeviceRooted(Context context){
        boolean isRooted = isrooted1() || isrooted2();
        return isRooted;
    }

    private boolean isrooted1() {

        File file = new File("/system/app/Superuser.apk");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    // try executing commands
    private boolean isrooted2() {
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su")
                || canExecuteCommand("which su");
    }
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }*/


    public static String convertStringFirstTextCaps(String name) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(name);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }


    public static JSONObject getConfigData(AppSession mSession) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(!TextUtils.isEmpty(mSession.getAppConfig()) ? mSession.getAppConfig() : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    public static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }


    public static String[]  countryCode = new String[]{"+91","+93", "+355", "+213", "+1 684", "+376", "+244", "+1 264", "+672", "+1 268", "+54", "+374",

            "+297", "+61", "+43", "+994", "+1 242", "+973", "+880", "+1 246", "+375", "+32", "+501",

            "+229", "+1 441", "+975", "+591", "+387", "+267", "+55", "+246", "+1 284", "+673", "+359",

            "+226", "+95", "+257", "+855", "+237", "+1", "+238", "+1 345", "+236", "+235", "+56", "+86",

            "+61", "+891", "+57", "+269", "+682", "+506", "+385", "+53", "+357", "+420", "+243", "+45",

            "+253", "+1 767", "+1 849", "+1 829", "+1 809", "+593", "+20", "+503", "+240", "+291", "+372",

            "+251", "+500", "+298", "+679", "+358", "+33", "+689", "+241", "+220", "+970", "+995", "+49",

            "+233", "+350", "+30", "+299", "+1 473", "+1 671", "+502", "+224", "+245", "+592", "+509",

            "+379", "+504", "+852", "+36", "+354",  "+62", "+98", "+964", "+353", "+44", "+972",

            "+39", "+225", "+1 876", "+81", "+44", "+962", "+7", "+254", "+686", "+381", "+965", "+996",

            "+856", "+371", "+961", "+266", "+231", "+218", "+423", "+370", "+352", "+853", "+389",

            "+261", "+265", "+60", "+960", "+223", "+356", "+692", "+222", "+230", "+262", "+52", "+691",

            "+373", "+377", "+976", "+382", "+1 664", "+212", "+258", "+264", "+674", "+977", "+31",

            "+599", "+687", "+64", "+505", "+227", "+234", "+683", "+672", "+850", "+1 670", "+47",

            "+968", "+92", "+680", "+507", "+675", "+595", "+51", "+63", "+870", "+48", "+351", "+1",

            "+974", "+242", "+40", "+7", "+250", "+590", "+290", "+1 869", "+1 758", "+1 599", "+508",

            "+1 784", "+685", "+378", "+239", "+966", "+221", "+381", "+248", "+232", "+65", "+421",

            "+386", "+677", "+252", "+27", "+82", "+34", "+94", "+249", "+597", "+268", "+46", "+41",

            "+963", "+886", "+992", "+255", "+66", "+670", "+228", "+690", "+676", "+1 868", "+216",

            "+90", "+993", "+1 649", "+688", "+256", "+380", "+971", "+44", "+1", "+598", "+1 340",

            "+998", "+678", "+58", "+84", "+681", "+970", "+967", "+260", "+263"};
}