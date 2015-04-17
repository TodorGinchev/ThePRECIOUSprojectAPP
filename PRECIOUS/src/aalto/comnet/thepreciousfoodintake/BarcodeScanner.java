package aalto.comnet.thepreciousfoodintake;


import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class BarcodeScanner extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity);        
        findViewById(R.id.scan_product).setOnClickListener(scanProduct);
        IntentIntegrator integrator = new IntentIntegrator(BarcodeScanner.this);
        integrator.addExtra("SCAN_WIDTH", 800);
        integrator.addExtra("SCAN_HEIGHT", 200);
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
        integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

    
    
    private final View.OnClickListener scanProduct = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          IntentIntegrator integrator = new IntentIntegrator(BarcodeScanner.this);
          integrator.addExtra("SCAN_WIDTH", 800);
          integrator.addExtra("SCAN_HEIGHT", 200);
          integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
          integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
          integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
        }
      };
      
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	    String barcode = (result.toString());
	    barcode = barcode.substring(barcode.indexOf("Contents:"));
	    barcode = barcode.substring(barcode.indexOf(" "));
	    barcode = barcode.substring(0,barcode.indexOf("\n"));
	    TextView tv = (TextView) findViewById(R.id.textView1);
	    tv.setText(barcode);
	    
	    Uri uri = Uri.parse("http://www.google.com/search?as_q="+barcode);//"http://www.google.com/#q=fish");
	    Intent i = new Intent(Intent.ACTION_VIEW, uri);
	    startActivity(i);
	  }
      
}
