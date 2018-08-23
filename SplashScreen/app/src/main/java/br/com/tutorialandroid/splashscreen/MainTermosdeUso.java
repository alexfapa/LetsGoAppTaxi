package br.com.tutorialandroid.splashscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class MainTermosdeUso extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_termosde_uso);

        pdfView = (PDFView) findViewById(R.id.pdfview);

        pdfView.fromAsset("termosdeusoletsgo.pdf").load();

    }
}