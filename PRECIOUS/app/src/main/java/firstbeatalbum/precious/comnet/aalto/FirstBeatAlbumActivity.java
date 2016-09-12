package firstbeatalbum.precious.comnet.aalto;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import aalto.comnet.thepreciousproject.R;

/**
 * Created by christopher on 12.09.16.
 */

public class FirstBeatAlbumActivity extends AppCompatActivity {

    public static final String TAG = "firstbeat-album";
    public static final int color = R.color.confidenceRuler;
    public static final int drawable = R.drawable.confidence_ruler_back;
    public static final String headerText = "Firstbeat Album";

    private FirstBeatAlbumView albumView;
    private FirstBeatAlbumAdapter adapter;
    private RelativeLayout fullscreenContainer;
    private FirstBeatAlbumImageView fullscreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // set the content view
        setContentView(R.layout.fb_album_activity);

        // gather the variables specific for this activity
        int color = getResources().getColor(FirstBeatAlbumActivity.color);
        String text = FirstBeatAlbumActivity.headerText;
        int drawable = FirstBeatAlbumActivity.drawable;

        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }

        // setup the toolbar
        setupToolbar(text, color, drawable);

        // setup the main view
        setupView();

        // setup popupview
        setupPopup();
    }

    private void setupView() {
        adapter = new FirstBeatAlbumAdapter(this);
        albumView = (FirstBeatAlbumView) findViewById(R.id.container);
        albumView.init(this, adapter);
    }

    private void setupToolbar(String text, int color, int drawable) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(text);
        toolbar.setTitleTextColor(color);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPopup() {
        fullscreenContainer = (RelativeLayout) findViewById(R.id.fullscreen_container);
        fullscreenContainer.setBackgroundColor(Color.WHITE);
        fullscreenImage = (FirstBeatAlbumImageView) fullscreenContainer.findViewById(R.id.fullscreen_image);
        fullscreenContainer.setVisibility(View.INVISIBLE);

        Button btn = (Button) fullscreenContainer.findViewById(R.id.fullscreen_close);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImageFullScreen();
            }
        });
    }

    public void showImageFullScreen(Bitmap bitmap) {
        fullscreenImage.setImageBitmap(bitmap);
        fullscreenContainer.setVisibility(View.VISIBLE);
        Log.i(TAG, "Showing full screen Image");
    }

    public void hideImageFullScreen() {
        fullscreenContainer.setVisibility(View.INVISIBLE);
        Log.i(TAG, "Hiding full screen Image");
    }

}
