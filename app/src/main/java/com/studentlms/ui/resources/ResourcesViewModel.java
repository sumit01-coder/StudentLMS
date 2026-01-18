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
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final ExecutorService executorService;

    public ResourcesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        resourceDao = database.resourceDao();
        allResources = resourceDao.getAllResources();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Resource>> getAllResources() {
        return allResources;
    }

    public LiveData<List<Resource>> getFilteredResources() {
        return Transformations.switchMap(filterType, type -> {
            if (type.equals("ALL")) {
                return allResources;
            } else {
                return resourceDao.getResourcesByType(type);
            }
        });
    }

    public void setFilterType(String type) {
        filterType.setValue(type);
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
