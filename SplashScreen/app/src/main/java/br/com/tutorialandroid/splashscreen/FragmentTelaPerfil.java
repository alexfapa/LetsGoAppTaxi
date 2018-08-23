package br.com.tutorialandroid.splashscreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentTelaPerfil extends Fragment {

    public FragmentTelaPerfil() {
        // Required empty public constructor
    }

    private TextView nome_perfil, num_cpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_tela_perfil, container, false);

        nome_perfil = (TextView) view.findViewById(R.id.nome_perfil);
        num_cpf = (TextView) view.findViewById(R.id.num_cpf);

        return view;
    }

}