package firstbeatalbum.precious.comnet.aalto;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aalto.comnet.thepreciousproject.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by christopher on 12.09.16.
 **/

public class FirstBeatAlbumAdapter extends FirstBeatAlbumView.Adapter<FirstBeatAlbumViewHolder> {

    FirstBeatAlbumActivity activity;
    ArrayList<Bitmap> bitmapArray;
    ArrayList<String> fileNamesList;
    int totalImages = 0;

    public FirstBeatAlbumAdapter(FirstBeatAlbumActivity activity, ArrayList<String> fileNames) {
        super();
        this.activity = activity;
        fileNamesList = fileNames;
        bitmapArray = new ArrayList<>();
        Bitmap bitmap;

        for (int count= 0; count < fileNamesList.size(); count ++) {
             bitmap = getBitmapFromAsset(activity, fileNamesList.get(count));
             if (bitmap != null)
                bitmapArray.add(count,bitmap);
             else
                fileNamesList.remove(count);
        }
        totalImages = bitmapArray.size();
    }

    @Override
    public FirstBeatAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_album_recycler_item, parent, false);
        return new FirstBeatAlbumViewHolder(this.activity, view);
    }

    @Override
    public void onBindViewHolder(FirstBeatAlbumViewHolder holder, int position) {
        if (position <= totalImages -1) {
            holder.update(fileNamesList.get(position), bitmapArray.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return totalImages;
    }

    public static Bitmap getBitmapFromAsset(Context context, String fileName) {
          Bitmap bitmap = null;
         try {
             FileInputStream fIn = context.openFileInput(fileName);
             bitmap = BitmapFactory.decodeStream(fIn);
        } catch (IOException e) {
            // handle exception
        }
        return bitmap;
    }
}
