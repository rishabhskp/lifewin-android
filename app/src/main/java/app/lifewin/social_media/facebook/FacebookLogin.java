package app.lifewin.social_media.facebook;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import app.lifewin.interfaces.ISocialMediaLoginResponse;
import app.lifewin.model.app.SocialMediaUserBean;
import app.lifewin.utils.Logger;

public class FacebookLogin {

    private static FacebookLogin uniqInstance;
    private static CallbackManager callbackManager;
    private static Context _context;
    private static ISocialMediaLoginResponse mSocialMediaCallbackInterface;
    /**
     * Private Constructor for not allowing other classes to instantiate this
     * class
     */
    private FacebookLogin() {

    }

    /**
     * @param context of the class calling this method
     * @return instance of this class This method is the global point of access
     * for getting the only one instance of this class
     */
    public static synchronized FacebookLogin getInstance(Context context) {
        if (uniqInstance == null) {
            _context = context;
            uniqInstance = new FacebookLogin();
            callbackManager = CallbackManager.Factory.create();
        }
        mSocialMediaCallbackInterface = (ISocialMediaLoginResponse) context;
        return uniqInstance;
    }


    public void registerLoginCallBack() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Logger.e("Success", "Login");
                        fetchUserProfile(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(_context, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(_context, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    public CallbackManager getCallbackManager() {
        return callbackManager;
    }


    protected void fetchUserProfile(LoginResult loginResult) {

        Profile profile = Profile.getCurrentProfile();
        final String profileImage=profile.getProfilePictureUri(500,500).toString();
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            System.out.println("ERROR");
                        } else {
                            System.out.println("Success");
                            try {
                                SocialMediaUserBean socialMediaUserInfoBean = new SocialMediaUserBean();
                                if (json.has("birthday")) {
                                    socialMediaUserInfoBean.setBirthday(json.getString("birthday"));
                                }
                                if (json.has("email")) {
                                    socialMediaUserInfoBean.setEmail(json.getString("email"));
                                }
                                socialMediaUserInfoBean.setFirstName(json.getString("first_name"));
                                socialMediaUserInfoBean.setLastName(json.getString("last_name"));
                                socialMediaUserInfoBean.setId(json.getString("id"));
                                socialMediaUserInfoBean.setGender(json.getString("gender"));
                                socialMediaUserInfoBean.setProfilepic(profileImage);
                                socialMediaUserInfoBean.setSocialAccessToken(AccessToken.getCurrentAccessToken().getToken());
                                socialMediaUserInfoBean.setSocialType("facebook");

                                mSocialMediaCallbackInterface.onSocialResult(socialMediaUserInfoBean);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,first_name,last_name,birthday");
        request.setParameters(parameters);
        request.executeAsync();



    }

    public void registerShareCallback() {

    }
}
