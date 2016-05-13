package food_diary.precious.comnet.aalto;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class fd_SelectFoodAdapter extends ArrayAdapter<String> {
    public static final String TAG = "fd_SelectFoodAdapter";
    private final Context context;
    private final String[] foodName;
    private final String[] foodCuantity;
    private static EditText[] tvFoodCuantity;

    public fd_SelectFoodAdapter(Context context, String[] foodName,
                                String[] foodCuantity) {
        super(context, R.layout.fd_meal_adapter_layout,foodName);
        this.context = context;
        this.foodName = foodName;
        this.foodCuantity = foodCuantity;
        this.tvFoodCuantity = new EditText[foodName.length];

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fd_select_food_adapter_layout, null);
        }

//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.fd_meal_adapter_layout, parent, false);
        TextView tvFoodName = (TextView) v.findViewById(R.id.tvFoodName);
        tvFoodName.setText(foodName[position]);


        tvFoodCuantity[position] = (EditText) v.findViewById(R.id.tvFoodCuantity);
        tvFoodCuantity[position].setText(foodCuantity[position]);
        tvFoodCuantity[position].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
//                fd_SelectFood.updateItemCuantity(position, s.toString());
                String [] aux = new String[tvFoodCuantity.length];
                for(int i=0;i<tvFoodCuantity.length;i++)
                    aux[i]=tvFoodCuantity[i].getText().toString();
                fd_SelectFood.updateItemCuantity(aux);
            }
        });


        ImageButton im = (ImageButton) v.findViewById(R.id.imageButton);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fd_SelectFood.removeItem(position);
            }
        });

        return v;
    }




}