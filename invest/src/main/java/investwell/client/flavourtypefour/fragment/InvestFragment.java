package investwell.client.flavourtypefour.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvestFragment extends Fragment {

    public InvestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        return inflater.inflate(R.layout.fragment_invest, container, false);
    }
}
