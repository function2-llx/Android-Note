package com.se.npe.androidnote;

import android.app.Application;

import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

public class PermissionTest {

    // no constructor
    private PermissionTest() {
    }

    public static void grantPermission(final Application app, final String permission) {
        ShadowApplication shadowApp = Shadows.shadowOf(app);
        shadowApp.grantPermissions(permission);
    }
}
