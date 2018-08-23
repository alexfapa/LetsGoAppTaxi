package br.com.tutorialandroid.splashscreen.loginActivity.conexoes;


public class User {

    private String taxi_nome;   //nome;      //taxi_nome
    private String taxi_email;  //email;     //taxi_email
    private String taxi_nascimento; //data_nasc; //taxi_nascimento
    private String taxi_sexo;   //sexo;      //taxi_sexo
    private String taxi_fone;   //telefone;  //taxi_fone
    private String taxi_cpf;    //cpf;       //taxi_cpf
    private String taxi_cep;    //cep;       //taxi_cep
    private String taxi_endereco;//endereco;  //taxi_endereco
    private String taxi_num;    //num;       //taxi_num
    private String taxi_complemento;//complemento;//taxi_complemento
    private String taxi_bairro; //bairro;    //taxi_bairro
    private String taxi_uf;     //uf;        //taxi_uf
    private String taxi_cidade; //cidade;    //taxi_cidade
    private double taxi_situacao;
    private String taxi_cnh;    //cnh;       //taxi_cnh
    private String taxi_placa;  //placa_veiculo;//taxi_placa
    private String taxi_alvara; //alvara;      //taxi_alvara
    private String taxi_cracha; //num_cracha;  //taxi_cracha
    private String taxi_agencia;//agencia;     //taxi_agencia
    private String taxi_operacao;//operacao;    //taxi_operacao
    private String taxi_conta;  //conta;       //taxi_conta
    private String taxi_token;
    private String taxi_id_unic;//id_unico;    //taxi_id_unic
    private String taxi_senha;  //senha;       //taxi_senha
    private String old_password;
    private String new_password;
    private String code;


    /*public String getId_unico() {
        return id_unico;
    }*/


    public String getId_unico() {
        return taxi_id_unic;
    }


    /*public String getNome() {
        return nome;
    }*/


    public String getNome() {
        return taxi_nome;
    }


    /*public String getEmail() {
        return email;
    }*/


    public String getEmail() {
        return taxi_email;
    }


   /* public String getData_nasc() {
        return data_nasc;
    }*/


    public String getData_nasc() {
        return taxi_nascimento;
    }


    /*public String getSexo() {
        return sexo;
    }*/

    public String getSexo() {
        return taxi_sexo;
    }

    /*public String getTelefone() {
        return telefone;
    }*/

    public String getTelefone() {
        return taxi_fone;
    }

    /*public String getCpf() {
        return cpf;
    }*/

    public String getCpf() {
        return taxi_cpf;
    }

    /*public String getCep() {
        return cep;
    }*/

    public String getCep() {
        return taxi_cep;
    }

    /*public String getEndereco() {
        return endereco;
    }*/

    public String getEndereco() {
        return taxi_endereco;
    }

    /*public String getNum() {
        return num;
    }*/

    public String getNum() {
        return taxi_num;
    }

   /* public String getComplemento() {
        return complemento;
    }*/

    public String getComplemento() {
        return taxi_complemento;
    }

    /*public String getBairro() {
        return bairro;
    }*/

    public String getBairro() {
        return taxi_bairro;
    }

    /*public String getCidade() {
        return cidade;
    }*/

    public String getCidade() {
        return taxi_cidade;
    }

    /*public String getUf() {
        return uf;
    }*/

    public double getSituacao() {
        return taxi_situacao;
    }


    public String getUf() {
        return taxi_uf;
    }

    /*public String getCnh() {
        return cnh;
    }*/

    public String getCnh() {
        return taxi_cnh;
    }

    /*public String getPlaca_veiculo() {
        return placa_veiculo;
    }*/

    public String getPlaca_veiculo() {
        return taxi_placa;
    }

    /*public String getNum_cracha() {
        return num_cracha;
    }*/

    public String getNum_cracha() {
        return taxi_cracha;
    }

    /*public String getAlvara() {
        return alvara;
    }*/

    public String getAlvara() {
        return taxi_alvara;
    }

    /*public String getAgencia() {
        return agencia;
    }*/

    public String getAgencia() {
        return taxi_agencia;
    }

    /*public String getOperacao() {
        return operacao;
    }*/

    public String getOperacao() {
        return taxi_operacao;
    }

    /*public String getConta() {
        return conta;
    }*/

    public String getConta() {
        return taxi_conta;
    }
/**/
    public String getToken() {
        return taxi_token;
    }







    /*public void setTelefone(String telefone) {
        this.telefone = telefone;
    }*/

    public void setTelefone(String telefone) {
        this.taxi_fone = telefone;
    }

    /*public void setCpf(String cpf) {
        this.cpf = cpf;
    }*/

    public void setCpf(String cpf) {
        this.taxi_cpf = cpf;
    }

    /*public void setCep(String cep) {
        this.cep = cep;
    }*/

    public void setCep(String cep) {
        this.taxi_cep = cep;
    }

    /*public void setEndereco(String endereco) {
        this.endereco = endereco;
    }*/

    public void setEndereco(String endereco) {
        this.taxi_endereco = endereco;
    }

    /*public void setNum(String num) {
        this.num = num;
    }*/

    public void setNum(String num) {
        this.taxi_num = num;
    }

    /*public void setComplemento(String complemento) {
        this.complemento = complemento;
    }*/

    public void setComplemento(String complemento) {
        this.taxi_complemento = complemento;
    }


    /*public void setUf(String uf) {
        this.uf = uf;
    }*/

    public void setUf(String uf) {
        this.taxi_uf = uf;
    }

     /*public void setBairro(String bairro) {
        this.bairro = bairro;
    }*/

   public void setBairro(String bairro) {
        this.taxi_bairro = bairro;
    }

    /*public void setCidade(String cidade) {
        this.cidade = cidade;
    }*/

    public void setCidade(String cidade) {
        this.taxi_cidade = cidade;
    }

   /* public void setPlaca_veiculo(String placa_veiculo) {
        this.placa_veiculo = placa_veiculo;
    }*/

    public void setSituacao(double situacao) {
        this.taxi_situacao = situacao;
    }


    public void setPlaca_veiculo(String placa_veiculo) {
        this.taxi_placa = placa_veiculo;
    }

    /*public void setCnh(String cnh) {
        this.cnh = cnh;
    }*/

    public void setCnh(String cnh) {
        this.taxi_cnh = cnh;
    }

    /*public void setAlvara(String alvara) {
        this.alvara = alvara;
    }*/

    public void setAlvara(String alvara) {
        this.taxi_alvara = alvara;
    }

    /*public void setNum_cracha(String num_cracha) {
        this.num_cracha = num_cracha;
    }*/

    public void setNum_cracha(String num_cracha) {
        this.taxi_cracha = num_cracha;
    }

    /*public void setAgencia(String agencia) {
        this.agencia = agencia;
    }*/

    public void setAgencia(String agencia) {
        this.taxi_agencia = agencia;
    }

    /*public void setOperacao(String operacao) {
        this.operacao = operacao;
    }*/

    public void setOperacao(String operacao) {
        this.taxi_operacao = operacao;
    }

    /*public void setConta(String conta) {
        this.conta = conta;
    }*/

    public void setConta(String conta) {
        this.taxi_conta = conta;
    }
/**/
    public void setToken(String taxi_token) {
        this.taxi_token = taxi_token;
    }


    /*public void setNome(String nome) {
        this.nome = nome;
    }*/

    public void setNome(String nome) {
        this.taxi_nome = nome;
    }

   /* public void setEmail(String email) {
        this.email = email;
    }*/

    public void setEmail(String email) {
        this.taxi_email = email;
    }

    /*public void setData_nasc(String data_nasc) {
        this.data_nasc = data_nasc;
    }*/

    public void setData_nasc(String data_nasc) {
        this.taxi_nascimento = data_nasc;
    }

    /*public void setSenha(String senha) {
        this.senha = senha;
    }*/

    public void setSenha(String senha) {
        this.taxi_senha = senha;
    }

    /*public void setSexo(String sexo) {
        this.sexo = sexo;
    }*/

    public void setSexo(String sexo) {
        this.taxi_sexo = sexo;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
