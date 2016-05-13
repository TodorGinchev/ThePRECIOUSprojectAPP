package food_diary.precious.comnet.aalto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import aalto.comnet.thepreciousproject.R;

public class fd_SelectFood extends AppCompatActivity {

    public static final String TAG = "fd_SelectFood";
    private static Context mContext;

    //Declare food composition TextViews

    private static TextView tvEnerc1000;
    private static TextView tvCho;
    private static TextView tvFasat;
    private static TextView tvFats;
    private static TextView tvNa1000;
    private static TextView tvProt;
    private static TextView tvSugar;

    private static ArrayList<Double> alEnerc1000;
    private static ArrayList<Double> alCho;
    private static ArrayList<Double> alFasat;
    private static ArrayList<Double> alFats;
    private static ArrayList<Double> alNa1000;
    private static ArrayList<Double> alProt;
    private static ArrayList<Double> alSugar;

    private static Double totalEnerc1000;
    private static Double totalCho;
    private static Double totalFasat;
    private static Double totalFats;
    private static Double totalNa1000;
    private static Double totalProt;
    private static Double totalSugar;

    private AutoCompleteTextView tvAuto;

    private static ListView lvSelectedFood;
    private static ArrayList<String> selectedFoods;// = {"food1","food2","food3"};
    private static ArrayList<String> selectedCuantities;// = {"100","300","500"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_select_food);
        mContext=this;

        tvEnerc1000 = (TextView) findViewById(R.id.tvEnerc1000);
        tvCho = (TextView) findViewById(R.id.tvCho);
        tvFasat = (TextView) findViewById(R.id.tvFasat);
        tvFats = (TextView) findViewById(R.id.tvFats);
        tvNa1000 = (TextView) findViewById(R.id.tvNa1000);
        tvProt = (TextView) findViewById(R.id.tvProt);
        tvSugar = (TextView) findViewById(R.id.tvSugar);

        alEnerc1000=new ArrayList<>();
        alCho=new ArrayList<>();
        alFasat=new ArrayList<>();
        alFats=new ArrayList<>();
        alNa1000=new ArrayList<>();
        alProt=new ArrayList<>();
        alSugar=new ArrayList<>();

        tvAuto = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);

        lvSelectedFood = (ListView) findViewById(R.id.listView);
        lvSelectedFood.setScrollContainer(false);
        selectedFoods = new ArrayList<>();
        selectedCuantities = new ArrayList<>();

        resetAutoCompleteEditText();
    }

    private void resetAutoCompleteEditText(){
        //Init automplete edit text
        String str[]= getResources().getStringArray(R.array.food_names);

        ArrayAdapter<String> adp=new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,str);
        tvAuto.setThreshold(1);
        tvAuto.setAdapter(adp);
        tvAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String str[] = getResources().getStringArray(R.array.food_names);
                String selectedFood = tvAuto.getText().toString();
                int index = -1;
                for (int i = 0; i < str.length; i++) {
                    if (str[i].equals(selectedFood)) {
                        index = i;
                        break;
                    }
                }

                Log.i(TAG, "Id=_" + index);


                alEnerc1000.add((double) (getResources().getIntArray(R.array.food_db_enerc1000)[index] / 1000));
                alCho.add((double) getResources().getIntArray(R.array.food_db_cho)[index] / 100000);
                alFasat.add((double) getResources().getIntArray(R.array.food_db_fasat)[index] / 1000000);
                alFats.add((double) getResources().getIntArray(R.array.food_db_fats)[index] / 1000000);
                alNa1000.add((double) getResources().getIntArray(R.array.food_db_na1000)[index] / 1000);
                alProt.add((double) getResources().getIntArray(R.array.food_db_prot)[index] / 1000000);
                alSugar.add((double) getResources().getIntArray(R.array.food_db_sugar)[index] / 1000000);

//                tvEnerc1000.setText(getString(R.string.enerc) + (double) (getResources().getIntArray(R.array.food_db_enerc1000)[index] / 1000) + "kJ");
//                tvCho.setText(getString(R.string.cho) + (double) getResources().getIntArray(R.array.food_db_cho)[index] / 1000000 + "g");
//                tvFasat.setText(getString(R.string.fasat) + (double) getResources().getIntArray(R.array.food_db_fasat)[index] / 1000000 + "g");
//                tvFats.setText(getString(R.string.fat) + (double) getResources().getIntArray(R.array.food_db_fats)[index] / 1000000 + "g");
//                tvNa1000.setText(getString(R.string.na) + (double) getResources().getIntArray(R.array.food_db_na1000)[index] / 1000 + "mg");
//                tvProt.setText(getString(R.string.prot) + (double) getResources().getIntArray(R.array.food_db_prot)[index] / 1000000 + "g");
//                tvSugar.setText(getString(R.string.sugar) + (double) getResources().getIntArray(R.array.food_db_sugar)[index] / 1000000 + "g");

                selectedFoods.add(selectedCuantities.size(),tvAuto.getText().toString());
                selectedCuantities.add(selectedCuantities.size(), "");

                fd_SelectFoodAdapter adapterSelFood = new fd_SelectFoodAdapter(mContext, selectedFoods.toArray(new String[selectedFoods.size()]),
                        selectedCuantities.toArray(new String[selectedCuantities.size()]));
                lvSelectedFood.setAdapter(adapterSelFood);
                setListViewHeightBasedOnChildren(lvSelectedFood);

                tvAuto.setText("");
                updateNutritionalInfo();

            }
        });
    }

    public void onCancelTouched(View v){
        finish();
    }
    public void onSaveTouched(View v) {
        for (int i=0; i<selectedCuantities.size();i++) {
            if (selectedCuantities.get(i).equals("") || Double.parseDouble(selectedCuantities.get(i)) == 0) {
                Toast.makeText(this, R.string.empty_param, Toast.LENGTH_LONG).show();
                return;
            }
        }
        finish();
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     *
     */
    public static void removeItem(int position){
        Log.i(TAG,"REMOVED"+position);
        selectedFoods.remove(position);
        selectedCuantities.remove(position);
        fd_SelectFoodAdapter adapterSelFood = new fd_SelectFoodAdapter(mContext, selectedFoods.toArray(new String[selectedFoods.size()]),
                selectedCuantities.toArray(new String[selectedCuantities.size()]));
        lvSelectedFood.setAdapter(adapterSelFood);
        setListViewHeightBasedOnChildren(lvSelectedFood);
        updateNutritionalInfo();
    }

    /**
     *
     */
//    public static void updateItemCuantity(int position,String quantity){
//        Log.i(TAG, "UPDATE CUANTITY_"+position+"=" + quantity);
//        selectedCuantities.set(position,quantity);
//        updateNutritionalInfo();
//    }

    public static void updateItemCuantity(String[] quantity){
        if(quantity.length!=selectedCuantities.size()) {
            Log.e(TAG, "updateItemCuantity: sizes are not the same");
            return;
        }
        for(int i=0;i<quantity.length;i++)
            selectedCuantities.set(i,quantity[i]);
        updateNutritionalInfo();
    }

    /**
     *
     */
    @SuppressWarnings("all")
    public static void updateNutritionalInfo(){
        Log.i(TAG, "updateNutritionalInfo");
        totalEnerc1000=0.0;
        totalCho=0.0;
        totalFasat=0.0;
        totalFats=0.0;
        totalNa1000=0.0;
        totalProt=0.0;
        totalSugar=0.0;
        for (int i=0; i<selectedCuantities.size();i++) {
            Log.i(TAG,"selectedCuantities at "+i+"="+selectedCuantities.get(i));
            if (selectedCuantities.get(i).equals("") || Double.parseDouble(selectedCuantities.get(i)) == 0) {
                Log.i(TAG, "updateNutritionalInfo "+i+"=_"+selectedCuantities.get(i)+"_");
                Log.i(TAG, "updateNutritionalInfo RETURN");
                continue;
            }
            else{
                totalEnerc1000 += Double.parseDouble(selectedCuantities.get(i))*alEnerc1000.get(i)/100;
                totalCho += Double.parseDouble(selectedCuantities.get(i))*alCho.get(i)/100;
                totalFasat += Double.parseDouble(selectedCuantities.get(i))*alFasat.get(i)/100;
                totalFats += Double.parseDouble(selectedCuantities.get(i))*alFats.get(i)/100;
                totalNa1000 += Double.parseDouble(selectedCuantities.get(i))*alNa1000.get(i)/100;
                totalProt += Double.parseDouble(selectedCuantities.get(i))*alProt.get(i)/100;
                totalSugar += Double.parseDouble(selectedCuantities.get(i))*alSugar.get(i)/100;
            }
        }


        tvEnerc1000.setText(mContext.getString(R.string.enerc) + totalEnerc1000 + "kJ");
        tvCho.setText(mContext.getString(R.string.cho) + totalCho + "g");
        tvFasat.setText(mContext.getString(R.string.fasat) + totalFasat + "g");
        tvFats.setText(mContext.getString(R.string.fat) + totalFats + "g");
        tvNa1000.setText(mContext.getString(R.string.na) + totalNa1000 + "mg");
        tvProt.setText(mContext.getString(R.string.prot) + totalProt + "g");
        tvSugar.setText(mContext.getString(R.string.sugar) + totalSugar + "g");
    }
}

