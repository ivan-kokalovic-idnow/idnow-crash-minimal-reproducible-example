package com.exampleproject;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import de.idnow.core.IDnowConfig;
import de.idnow.core.IDnowResult;
import de.idnow.core.IDnowSDK;

@ReactModule(name = AutoidentModule.NAME)
public class AutoidentModule extends ReactContextBaseJavaModule implements IDnowSDK.IDnowResultListener {
    public static final String NAME = "Autoident";

    private static final String E_IDENT_CANCELED = "E_IDENT_CANCELED";
    private static final String E_IDENT_FAILED = "E_IDENT_FAILED";
    private static final String E_IDENT_GENERIC_FAIL = "E_IDENT_GENERIC_FAIL";
    private static final String E_IDENT_FINISHED = "E_IDENT_FINISHED";

    private final ReactApplicationContext reactContext;
    private Promise mStartPromise = null;
    private IDnowSDK idnowSdk;

    public AutoidentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public void onIdentResult(IDnowResult iDnowResult) {
        if (iDnowResult.getResultType() == IDnowResult.ResultType.FINISHED) {
             this.mStartPromise.resolve(E_IDENT_FINISHED);
        } else if (iDnowResult.getResultType() == IDnowResult.ResultType.CANCELLED) {
            this.mStartPromise.resolve(E_IDENT_CANCELED);
        } else if (iDnowResult.getResultType() == IDnowResult.ResultType.ERROR) {
            this.mStartPromise.resolve(E_IDENT_FAILED);
        }
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void startAutoIdent(String identId, Promise promise) {
        this.mStartPromise = promise;
        try {
            Context context = getReactApplicationContext();
            IDnowConfig iDnowConfig = IDnowConfig.Builder.getInstance().build();
            idnowSdk = IDnowSDK.getInstance();
            idnowSdk.initialize(getCurrentActivity(), iDnowConfig);
            IDnowSDK.getInstance().startIdent(identId, this);
        } catch (Exception e) {
            promise.resolve(E_IDENT_GENERIC_FAIL);
        }
    }
}
