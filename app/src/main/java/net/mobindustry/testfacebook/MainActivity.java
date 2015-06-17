package net.mobindustry.testfacebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivityTag";

    private CallbackManager callbackManager;

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i(TAG, "Result: " + loginResult.getRecentlyGrantedPermissions());
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel");
        }

        @Override
        public void onError(FacebookException exception) {
            Log.i(TAG, "onError");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
        setupButtons();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupButtons() {

        findViewById(R.id.custom_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "public_profile" permission by default
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("user_friends"));
//                List<String> permissionNeeds = Arrays.asList("publish_actions");
//                LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this, permissionNeeds);
            }
        });

        findViewById(R.id.me_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.i(TAG, "object: " + object);
                                    Log.i(TAG, "response: " + response);
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

            }
        });

        findViewById(R.id.me_request_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, "/me", new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            Log.i(TAG, "graphResponse: " + graphResponse);
                        }
                    });
                    request.executeAsync();
                }
            }
        });

        findViewById(R.id.request_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this, Arrays.asList("publish_actions"));
            }
        });

        findViewById(R.id.check_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
                Log.i(TAG, "permissions: " + permissions);
            }
        });


    }
}
