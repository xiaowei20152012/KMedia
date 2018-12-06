package com.k.todo.base


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat

open class PermissionActivity : ThemeActivity() {

    private var hadPermissions: Boolean = false
    private var permissions: Array<String>? = null
    private//        return permissionDeniedMessage == null ? getString(R.string.permissions_denied) : permissionDeniedMessage;
    var permissionDeniedMessage: String? = null;
//        get() = "permissionDeniedMessage"
//        protected set

    protected val permissionsToRequest: Array<String>?
        get() = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissions = permissionsToRequest
        hadPermissions = hasPermissions()

        permissionDeniedMessage = null
    }

    protected fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions!!, PERMISSION_REQUEST)
        }
    }

    protected fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (permission in permissions!!) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@PermissionActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                    return
                }
            }
            hadPermissions = true
            onHasPermissionsChanged(true)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (!hasPermissions()) {
            requestPermissions()
        }
    }

    protected fun onHasPermissionsChanged(hasPermissions: Boolean) {
        // implemented by sub classes
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val hasPermissions = hasPermissions()
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onHasPermissionsChanged(hasPermissions)
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

        val PERMISSION_REQUEST = 100
    }

}
