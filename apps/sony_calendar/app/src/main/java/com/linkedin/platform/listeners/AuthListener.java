package com.linkedin.platform.listeners;

import com.linkedin.platform.errors.LIAuthError;

/* JADX INFO: loaded from: classes.dex */
public interface AuthListener {
    void onAuthError(LIAuthError lIAuthError);

    void onAuthSuccess();
}
