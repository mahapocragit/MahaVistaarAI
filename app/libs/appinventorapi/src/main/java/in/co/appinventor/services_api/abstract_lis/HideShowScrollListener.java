package in.co.appinventor.services_api.abstract_lis;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: in.co.appinventor.services_api.abstract_lis.HideShowScrollListener */
public abstract class HideShowScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;
    private boolean controlsVisible = true;
    private int scrolledDistance = 0;

    public abstract void onHide();

    public abstract void onShow();

    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        HideShowScrollListener.super.onScrolled(recyclerView, dx, dy);
        if (this.scrolledDistance > 20 && this.controlsVisible) {
            onHide();
            this.controlsVisible = false;
            this.scrolledDistance = 0;
        } else if (this.scrolledDistance < -20 && !this.controlsVisible) {
            onShow();
            this.controlsVisible = true;
            this.scrolledDistance = 0;
        }
        if ((this.controlsVisible && dy > 0) || (!this.controlsVisible && dy < 0)) {
            this.scrolledDistance += dy;
        }
    }
}
