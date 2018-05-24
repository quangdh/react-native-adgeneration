package com.github.chuross.rn;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNAdGenerationModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNAdGenerationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAdGeneration";
    }
}