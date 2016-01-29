package aalto.comnet.thepreciousfoodintake;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import theprecioussandbox.comnet.aalto.precious.R;


public class BarcodeScanner extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        //findViewById(R.id.scan_product).setOnClickListener(scanProduct);
        IntentIntegrator integrator = new IntentIntegrator(BarcodeScanner.this);
        integrator.addExtra("SCAN_WIDTH", 800);
        integrator.addExtra("SCAN_HEIGHT", 200);
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
        integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
	    
    }
    
//    private static final String HOST_NAME = "api.outpan.com";
//    private static final String URL = "http://www.outpan.com/api/get_product.php?barcode=5449000000996";
//    private static final String USER_NAME = "b428a2419f57a7dfa196e99b2ba23e9a:";
//    //private static final String PASSWORD = ":";
//    private void basicAuthDemo() {
//    	DefaultHttpClient httpclient = new DefaultHttpClient();
//	      int SDK_INT = android.os.Build.VERSION.SDK_INT;
//	      if (SDK_INT > 8) 
//	      {
//	    	  	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//	                  .permitAll().build();
//	          	StrictMode.setThreadPolicy(policy);
//		        try {
//		            httpclient.getCredentialsProvider().setCredentials(
//		                    new AuthScope(HOST_NAME, 443),
//		                    new UsernamePasswordCredentials(USER_NAME));
//		 
//		            HttpGet httpget = new HttpGet(URL);
//		 
//		            System.out.println("executing request" + httpget.getRequestLine());
//		            HttpResponse response = httpclient.execute(httpget);
//		            HttpEntity entity = response.getEntity();
//		 
//		            System.out.println("----------------------------------------");
//		            System.out.println();
//		            if (entity != null) {
//		                System.out.println("Response content length: " + entity.getContentLength());
//		                System.out.println(EntityUtils.toString(entity));
//		            }
//		        } catch(Exception e){
//		        	e.printStackTrace();
//		        	Log.e("TAG","",e);
//		        }finally {
//		            // When HttpClient instance is no longer needed,
//		            // shut down the connection manager to ensure
//		            // immediate deallocation of all system resources
//		            httpclient.getConnectionManager().shutdown();
//		        }
//	      }
//    }

    
    
//    private final View.OnClickListener scanProduct = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          IntentIntegrator integrator = new IntentIntegrator(BarcodeScanner.this);
//          integrator.addExtra("SCAN_WIDTH", 800);
//          integrator.addExtra("SCAN_HEIGHT", 200);
//          integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
//          integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
//          integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
//        }
//      };
      
      public void onScan(View view){
          IntentIntegrator integrator = new IntentIntegrator(BarcodeScanner.this);
          integrator.addExtra("SCAN_WIDTH", 800);
          integrator.addExtra("SCAN_HEIGHT", 200);
          integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
          integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
          integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
      }
    
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	    String barcode = (result.toString());
	    barcode = barcode.substring(barcode.indexOf("Contents:"));
	    barcode = barcode.substring(barcode.indexOf(" "));
	    barcode = barcode.substring(0,barcode.indexOf("\n"));
	    TextView tv = (TextView) findViewById(R.id.textView1);
	    tv.setText(barcode);
	    
//	    Intent launchactivity= new Intent(this,BrowserActivity.class);     
//	    launchactivity.putExtra("url", "http://www.google.com/search?as_q="+barcode);
//	    startActivity(launchactivity);  
	    WebView webView = (WebView) findViewById(R.id.web_view);
	    webView.setWebViewClient(new WebViewClient());
	    webView.loadUrl("http://www.ean-search.org/perl/ean-search.pl?q="+barcode);
	    
//	    Uri uri = Uri.parse("http://www.google.com/search?as_q="+barcode);//"http://www.google.com/#q=fish");
//	    Intent i = new Intent(Intent.ACTION_VIEW, uri);
//	    startActivity(i);
	  }
	  
		 public void saveInfo(View view){
			 Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
			 finish();
		 }
      
}
