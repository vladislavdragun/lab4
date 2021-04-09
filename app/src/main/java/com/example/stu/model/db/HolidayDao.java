package com.example.stu.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface HolidayDao {

    @Query("SELECT * FROM HolidayDbEntity WHERE name = :name ORDER BY name COLLATE NOCASE")
    List<HolidayDbEntity> getRepositories(String name);

    @Query("SELECT * FROM HolidayDbEntity WHERE date = :date")
    HolidayDbEntity getByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHolidays(List<HolidayDbEntity> repositories);

    @Query("DELETE FROM HolidayDbEntity WHERE name = :name")
    void deleteHoliday(String name);

    @Transaction
    default void updateHolidayByName(String name, List<HolidayDbEntity> entities) {
        deleteHoliday(name);
        insertHolidays(entities);
    }

}
