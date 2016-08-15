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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;

public class fd_SelectFood extends AppCompatActivity {

    public static final String TAG = "fd_SelectFood";
    private static Context mContext;

    //Declare food composition TextViews

    private static Button bEnerc1000;
//    private static Button bCho;
    private static Button bFasat;
    private static Button bFats;
    private static Button bNa1000;
//    private static Button bProt;
    private static Button bSugar;

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

    private static int selectedMealType=-1; //1:breakfast, 2:morning snack, 3:lunch, 4:evening snack, 5:dinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_select_food);
        mContext=this;

        bEnerc1000 = (Button) findViewById(R.id.bEnerc1000);
//        bCho = (Button) findViewById(R.id.bCho);
        bFasat = (Button) findViewById(R.id.bFasat);
        bFats = (Button) findViewById(R.id.bFats);
        bNa1000 = (Button) findViewById(R.id.bNa1000);
//        bProt = (Button) findViewById(R.id.bProt);
        bSugar = (Button) findViewById(R.id.bSugar);

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

        Calendar c = Calendar.getInstance();
        int hourOFDay = c.get(Calendar.HOUR_OF_DAY);
        if(hourOFDay<=10){
            selectedMealType=1; //breakfast
            updateSelectedMeal();
        }
        else if(hourOFDay<=12){
            selectedMealType=2; //morning snack
            updateSelectedMeal();
        }
        else if(hourOFDay<=15){
            selectedMealType=3; //lunch
            updateSelectedMeal();
        }
        else if(hourOFDay<=18){
            selectedMealType=4; //evening snack
            updateSelectedMeal();
        }
        else {
            selectedMealType=5; //dinner
            updateSelectedMeal();
        }
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);

        }
        super.onPause();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e) {
            Log.e(TAG, " ", e);
        }
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


                alEnerc1000.add((double) (getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000));
                alCho.add((double) getResources().getIntArray(R.array.food_db_cho)[index] / 100000);
                alFasat.add((double) getResources().getIntArray(R.array.food_db_fasat)[index] / 1000000);
                alFats.add((double) getResources().getIntArray(R.array.food_db_fats)[index] / 1000000);
                alNa1000.add((double) getResources().getIntArray(R.array.food_db_na_1000)[index] / 1000);
                alProt.add((double) getResources().getIntArray(R.array.food_db_prot)[index] / 1000000);
                alSugar.add((double) getResources().getIntArray(R.array.food_db_sugar)[index] / 1000000);

//                tvEnerc1000.setText(getString(R.string.enerc) + (double) (getResources().getIntArray(R.array.food_db_enerc1000)[index] / 1000) + "kJ");
//                tvCho.setText(getString(R.string.cho) + (double) getResources().getIntArray(R.array.food_db_cho)[index] / 1000000 + "g");
//                tvFasat.setText(getString(R.string.fasat) + (double) getResources().getIntArray(R.array.food_db_fasat)[index] / 1000000 + "g");
//                tvFats.setText(getString(R.string.fat) + (double) getResources().getIntArray(R.array.food_db_fats)[index] / 1000000 + "g");
//                tvNa1000.setText(getString(R.string.na) + (double) getResources().getIntArray(R.array.food_db_na1000)[index] / 1000 + "mg");
//                tvProt.setText(getString(R.string.prot) + (double) getResources().getIntArray(R.array.food_db_prot)[index] / 1000000 + "g");
//                tvSugar.setText(getString(R.string.sugar) + (double) getResources().getIntArray(R.array.food_db_sugar)[index] / 1000000 + "g");

                selectedFoods.add(selectedCuantities.size(), tvAuto.getText().toString());
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
        //Save in DB
        Bundle extras = getIntent().getExtras();

        for (int i=0; i<selectedCuantities.size(); i++){
            Calendar c_aux = Calendar.getInstance();
            Calendar c_aux2 = Calendar.getInstance();
            c_aux2.setTimeInMillis(extras.getLong("timestamp"));
            c_aux.set(Calendar.YEAR, c_aux2.get(Calendar.YEAR));
            c_aux.set(Calendar.MONTH,c_aux2.get(Calendar.MONTH));
            c_aux.set(Calendar.DAY_OF_MONTH,c_aux2.get(Calendar.DAY_OF_MONTH));
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertFood(c_aux.getTimeInMillis(), selectedMealType, selectedFoods.get(i), Integer.parseInt(selectedCuantities.get(i)), -1);
//            ui_MainActivity.dbhelp.updateFood(c_aux.getTimeInMillis(), selectedMealType, selectedFoods.get(i), Integer.parseInt(selectedCuantities.get(i)), -1);
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
        Log.i(TAG,"REMOVED item at position "+position);
        for (int i=0; i<selectedCuantities.size();i++)
            Log.i(TAG, "before removing selectedCuantities at " + i + " is " + selectedCuantities.get(i));
        selectedFoods.remove(position);
        selectedCuantities.remove(position);
        alEnerc1000.remove(position);
        alCho.remove(position);
        alFasat.remove(position);
        alFats.remove(position);
        alNa1000.remove(position);
        alProt.remove(position);
        alSugar.remove(position);

        for (int i=0; i<selectedCuantities.size();i++)
            Log.i(TAG, "selectedCuantities at " + i + " is " + selectedCuantities.get(i));

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
        Log.i(TAG, "updateItemCuantity");
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
        int totalCuantity=0;
        if(selectedCuantities==null)
            return;
        for(int i=0;i<selectedCuantities.size();i++) {
            if(selectedCuantities.get(i)==null)
                continue;
            if (!selectedCuantities.get(i).equals(""))
                totalCuantity += Integer.parseInt(selectedCuantities.get(i));
        }
        if(totalCuantity==0)
            return;


        Log.i(TAG, "updateNutritionalInfo");
        totalEnerc1000=0.0;
        totalCho=0.0;
        totalFasat=0.0;
        totalFats=0.0;
        totalNa1000=0.0;
        totalProt=0.0;
        totalSugar=0.0;
        for (int i=0; i<selectedCuantities.size();i++) {
//            Log.i(TAG,"selectedCuantities at "+i+"="+selectedCuantities.get(i));
            if(selectedCuantities.get(i)==null)
                continue;
            if (selectedCuantities.get(i).equals("") || Double.parseDouble(selectedCuantities.get(i)) == 0) {
//                Log.i(TAG, "updateNutritionalInfo "+i+"=_"+selectedCuantities.get(i)+"_");
//                Log.i(TAG, "updateNutritionalInfo RETURN");
                continue;
            }
            else{
                Log.i(TAG,"Item:"+i+" Cal:"+alEnerc1000.get(i));
                totalEnerc1000 += Double.parseDouble(selectedCuantities.get(i))*alEnerc1000.get(i)/100;
                Log.i(TAG,"Item:"+i+" Total Cal:"+totalEnerc1000);
                totalCho += Double.parseDouble(selectedCuantities.get(i))*alCho.get(i)/100;
                totalFasat += Double.parseDouble(selectedCuantities.get(i))*alFasat.get(i)/100;
                totalFats += Double.parseDouble(selectedCuantities.get(i))*alFats.get(i)/100;
                totalNa1000 += Double.parseDouble(selectedCuantities.get(i))*alNa1000.get(i)/100;
                totalProt += Double.parseDouble(selectedCuantities.get(i))*alProt.get(i)/100;
                totalSugar += Double.parseDouble(selectedCuantities.get(i))*alSugar.get(i)/100;
            }
        }

        double fatRatio = 100*((double)totalFats)/((double)totalCuantity);
        double fasatRatio = 100*((double)totalFasat)/((double)totalCuantity);
        double sugarRatio = 100*((double)totalSugar)/((double)totalCuantity);
        double naRatio = 100*((double)totalNa1000/1000)/((double)totalCuantity);


        bEnerc1000.setText(mContext.getString(R.string.enerc2)+"\n" + String.format( "%.0f", totalEnerc1000 ));
//        bCho.setText(mContext.getString(R.string.cho) + String.format( "%.2f", totalCho ) + "g");
        bFasat.setText(mContext.getString(R.string.fasat2)+"\n" + String.format( "%.2f", totalFasat ) + "g"+"\n"+(int)fasatRatio+"g/100g");
        bFats.setText(mContext.getString(R.string.fat2)+"\n" + String.format( "%.2f", totalFats ) + "g"+"\n"+(int)fatRatio+"g/100g");
        bNa1000.setText(mContext.getString(R.string.na2)+"\n" + String.format( "%.2f", totalNa1000/1000 ) + "g"+"\n"+(int)naRatio+"g/100g");
//        bProt.setText(mContext.getString(R.string.prot) + String.format( "%.2f", totalProt ) + "g");
        bSugar.setText(mContext.getString(R.string.sugar2)+"\n"  + String.format( "%.2f", totalSugar ) + "g"+"\n"+(int)sugarRatio+"g/100g");

        //Change background colors depending on %
//        Log.i(TAG, "RATIO=" + fatRatio);

        //FAT
        if(fatRatio<=3)
            bFats.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
        else if(fatRatio<=17.5)
            bFats.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
        else
            bFats.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
        //FASAT
        if(fasatRatio<=1.5)
            bFasat.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
        else if(fasatRatio<=5)
            bFasat.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
        else
            bFasat.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
        //SUGAR
        if(sugarRatio<=5)
            bSugar.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
        else if(sugarRatio<=22.5)
            bSugar.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
        else
            bSugar.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
        //SALT
        if(naRatio<=0.3)
            bNa1000.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
        else if(naRatio<=1.5)
            bNa1000.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
        else
            bNa1000.setBackground(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
    }

    public void updateSelectedMeal(){
        Button b1 = (Button) findViewById(R.id.buttonBreakfast);
        Button b2 = (Button) findViewById(R.id.buttonMorningSnack);
        Button b3 = (Button) findViewById(R.id.buttonLunch);
        Button b4 = (Button) findViewById(R.id.buttonEveningSnack);
        Button b5 = (Button) findViewById(R.id.buttonDinner);
        b1.setBackgroundColor(getResources().getColor(R.color.fd_unselected_tab_background));
        b2.setBackgroundColor(getResources().getColor(R.color.fd_unselected_tab_background));
        b3.setBackgroundColor(getResources().getColor(R.color.fd_unselected_tab_background));
        b4.setBackgroundColor(getResources().getColor(R.color.fd_unselected_tab_background));
        b5.setBackgroundColor(getResources().getColor(R.color.fd_unselected_tab_background));

        switch (selectedMealType){
            case 1 :  b1.setBackgroundColor(getResources().getColor(R.color.fd_selected_tab_background));break;
            case 2 :  b2.setBackgroundColor(getResources().getColor(R.color.fd_selected_tab_background));break;
            case 3 :  b3.setBackgroundColor(getResources().getColor(R.color.fd_selected_tab_background));break;
            case 4 :  b4.setBackgroundColor(getResources().getColor(R.color.fd_selected_tab_background));break;
            case 5 :  b5.setBackgroundColor(getResources().getColor(R.color.fd_selected_tab_background));break;
            default: break;
        }
    }

    public void updateSelectedMealType(View v){
        switch (v.getId()){
            case R.id.buttonBreakfast : selectedMealType=1;break;
            case R.id.buttonMorningSnack : selectedMealType=2;break;
            case R.id.buttonLunch : selectedMealType=3;break;
            case R.id.buttonEveningSnack : selectedMealType=4;break;
            case R.id.buttonDinner : selectedMealType=5;break;
            default: break;
        }
        updateSelectedMeal();
    }
}