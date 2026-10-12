package com.vedicinsights.agent.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vedic_events")
public class VedicEventEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String category;
    public String notes;
    public boolean isActive = true;

    // The 8 Nullable Vedic Criteria for dynamic subset matching
    public Integer targetTithiIndex;
    public String targetPaksha;
    public Integer targetNakshatraIndex;
    public Integer targetPada;
    public Integer targetPurnimantaMonth;
    public String targetHora;
    public String targetChaughadiya;
    public String targetLagna;
}