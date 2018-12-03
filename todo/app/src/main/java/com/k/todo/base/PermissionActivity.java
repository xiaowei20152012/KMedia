package com.k.todo.base;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

public class PermissionActivity extends ThemeActivity {

    public static final int PERMISSION_REQUEST = 100;

    private boolean hadPermissions;
    private String[] permissions;
    private String permissionDeniedMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions = getPermissionsToRequest();
        hadPermissions = hasPermissions();

        setPermissionDeniedMessage(null);
    }

    @Nullable
    protected String[] getPermissionsToRequest() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    protected void setPermissionDeniedMessage(String message) {
        permissionDeniedMessage = message;
    }

    private String getPermissionDeniedMessage() {
//        return permissionDeniedMessage == null ? getString(R.string.permissions_denied) : permissionDeniedMessage;
        return "permissionDeniedMessage";
    }

    protected void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, PERMISSION_REQUEST);
        }
    }

    protected boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //User has deny from permission dialog
//                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
//                                Snackbar.LENGTH_INDEFINITE)
//                                .setAction(R.string.action_grant, view -> requestPermissions())
//                                .setActionTextColor(ThemeStore.accentColor(this))
//                                .show();
                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
//                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
//                                Snackbar.LENGTH_INDEFINITE)
//                                .setAction(R.string.action_settings, view -> {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", PermissionActivity.this.getPackageName(), null);
//                                    intent.setData(uri);
//                                    startActivity(intent);
//                                })
//                                .setActionTextColor(ThemeStore.accentColor(this))
//                                .show();
                    }
                    return;
                }
            }
            hadPermissions = true;
            onHasPermissionsChanged(true);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!hasPermissions()) {
            requestPermissions();
        }
    }

    protected void onHasPermissionsChanged(boolean hasPermissions) {
        // implemented by sub classes
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean hasPermissions = hasPermissions();
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHasPermissionsChanged(hasPermissions);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
