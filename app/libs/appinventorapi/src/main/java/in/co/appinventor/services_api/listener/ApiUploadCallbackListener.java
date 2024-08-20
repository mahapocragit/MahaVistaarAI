package in.co.appinventor.services_api.listener;

/* renamed from: in.co.appinventor.services_api.listener.ApiUploadCallbackListener */
public interface ApiUploadCallbackListener {
    void onFailure(int i, int i2, String str);

    void onProgress(long j, long j2);

    void onSuccess(int i, int i2, String str);
}
