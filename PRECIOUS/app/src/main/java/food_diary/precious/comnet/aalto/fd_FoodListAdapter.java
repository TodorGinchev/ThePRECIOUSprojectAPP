package food_diary.precious.comnet.aalto;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class fd_FoodListAdapter extends ArrayAdapter<String> {
    public static final String TAG = "fd_FoodListAdapter";
    private final Context context;
    private final String[] foodName;
    private final String[] foodDescription;
    private final String[] foodCuantity;
    private final String[] foodCuantityKcal;
    private final Long[]   ButtonTAG;

    public fd_FoodListAdapter(Context context, String[] foodName, String[] foodDescription,
                              String[] foodCuantity, String[] foodCuantityKcal, Long[] ButtonTAG) {
        super(context, R.layout.fd_meal_adapter_layout,foodName);
        this.context = context;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.foodCuantity = foodCuantity;
        this.foodCuantityKcal = foodCuantityKcal;
        this.ButtonTAG = ButtonTAG;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fd_meal_adapter_layout, null);
        }

//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.fd_meal_adapter_layout, parent, false);
        TextView tvFoodName = (TextView) v.findViewById(R.id.tvFoodName);
        TextView tvFoodDescription = (TextView) v.findViewById(R.id.tvFoodDescription);
        TextView tvFoodCuantity = (TextView) v.findViewById(R.id.tvFoodCuantity);
        TextView tvFoodKcal = (TextView) v.findViewById(R.id.tvFoodKcal);
        ImageButton mb = (ImageButton) v.findViewById(R.id.imageButton);

        tvFoodName.setText(foodName[position]);
        tvFoodDescription.setText(foodDescription[position]);
        tvFoodCuantity.setText(foodCuantity[position]);
        tvFoodKcal.setText(foodCuantityKcal[position]);
        mb.setTag(ButtonTAG[position]);
        return v;
    }



}