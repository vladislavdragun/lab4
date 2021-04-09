package com.example.stu.model;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import retrofit2.Response;
import com.example.stu.logger.Logger;
import com.example.stu.model.db.HolidayDao;
import com.example.stu.model.db.HolidayDbEntity;
import com.example.stu.model.network.HolidayApi;
import com.example.stu.model.network.RepositoryNetworkEntity;

public class HolidayService {

    private ExecutorService executorService;
    private HolidayDao holidayDao;
    private HolidayApi holidayApi;
    private Logger logger;

    public HolidayService(HolidayApi holidayApi, HolidayDao holidayDao,
                          ExecutorService executorService, Logger logger) {
        this.holidayApi = holidayApi;
        this.holidayDao = holidayDao;
        this.executorService = executorService;
        this.logger = logger;
    }

    public Cancellable getHolidays(String name, Callback<List<Holiday>> callback) {
        Future<?> future = executorService.submit(() -> {
            try {
                List<HolidayDbEntity> entities = holidayDao.getRepositories(name);
                List<Holiday> holidays = convertToHoliday(entities);
                callback.onResults(holidays);

                Response<List<RepositoryNetworkEntity>> response = holidayApi.getRepositories(name).execute();
                if (response.isSuccessful()) {
                    List<HolidayDbEntity> newDbRepositories = networkToDbEntities(response.body());
                    holidayDao.updateHolidayByName(name, newDbRepositories);
                    callback.onResults(convertToHoliday(newDbRepositories));
                } else {
                    if (!holidays.isEmpty()) {
                        RuntimeException exception = new RuntimeException("Something happened");
                        logger.e(exception);
                        callback.onError(exception);
                    }
                }
            } catch (Exception e) {
                logger.e(e);
                callback.onError(e);
            }
        });

        return new FutureCancellable(future);
    }

    public Cancellable getHolidayByData(String date, Callback<Holiday> callback) {
        Future<?> future = executorService.submit(() -> {
            try {
                HolidayDbEntity dbEntity = holidayDao.getByDate(date);
                Holiday holiday = new Holiday(dbEntity);
                callback.onResults(holiday);
            } catch (Exception e) {
                logger.e(e);
                callback.onError(e);
            }
        });
        return new FutureCancellable(future);
    }

    private List<Holiday> convertToHoliday(List<HolidayDbEntity> entities) {
        return Stream.of(entities).map(Holiday::new).toList();
    }

    private List<HolidayDbEntity> networkToDbEntities(List<RepositoryNetworkEntity> entities) {
        return Stream.of(entities)
                .map(HolidayDbEntity::new)
                .toList();
    }

    static class FutureCancellable implements Cancellable {
        private Future<?> future;

        public FutureCancellable(Future<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            future.cancel(true);
        }
    }

}
