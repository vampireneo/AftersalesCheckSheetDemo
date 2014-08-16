package hk.com.inchcape.aftersaleschecksheetdemo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Created by neochoi on 16/8/14.
 */
public class CheckSheetScrollAdapter extends CardScrollAdapter {
    private List<RelativeLayout> mViews;

    public CheckSheetScrollAdapter(List<RelativeLayout> views) {
        mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object getItem(int i) {
        return mViews.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return mViews.get(i);
    }

    @Override
    public int getPosition(Object o) {
        return mViews.indexOf(o);
    }
}
