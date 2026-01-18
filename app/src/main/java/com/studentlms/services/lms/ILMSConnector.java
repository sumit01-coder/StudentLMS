package com.studentlms.services.lms;

import com.studentlms.data.models.LMSAccount;

public interface ILMSConnector {
    /**
     * Authenticate user with the LMS platform
     * 
     * @return true if authentication successful
     */
    boolean authenticate(String email);

    /**
     * Sync assignments from LMS to local database
     * 
     * @param account The LMS account to sync
     */
    void syncAssignments(LMSAccount account);

    /**
     * Get the number of upcoming assignments
     * 
     * @param account The LMS account
     * @return Count of upcoming assignments
     */
    int getUpcomingDeadlinesCount(LMSAccount account);

    /**
     * Disconnect the LMS account
     * 
     * @param account The LMS account to disconnect
     */
    void disconnect(LMSAccount account);
}
