package br.com.tutorialandroid.splashscreen.Historico;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.tutorialandroid.splashscreen.R;

/**
 * Created by Renato Almeida on 08/08/2017.
 */

public class ListViewHistorico extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> id;
    private final ArrayList<String> origem;
    private final ArrayList<String> destino;
    private final ArrayList<String> data;
    private final ArrayList<String> hora;
    private final ArrayList<String> preco;
    private final ArrayList<String> descricao;
    private final ArrayList<String> id_taxista;
    LayoutInflater layoutInflater;

    public ListViewHistorico(Context ctx, ArrayList<String> id, ArrayList<String> origem, ArrayList<String> destino, ArrayList<String> data,
                             ArrayList<String> hora, ArrayList<String> id_taxista, ArrayList<String> preco, ArrayList<String> descricao) {
        this.context = ctx;
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.data = data;
        this.hora = hora;
        this.id_taxista = id_taxista;
        this.preco = preco;
        this.descricao = descricao;

    }

    @Override
    public int getCount() {
        return id_taxista.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        Holder holder = new Holder();
        layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(R.layout.list_historico, null);
        holder.txt_id = (TextView)view.findViewById(R.id.tv_id_cliente);
        holder.txt_origem = (TextView)view.findViewById(R.id.origem_c);
        holder.txt_destino = (TextView)view.findViewById(R.id.destino_c);
        holder.txt_data = (TextView)view.findViewById(R.id.data_c);
        holder.txt_hora = (TextView)view.findViewById(R.id.hora_c);
        holder.txt_preco = (TextView)view.findViewById(R.id.tv_preco_cliente);
        holder.txt_descricao = (TextView)view.findViewById(R.id.tv_descricao_cliente);

        holder.txt_id.setText(id.get(position));
        holder.txt_origem.setText(origem.get(position));
        holder.txt_destino.setText(destino.get(position));
        holder.txt_data.setText(data.get(position));
        holder.txt_hora.setText(hora.get(position));
        holder.txt_preco.setText("R$ " + preco.get(position));
        holder.txt_descricao.setText(descricao.get(position));

        return view;
    }

    static class Holder {
        TextView txt_id,txt_preco,txt_origem,txt_destino,txt_data,txt_hora, txt_descricao;
    }
}
