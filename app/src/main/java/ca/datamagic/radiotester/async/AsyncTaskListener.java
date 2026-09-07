package ca.datamagic.radiotester.async;

public interface AsyncTaskListener<T> {
    public void completed(AsyncTaskResult<T> result);
}
