package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.data.DataManager;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerWrapper extends RelativeLayout {

    private RecyclerAdapter adapter;
    private RecyclerView view;
    private JourneyActivity.JourneyStore store;

    public RecyclerWrapper(Context context, JourneyActivity.JourneyStore store) {

        super(context);

        // store datamanager
        this.store = store;

        // create the adapter & set data
        adapter = new RecyclerAdapter(store);
        this.store.data.setAdapter(adapter);

        // create the recycler view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view = new RecyclerView(context, store);
        view.setAdapter(adapter);
        this.store.data.setRecyclerView(view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        // reverse the layout, so most up to date are last in the state's arrays
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        view.setLayoutManager(llm);

        // add the view
        this.addView(view, params);

    }


}