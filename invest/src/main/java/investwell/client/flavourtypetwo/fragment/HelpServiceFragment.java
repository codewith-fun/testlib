package investwell.client.flavourtypetwo.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.iw.acceleratordemo.R;

import java.util.ArrayList;
import java.util.List;

import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.client.flavourtypetwo.adapter.HelpServicesAdapter;
import investwell.client.flavourtypetwo.model.ServicesTypeTwo;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpServiceFragment extends Fragment implements HelpServicesAdapter.InvestRouteListenerTypeTwo {
    private MainActivityTypeTwo mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityTypeTwo) {
            this.mActivity = (MainActivityTypeTwo) context;
        }

    }

    public HelpServiceFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private HelpServicesAdapter helpServicesAdapter;

    private List<ServicesTypeTwo> servicesTypeTwoList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_service, container, false);
        initializer(view);
        setServiceAdapter();
        return view;
    }

    private void initializer(View view) {
        servicesTypeTwoList=new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_services);
        helpServicesAdapter = new HelpServicesAdapter(mActivity, servicesTypeTwoList, this);

    }

    private void setServiceAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(helpServicesAdapter);
        prepareServiceData();
    }

    private void prepareServiceData(){
        int[] routeIcons = new int[]{
                R.mipmap.ic_services_portfolio_tracker,
                R.mipmap.ic_services_nsdl_spped_e,
                R.mipmap.ic_services_ndls_payment,
                R.mipmap.ic_services_online_accounts,

        };

        ServicesTypeTwo a = new ServicesTypeTwo(routeIcons[0],"Portfolio Tracker");
        servicesTypeTwoList.add(a);

        a = new ServicesTypeTwo(routeIcons[1],"NSDL Speed-e");
        servicesTypeTwoList.add(a);

        a = new ServicesTypeTwo(routeIcons[2],"NSDL Payment Bank");
        servicesTypeTwoList.add(a);

        a = new ServicesTypeTwo(routeIcons[3],"Online Accounts");
        servicesTypeTwoList.add(a);


        helpServicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRoutesClick(int position) {

    }
}
