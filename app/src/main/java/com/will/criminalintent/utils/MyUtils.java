package com.will.criminalintent.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class MyUtils {
    public static void MyTest(Context context) {
        Log.e("WillWolf", "MyTest-->" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Set<String> volumeNames = MediaStore.getExternalVolumeNames(context);

            for (String volumeName : volumeNames) {
                Log.d("VolumeInfo", "Volume name: " + volumeName);
                try {
                    // 为每个卷创建一个文件
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "test_file_" + volumeName + ".txt");
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/MyApp");

                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = resolver.insert(MediaStore.Files.getContentUri(volumeName), values);

                    if (uri != null) {
                        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                            if (outputStream != null) {
                                outputStream.write(("This is a test file for volume: " + volumeName).getBytes());
                            }
                        }
                        Log.d("FileCreation", "File created successfully in volume: " + volumeName);
                    }
                } catch (IOException e) {
                    Log.e("FileCreation", "Error creating file in volume: " + volumeName, e);
                }
            }
        } else {
            Log.d("VolumeInfo", "MediaStore.getExternalVolumeNames() is not available on this Android version");
        }
    }
}
