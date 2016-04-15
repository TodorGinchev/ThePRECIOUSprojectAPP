package food_diary.precious.comnet.aalto;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class fd_FoodListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] foodName;
    private final String[] foodDescription;
    private final String[] foodCuantity;
    private final String[] foodCuantityKcal;

    public fd_FoodListAdapter(Context context, String[] foodName, String[] foodDescription,
                              String[] foodCuantity, String[] foodCuantityKcal) {
        super(context, R.layout.fd_meal_adapter_layout,foodName);
        this.context = context;
        this.foodName = foodName;
        this.foodDescription = foodDescription;
        this.foodCuantity = foodCuantity;
        this.foodCuantityKcal = foodCuantityKcal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fd_meal_adapter_layout, parent, false);
        TextView tvFoodName = (TextView) rowView.findViewById(R.id.tvFoodName);
        TextView tvFoodDescription = (TextView) rowView.findViewById(R.id.tvFoodDescription);
        TextView tvFoodCuantity = (TextView) rowView.findViewById(R.id.tvFoodCuantity);
        TextView tvFoodKcal = (TextView) rowView.findViewById(R.id.tvFoodKcal);

        tvFoodName.setText(foodName[position]);
        tvFoodDescription.setText(foodDescription[position]);
        tvFoodCuantity.setText(foodCuantity[position]);
        tvFoodKcal.setText(foodCuantityKcal[position]);
        return rowView;
    }
}