package com.studentlms.ui.reminders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.studentlms.data.dao.ReminderDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.Reminder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemindersViewModel extends AndroidViewModel {
    private final ReminderDao reminderDao;
    private final LiveData<List<Reminder>> allReminders;
    private final LiveData<List<Reminder>> upcomingReminders;
    private final ExecutorService executorService;

    public RemindersViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        reminderDao = database.reminderDao();
        allReminders = reminderDao.getAllReminders();
        upcomingReminders = reminderDao.getUpcomingReminders(System.currentTimeMillis());
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }

    public LiveData<List<Reminder>> getUpcomingReminders() {
        return upcomingReminders;
    }

    public void insertReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.insert(reminder));
    }

    public void updateReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.update(reminder));
    }

    public void deleteReminder(Reminder reminder) {
        executorService.execute(() -> reminderDao.delete(reminder));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
