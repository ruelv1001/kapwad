package kapwad.reader.app.utils.facedetect;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageManager {

    public static final String TAG = ImageManager.class.getName();

    public static void copyFileAppDirectory(final Context context, final File copyFrom, final File copyTo, final Callback callback){
        copyFileAppDirectory(context, copyFrom,  copyTo, callback, true);
    }
    public static void copyFileAppDirectory(final Context context, final File copyFrom, final File copyTo, final Callback callback, final boolean hasLoading){
        if (!copyFrom.exists()) {
            return;
        }
        new AsyncTask<Object, Object, Object>() {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(hasLoading){
                    progressDialog = new ProgressDialog(context).show(context,"", "Loading image...", true, false);
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    FileChannel source = new FileInputStream(copyFrom).getChannel();
                    FileChannel destination = new FileOutputStream(copyTo).getChannel();

                    if (destination != null && source != null) {
                        destination.transferFrom(source, 0, source.size());
                    }
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }

                    if(copyTo != null){
                        return copyTo;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return copyTo;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(progressDialog != null){
                    progressDialog.cancel();
                }
                if(callback != null){
                    callback.success(copyTo);
                }
            }
        }.execute();


    }

    public static File pictureDirectoryFile(Context context, String directoryName){
        File storageDir = new File(context.getExternalCacheDir(), directoryName);
        if ( !storageDir.exists() ) {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public static File createDirectoryFolder(Context context, File dir, String directoryName){
        File storageDir = new File(dir, directoryName);
        if ( !storageDir.exists() ) {
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public static File createImageFile(Context context, String directoryName) {
        return createImageFile(context,directoryName, null);
    }

    public static File createImageFile(Context context, String directoryName, String imageName) {
        if(imageName == null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageName = "JPEG_" + timeStamp;
        }

        File image = null;
        try {
            image = File.createTempFile(
                    imageName,  /* prefix */
                    ".jpg",         /* suffix */
                    pictureDirectoryFile(context, directoryName)      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static File createImageFile(File directory, String imageName) {
        if(imageName == null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageName = "JPEG_" + timeStamp;
        }

        File image = null;
        try {
            image = File.createTempFile(
                    imageName,  /* prefix */
                    ".jpg",         /* suffix */
                    directory      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private static int getOrientationFromExif(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to get image exif orientation", e);
        }

        return orientation;
    }

    public static int getOrientation(String path) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }
        return rotate;
    }

    public static int getImageOrientation(Context context, String imagePath) {
        int orientation = getOrientationFromExif(imagePath);
        if(orientation <= 0) {
            orientation = getOrientationFromMediaStore(context, imagePath);
        }
        return orientation;
    }

    private static int getOrientationFromMediaStore(Context context, String imagePath) {
        Uri imageUri = getImageContentUri(context, new File(imagePath));
        if(imageUri == null) {
            return -1;
        }

        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);

        int orientation = -1;
        if (cursor != null && cursor.moveToFirst()) {
            orientation = cursor.getInt(0);
            cursor.close();
        }

        return orientation;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void autoRotate(final Context context, final File file, final Callback callback){
        rotate(context, file, getImageOrientation(context, file.getAbsolutePath()), callback);
    }

    public static void rotate(final Context context, final File file, final float degrees, final Callback callback) {

        new AsyncTask<Object, Object, Object>() {

            ProgressDialog progressDialog;
            String path;

            @Override
            protected Object doInBackground(Object[] params) {
                path= file.getAbsolutePath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);

                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                Matrix mtx = new Matrix();
                mtx.preRotate(degrees);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Bitmap.CompressFormat format = null;

                    if(path.substring(path.lastIndexOf(".")).equals(".jpg")) {
                        format = Bitmap.CompressFormat.JPEG;
                    } else if(path.substring(path.lastIndexOf(".")).equals(".png")) {
                        format = Bitmap.CompressFormat.PNG;
                    }

                    if(format != null) {
                        bitmap.compress(format, 100, fOut);
                    }

                    fOut.flush();
                    fOut.close();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
//                if(progressDialog != null){
//                    progressDialog.dismiss();
//                }
                if(callback != null){
                    callback.success(file);
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                ((BaseActivity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog = new ProgressDialog(context).show(context,"", "Loading...", true,false);
//                    }
//                });
            }
        }.execute();

    }

    public static void getFileFromBitmap(final Context context, final Bitmap bitmap, final String imageName, final Callback callback){
        new AsyncTask<Object, Object, Object>(){
            File file;
            @Override
            protected Object doInBackground(Object[] params) {

                file = ImageManager.createImageFile(context, imageName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (callback != null){
                    callback.success(file);
                }
            }
        }.execute();
    }

    public static void getFileFromBitmap(final Context context, final View view, final Callback callback){
        new AsyncTask<Object, Object, Object>(){
            File file;
            Bitmap bitmap;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                file = ImageManager.createImageFile(context, "final");
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (callback != null){
                    callback.success(file);
                }
            }
        }.execute();
    }

    public interface Callback{
        void success(File file);

        void success(File file1, File file2);
    }
}
