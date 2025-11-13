package com.ThePod.Admirals.network.callback;

import com.ThePod.Admirals.exception.AdmiralsException;

public interface ConnectionCallback {
    void onConnect();
    void onDisconnect(AdmiralsException e);
}
