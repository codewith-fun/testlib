package investwell.client.fragment.nfo.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.fragment.nfo.Adapter.NFOAdapter;
import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

public class FragOpen extends Fragment {
    private AppSession mSession;
    private MainActivity mActivity;
    private Bundle bundle;
    private RecyclerView mOpenRecycle;
    private NFOAdapter nfoAdapter;

    private LinearLayout ll_no_data_found;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_open, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        bundle = getArguments();
        mOpenRecycle = view.findViewById(R.id.open_recycle);


        ll_no_data_found = view.findViewById(R.id.ll_no_data_found);
        nfoAdapter = new NFOAdapter(getActivity(), new ArrayList<JSONObject>(), FragOpen.this,mActivity);
        mOpenRecycle.setHasFixedSize(true);
        mOpenRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mOpenRecycle.setItemAnimator(new DefaultItemAnimator());
        mOpenRecycle.setNestedScrollingEnabled(false);
        mOpenRecycle.setAdapter(nfoAdapter);
        setOpenData(bundle.getString("Type"));
        return view;
    }


    private void setOpenData(String type) {

        try {
            ArrayList<JSONObject> list = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(bundle.getString("AllData"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (type.equalsIgnoreCase("Open Now")){
                        if (jsonObject.optString("NFOStatus").equalsIgnoreCase("Current")){
                            list.add(jsonObject);
                            if (list.isEmpty()){
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            }else{
                                ll_no_data_found.setVisibility(View.GONE);
                            }
                        }
                    }else{
                        if (!jsonObject.optString("NFOStatus").equalsIgnoreCase("Current")){
                            list.add(jsonObject);
                            if (list.isEmpty()){
                                ll_no_data_found.setVisibility(View.VISIBLE);
                            }else{
                                ll_no_data_found.setVisibility(View.GONE);
                            }
                        }
                    }


                }
            setOpenAdapter(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOpenAdapter(ArrayList<JSONObject> list){


        nfoAdapter.updatelist(list);
    }




}
