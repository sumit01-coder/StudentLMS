package com.studentlms.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.dao.LMSAccountDao;
import com.studentlms.data.models.LMSAccount;
import com.studentlms.services.lms.*;

import java.util.List;

public class LMSSyncWorker extends Worker {

    public LMSSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            LMSAccountDao accountDao = db.lmsAccountDao();

            // Get all active LMS accounts (synchronous call in background)
            List<LMSAccount> accounts = getActiveAccountsSync(accountDao);

            for (LMSAccount account : accounts) {
                syncLMSAccount(account);
            }

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private List<LMSAccount> getActiveAccountsSync(LMSAccountDao dao) {
        // This would normally use a blocking call or callback
        // For simplicity, returning empty list - implement proper sync mechanism
        return dao.getActiveAccounts().getValue() != null ? dao.getActiveAccounts().getValue()
                : java.util.Collections.emptyList();
    }

    private void syncLMSAccount(LMSAccount account) {
        ILMSConnector connector = null;

        switch (account.getLmsType()) {
            case "GOOGLE_CLASSROOM":
                connector = new GoogleClassroomConnector(getApplicationContext());
                break;
            case "MOODLE":
                connector = new MoodleConnector(getApplicationContext());
                break;
            case "CANVAS":
                connector = new CanvasConnector(getApplicationContext());
                break;
        }

        if (connector != null) {
            connector.syncAssignments(account);
        }
    }
}
