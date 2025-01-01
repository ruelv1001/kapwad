package kapwad.reader.app.utils.facedetect.facedetectionutil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import kapwad.reader.app.utils.facedetect.facedetectionutil.common.FrameMetadata;
import kapwad.reader.app.utils.facedetect.facedetectionutil.common.GraphicOverlay;

import java.util.List;

/**
 * Created by Jaison.
 */
public interface FaceDetectionResultListener {
    void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay);

    void onFailure(@NonNull Exception e);
}
