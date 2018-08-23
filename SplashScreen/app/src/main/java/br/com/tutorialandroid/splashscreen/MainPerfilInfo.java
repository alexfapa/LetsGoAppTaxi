package br.com.tutorialandroid.splashscreen;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainPerfilInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_perfil_info);

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainPerfilInfo.this, MainScrollingActivity.class);

                Pair<View, String> pair1 = Pair.create(findViewById(R.id.imageView), "myImage");
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.textView), "myTitle");

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainPerfilInfo.this, pair1, pair2);

                        //findViewById(R.id.imageView), "myImage"); //TransitionName

                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }
}
