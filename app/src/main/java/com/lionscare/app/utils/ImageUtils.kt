package com.lionscare.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun getBitmapFromAsset(context: Context, imageName: String): Bitmap? {
    val assetManager = context.assets

    val istr: InputStream
    var bitmap: Bitmap? = null
    try {
        istr = assetManager.open(imageName)
        bitmap = BitmapFactory.decodeStream(istr)
    } catch (e: IOException) {
        // handle exception
    }

    return bitmap
}

fun getFileFromUri(context: Context, uri: Uri?): File? {
    uri ?: return null
    uri.path ?: return null
    var newUriString = uri.toString()
    newUriString = newUriString.replace(
        "content://com.android.providers.downloads.documents/",
        "content://com.android.providers.media.documents/"
    )
    newUriString = newUriString.replace(
        "/msf%3A", "/image%3A"
    )
    val newUri = Uri.parse(newUriString)
    var realPath = String()
    val databaseUri: Uri
    val selection: String?
    val selectionArgs: Array<String>?
    if (newUri.path?.contains("/document/image:") == true) {
        databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        selection = "_id=?"
        selectionArgs = arrayOf(DocumentsContract.getDocumentId(newUri).split(":")[1])
    } else {
        databaseUri = newUri
        selection = null
        selectionArgs = null
    }
    try {
//        val column = "_data"
//        val projection = arrayOf(column)
//        val cursor = context.contentResolver.query(
//            databaseUri,
//            projection,
//            selection,
//            selectionArgs,
//            null
//        )
//        cursor?.let {
//            if (it.moveToFirst()) {
//                val columnIndex = cursor.getColumnIndexOrThrow(column)
//                realPath = cursor.getString(columnIndex)
//            }
//            cursor.close()
//        }
        realPath = getRealPathFromURI(uri,context)

    } catch (e: Exception) {
        Log.i("GetFileUri Exception:", e.message ?: "")
    }
    val path = realPath.ifEmpty {
        when {
            newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                "/document/raw:",
                ""
            )
            newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                "/document/primary:",
                "/storage/emulated/0/"
            )
            else -> return null
        }
    }
    return if (path.isNullOrEmpty()) null else File(path)
}

fun getRealPathFromURI(uri: Uri, context: Context): String {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    val size = returnCursor.getLong(sizeIndex).toString()
    val file = File(context.filesDir, name)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int = inputStream?.available() ?: 0
        //int bufferSize = 1024;
        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffers = ByteArray(bufferSize)
        while (inputStream?.read(buffers).also {
                if (it != null) {
                    read = it
                }
            } != -1) {
            outputStream.write(buffers, 0, read)
        }
        Log.e("File Size", "Size " + file.length())
        inputStream?.close()
        outputStream.close()
        Log.e("File Path", "Path " + file.path)

    } catch (e: java.lang.Exception) {
        Log.e("Exception", e.message!!)
    }
    return file.path
}