package aalto.comnet.thepreciousjavascript;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class JavascriptTest  extends Activity {
	
	private static final String URL = "file:///android_asset/index.html";
	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.javascript_activity);
		mWebView = (WebView) findViewById(R.id.webview); 
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		refreshWebView();

	}
	
	private void refreshWebView() {
		mWebView.loadUrl(URL);
	}
}
