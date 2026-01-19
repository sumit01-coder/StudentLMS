package com.studentlms.ui.resources;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.studentlms.data.dao.ResourceDao;
import com.studentlms.data.database.AppDatabase;
import com.studentlms.data.models.Resource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourcesViewModel extends AndroidViewModel {
    private final ResourceDao resourceDao;
    private final LiveData<List<Resource>> allResources;
    private final MutableLiveData<String> filterType = new MutableLiveData<>("ALL");
    private final MutableLiveData<Integer> filterSemester = new MutableLiveData<>(0); // 0 = All semesters
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final ExecutorService executorService;

    private final com.studentlms.data.dao.SubjectDao subjectDao;
    private final com.studentlms.services.lms.ERPPortalConnector erpConnector;
    private final MutableLiveData<Boolean> isSyncing = new MutableLiveData<>(false);

    public ResourcesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        resourceDao = database.resourceDao();
        subjectDao = database.subjectDao();
        allResources = resourceDao.getAllResources();
        executorService = Executors.newSingleThreadExecutor();
        erpConnector = new com.studentlms.services.lms.ERPPortalConnector();
    }

    public LiveData<Boolean> getIsSyncing() {
        return isSyncing;
    }

    /**
     * Sync resources from ERP for a specific subject
     */
    public void syncResources(String contentUrl, String subjectCode, int semester) {
        if (contentUrl == null || contentUrl.isEmpty())
            return;

        isSyncing.setValue(true);
        executorService.execute(() -> {
            try {
                // 1. Fetch from ERP
                List<Resource> scrapedResources = erpConnector.fetchResources(contentUrl);

                if (!scrapedResources.isEmpty()) {
                    // 2. Resolve Subject ID
                    int subjectId = -1;
                    if (subjectCode != null && !subjectCode.isEmpty()) {
                        com.studentlms.data.models.Subject subject = subjectDao.getSubjectByCodeSync(subjectCode);
                        if (subject != null) {
                            subjectId = subject.getId();
                        }
                    }

                    // 3. Insert unique resources
                    for (Resource res : scrapedResources) {
                        res.setSubjectId(subjectId);
                        res.setSemester(semester);

                        // Check if exists
                        if (resourceDao.countByUrl(res.getUrlOrPath()) == 0) {
                            resourceDao.insert(res);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isSyncing.postValue(false);
            }
        });
    }

    public LiveData<List<Resource>> getAllResources() {
        return allResources;
    }

    public LiveData<List<Resource>> getFilteredResources() {
        return Transformations.switchMap(filterSemester, semester -> {
            return Transformations.switchMap(filterType, type -> {
                // All semesters, all types
                if (semester == 0 && type.equals("ALL")) {
                    return allResources;
                }
                // All semesters, specific type
                else if (semester == 0 && !type.equals("ALL")) {
                    return resourceDao.getResourcesByType(type);
                }
                // Specific semester, all types
                else if (semester > 0 && type.equals("ALL")) {
                    return resourceDao.getResourcesBySemester(semester);
                }
                // Specific semester, specific type
                else {
                    return resourceDao.getResourcesBySemesterAndType(semester, type);
                }
            });
        });
    }

    public void setFilterType(String type) {
        filterType.setValue(type);
    }

    public void setFilterSemester(int semester) {
        filterSemester.setValue(semester);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Resource>> searchResources(String query) {
        return resourceDao.searchResources(query);
    }

    public void insertResource(Resource resource) {
        executorService.execute(() -> resourceDao.insert(resource));
    }

    public void updateResource(Resource resource) {
        executorService.execute(() -> resourceDao.update(resource));
    }

    public void deleteResource(Resource resource) {
        executorService.execute(() -> resourceDao.delete(resource));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
