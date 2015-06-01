package aalto.comnet.thepreciousfoodintake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class ManualInput extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.manual_food_input);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, FoodDDBB.food_name);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.food_list_autocomplete);
        textView.setAdapter(adapter);
        
//        textView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            	updateNutritionalInfo(pos); 
//            }
//            public void onNothingSelected(AdapterView<?> parent) {
//            	updateNutritionalInfo(0); 
//            }
//        });
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	updateNutritionalInfo(position);
            }
        });

    }
    
	public void updateNutritionalInfo(int pos){
    	TextView tv = (TextView) findViewById(R.id.tvNutritionalInfo);
        AssetManager assetManager = getAssets();
        InputStream input;
        try{
	        input = assetManager.open("food_ddbb.txt");  
	        BufferedReader entrada = new BufferedReader(  new InputStreamReader(input));
	        int i = 0;
	        while (i<=pos){
	        	entrada.readLine();
	        	i++;
	        }
	        String line = entrada.readLine();  
	        String aux="";
	        for (int j=0; j<FoodDDBB.food_data.length-1;j++){
	        	if(j==FoodDDBB.food_data.length-2)
	        		aux = aux.concat(line+FoodDDBB.food_data[j]);
	        	else{
		        	aux = aux.concat(FoodDDBB.food_data[j]+": "+line.substring(0,line.indexOf(","))+" "+FoodDDBB.food_data_metrics[j]+"\n");
		        	line = line.substring(line.indexOf(",")+1);
	        	}
	        }
	        tv.setText(aux);
	        input.close(); 
        }catch (Exception e){
        	Log.e("TAG","",e);
        }        
	}
}