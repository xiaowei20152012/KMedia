package com.mplayer.android.permission;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mplayer.android.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PlayerPermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    private static final String TAG = "MainActivity";
    private static final String[] MANIFEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int RC_WRITE_EXTERNAL_STORAGE = 112;
    private static final int RC_READ_EXTERNAL_STORAGE = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    protected void storagePermissionSuccess() {

    }

    @AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE)
    public void checkStoragePermission() {
        if (hasStoragePermission()) {
            // Have permission, do the thing!
            storagePermissionSuccess();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    RC_WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            String yes = getString(R.string.yes);
//            String no = getString(R.string.no);
//
//             Do something after user returned from app settings screen, like showing a Toast.
//            Toast.makeText(
//                    this,
//                    getString(R.string.returned_from_app_settings_to_activity,
//                            hasCameraPermission() ? yes : no,
//                            hasLocationAndContactsPermissions() ? yes : no,
//                            hasSmsPermission() ? yes : no),
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
//        Log.d(TAG, "onRationaleDenied:" + requestCode);
    }


}
