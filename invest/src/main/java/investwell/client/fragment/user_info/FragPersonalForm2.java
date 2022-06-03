package investwell.client.fragment.user_info;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import android.widget.TextView;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.Config;
import investwell.utils.customView.CustomTextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by shruti on 11/5/18.
 */

public class FragPersonalForm2 extends Fragment implements View.OnClickListener {

    Spinner country_spinner, state_spinner;
    LinearLayout layout_second_pan, layout_second_name, foreigner_layout;
    EditText second_pan , address_three;
    EditText foreign_address, foreign_country, foreign_state, foreign_city, foreign_pin;
    CustomTextInputEditText mEtHouseNo, mEtStreet, mEtCity,  mEtPin;
    TextView second_name;
    RadioButton single, joint;
    String[] code, state;
    String holding_nature_code = "SI", address_one_value, address_second_value, address_third_value, city_value, pin_value, second_pan_value, second_name_vlaue;
    String foreign_address_value, foreign_country_value, foreign_state_value, foreign_city_value, foreign_pin_value;
    String PANKYCCheckResult;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private Bundle mBundle;
    private ToolbarFragment fragToolBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_personal_form2, container, false);

        country_spinner = view.findViewById(R.id.country_spinner);
        state_spinner = view.findViewById(R.id.state_spinner);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mBundle = getArguments();
        setUpToolBar();
        single = view.findViewById(R.id.single);
        joint = view.findViewById(R.id.joint);
        layout_second_pan = view.findViewById(R.id.layout_second_pan);
        layout_second_name = view.findViewById(R.id.layout_second_name);
        foreigner_layout = view.findViewById(R.id.foreigner_layout);
        second_pan = view.findViewById(R.id.second_pan);
        second_name = view.findViewById(R.id.second_name);
        mEtHouseNo = view.findViewById(R.id.et_address);
        mEtStreet = view.findViewById(R.id.et_street);
        //  address_three = view.findViewById(R.id.address_three);
        foreign_address = view.findViewById(R.id.foreign_address);
        foreign_country = view.findViewById(R.id.foreign_country);
        foreign_state = view.findViewById(R.id.foreign_state);
        foreign_city = view.findViewById(R.id.foreign_city);
        foreign_pin = view.findViewById(R.id.foreign_pin);
        mEtCity = view.findViewById(R.id.et_city);
        mEtPin = view.findViewById(R.id.et_pin);
        view.findViewById(R.id.btn_continue_nse_fatca).setOnClickListener(this);
        view.findViewById(R.id.btn_previous_nse_fatca).setOnClickListener(this);

     //   country_spinner.getBackground().setColorFilter(ContextCompat.getColor(mActivity, R.color.dark_gray), PorterDuff.Mode.SRC_ATOP);
   /*     if (mBundle.getString("tax_status").equals("24")) {
            foreigner_layout.setVisibility(View.VISIBLE);
        }*/

        setStateData();

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String state_code = code[i];
                mBundle.putString("state_code", state_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_address_details_form), true, false, false, false, false, false, false,"");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            goToNext();
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    private void goToNext() {
        address_one_value = mEtHouseNo.getText().toString();
        address_second_value = mEtStreet.getText().toString();
        // address_third_value = address_three.getText().toString();
        city_value = mEtCity.getText().toString();
        pin_value = mEtPin.getText().toString();
        second_pan_value = second_pan.getText().toString();
        second_name_vlaue = second_name.getText().toString();

        foreign_address_value = foreign_address.getText().toString();
        foreign_country_value = foreign_country.getText().toString();
        foreign_state_value = foreign_state.getText().toString();
        foreign_city_value = foreign_city.getText().toString();
        foreign_pin_value = foreign_pin.getText().toString();

        if (address_one_value.equals("")) {

            mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_empty_address));
        } else if (address_second_value.equals("")) {

            mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_empty_street_name));
        }  else if (city_value.toString().equals("")) {

            mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_empty_city));
        } else if (pin_value.toString().equals("")) {

            mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_empty_pin));
        } else if (pin_value.length() < 6) {

            mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_invalid_pin));
        } else {

            if (foreigner_layout.getVisibility() == View.VISIBLE) {
                if (foreign_address_value.toString().equals("")) {

                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_foreign_address));

                } else if (foreign_country_value.toString().equals("")) {

                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_country));
                } else if (foreign_state_value.toString().equals("")) {

                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_foreign_state));
                } else if (foreign_city_value.toString().equals("")) {

                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_foreign_city));
                } else if (foreign_pin_value.toString().equals("")) {
                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_foreign_empty_pin));
                } else if (foreign_pin_value.length() < 6) {
                    mApplication.showSnackBar(foreign_address,getResources().getString(R.string.personal_details_error_foreign_invalid_pin));
                }
            }

            mBundle.putString("holding_nature_code", holding_nature_code);
            mBundle.putString("second_pan_value", second_pan_value);
            mBundle.putString("second_name_value", "");
            mBundle.putString("address_one_value", address_one_value);
            mBundle.putString("address_second_value", address_second_value);
            // mBundle.putString("address_third_value", address_third_value);
            mBundle.putString("city_value", city_value);
            mBundle.putString("pin_value", pin_value);
            mBundle.putString("second_pan_value", second_pan_value);
            mBundle.putString("second_name_vlaue", second_name_vlaue);

            mBundle.putString("foreign_address_value", foreign_address_value);
            mBundle.putString("foreign_country_value", foreign_country_value);
            mBundle.putString("foreign_state_value", foreign_state_value);
            mBundle.putString("foreign_city_value", foreign_city_value);
            mBundle.putString("foreign_pin_value", foreign_pin_value);

            mActivity.displayViewOther(7, mBundle);

        }
    }


    private void setStateData() {

        try {
            JSONObject jsonObject = new JSONObject(Config.STATE);
            String BSEStateListResult = jsonObject.getString("BSEStateListResult");
            JSONArray jsonArray = new JSONArray(BSEStateListResult);

            code = new String[jsonArray.length()];
            state = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject state_value = jsonArray.getJSONObject(i);
                code[i] = state_value.getString("CODE");
                state[i] = state_value.getString("STATE");
            }

            ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, state);
            spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            state_spinner.setAdapter(spinner_value);

            state_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}