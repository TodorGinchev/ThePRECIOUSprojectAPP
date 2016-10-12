package firstbeatalbum.precious.comnet.aalto;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import aalto.comnet.thepreciousproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by christopher on 12.09.16.
 **/

public class FirstBeatAlbumAdapter extends FirstBeatAlbumView.Adapter<FirstBeatAlbumViewHolder> {

    FirstBeatAlbumActivity activity;
    Bitmap testBitmap;

    public FirstBeatAlbumAdapter(FirstBeatAlbumActivity activity, ArrayList<String> fileNames) {
        super();
        this.activity = activity;
        testBitmap = getBitmapFromAsset(activity, "report.bmp");
    }

    @Override
    public FirstBeatAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_album_recycler_item, parent, false);
        return new FirstBeatAlbumViewHolder(this.activity, view);
    }

    @Override
    public void onBindViewHolder(FirstBeatAlbumViewHolder holder, int position) {
        holder.update("01.01.2001", testBitmap);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }
        return bitmap;
    }
}
