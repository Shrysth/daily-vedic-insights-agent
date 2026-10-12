package com.vedicinsights.agent.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import java.util.List;

@Dao
public interface VedicEventDao {
    @Insert
    void insertEvent(VedicEventEntity event);

    @Query("SELECT * FROM vedic_events WHERE isActive = 1")
    List<VedicEventEntity> getAllActive();
}