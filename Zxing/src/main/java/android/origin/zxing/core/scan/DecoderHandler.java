package android.origin.zxing.core.scan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import android.origin.zxing.core.Transform;
import com.google.zxing.Result;

import java.lang.ref.WeakReference;


/**
 * Created by you on 2018-04-26.
 * {@link DecodeThread}与主线程 交互
 */
public final class DecoderHandler extends Handler {

    private final DecodeThread decodeThread;

    private SoundVibratorHelper soundVibratorHelper;

    private final WeakReference<DecoderCallback> callbackWeakReference;

    public DecoderHandler(Context context, int w, int h, Transform transform, DecoderCallback callback) {
        this(context, w, h, new FormatDecoder(), transform, true, callback);
    }

    public DecoderHandler(Context context, int w, int h, Transform transform, boolean successQuit, DecoderCallback callback) {
        this(context, w, h, new FormatDecoder(), transform, successQuit, callback);
    }

    public DecoderHandler(Context context, int w, int h, FormatDecoder decoder,
                          Transform transform, boolean successQuit, DecoderCallback callback) {
        callbackWeakReference = new WeakReference<>(callback);
        decodeThread = new DecodeThread(w, h, decoder, this, transform, successQuit);
        soundVibratorHelper = new SoundVibratorHelper(context);
        decodeThread.start();
    }

    public void push(byte[] data) {
        decodeThread.push(data);
    }

    public void stop() {
        soundVibratorHelper.stop();
        decodeThread.quit();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == DecodeThread.HANDLE_SUCCESS && msg.obj != null) {
            Result result = (Result) msg.obj;
            DecoderCallback callback = callbackWeakReference.get();
            if (callback != null) {
                soundVibratorHelper.play();
                callback.handleResult(result);
            }
        }
    }

    /**
     * 回调处理
     */
    public interface DecoderCallback {
        void handleResult(Result result);
    }
}
