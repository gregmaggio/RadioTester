package ca.datamagic.radiotester.async;

public class AsyncTaskResult<T> {
    private T result = null;
    private Throwable throwable = null;

    public AsyncTaskResult() {
        super();
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Throwable throwable) {
        super();
        this.throwable = throwable;
    }

    public T getResult() {
        return this.result;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
