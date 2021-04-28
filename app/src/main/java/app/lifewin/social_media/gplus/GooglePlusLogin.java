package app.lifewin.social_media.gplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Name;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.io.IOException;
import java.util.List;

import app.lifewin.constants.AppConstants;
import app.lifewin.interfaces.ISocialMediaLoginResponse;
import app.lifewin.model.app.SocialMediaUserBean;
import app.lifewin.utils.Logger;

public class GooglePlusLogin implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    /**
     * Enum for getting the login purpose
     */
    private enum LOGIN_PURPOSE {
        LOGIN_SOCIAL,
        SHARE_DIALOG,
        FETCH_FRIENDS,
        SHARE_ON_CIRCLE
    }

    private static Context mActivity;
    public static GooglePlusLogin instance;
    private static GoogleApiClient mGoogleApiClient;
    private static boolean mIntentInProgress;
    private static boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SocialMediaUserBean mSocialBean;
    private static String accessToken = "";
    private static ISocialMediaLoginResponse mSocialMediaCallbackInterface;


    private List<Person> mFriendList;
    private String sharingText;
    private String sharingUrl;

    private LOGIN_PURPOSE enumLoginP;


    public GooglePlusLogin(Activity activity) {
        this.mActivity = activity;
        setUpGoogleAccount();
    }

    private void setUpGoogleAccount() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

    }

    public static GooglePlusLogin getInstance(Activity activity) {

        if (instance == null) {
            instance = new GooglePlusLogin(activity);
        }
        mActivity = activity;
        mSocialMediaCallbackInterface = (ISocialMediaLoginResponse) activity;
        return instance;
    }

    public void signInGooglePlus() {
        if (!mGoogleApiClient.isConnected()) {
            enumLoginP = LOGIN_PURPOSE.LOGIN_SOCIAL;
            mSignInClicked = true;
            mGoogleApiClient.connect();
        } else {
//            if (accessToken.equalsIgnoreCase("")) {
//                fetchAccessToken();
//            }else{
//                onShareDialog("Testing...", "");
//                fetchFriendList();
                getProfileInformation();
//                mSocialBean.setSocialAccessToken(accessToken);
//                mSocialMediaCallbackInterface.onSocialResult(mSocialBean);
//            }
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), (Activity)mActivity,
                    0).show();
            return;
        }
        if (result.SIGN_IN_REQUIRED == 4 && !mGoogleApiClient.isConnected() && mIntentInProgress) {
            mIntentInProgress = false;
        }
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
//        if (accessToken.equalsIgnoreCase("")) {
//            fetchAccessToken();
//        } else {
            getProfileInformation();
//            mSocialBean.setSocialAccessToken(accessToken);
//            mSocialMediaCallbackInterface.onSocialResult(mSocialBean);
//        }
    }

    public void onShareDialog(String text, String url) {
        enumLoginP= LOGIN_PURPOSE.SHARE_DIALOG;
        if (TextUtils.isEmpty(url)) {
            Intent shareIntent = new PlusShare.Builder(mActivity)
                    .setType("text/plain")
                    .setText(text)
                    .getIntent();
            ((Activity)mActivity).startActivityForResult(shareIntent, 0);
        } else {
            Intent shareIntent = new PlusShare.Builder(mActivity)
                    .setType("text/plain")
                    .setText(text)
                    .setContentUrl(Uri.parse(url))
                    .getIntent();
            ((Activity)mActivity).startActivityForResult(shareIntent, 0);
        }

    }

    public void onShareDialog(String text, String url, List<Person> friendList) {
        enumLoginP= LOGIN_PURPOSE.SHARE_ON_CIRCLE;
        mFriendList = friendList;
        sharingText=text;
        sharingUrl=url;
    }

    private void shareWithFriends(){
        if(TextUtils.isEmpty(sharingUrl)){
            Intent shareIntent = new PlusShare.Builder(mActivity)
                    .setType("text/plain")
                    .setText(sharingText)
                    .setRecipients(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mFriendList)
                    .getIntent();
            ((Activity)mActivity).startActivityForResult(shareIntent, 0);
        }else{
            Intent shareIntent = new PlusShare.Builder(mActivity)
                    .setType("text/plain")
                    .setText(sharingText)
                    .setRecipients(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient), mFriendList)
                    .setContentUrl(Uri.parse(sharingUrl))
                    .getIntent();
            ((Activity)mActivity).startActivityForResult(shareIntent, 0);
        }

    }


    public void fetchFriendList() {
        enumLoginP= LOGIN_PURPOSE.FETCH_FRIENDS;
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(GooglePlusLogin.this);
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(((Activity)mActivity), AppConstants.GOOGLE_PLUS_LOGIN_FLAG);
                } catch (SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                        Person currentPerson = Plus.PeopleApi
                                .getCurrentPerson(mGoogleApiClient);
                        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                        mSocialBean = new SocialMediaUserBean();
                        mSocialBean.setEmail(email);
                        mSocialBean.setSocialAccessToken(accessToken);
                        mSocialBean.setId(currentPerson.getId());
                        mSocialBean.setSocialType("googleplus");
                        Name n = currentPerson.getName();
                        mSocialBean.setFirstName(n.getGivenName());
                        String lastName = n.hasMiddleName() ? n.getMiddleName() : "";
                        lastName += n.hasFamilyName() ? n.getFamilyName() : "";
                        mSocialBean.setLastName(lastName);
                        String personPhotoUrl = currentPerson.getImage().getUrl();
                        mSocialBean.setProfilepic(personPhotoUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return accessToken;
            }

            @Override
            protected void onPostExecute(String token) {
                Logger.i("AccessToken", "Access token retrieved:" + token);
                mSocialMediaCallbackInterface.onSocialResult(mSocialBean);
//                onShareMessage();
//                Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
//                        .setResultCallback(GooglePlusLogin.this);


                /*if (mActivity instanceof LoginHomeFragmentActivity && mSocialBean != null) {
                    mSocialBean.setAccessToken(accessToken);
                    ((LoginHomeFragmentActivity) mActivity).googlePlusUserData(mSocialBean);
                }*/
            }

        }.execute(null, null, null);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    public static void updateData(int requestCode) {
        if (requestCode == AppConstants.GOOGLE_PLUS_LOGIN_FLAG) {
            mSignInClicked = false;
        }
        mIntentInProgress = false;

        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    private void fetchAccessToken() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN;
                try {
                    accessToken = GoogleAuthUtil.getToken(mActivity,
                            Plus.AccountApi.getAccountName(mGoogleApiClient),
                            scope);

                    Logger.e("GooglePlus Access Token" + accessToken);

                    getProfileInformation();
                    mSocialBean.setSocialAccessToken(accessToken);

                } catch (IOException transientEx) {
                    Logger.e(GooglePlusLogin.this.toString(), transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    accessToken = null;
                } catch (GoogleAuthException authEx) {
                    Logger.e(GooglePlusLogin.this.toString(), authEx.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return accessToken;
            }

            @Override
            protected void onPostExecute(String token) {
                Logger.i("AccessToken", "Access token retrieved:" + token);
                mSocialMediaCallbackInterface.onSocialResult(mSocialBean);
//                onShareMessage();
//                Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
//                        .setResultCallback(GooglePlusLogin.this);


                /*if (mActivity instanceof LoginHomeFragmentActivity && mSocialBean != null) {
                    mSocialBean.setAccessToken(accessToken);
                    ((LoginHomeFragmentActivity) mActivity).googlePlusUserData(mSocialBean);
                }*/
            }

        }.execute(null, null, null);
    }


    public static GoogleApiClient getGoogleApiClientInstance() {
        return mGoogleApiClient;
    }


    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Logger.error("TAG Display name: " + personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }
        } else {
            Logger.error("TAG, Error requesting visible circles: " + peopleData.getStatus());
        }
    }


    public void onShareMessage(String message) {
        Intent shareIntent = new PlusShare.Builder(mActivity)
                .setType("text/plain")
                .setText(message)
                .getIntent();
        ((Activity)mActivity).startActivityForResult(shareIntent, 0);
    }
}
