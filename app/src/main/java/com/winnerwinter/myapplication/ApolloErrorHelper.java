package com.winnerwinter.myapplication;
import com.apollographql.apollo.api.Error;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApolloErrorHelper {
    public static BackendError getBackendError(@NotNull List<Error> apolloErrors) {
        Error err = apolloErrors.get(0);
        String message = err.getMessage();
        String code = "";
        try {
            code = ((LinkedHashMap<String, String>) err.getCustomAttributes().get("extensions")).get("code");
        } catch (Exception ignored) {}

        return new BackendError(code, message);
    }
}
