package utils;

import api.IApiClient;
import api.RetrofitInstance;

public class ApiClientUtils {
    private IApiClient apiClient;

    public synchronized IApiClient getApiClient() {
        apiClient = RetrofitInstance.create(String.valueOf(Constants.BASE_URL.value));
        return apiClient;
    }
}
