package firstbeatalbum.precious.comnet.aalto;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import aalto.comnet.thepreciousproject.R;

/**
 * Created by christopher on 12.09.16.
 */

public class FirstBeatAlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView imageView;
    TextView textView;
    FirstBeatAlbumActivity activity;
    Bitmap bitmap;

    public FirstBeatAlbumViewHolder(FirstBeatAlbumActivity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        textView = (TextView) itemView.findViewById(R.id.holderTitle);
        imageView = (ImageView) itemView.findViewById(R.id.holderImage);
        itemView.setOnClickListener(this);
    }

    public void update(String text, Bitmap bitmap) {
        textView.setText(text);
        imageView.setImageBitmap(bitmap);
        this.bitmap = bitmap;
    }

    @Override
    public void onClick(View v) {
        activity.showImageFullScreen(bitmap);
    }
}
