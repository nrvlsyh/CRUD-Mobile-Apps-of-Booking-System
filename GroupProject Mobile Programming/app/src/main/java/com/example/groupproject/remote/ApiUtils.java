package com.example.groupproject.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "http://192.168.1.132/prestige/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
    // return TaskService instance
    public static TaskService getTaskService() {
        return RetrofitClient.getClient(BASE_URL).create(TaskService.class);
    }



}
