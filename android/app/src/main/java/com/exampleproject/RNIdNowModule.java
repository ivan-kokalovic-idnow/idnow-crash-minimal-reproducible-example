package com.exampleproject;

import java.util.Map;
import java.util.HashMap;
import java.lang.IllegalArgumentException;

import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;

import de.idnow.sdk.IDnowSDK;
import de.idnow.sdk.IDnowSDK.Server;

public class RNIdNowModule extends ReactContextBaseJavaModule {
  private static final String E_INIT_ERROR = "E_INIT_ERROR";
  private static final String E_PARAMS_ERROR = "E_PARAMS_ERROR";
  private static final String E_IDENT_START_ERROR = "E_IDENT_START_ERROR";
  private static final String E_IDENT_CANCELED = "E_IDENT_CANCELED";
  private static final String E_IDENT_FAILED = "E_IDENT_FAILED";
  private static final String E_IDENT_GENERIC_FAIL = "E_IDENT_GENERIC_FAIL";

  private final Map<String, Server> environments = new HashMap<>();
  private final Map<String, IDnowSDK.ConnectionType> connectionTypes = new HashMap<>();
  private final ReactApplicationContext reactContext;
  private Promise mStartPromise = null;

  public RNIdNowModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.reactContext.addActivityEventListener(mActivityEventListener);
    environments.put("test", Server.TEST);
    environments.put("dev", Server.DEV);
    environments.put("live", Server.LIVE);
    environments.put("custom", Server.CUSTOM);
    connectionTypes.put("longPolling", IDnowSDK.ConnectionType.LONG_POLLING);
    connectionTypes.put("websocket", IDnowSDK.ConnectionType.WEBSOCKET);
  }

  @Override
  public String getName() {
    return "RNIdNow";
  }

  @ReactMethod
  public void start(ReadableMap options, Promise promise) {
    ReactApplicationContext context = getReactApplicationContext();
    this.mStartPromise = promise;
    try {
      // Company ID
      String companyId = options.hasKey("companyId") ? options.getString("companyId") : "";
      IDnowSDK.getInstance().initialize(getCurrentActivity(), companyId);

      // Transaction Token
      String transactionToken = options.hasKey("transactionToken") ? options.getString("transactionToken") : "";
      IDnowSDK.setTransactionToken(transactionToken);

      // Environment
      Server environment = options.hasKey("environment") ? environments.get(options.getString("environment")) : Server.LIVE;
      IDnowSDK.setEnvironment(environment);

      if (environment.equals("custom")) {
        if (!options.hasKey("apiHost") || !options.hasKey("webHost") || !options.hasKey("websocketHost")) {
          throw new IllegalArgumentException("When specifying a custom server environment, you need to provide: apiHost, webHost and websocketHost params.");
        }
        IDnowSDK.setApiHost(options.getString("apiHost"), context);
        IDnowSDK.setWebHost(options.getString("webHost"), context);
        IDnowSDK.setWebsocketHost(options.getString("websocketHost"), context);
      }
      if (options.hasKey("videoHost")) {
        IDnowSDK.setVideoHost(options.getString("videoHost"), context);
      }
      if (options.hasKey("stunHost")) {
        IDnowSDK.setStunHost(options.getString("stunHost"), context);
      }
      if (options.hasKey("stunPort")) {
        IDnowSDK.setStunPort(options.getInt("stunPort"), context);
      }
      if (options.hasKey("connectionType")) {
        IDnowSDK.ConnectionType connectionType = this.connectionTypes.get(options.getString("connectionType"));
        IDnowSDK.setConnectionType(connectionType, context);
      }

      if (options.hasKey("showVideoOverviewCheck")) {
        IDnowSDK.setShowVideoOverviewCheck(options.getBoolean("showVideoOverviewCheck"), context);
      }

      if (options.hasKey("showErrorSuccessScreen")) {
        IDnowSDK.setShowErrorSuccessScreen(options.getBoolean("showErrorSuccessScreen"), context);
      }

      // Logging
      if (options.hasKey("disableLogging") && options.getBoolean("disableLogging")) {
        IDnowSDK.disableLogging();
      }
    } catch (IllegalArgumentException e) {
      promise.reject(E_PARAMS_ERROR, e);
    } catch (Exception e) {
      promise.reject(E_INIT_ERROR, e);
    }

    // Start the ID process
    try {
      IDnowSDK.getInstance().start(IDnowSDK.getTransactionToken());
    } catch (Exception e) {
      promise.reject(E_IDENT_START_ERROR, e);
    }
  }

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      if (requestCode == IDnowSDK.REQUEST_ID_NOW_SDK) {
        String errorCode = "";
        String errorMessage = "";
        boolean success = false;

        switch (resultCode) {
            case IDnowSDK.RESULT_CODE_SUCCESS:
                success = true;
                break;
            case IDnowSDK.RESULT_CODE_CANCEL:
                errorCode = E_IDENT_CANCELED;
                if (data != null)
                  errorMessage = data.getStringExtra(IDnowSDK.RESULT_DATA_ERROR);
                break;
            case IDnowSDK.RESULT_CODE_FAILED:
                errorCode = E_IDENT_FAILED;
                if (data != null)
                  errorMessage = data.getStringExtra(IDnowSDK.RESULT_DATA_ERROR);
                break;
            default:
                errorCode = E_IDENT_GENERIC_FAIL;
        }

        if (mStartPromise != null) {
          if (success) {
            mStartPromise.resolve(true);
          } else {
            mStartPromise.reject(errorCode, errorMessage);
          }
          mStartPromise = null;
        }
      }
    }
  };
}
