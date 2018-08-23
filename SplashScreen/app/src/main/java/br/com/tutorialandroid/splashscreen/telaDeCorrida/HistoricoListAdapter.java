package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.tutorialandroid.splashscreen.R;

/**
 * Created by Renato Almeida on 24/07/2017.
 */

public class HistoricoListAdapter extends BaseAdapter{

    private Context mContext;
    private List<Historico> mHistoricoList;


    //CONSTRUTOR

    public HistoricoListAdapter(Context mContext, List<Historico> mHistoricoList) {
        this.mContext = mContext;
        this.mHistoricoList = mHistoricoList;
    }

    @Override
    public int getCount() {
        return mHistoricoList.size();
    }

    @Override
    public Object getItem(int posicao) {
        return mHistoricoList.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return posicao;
    }

/*
    @Override
    public View getView(int posicao, View convertView, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.item_historico_corrida_list, null);

        TextView tvNome = (TextView)v.findViewById(R.id.tv_nome_cli);
        TextView tvPreco = (TextView)v.findViewById(R.id.tv_preco_cli);
        TextView tvDescricao = (TextView)v.findViewById(R.id.tv_descricao_cli);

        //Pega Texto para TextView
        tvNome.setText(mHistoricoList.get(posicao).getNome());
        tvPreco.setText(String.valueOf("R$ " + mHistoricoList.get(posicao).getPreco()));
        tvDescricao.setText(mHistoricoList.get(posicao).getDescricao());

        //Salva id dos produtos para a tag
        v.setTag(mHistoricoList.get(posicao).getId());


        return v;
    }
*/

    @Override
    public View getView(int posicao, View convertView, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.item_historico_corrida_list, null);

        TextView tvNome = (TextView)v.findViewById(R.id.tv_nome_cli);
        TextView tvPreco = (TextView)v.findViewById(R.id.tv_preco_cli);
        TextView tvDescricao = (TextView)v.findViewById(R.id.tv_descricao_cli);

        //Pega Texto para TextView
        tvNome.setText(mHistoricoList.get(posicao).getNome());
        tvPreco.setText(String.valueOf("R$ " + mHistoricoList.get(posicao).getPreco()));
        tvDescricao.setText(mHistoricoList.get(posicao).getDescricao());

        //Salva id dos produtos para a tag
        v.setTag(mHistoricoList.get(posicao).getId());


        return v;
    }
}