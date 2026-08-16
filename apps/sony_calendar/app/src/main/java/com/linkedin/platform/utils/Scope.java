package com.linkedin.platform.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class Scope {
    private Set<LIPermission> permissions = new HashSet();
    public static final LIPermission R_BASICPROFILE = new LIPermission("r_basicprofile", "Name, photo, headline and current position");
    public static final LIPermission R_FULLPROFILE = new LIPermission("r_fullprofile", "Full profile including experience, education, skills and recommendations");
    public static final LIPermission R_EMAILADDRESS = new LIPermission("r_emailaddress", "Your email address");
    public static final LIPermission R_CONTACTINFO = new LIPermission("r_contactinfo", "Your contact info");
    public static final LIPermission RW_COMPANY_ADMIN = new LIPermission("rw_company_admin", "Manage your company page and post updates");
    public static final LIPermission W_SHARE = new LIPermission("w_share", "Post updates, make comments and like posts as you");

    public static synchronized Scope build(LIPermission... lIPermissionArr) {
        return new Scope(lIPermissionArr);
    }

    private Scope(LIPermission... lIPermissionArr) {
        if (lIPermissionArr == null) {
            return;
        }
        for (LIPermission lIPermission : lIPermissionArr) {
            this.permissions.add(lIPermission);
        }
    }

    public String createScope() {
        return join(" ", this.permissions);
    }

    private static String join(CharSequence charSequence, Collection<LIPermission> collection) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (LIPermission lIPermission : collection) {
            if (z) {
                z = false;
            } else {
                sb.append(charSequence);
            }
            sb.append(lIPermission.name);
        }
        return sb.toString();
    }

    public String toString() {
        return createScope();
    }

    public static class LIPermission {
        private final String description;
        private final String name;

        public LIPermission(String str, String str2) {
            this.name = str;
            this.description = str2;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
