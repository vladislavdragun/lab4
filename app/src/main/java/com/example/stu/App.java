package com.example.stu;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.stu.logger.AndroidLogger;
import com.example.stu.logger.Logger;
import com.example.stu.model.HolidayService;
import com.example.stu.model.db.AppDatabase;
import com.example.stu.model.db.HolidayDao;
import com.example.stu.model.network.HolidayApi;

public class App extends Application {

    private static final String BASE_URL = "https://date.nager.at/";

    private ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger logger = new AndroidLogger();

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HolidayApi holidayApi = retrofit.create(HolidayApi.class);

        ExecutorService executorService = Executors.newCachedThreadPool();

        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "database.db")
                .build();
        HolidayDao holidayDao = appDatabase.getRepositoryDao();

        HolidayService holidayService = new HolidayService(holidayApi, holidayDao, executorService, logger);
        viewModelFactory = new com.example.stu.ViewModelFactory(holidayService);
    }

    public ViewModelProvider.Factory getViewModelFactory() {
        return viewModelFactory;
    }

}
