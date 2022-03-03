/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of CameraFolder https://github.com/k3b/VirtualCamera/
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.camerafolder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.k3b.util.TempFileUtil;

/**
 * Translates from ACTION_GET_CONTENT.to MediaStore.ACTION_IMAGE_CAPTURE
 * Every client app with a chooser to open a Photo from existing photos will get
 * the possibility to take a photo instead.
 */
public class GetDocument2CameraActivity extends Activity {
    private static final String TAG = "k3b.camerafolder";

    private static final String[] AO10_ADDITIONAL_KNOWN_CAMERA_APPS = new String[] {
            "net.sourceforge.opencamera", // https://sourceforge.net/p/opencamera/code
            "com.simplemobiletools.camera", // https://github.com/SimpleMobileTools/Simple-Camera
            "com.flipcamera", // https://bitbucket.org/kingsimmy/frontcamera/src
            "org.witness.sscphase1", // https://github.com/guardianproject/ObscuraCam
            "com.lun.chin.aicamera", // https://bitbucket.org/chlun/aicamera
            "com.lightbox.android.camera" , // https://github.com/lightbox/QuickSnap
            "com.google.android.GoogleCamera",
            "tools.photo.hd.camera",
    };

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 100;
    static private final int ACTION_REQUEST_IMAGE_CAPTURE = 4;
    private static final String STATE_RESULT_PHOTO_URI = "resultPhotoUri";

    private Uri resultPhotoUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.resultPhotoUri = savedInstanceState.getParcelable(STATE_RESULT_PHOTO_URI);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
            return;
        }

        onRequestPhoto();

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRequestPhoto();
            } else {
                String message = "" + getText(R.string.permission_error);
                Toast.makeText(this,message , Toast.LENGTH_LONG).show();
                Log.e(TAG, message);
                setResult(Activity.RESULT_CANCELED, null);
                finish();
            }

        }
    }

    private void onRequestPhoto() {

        this.resultPhotoUri = createSharedUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, this.resultPhotoUri)
                .putExtra(MediaStore.EXTRA_MEDIA_TITLE, getString(R.string.label_select_picture))
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Since android 10 (API30) only default camera app is allowed for ACTION_IMAGE_CAPTURE.
            // You can whitelist other known camera-apps if added via EXTRA_INITIAL_INTENTS.
            // see https://commonsware.com/blog/2020/08/16/action-image-capture-android-r.html
            // code inspired by https://gitlab.com/commonsguy/cw-android-r/-/blob/v0.3/CamChooser/src/main/java/com/commonsware/android/r/camchooser/CameraIntent.kt
            addInitialIntents(intent, AO10_ADDITIONAL_KNOWN_CAMERA_APPS);
        }

        // start the image capture Intent
        startActivityForResult(intent, ACTION_REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Since android 10 (API30) only default camera app is allowed for ACTION_IMAGE_CAPTURE.
     * You can whitelist other known camera-apps if added via EXTRA_INITIAL_INTENTS.
     * see https://commonsware.com/blog/2020/08/16/action-image-capture-android-r.html
     * code inspired by https://gitlab.com/commonsguy/cw-android-r/-/blob/v0.3/CamChooser/src/main/java/com/commonsware/android/r/camchooser/CameraIntent.kt
     */
    private void addInitialIntents(Intent baseIntent, String... packageIdCandidates) {
        PackageManager packageManager = this.getPackageManager();
        List<Intent> whitelist = new ArrayList<>();
        for (String packageId : packageIdCandidates) {
            Intent candidate = new Intent(baseIntent).setPackage(packageId);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(candidate, 0);
            if (!resolveInfos.isEmpty()) {
                Log.i(TAG, "additional known camera app added '" + packageId + "' = " + resolveInfos.get(0));
                whitelist.add(candidate);
            }
        }

        if (!whitelist.isEmpty()) {
            baseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, whitelist.toArray(new Intent[0]));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_REQUEST_IMAGE_CAPTURE && this.resultPhotoUri != null) {
            if (resultCode == RESULT_OK) {
                onResponsePhoto(this.resultPhotoUri);
            } else {
                getContentResolver().delete(this.resultPhotoUri, null, null);
                this.resultPhotoUri = null;
                setResult(Activity.RESULT_CANCELED, null);
                finish();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onResponsePhoto(Uri photo) {
        Intent result = new Intent();
        result.setData(photo);
        result.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    protected File getSharedDir() {
        File sharedDir = new File(this.getFilesDir(), "shared");
        sharedDir.mkdirs();

        // #11: remove unused temporary crops from send/get_content after some time.
        TempFileUtil.removeOldTempFiles(sharedDir, System.currentTimeMillis());
        return sharedDir;
    }

    protected String createCropFileName() {
        return new SimpleDateFormat("'img_'yyyyMMsd-HHmmss'.jpg'").format(new Date());
    }

    protected Uri createSharedUri() {
        File outFile = new File(getSharedDir(), createCropFileName());
        return FileProvider.getUriForFile(this, "de.k3b.camerafolder", outFile);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_RESULT_PHOTO_URI, this.resultPhotoUri);
    }


}
