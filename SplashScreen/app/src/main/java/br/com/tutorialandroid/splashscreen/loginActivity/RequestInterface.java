package br.com.tutorialandroid.splashscreen.loginActivity;

import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerRequest;
import br.com.tutorialandroid.splashscreen.loginActivity.conexoes.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("taxi/") //TROCAR ISSO @POST("cad_Taxista/") POR ISSO: @POST("taxi/") PARA TESTARLONK QUASE OFICIAL
    Call<ServerResponse> operation(@Body ServerRequest request);

}