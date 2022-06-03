package investwell.common.welcome;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import investwell.utils.AppSession;
import investwell.utils.Utils;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ViewPager pager;
    Context context;
    private int mPosition;
    private JSONArray count;
    private AppSession mSession;

    public ViewPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);
        this.pager = viewPager;
        mSession = AppSession.getInstance(context);
        count = (Utils.getConfigData(mSession).optJSONArray("SliderList"));

    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int positon) {
        mPosition = positon;
        return ViewPagerFragment.getInstance(positon);
    }

    @Override
    public int getCount() {
        return count.length();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Object obj = super.instantiateItem(container, position);
        return obj;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return super.isViewFromObject(view, object);
    }


}
