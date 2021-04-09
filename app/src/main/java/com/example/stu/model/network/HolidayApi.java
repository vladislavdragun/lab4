package com.example.stu.model.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HolidayApi {
    @GET("api/v2/publicholidays/2021/{username}")
    Call<List<RepositoryNetworkEntity>> getRepositories(@Path("username") String username);
}
