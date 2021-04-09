package com.example.stu.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stu.BaseViewModel;
import com.example.stu.model.Callback;
import com.example.stu.model.Cancellable;
import com.example.stu.model.Holiday;
import com.example.stu.model.HolidayService;
import com.example.stu.model.Result;

public class DetailsViewModel extends BaseViewModel {

    private MutableLiveData<Result<Holiday>> repositoryLiveData = new MutableLiveData<>();

    {
        repositoryLiveData.setValue(Result.empty());
    }

    private Cancellable cancellable;

    public DetailsViewModel(HolidayService holidayService) {
        super(holidayService);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (cancellable != null) cancellable.cancel();
    }

    public void loadRepositoryById(String data) {
        repositoryLiveData.setValue(Result.loading());
        cancellable = getHolidayService().getHolidayByData(data, new Callback<Holiday>() {
            @Override
            public void onError(Throwable error) {
                repositoryLiveData.postValue(Result.error(error));
            }

            @Override
            public void onResults(Holiday data) {
                repositoryLiveData.postValue(Result.success(data));
            }
        });
    }

    public LiveData<Result<Holiday>> getResults() {
        return repositoryLiveData;
    }

}
