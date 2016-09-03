package precious_rule_system.journeyview_new.grid;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.page.JourneyPageViewWrapper;
import precious_rule_system.journeyview_new.page.JourneyPageViewWrapperDelegate;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyGridAdapter extends BaseAdapter implements JourneyPageViewWrapperDelegate {

    private Context context;
    private int width = 0;
    private int height = 0;
    private JourneyAssets assets;
    private Layout layout;
    private JourneyPageViewWrapperDelegate delegate;
    private JourneyGridAdapterDelegate data;

    public JourneyGridAdapter(int width, int height, Context context, JourneyPageViewWrapperDelegate delegate, JourneyGridAdapterDelegate data, JourneyAssets assets, Layout layout) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.assets = assets;
        this.layout = layout;
        this.delegate = delegate;
        this.data = data;
    }

    @Override
    public int getCount() {
        return this.data.getNumberOfItems();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JourneyPageViewWrapper view;
        if (convertView == null) {
            view = new JourneyPageViewWrapper(context, this.assets, this);
            view.setLayoutParams(new GridView.LayoutParams(width, height));
            view.setDimensions(width, height);
        } else {
            view = (JourneyPageViewWrapper) convertView;
            view.reset();
        }

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Page clicked: " + _p);
            }
        });*/

        view.addAssets(data.getAssets(position), data.getLandscapeForItem(position));
        ArrayList<Tuple<RewardEvent, Float>> events = data.getEvents(position);
        for(Tuple<RewardEvent, Float> e: events) view.addRewardEvent(e.x,e.y);

        Float playerPosition = data.getPlayerPosition(position);
        if (playerPosition != null) view.addPlayer(playerPosition);

        return view;

    }

    public void rewardEventClicked(RewardEvent e) {
        this.delegate.rewardEventClicked(e);
    }


}
