package com.linkedin.platform.listeners;

import com.linkedin.platform.errors.LIDeepLinkError;

/* JADX INFO: loaded from: classes.dex */
public interface DeepLinkListener {
    void onDeepLinkError(LIDeepLinkError lIDeepLinkError);

    void onDeepLinkSuccess();
}
