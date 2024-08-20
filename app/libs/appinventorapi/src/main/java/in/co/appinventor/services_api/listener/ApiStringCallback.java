package in.co.appinventor.services_api.listener;

/* renamed from: in.co.appinventor.services_api.listener.ApiStringCallback */
public interface ApiStringCallback {
    void onFailure(Throwable th, int i);

    void onResponse(String str, int i);
}
