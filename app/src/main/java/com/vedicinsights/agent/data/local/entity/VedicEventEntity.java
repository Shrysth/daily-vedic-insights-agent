package com.vedicinsights.agent.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "vedic_events")
public class VedicEventEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String category;
    public String notes;
    public boolean isActive = true;

    // A null target means that the user left that criterion set to "Any".
    public Integer targetTithiIndex;
    public String targetPaksha;
    public Integer targetNakshatraIndex;
    public Integer targetPada;
    public Integer targetPurnimantaMonth;
    public Boolean targetAdhikaMasa;
    public String targetHora;
    public String targetChaughadiya;
    public String targetLagna;
    @ColumnInfo(defaultValue = "'ALL'")
    public String matchMode = "ALL";
    @ColumnInfo(defaultValue = "1")
    public int frequencyDays = 1;
    @ColumnInfo(defaultValue = "0")
    public long createdAtEpochMillis;
}