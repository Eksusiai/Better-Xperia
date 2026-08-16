package com.linkedin.platform.listeners;

import com.linkedin.platform.errors.LIApiError;

/* JADX INFO: loaded from: classes.dex */
public interface ApiListener {
    void onApiError(LIApiError lIApiError);

    void onApiSuccess(ApiResponse apiResponse);
}
