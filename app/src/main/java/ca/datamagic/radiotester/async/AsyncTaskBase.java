package ca.datamagic.radiotester.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class AsyncTaskBase<Result> implements Runnable {
    private static final Logger logger = Logger.getLogger(AsyncTaskBase.class.getName());
    private static final Executor executor = new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private List<AsyncTaskListener<Result>> listeners = new ArrayList<>();

    public void addListener(AsyncTaskListener<Result> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AsyncTaskListener<Result> listener) {
        this.listeners.remove(listener);
    }

    protected void fireCompleted(AsyncTaskResult<Result> result) {

        for (AsyncTaskListener<Result> listener : this.listeners) {
            try {
                listener.completed(result);
            } catch (Throwable t) {
                logger.warning("Throwable: " + t.getMessage());
            }
        }
    }

    public void execute() {
        this.executor.execute(this);
    }

    public void run() {
        AsyncTaskResult<Result> result = null;
        try {
            result = doInBackground();
        } catch (Throwable t) {
            result = new AsyncTaskResult<>(t);
        }
        fireCompleted(result);
    }

    protected abstract AsyncTaskResult<Result> doInBackground();
}
