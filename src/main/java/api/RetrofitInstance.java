package api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {
    public static IApiClient create(String address) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + address + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IApiClient.class);
    }
}
