package com.vedicinsights.agent.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.vedicinsights.agent.data.astronomical.PanchangCalculator;
import com.vedicinsights.agent.data.calendar.DeviceCalendarRepository;
import com.vedicinsights.agent.data.local.AppDatabase;
import com.vedicinsights.agent.data.local.entity.VedicEventEntity;
import com.vedicinsights.agent.domain.usecase.EvaluateVedicEventsUseCase;
import java.util.List;

public class DailyPipelineWorker extends Worker {
    public DailyPipelineWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        try {
            // 1. Compute Offline Astronomical Metrics (Moshier Engine)
            PanchangCalculator calc = new PanchangCalculator();
            double julianDay = (System.currentTimeMillis() / 86400000.0) + 2440587.5;
            PanchangCalculator.FullPanchang panchang = calc.compute(julianDay);

            // 2. Query Device Calendar Events
            List<String> calendarEvents = DeviceCalendarRepository.getTodayEvents(context);

            // 3. Evaluate User-Defined Vedic Recurring Events
            List<VedicEventEntity> savedEvents = AppDatabase.getInstance(context).vedicEventDao().getAllActive();
            List<VedicEventEntity> matched = new EvaluateVedicEventsUseCase().evaluate(panchang, savedEvents);

            // 4. Deliver High-Priority Notification
            String notificationTitle = "Vedic Guidance: " + panchang.paksha + " Tithi " + panchang.tithiIndex;
            String notificationBody = "Nakshatra: " + panchang.nakshatraIndex + " | Matched Events: " + matched.size();
            NotificationHelper.sendNotification(context, notificationTitle, notificationBody);

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}