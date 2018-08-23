package br.com.tutorialandroid.splashscreen.conexao;

/**
 * Created by delaroystudios on 10/5/2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.tutorialandroid.splashscreen.R;

public class ListViewAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> id;
    private final ArrayList<String> nome;
    private final ArrayList<String> data_nasc;
    private final ArrayList<String> phone;
    private final ArrayList<String> cpf;
    private final ArrayList<String> email;
    private final ArrayList<String> cep;
    private final ArrayList<String> end;
    private final ArrayList<String> end_num;
    private final ArrayList<String> complemento;
    private final ArrayList<String> bairro;
    private final ArrayList<String> uf;
    private final ArrayList<String> cidade;
    private final ArrayList<String> cnh;
    private final ArrayList<String> placa_carro;
    private final ArrayList<String> alvara;
    private final ArrayList<String> cracha;
    private final ArrayList<String> agencia;
    private final ArrayList<String> operacao;
    private final ArrayList<String> conta;


    LayoutInflater layoutInflater;

    public ListViewAdapter(Context ctx, ArrayList<String> id, ArrayList<String> nome, ArrayList<String> data_nasc, ArrayList<String> phone,
                           ArrayList<String> cpf, ArrayList<String> email, ArrayList<String> cep, ArrayList<String> end, ArrayList<String> end_num,
                           ArrayList<String> complemento, ArrayList<String> bairro, ArrayList<String> uf, ArrayList<String> cidade, ArrayList<String> cnh,
                           ArrayList<String> placa_carro, ArrayList<String> alvara, ArrayList<String> cracha, ArrayList<String> agencia,
                           ArrayList<String> operacao, ArrayList<String> conta) {
        this.context = ctx;
        this.id = id;
        this.nome = nome;
        this.data_nasc = data_nasc;
        this.phone = phone;
        this.cpf = cpf;
        this.email = email;
        this.cep = cep;
        this.end = end;
        this.end_num = end_num;
        this.complemento = complemento;
        this.bairro = bairro;
        this.uf = uf;
        this.cidade = cidade;
        this.cnh = cnh;
        this.placa_carro= placa_carro;
        this.alvara = alvara;
        this.cracha = cracha;
        this.agencia = agencia;
        this.operacao = operacao;
        this.conta = conta;

    }

    @Override
    public int getCount() {
        return id.size();
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

        view = layoutInflater.inflate(R.layout.list_item, null);
        holder.txt_nome=(TextView)view.findViewById(R.id.nome);
        holder.txt_dn=(TextView)view.findViewById(R.id.data_nasc);
        holder.txt_phone=(TextView)view.findViewById(R.id.phone);
        holder.txt_email=(TextView)view.findViewById(R.id.email);
        holder.txt_cep=(TextView)view.findViewById(R.id.cep);
        holder.txt_end=(TextView)view.findViewById(R.id.end);
        holder.txt_end_num=(TextView)view.findViewById(R.id.end_num);
        holder.txt_comp=(TextView)view.findViewById(R.id.complemento);
        holder.txt_bairro=(TextView)view.findViewById(R.id.bairro);
        holder.txt_uf=(TextView)view.findViewById(R.id.uf);
        holder.txt_cidade=(TextView)view.findViewById(R.id.cidade);
        /*holder.txt_cnh=(TextView)view.findViewById(R.id.cnh);
        holder.txt_placa=(TextView)view.findViewById(R.id.placa_carro);
        holder.txt_alvara=(TextView)view.findViewById(R.id.alvara);
        holder.txt_cracha=(TextView)view.findViewById(R.id.cracha);
        holder.txt_agencia=(TextView)view.findViewById(R.id.agencia);
        holder.txt_operacao=(TextView)view.findViewById(R.id.operacao);
        holder.txt_conta=(TextView)view.findViewById(R.id.conta);*/


        holder.txt_nome.setText(nome.get(position));
        holder.txt_dn.setText(data_nasc.get(position));
        holder.txt_phone.setText(phone.get(position));
        holder.txt_email.setText(email.get(position));
        holder.txt_cep.setText(cep.get(position));
        holder.txt_end.setText(end.get(position));
        holder.txt_end_num.setText(end_num.get(position));
        holder.txt_comp.setText(complemento.get(position));
        holder.txt_bairro.setText(bairro.get(position));
        holder.txt_uf.setText(uf.get(position));
        holder.txt_cidade.setText(cidade.get(position));
       /* holder.txt_cnh.setText(cnh.get(position));
        holder.txt_placa.setText(placa_carro.get(position));
        holder.txt_alvara.setText(alvara.get(position));
        holder.txt_cracha.setText(cracha.get(position));
        holder.txt_agencia.setText(agencia.get(position));
        holder.txt_operacao.setText(operacao.get(position));
        holder.txt_conta.setText(conta.get(position));*/

        return view;
    }

    static class Holder {
        TextView txt_nome,txt_dn,txt_phone,txt_email, txt_cep, txt_end, txt_end_num, txt_comp, txt_bairro, txt_uf, txt_cidade;
    }
}
