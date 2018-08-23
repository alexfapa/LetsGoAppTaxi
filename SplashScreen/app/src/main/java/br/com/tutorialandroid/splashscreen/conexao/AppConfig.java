package br.com.tutorialandroid.splashscreen.conexao;

/**
 * Created by delaroystudios on 10/5/2016.
 */

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class AppConfig {

    public interface insert {
        @FormUrlEncoded
        @POST("/cadTaxista/insertData.php")
        void insertData(
                @Field("nome") String nome,
                @Field("data_nasc") String data_nasc,
                @Field("telefone") String telefone,
                @Field("cpf") String cpf,
                @Field("email") String email,
                @Field("senha") String senha,
                @Field("cep") String cep,
                @Field("endereco") String endereco,
                @Field("num") String num,
                @Field("complemento") String complemento,
                @Field("bairro") String bairro,
                @Field("uf") String uf,
                @Field("cidade") String cidade,
                @Field("cnh") String cnh,
                @Field("placa_veiculo") String placa_veiculo,
                @Field("alvara") String alvara,
                @Field("num_cracha") String num_cracha,
                @Field("agencia") String agencia,
                @Field("operacao") String operacao,
                @Field("conta") String conta,
                Callback<Response> callback);
    }

    //NÃO ESTÁ SENDO USADO
    public interface read {
        @GET("/cadTaxista/displayAll.php")
        void readData(Callback<JsonElement> callback);
    }

    /*public interface getTaxistaId {
        @GET("/cad_Taxista/getById.php")
        void getDados(
                @Query("id_unico") String id_unico,
                Callback<JsonElement> callback);
    }*/

    //O TAXISTA SERÁ PEGO PELO ID OFICIAL ====  LINK QUASE OFICIAL
    public interface getTaxistaId {
        @GET("/taxi/getById.php")
        void getDados(@Query("taxi_id_unic") String taxi_id_unic,
                Callback<JsonElement> callback);
    }


    //O CLIENTE SERÁ PEGO PELO EMAIL OFICIAL ====  LINK QUASE OFICIAL
    public interface getCliEmail {
        @GET("/taxi/getEmailUsu.php")
        void getDadosCli(@Query("usu_email") String usu_email,
                      Callback<JsonElement> callback);
    }

    //BUSCA ID DE CHAMADA CANCELADA NA TELA DE PASSAGEIROAGUARDA...
    public interface getCancelChamada{
        @GET("/taxi/getCancelChamada.php")
        void getCancelId(@Query("usu_id") String usu_id,
                         Callback<JsonElement> callback);
    }


    public interface getIdPassageiro {
        @GET("/taxi/getIdPassageiro.php")
        void getDadosPassageiro(@Query("usu_id") String id_usu,
                         Callback<JsonElement> callback);
    }

    //PEGA OS DADOS DOCLIENTE POR ID
    public interface getCliId {
        @GET("/taxi/getIdUsu.php")
        void getDadosCliId(@Query("usu_id") String usu_id,
                         Callback<JsonElement> callback);
    }


    //MOSTRA NA TELA DE HISTÓRICO DE CORRIDA SOMENTE DADOS DESSE TAXISTA
    public interface getDadosIdTaxista {
        @GET("/taxi/status_Taxista/getIdDadosTaxista.php")
        void getIdDados(@Query("ht_idtaxista") String ht_idtaxista,
                      Callback<JsonElement> callback);
    }


    //NÃO ESTÁ SENDO USADO
    public interface delete {
        @FormUrlEncoded
        @POST("/cadTaxista/deleteData.php")
        void deleteData(
                @Field("id") String id,
                Callback<Response> callback);
    }

    public interface update {
        @FormUrlEncoded
        @POST("/taxi/updateData.php")
        void updateData(
                @Field("taxi_id_unic") String id,
                @Field("taxi_nome") String nome,
                @Field("taxi_nascimento") String data_nasc,
                @Field("taxi_fone") String telefone,
                @Field("taxi_cpf") String cpf,
                @Field("taxi_email") String email,
                @Field("taxi_sexo") String sexo,
                @Field("taxi_cep") String cep,
                @Field("taxi_endereco") String endereco,
                @Field("taxi_num") String num,
                @Field("taxi_complemento") String complemento,
                @Field("taxi_bairro") String bairro,
                @Field("taxi_uf") String uf,
                @Field("taxi_cidade") String cidade,
                @Field("taxi_cnh") String cnh,
                @Field("taxi_placa") String placa_veiculo,
                @Field("taxi_alvara") String alvara,
                @Field("taxi_cracha") String num_cracha,
                @Field("taxi_agencia") String agencia,
                @Field("taxi_operacao") String operacao,
                @Field("taxi_conta") String conta,
                Callback<Response> callback);

/*
    public interface update {
        @FormUrlEncoded
        @POST("/cad_Taxista/updateData.php")
        void updateData(
                @Field("id_unico") String id,
                @Field("nome") String nome,
                @Field("data_nasc") String data_nasc,
                @Field("telefone") String telefone,
                @Field("cpf") String cpf,
                @Field("email") String email,
                @Field("sexo") String sexo,
                @Field("cep") String cep,
                @Field("endereco") String endereco,
                @Field("num") String num,
                @Field("complemento") String complemento,
                @Field("bairro") String bairro,
                @Field("uf") String uf,
                @Field("cidade") String cidade,
                @Field("cnh") String cnh,
                @Field("placa_veiculo") String placa_veiculo,
                @Field("alvara") String alvara,
                @Field("num_cracha") String num_cracha,
                @Field("agencia") String agencia,
                @Field("operacao") String operacao,
                @Field("conta") String conta,
                Callback<Response> callback);
*/

        /* O OFICIAL SERÁ ASSIM
        public interface update {
        @FormUrlEncoded
        @POST("/cad_Taxista/updateData.php")
        void updateData(
                @Field("taxi_id_unic") String id,
                @Field("taxi_nome") String nome,
                @Field("taxi_nascimento") String data_nasc,
                @Field("taxi_fone") String telefone,
                @Field("taxi_cpf") String cpf,
                @Field("taxi_email") String email,
                @Field("taxi_sexo") String sexo,
                @Field("taxi_cep") String cep,
                @Field("taxi_endereco") String endereco,
                @Field("taxi_num") String num,
                @Field("taxi_complemento") String complemento,
                @Field("taxi_bairro") String bairro,
                @Field("taxi_uf") String uf,
                @Field("taxi_cidade") String cidade,
                @Field("taxi_cnh") String cnh,
                @Field("taxi_placa") String placa_veiculo,
                @Field("taxi_alvara") String alvara,
                @Field("taxi_cracha") String num_cracha,
                @Field("taxi_agencia") String agencia,
                @Field("taxi_operacao") String operacao,
                @Field("taxi_conta") String conta,
                Callback<Response> callback);
    }*/
    }

    /*
    * @Field("id") String id,
    */

    //=================================== STATUS_TAXISTA ====================================

    //ATUALIZA STATUS
    public interface atualizaStatus {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/updateData.php")
        void atualizaDados(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_situacao") double taxi_situacao,
                Callback<Response> callback);
    }

    //ATUALIZA STATUS
    public interface mensagemRapida {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/mensagemRapida.php")
        void atualizaMsgm(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_message") String mensagem_rapida,
                Callback<Response> callback);
    }

    //ATUALIZA TOKEN
    public interface atualizaTokenN {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/atualizaToken.php")
        void atualizaTokenDeN(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_token") String taxi_token,
                Callback<Response> callback);
    }

    //ATUALIZA COORDENADAS
    public interface atualizaCoordenadas {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/atualizaCoordenadas.php")
        void atualizaCoord(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_latitude") double coord_lat,
                @Field("taxi_longitude") double coord_lng,
                Callback<Response> callback);
    }

    //ATUALIZA STATUS
    public interface aceiteCorrida {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/taxiNotificacao.php")
        void corridaaceite(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_notificacao") double taxi_notificacao,
                Callback<Response> callback);
    }

    //ENVIA COORDENADAS NO MOMENTO DO CADASTRO
    public interface cadCoordenadas {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/cadCoordenadas.php")
        void cadCoord(
                @Field("taxi_email") String email,
                @Field("taxi_latitude") double coord_lat,
                @Field("taxi_longitude") double coord_lng,
                Callback<Response> callback);
    }

       /*  //ATUALIZA COORDENADAS TESTE
    public interface atualizaCoordenadas {
        @FormUrlEncoded
        @POST("/taxi/status_Taxista/atualizaCoordenadas.php")
        void atualizaCoord(
                @Field("taxi_id_unic") String taxi_id_unic,
                @Field("taxi_latitude") String taxi_latitude,
                @Field("taxi_longitude") String taxi_longitude,
                Callback<Response> callback);
    }
*/

    /*ATUALIZA STATUS
    public interface atualizaStatus {
        @FormUrlEncoded
        @POST("/firebase_TokeneStatus/updateData.php")
        void atualizaDados(
                @Field("email") String email,
                @Field("status_taxista") String status_taxista,
                Callback<Response> callback);
    }*/

    /*NOVO CÓDIGO OFICIAL
    public interface atualizaStatus {
        @FormUrlEncoded
        @POST("/cad_Taxista/firebase_TokeneStatus/updateData.php")
        void atualizaDados(
                @Field("taxi_email") String email,
                @Field("taxi_situacao") String status_taxista,
                Callback<Response> callback);
    }
    */

    public interface insertHistorico {
        @FormUrlEncoded
        @POST("/taxi/historicoCorridas.php")
        void insertHist(
                @Field("ht_idtaxista") String ht_idtaxista,
                @Field("ht_idcliente") int ht_idcliente,
                @Field("ht_dtcorrida") String ht_dtcorrida,
                @Field("ht_hrcorrida") String ht_hrcorrida,
                @Field("ht_origem") String ht_origem,
                @Field("ht_destino") String ht_destino,
                @Field("ht_valor") double ht_valor,
                @Field("ht_formapgto") String ht_formapgto,
                //@Field("ht_feedback") String ht_feedback,
                Callback<Response> callback);
    }

    //=================================== STATUS_TAXISTA ====================================

}