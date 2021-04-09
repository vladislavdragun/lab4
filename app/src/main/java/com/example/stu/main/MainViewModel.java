package com.example.stu.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import com.example.stu.BaseViewModel;
import com.example.stu.model.Callback;
import com.example.stu.model.Cancellable;
import com.example.stu.model.Holiday;
import com.example.stu.model.HolidayService;
import com.example.stu.model.Result;

import static com.example.stu.model.Result.Status.EMPTY;
import static com.example.stu.model.Result.Status.ERROR;
import static com.example.stu.model.Result.Status.LOADING;
import static com.example.stu.model.Result.Status.SUCCESS;

public class MainViewModel extends BaseViewModel {

    private Result<List<Holiday>> repositoriesResult = Result.empty();
    private MutableLiveData<ViewState> stateLiveData = new MutableLiveData<>();

    {
        updateViewState(Result.empty());
    }

    private Cancellable cancellable;

    @Override
    protected void onCleared() {
        super.onCleared();
        if (cancellable != null) cancellable.cancel();
    }

    public MainViewModel(HolidayService holidayService) {
        super(holidayService);
    }

    public LiveData<ViewState> getViewState() {
        return stateLiveData;
    }

    public void getRepositories(String username) {
        updateViewState(Result.loading());
        cancellable = getHolidayService().getHolidays(username, new Callback<List<Holiday>>() {
            @Override
            public void onError(Throwable error) {
                if (repositoriesResult.getStatus() != SUCCESS) {
                    updateViewState(Result.error(error));
                }
            }

            @Override
            public void onResults(List<Holiday> data) {
                updateViewState(Result.success(data));
            }
        });
    }

    private void updateViewState(Result<List<Holiday>> repositoriesResult) {
        this.repositoriesResult = repositoriesResult;
        ViewState state = new ViewState();
        state.enableSearchButton = repositoriesResult.getStatus() != LOADING;
        state.showList = repositoriesResult.getStatus() == SUCCESS;
        state.showEmptyHint = repositoriesResult.getStatus() == EMPTY;
        state.showError = repositoriesResult.getStatus() == ERROR;
        state.showProgress = repositoriesResult.getStatus() == LOADING;
        state.repositories = repositoriesResult.getData();
        stateLiveData.postValue(state);
    }

    static class ViewState {
        private boolean enableSearchButton;
        private boolean showList;
        private boolean showEmptyHint;
        private boolean showError;
        private boolean showProgress;
        private List<Holiday> repositories;

        public boolean isEnableSearchButton() {
            return enableSearchButton;
        }

        public boolean isShowList() {
            return showList;
        }

        public boolean isShowEmptyHint() {
            return showEmptyHint;
        }

        public boolean isShowError() {
            return showError;
        }

        public boolean isShowProgress() {
            return showProgress;
        }

        public List<Holiday> getRepositories() {
            return repositories;
        }
    }
}
