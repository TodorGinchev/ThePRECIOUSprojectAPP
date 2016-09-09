package precious_rule_system.journeyview;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 09.09.16.
 */

public class JourneyRewardPopupDialog extends DialogFragment {

    RewardEvent event;

    static JourneyRewardPopupDialog newInstance(RewardEvent event) {
        JourneyRewardPopupDialog dialog = new JourneyRewardPopupDialog();
        dialog.setArguments(event.toBundle());
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = new RewardEvent(getArguments());
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 300;
        params.height = 600;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}
