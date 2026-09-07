package ca.datamagic.radiotester.async;

import java.util.logging.Logger;

import ca.datamagic.radiotester.util.FaviconParser;

public class FaviconAsync extends AsyncTaskBase<String> {
    private static final Logger logger = Logger.getLogger(FaviconAsync.class.getName());
    private String url = null;

    public FaviconAsync(String url) {
        this.url = url;
    }

    @Override
    protected AsyncTaskResult<String> doInBackground() {
        try {
            String iconURL = FaviconParser.getFaviconUrl(url);
            return new AsyncTaskResult<>(iconURL);
        } catch (Throwable t) {
            return new AsyncTaskResult<>(t);
        }
    }
}
