package in.co.appinventor.services_api.listener;

import android.widget.AbsListView;

/* renamed from: in.co.appinventor.services_api.listener.EndlessScrollListener */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int currentPage = 0;
    private boolean loading = true;
    private int previousTotalItemCount = 0;
    private int startingPageIndex = 1;
    private int visibleThreshold = 10;

    public abstract void onLoadMore(int i, int i2);

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold2) {
        this.visibleThreshold = visibleThreshold2;
    }

    public EndlessScrollListener(int visibleThreshold2, int startPage) {
        this.visibleThreshold = visibleThreshold2;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount < this.previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        if (this.loading && totalItemCount > this.previousTotalItemCount) {
            this.loading = false;
            this.previousTotalItemCount = totalItemCount;
            this.currentPage++;
        }
        if (!this.loading && totalItemCount - visibleItemCount <= this.visibleThreshold + firstVisibleItem) {
            onLoadMore(this.currentPage + 1, totalItemCount);
            this.loading = true;
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
