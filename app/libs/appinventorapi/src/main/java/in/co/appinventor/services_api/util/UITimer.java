package in.co.appinventor.services_api.util;

import android.os.Handler;

import in.co.appinventor.services_api.debug.DebugLog;

/* renamed from: in.co.appinventor.services_api.util.UITimer */
public class UITimer {
    /* access modifiers changed from: private */
    public boolean enabled;
    /* access modifiers changed from: private */
    public Handler handler;
    /* access modifiers changed from: private */
    public int intervalMs;
    /* access modifiers changed from: private */
    public boolean oneTime;
    /* access modifiers changed from: private */
    public Runnable runMethod;
    /* access modifiers changed from: private */
    public Runnable timer_tick;

    public UITimer(Handler handler2, Runnable runMethod2, int intervalMs2) {
        this.enabled = false;
        this.oneTime = false;
        this.timer_tick = new Runnable() {
            public void run() {
                if (UITimer.this.enabled) {
                    UITimer.this.handler.post(UITimer.this.runMethod);
                    if (UITimer.this.oneTime) {
                        boolean unused = UITimer.this.enabled = false;
                    } else {
                        UITimer.this.handler.postDelayed(UITimer.this.timer_tick, (long) UITimer.this.intervalMs);
                    }
                }
            }
        };
        this.handler = handler2;
        this.runMethod = runMethod2;
        this.intervalMs = intervalMs2;
    }

    public UITimer(Handler handler2, Runnable runMethod2, int intervalMs2, boolean oneTime2) {
        this(handler2, runMethod2, intervalMs2);
        this.oneTime = oneTime2;
    }

    public void start() {
        if (!this.enabled) {
            if (this.intervalMs < 1) {
                DebugLog.getInstance().e("timer start: Invalid interval:" + this.intervalMs);
                return;
            }
            this.enabled = true;
            this.handler.postDelayed(this.timer_tick, (long) this.intervalMs);
        }
    }

    public void stop() {
        if (this.enabled) {
            this.enabled = false;
            this.handler.removeCallbacks(this.runMethod);
            this.handler.removeCallbacks(this.timer_tick);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
