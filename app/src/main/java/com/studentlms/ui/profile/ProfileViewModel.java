package com.studentlms.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.studentlms.data.dao.LMSAccountDao;
import com.studentlms.data.dao.StudySessionDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.LMSAccount;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {
    private final LMSAccountDao lmsAccountDao;
    private final StudySessionDao studySessionDao;
    private final LiveData<List<LMSAccount>> connectedAccounts;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        lmsAccountDao = database.lmsAccountDao();
        studySessionDao = database.studySessionDao();
        connectedAccounts = lmsAccountDao.getAllAccounts();
    }

    public LiveData<List<LMSAccount>> getConnectedAccounts() {
        return connectedAccounts;
    }
}
