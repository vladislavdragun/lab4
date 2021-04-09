package com.example.stu.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {HolidayDbEntity.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract HolidayDao getRepositoryDao();

}
