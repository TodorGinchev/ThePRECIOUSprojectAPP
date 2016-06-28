package food_diary.precious.comnet.aalto;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aalto.comnet.thepreciousproject.R;


public class fd_SelectFoodAdapter extends ArrayAdapter<String> {
    public static final String TAG = "fd_SelectFoodAdapter";
    private final Context context;
    private final String[] foodName;
    private final String[] foodCuantity;
    private static EditText[] tvFoodCuantity;
    private Spinner [] spinnerFooodCuantity;

    public fd_SelectFoodAdapter(Context context, String[] foodName,
                                String[] foodCuantity) {
        super(context, R.layout.fd_meal_adapter_layout,foodName);
        this.context = context;
        this.foodName = foodName;
        this.foodCuantity = foodCuantity;
        tvFoodCuantity = new EditText[foodCuantity.length];
        spinnerFooodCuantity = new Spinner[foodCuantity.length];

        for (int i=0; i<foodCuantity.length;i++)
            Log.i(TAG, "At " + i + ", name: " + foodName[i] + ", grams: " + foodCuantity[i]);
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
        spinnerFooodCuantity[position] = (Spinner) v.findViewById(R.id.spinnerFooodCuantity);
        List<String> list = new ArrayList<String>();
        list.add("g");
        list.add(context.getResources().getString(R.string.fd_plato));
        list.add(context.getResources().getString(R.string.fd_porcion));
        list.add(context.getResources().getString(R.string.fd_cucharadita));
        list.add(context.getResources().getString(R.string.fd_cucharada));
        list.add(context.getResources().getString(R.string.fd_vaso_agua));
        list.add(context.getResources().getString(R.string.fd_taza_te));
        list.add(context.getResources().getString(R.string.fd_taza_cafe));
        list.add(context.getResources().getString(R.string.fd_tazon_cafe));
        list.add(context.getResources().getString(R.string.fd_copa_vino));
        list.add(context.getResources().getString(R.string.fd_copita));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFooodCuantity[position].setAdapter(dataAdapter);


        spinnerFooodCuantity[position].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Force view to update
                tvFoodCuantity[position].setText(tvFoodCuantity[position].getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Force view to update
                tvFoodCuantity[position].setText(tvFoodCuantity[position].getText());
            }
        });

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
//                Log.i(TAG,"tvFoodCuantity size is=_"+tvFoodCuantity.length);
                for(int i=0;i<tvFoodCuantity.length;i++) {
//                    Log.i(TAG,"Accessing tvFoodCuantity at "+i);
                    if(tvFoodCuantity[i]!=null) {
                        int portionsize;
                        switch (spinnerFooodCuantity[i].getSelectedItemPosition()){
                            case 0  :   portionsize=1;      break;
                            case 1  :   portionsize=200;    break;
                            case 2  :   portionsize=30;     break;
                            case 3  :   portionsize=5;      break;
                            case 4  :   portionsize=15;     break;
                            case 5  :   portionsize=200;    break;
                            case 6  :   portionsize=150;    break;
                            case 7  :   portionsize=100;    break;
                            case 8  :   portionsize=250;    break;
                            case 9  :   portionsize=150;    break;
                            case 10 :   portionsize=50;     break;
                            default : portionsize=1;
                        }
                        try {
                            aux[i] = Integer.parseInt(tvFoodCuantity[i].getText().toString()) * portionsize + "";
                        }catch (Exception e){
                            aux[i]=0+"";
                        }
                    }
//                    else return;
                }

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
