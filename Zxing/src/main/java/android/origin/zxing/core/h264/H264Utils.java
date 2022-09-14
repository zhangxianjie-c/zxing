package android.origin.zxing.core.h264;


import static android.origin.zxing.Constant.IFRAME_INTERVAL;
import static android.origin.zxing.Constant.VIDEO_BITRATE_COEFFICIENT;

import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaCodec;

import android.origin.zxing.Constant;
import android.origin.zxing.camera.CameraUtils;
import android.origin.zxing.core.BytePool;
import android.origin.zxing.core.MediaEncoder;
import android.origin.zxing.core.MediaUtils;
import android.origin.zxing.core.Orientation;
import android.origin.zxing.core.Transform;

/**
 * Created by you on 2018-05-19.
 */
public final class H264Utils {

    private H264Utils() {}

    public static MediaEncoder createH264MediaEncoder(int width, int height, Matrix matrix,
                                                      @Orientation.OrientationMode int orientation,
                                                      MediaEncoder.Callback callback) {
        int h264BitRate = width * height * VIDEO_BITRATE_COEFFICIENT;
        return createH264MediaEncoder(MediaUtils.selectColorFormat(), width, height, matrix,
                orientation, h264BitRate, Constant.FRAME_RATE, IFRAME_INTERVAL, callback);
    }

    public static MediaEncoder createH264MediaEncoder(int colorFormat, int width, int height, Matrix matrix,
                                                      @Orientation.OrientationMode int orientation,
                                                      int h264BitRate, int frameRate, int frameInterval,
                                                      MediaEncoder.Callback callback) {
        Point matrixSize = CameraUtils.matrixSize(width, height, matrix);
        BytePool bytePool = new BytePool(matrixSize.x * matrixSize.y * 3 / 2);
        MediaCodec h264Codec = MediaUtils.createAvcMediaCodec(matrixSize.x, matrixSize.y,
                colorFormat, orientation, h264BitRate, frameRate, frameInterval);
        Transform transform = matrixSize.equals(width, height) ?
                new AvcTransform(width, height, colorFormat, orientation)
                : new ClipAvcTransform(matrixSize.x, matrixSize.y, width, height, colorFormat, orientation);
        return new MediaEncoder(h264Codec, bytePool, transform, callback);
    }

}
