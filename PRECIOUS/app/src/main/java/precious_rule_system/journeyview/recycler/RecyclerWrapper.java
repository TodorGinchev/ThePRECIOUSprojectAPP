package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RelativeLayout;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerWrapper extends RelativeLayout {

    private RecyclerAdapter adapter;
    private RecyclerView view;
    private ui_MainActivity.JourneyStore store;

    public RecyclerWrapper(Context context, ui_MainActivity.JourneyStore store) {

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

        // create the layout manager
        RecyclerLayoutManager llm = new RecyclerLayoutManager(context, store);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        view.setLayoutManager(llm);

        // add the view
        this.addView(view, params);

    }


}
