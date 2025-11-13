package com.ThePod.Admirals.network.callback;

import com.ThePod.Admirals.exception.AdmiralsException;

public interface ConnectionCallback {
    /**
     * Method called when connection to host is successful
     */
    void onConnect();

    /**
     * Method called when connection was unsuccessful or disconnection was successful
     * @param e exception if unsuccessful, null if success
     */
    void onDisconnect(AdmiralsException e);
}
