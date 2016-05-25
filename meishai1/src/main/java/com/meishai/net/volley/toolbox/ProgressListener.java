package com.meishai.net.volley.toolbox;

/**
 * Callback interface for delivering the progress of the responses.
 */
public interface ProgressListener {
    /**
     * Callback method thats called on each byte transfer.
     */
    void onProgress(long transferredBytes, long totalSize);
}
