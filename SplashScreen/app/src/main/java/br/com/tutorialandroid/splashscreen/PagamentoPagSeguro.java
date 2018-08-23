package br.com.tutorialandroid.splashscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PagamentoPagSeguro extends AppCompatActivity {

    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagamento_pag_seguro);

        wv = (WebView) findViewById(R.id.wv1);

        WebSettings ws = wv.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);

        wv.loadUrl("https://www.letsgosobral.com/letsgo/formulariopaypal.php");

        wv.setWebViewClient(new WebViewClient());

    }

    @Override
    public void onBackPressed() {
        if(wv.canGoBack()){
            wv.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
