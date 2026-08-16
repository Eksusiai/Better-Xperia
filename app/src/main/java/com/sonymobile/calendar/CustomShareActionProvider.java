package com.sonymobile.calendar;

import android.content.Context;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.appcompat.widget.ShareActionProvider;

/* JADX INFO: loaded from: classes2.dex */
public class CustomShareActionProvider extends ShareActionProvider {
    private boolean parseSuccessfull;

    @Override // androidx.core.view.ActionProvider
    public View onCreateActionView(MenuItem menuItem) {
        return null;
    }

    public CustomShareActionProvider(Context context) {
        super(context);
    }

    @Override // androidx.appcompat.widget.ShareActionProvider, androidx.core.view.ActionProvider
    public void onPrepareSubMenu(SubMenu subMenu) {
        super.onPrepareSubMenu(subMenu);
        int size = subMenu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = subMenu.getItem(i);
            item.setIcon(new ShareDrawable(item.getIcon()));
            if (item.hasSubMenu()) {
                SubMenu subMenu2 = item.getSubMenu();
                for (int i2 = 0; i2 < subMenu2.size(); i2++) {
                    MenuItem item2 = subMenu2.getItem(i2);
                    item2.setIcon(new ShareDrawable(item2.getIcon()));
                }
            }
        }
    }

    public void setParseSuccessfull(boolean z) {
        this.parseSuccessfull = z;
    }

    public boolean getParseSuccessfull() {
        return this.parseSuccessfull;
    }
}
