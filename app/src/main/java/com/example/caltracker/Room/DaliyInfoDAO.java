package com.example.caltracker.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaliyInfoDAO {
    @Query("SELECT * FROM DaliyInfo")
    List<DaliyInfo> getAll();

    @Query("SELECT * FROM DaliyInfo WHERE uid = :uid")
    List<DaliyInfo> findByUID(int uid);
    @Insert
    void insertAll(DaliyInfo... infos);
    @Insert
    long insert(DaliyInfo info);
    @Delete
    void delete(DaliyInfo info);
    @Update(onConflict = REPLACE)
    public void updateInfos(DaliyInfo... infos);
    @Query("DELETE FROM DaliyInfo")
    void deleteAll();
    @Query("SELECT * FROM DaliyInfo WHERE id = :id LIMIT 1")
    DaliyInfo findByID(int id);
    @Query("DELETE FROM DaliyInfo WHERE id = :id")
    void deleteOne(int id);
    @Query("SELECT sum(steps) FROM DaliyInfo WHERE uid = :uid")
    int totalSteps(int uid);

}
