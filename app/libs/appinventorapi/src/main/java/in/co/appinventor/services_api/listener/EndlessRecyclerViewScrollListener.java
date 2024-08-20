package in.co.appinventor.services_api.listener;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/* renamed from: in.co.appinventor.services_api.listener.EndlessRecyclerViewScrollListener */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;
    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        this.visibleThreshold *= layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        this.visibleThreshold *= layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;

        for(int i = 0; i < lastVisibleItemPositions.length; ++i) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }

        return maxSize;
    }

    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = this.mLayoutManager.getItemCount();
        if (this.mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager)this.mLayoutManager).findLastVisibleItemPositions((int[])null);
            lastVisibleItemPosition = this.getLastVisibleItem(lastVisibleItemPositions);
        } else if (this.mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager)this.mLayoutManager).findLastVisibleItemPosition();
        } else if (this.mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager)this.mLayoutManager).findLastVisibleItemPosition();
        }

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
        }

        if (!this.loading && lastVisibleItemPosition + this.visibleThreshold > totalItemCount) {
            ++this.currentPage;
            this.onLoadMore(this.currentPage, totalItemCount, view);
            this.loading = true;
        }

    }

    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public abstract void onLoadMore(int var1, int var2, RecyclerView var3);
}
