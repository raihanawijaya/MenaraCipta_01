package raihana.msd.rgl.fragment;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    abstract public void sync();
    abstract public void onRefresh();

}
