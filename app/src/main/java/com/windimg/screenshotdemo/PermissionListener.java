package com.windimg.screenshotdemo;

import java.util.List;

public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermission);

    void onShouldShowRationale(List<String> deniedPermission);
}
