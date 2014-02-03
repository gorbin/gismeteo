package com.gink.samples.location;

import android.location.Location;
import android.util.Log;

import com.gink.samples.BuildConfig;

public interface LocationFound {
    void locationFound(Location location);
}
