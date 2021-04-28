package app.lifewin.parse.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.utils.AppUtils;
import app.lifewin.utils.Logger;


public class ParseUtils {

    public static ParseUtils parseUtils;


    public static ParseUtils getInstance() {
        if (parseUtils == null) {
            parseUtils = new ParseUtils();
        }
        return parseUtils;
    }


    /**
     * Method for fetching the data from database.
     *
     * @param context     Context of callback
     * @param tableName   Table name on which need to excute the fetch query.
     * @param whereClause Table fields name
     * @param argsValues  Arguments values
     */
    public void onFetchPQuery(final Context context, String tableName, List<String> whereClause, List<String> argsValues) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        if (whereClause.size() == argsValues.size()) {
            for (int i = 0; i < whereClause.size(); i++) {
                query.whereEqualTo(whereClause.get(i), argsValues.get(i));
            }
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(scoreList, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                        }
                    });
                    ((IParseQueryResult) context).onParseQuerySuccess(scoreList);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });

    }


    /**
     * Method for fetching the data from database.
     *
     * @param context     Context of callback
     * @param tableName   Table name on which need to excute the fetch query.
     * @param whereClause Table fields name
     * @param argsValues  Arguments values
     */
    public void onFetchCompletedTaskQuery(final Context context, String tableName, List<String> whereClause, List<String> argsValues) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        if (whereClause.size() == argsValues.size()) {
            for (int i = 0; i < whereClause.size(); i++) {
                query.whereEqualTo(whereClause.get(i), argsValues.get(i));
            }
        }
        if (MySharedPreference.getInstance().getCompletedTaskSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(scoreList, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            MySharedPreference.getInstance().setCompletedTaskSyncDatabase(true);
                        }
                    });
                    ((IParseQueryResult) context).onParseQuerySuccess(scoreList);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });

    }

    /**
     * Method is usedfor saving the points information with relation.
     *
     * @param context
     * @param tableName
     * @param mValues
     */
    public void onSavePointsPQuery(final IParseQueryResult context, final String tableName, final HashMap<String, Object> mValues) {
        ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.put("user_id", ParseUser.getCurrentUser());
        parseObject.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String response = "";
                    if (tableName.equalsIgnoreCase(AppConstants.USERS_POINTS)) {
                        response = "{'code':200,'table_name':" + tableName + ",'points':" + mValues.get("points") + "}";
                    } else {
                        response = "{'code':200,'table_name':" + tableName + "}";
                    }

                    ((IParseQueryResult) context).onParseQuerySuccess(response);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
    }


    /**
     * Method for saving the objects in table.
     *
     * @param context   Context for callback
     * @param tableName Table name in which we need to insert data.
     * @param mValues   Values in HashMap with key and values for save.
     */
    public void onSavePQuery(final Context context, final String tableName, final HashMap<String, Object> mValues) {
        ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String response = "";
                    if (tableName.equalsIgnoreCase(AppConstants.USERS_POINTS)) {
                        response = "{'code':200,'table_name':" + tableName + ",'points':" + mValues.get("points") + "}";
                    } else {
                        response = "{'code':200,'table_name':" + tableName + "}";
                    }

                    ((IParseQueryResult) context).onParseQuerySuccess(response);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method for saving the objects in table.
     *
     * @param context   Context for callback
     * @param tableName Table name in which we need to insert data.
     * @param mValues   Values in HashMap with key and values for save.
     */
    public void onSavePQuery(final Context context, String tableName, HashMap<String, Object> mValues, final ParseUser user, final boolean isUpdateSetting) {
        ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (isUpdateSetting) {
                        user.put("is_setting", true);
                        user.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    ((IParseQueryResult) context).onParseQuerySuccess(user);
                                } else {
                                    ((IParseQueryResult) context).onParseQueryFail(e);
                                }
                            }
                        });
                    } else {
                        ((IParseQueryResult) context).onParseQuerySuccess(user);
                    }
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method is used for uploading the user profile image.
     *
     * @param context  Context for callback.
     * @param filePath FilePath for uploading.
     */
    public void onUploadUserImagePQuery(final Context context, String filePath) {
        try {
            byte[] image = getBytesFromFile(new File(filePath));
            if (image != null) {
                final ParseFile file = new ParseFile("user_img.jpg", image);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            onUpdateUserImagePQuery(context, file);
                        } else {
                            ((IParseQueryResult) context).onParseQueryFail(e);
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        Logger.e("Percentages:-" + integer);
                    }
                });
            }
        } catch (IOException e) {
            if (e.getMessage().equalsIgnoreCase("File is too large!")) {
                ((IParseQueryResult) context).onParseQueryFail(e);
            } else {
                ((IParseQueryResult) context).onParseQueryFail(e);
            }
        }


    }

    /**
     * Returns the contents of the file in a byte array.
     *
     * @param file File which need to upload
     * @return Return the byte array of file.
     * @throws IOException Exception in case file size exceed from limit to upload and file content is not fully copied
     */
    private byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > 10000000) {
            // File is too large
//                return null;
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;

    }


    /**
     * This method is used for updating the user profile.
     *
     * @param context Context for callback
     * @param file    ParseFile object for uploading image.
     */
    public void onUpdateUserImagePQuery(@NonNull final Context context, ParseFile file) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("user_image", file);
        user.put("image", "");
        // Create the class and the columns
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String valueJson = "{'status':true,'key':'user_image','value':'image_path'}";
                    ((IParseQueryResult) context).onParseQuerySuccess(valueJson);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }

            }
        });
    }

    /**
     * This method is used for fetch the image from the parse server.
     *
     * @param context   Context for callback
     * @param userImage ParseFile object for downloading
     */
    public void onRetrieveImageFromParse(final IParseQueryResult context, ParseFile userImage) {
        userImage.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    // Decode the Byte[] into
                    // Bitmap
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    // Set the Bitmap into the
                    // ImageView
                    ((IParseQueryResult) context).onParseQuerySuccess(bmp);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });

    }

    /**
     * Method is used for signup witn image.
     *
     * @param context      Context for callback.
     * @param userName     Username of the user it must be unique
     * @param password     Password of the user.
     * @param emailAddress Email for user signup.
     * @param mValues      Extras Values of user.
     * @param filePath     FilePath for uploading.
     */
    public void onSignUpWithImagePQuery(final Context context, @NonNull final String userName, @NonNull final
    String password, @NonNull final String emailAddress, final HashMap<String, Object> mValues, String filePath) {
        try {
            byte[] image = getBytesFromFile(new File(filePath));
            if (image != null) {
                final ParseFile file = new ParseFile("user_img.jpg", image);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            mValues.put("image", file);
                            onSignUpPQuery(context, userName, password, emailAddress, mValues);
                        } else {
                            ((IParseQueryResult) context).onParseQueryFail(e);
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        Logger.e("Percentages:-" + integer);
                    }
                });
            }
        } catch (IOException e) {
            if (e.getMessage().equalsIgnoreCase("File is too large!")) {
                ((IParseQueryResult) context).onParseQueryFail(e);
            } else {
                ((IParseQueryResult) context).onParseQueryFail(e);
            }
        }


    }

    /**
     * Method is used for signup account on the parse.
     *
     * @param context      Context for getting the callback.
     * @param userName     Username of the user it must be unique
     * @param password     Password of the user.
     * @param emailAddress Email for user signup.
     * @param mValues      Extras Values of user.
     */
    public void onSignUpPQuery(@NonNull final Context context, @NonNull final String userName, @NonNull final
    String password, @NonNull String emailAddress, final HashMap<String, Object> mValues) {
        final ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(emailAddress);
        if (mValues != null) {
            for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                user.put(key, value);
            }
        }
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    if (mValues.get("user_type").equals("app")) {
                        String resp = "{'status':200,'response':'success','type':'app'}";
                        ((IParseQueryResult) context).onParseQuerySuccess(resp);
                    } else {
                        //TODO Make
                        onLoginPQuery(context, userName, password, true);
                    }

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });

    }

    /**
     * Method for changing the password.
     *
     * @param mContext
     * @param newpassword
     */
    public void onChangePassword(@NonNull final Context mContext, @NonNull final String newpassword) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.setPassword(newpassword);
        try {
            byte[] data = new byte[0];
            data = newpassword.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            currentUser.put("token", base64);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        onSendEmailByCloud(mContext, currentUser.getEmail(), "Change Password", "Hello " + currentUser.getString("name") + ",\n\n Your new password is :- " + newpassword, false);
                        String resp = "{'status':true,'key':'password'}";
                        ((IParseQueryResult) mContext).onParseQuerySuccess(resp);
                    } else {
                        ((IParseQueryResult) mContext).onParseQueryFail(e);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    /**
     * Method for login in parse. If your application does not have the user name in that case you
     * can pass the email address as a user name.
     *
     * @param context       Context for getting the callback.
     * @param userName      UserName for login.
     * @param password      Password for login
     * @param createDefault Boolean for creating the default Setting table for user.
     */
    public void onLoginPQuery(@NonNull final Context context, @NonNull final String userName, @NonNull final String password, final boolean createDefault) {
        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    if (user.getBoolean("emailVerified") || !user.getString("user_type").equalsIgnoreCase("app")) {
                        if (!user.getBoolean("is_setting")) {
                            HashMap<String, Object> mValues = new HashMap<String, Object>();
                            mValues.put(AppConstants.POMODORO_METER, context.getString(R.string.txt_25m_5m));
                            mValues.put(AppConstants.HOURLY_RATE, context.getString(R.string.txt_10));
                            mValues.put(AppConstants.FIRST_DAY, context.getString(R.string.txt_monday));
                            mValues.put(AppConstants.USER_ID, user.getObjectId());
                            onSavePQuery(context, AppConstants.USER_SETTINGS, mValues, user, true);
                            try {
                                byte[] data = Base64.decode(user.getString("token"), Base64.DEFAULT);
                                String password = null;
                                password = new String(data, "UTF-8");
//                                String userNamet="vivek@appstudioz.com";
                                onSendEmailByCloud(context, userName, "New Sign Up", "Hello " + user.getString("name") + "\n\n Your password is :- " + password, false);
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            ((IParseQueryResult) context).onParseQuerySuccess(user);
                        }
                    } else {
                        String resp = "{'status':201,'response':'failed'}";
                        ((IParseQueryResult) context).onParseQuerySuccess(resp);
                    }
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }


    public void onSocialLoginPQuery(@NonNull final Context context, @NonNull final String userName, final HashMap<String, Object> mValues) {
        ParseUser.getQuery().whereEqualTo("email", userName).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                ParseUser onp = list.get(0);
//                onp.
            }
        });
    }


    public void onCustomOwnLogin(final Context context, String session_token) {
        ParseUser.becomeInBackground(session_token, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    ((IParseQueryResult) context).onParseQuerySuccess(parseUser);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    public void onSocialLoginPQuery(@NonNull final Context context, @NonNull final String userName, @NonNull final String password, @NonNull final String email, final HashMap<String, Object> mValues) {
        ParseUser.getQuery().whereEqualTo("email", email).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseUser onp = list.get(0);
                        try {
                            byte[] data = Base64.decode(onp.getString("token"), Base64.DEFAULT);
                            final String password = new String(data, "UTF-8");
                            ParseUser.logInInBackground(userName, password, new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        ((IParseQueryResult) context).onParseQuerySuccess(user);
                                    } else {
                                        if (e.getCode() == 101) {
                                            onSignUpPQuery(context, userName, password, email, mValues);
                                        } else {
                                            ((IParseQueryResult) context).onParseQueryFail(e);
                                        }
                                    }
                                }
                            });

                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                            ((IParseQueryResult) context).onParseQueryFail(e1);
                        }
                    } else {
                        onSignUpPQuery(context, userName, password, email, mValues);
                    }
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
        /*ParseUser.logInInBackground(userName, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {

                if (user != null) {
                    // Hooray! The user is logged in.
                    ((IParseQueryResult) context).onParseQuerySuccess(user);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    if (e.getCode() == 101) {

                    } else {
                        ((IParseQueryResult) context).onParseQueryFail(e);
                    }
                }
            }
        });*/

    }

    /**
     * Method for reset the password/forgot password.
     *
     * @param context Context for getting the callback.
     * @param email   Email for getting the reset password link.
     */
    public void onForgotPasswordPQuery(@NonNull final Context context, @NonNull String email) {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // An email was successfully sent with reset instructions.
                    ((IParseQueryResult) context).onParseQuerySuccess(true);
                } else {
                    // Something went wrong. Look at the ParseException to see what's up.
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method for sending the password to user.
     *
     * @param context Context for getting the callback.
     * @param email   Email for getting the reset password link.
     */
    public void onForgotPasswordByEmailPQuery(@NonNull final Context context, @NonNull final String email) {
        ParseUser.getQuery().whereEqualTo("email", email).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseUser onp = list.get(0);
                        try {
                            byte[] data = Base64.decode(onp.getString("token"), Base64.DEFAULT);
                            String password = new String(data, "UTF-8");
//                            String email = "vivek@appstudioz.com";
                            onSendEmailByCloud(context, email, "Forgot Password", "Hello " + onp.getString("name") + "\n\n Your password is :- " + password, true);
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                            ((IParseQueryResult) context).onParseQueryFail(e1);
                        }
                    } else {
                        String resp = "{'status':201}";
                        ((IParseQueryResult) context).onParseQuerySuccess(resp);
                    }
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }


    /**
     * sendthe email to
     */
    private void onSendEmailByCloud(final Context context, String toEmail, String title, String message, final boolean isCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("text", message);
        params.put("subject", title);
        params.put("fromEmail", "lifewin@gmail.com");
        params.put("fromName", "LifeWin");
        params.put("toEmail", toEmail);
        params.put("toName", "User");
        ParseCloud.callFunctionInBackground("sendMailMailgun", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (isCallback) {
                    if (exc == null) {
                        String resp = "{'status':200}";
                        ((IParseQueryResult) context).onParseQuerySuccess(resp);
                    } else {
                        ((IParseQueryResult) context).onParseQueryFail(exc);
                    }
                }
            }
        });
    }

    /**
     * Method for delete the object from the database based on the object id.
     *
     * @param context   Context for getting the callback.
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to delete.
     */
    public void onDeleteObjectPQuery(@NonNull final Context context, @NonNull String tableName, @NonNull String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ((IParseQueryResult) context).onParseQuerySuccess(true);
                            } else {
                                ((IParseQueryResult) context).onParseQueryFail(e);
                            }
                        }
                    });
                } else {
                    // something went wrong
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }


    /**
     * Method for remove a single column from the table.
     *
     * @param context   Context for getting the callback.
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to delete.
     * @param fieldName Field name for remove from table.
     */
    public void onDeleteSingleFieldPQuery(@NonNull final Context context, @NonNull String tableName, @NonNull String objectId, @NonNull final String fieldName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.getInBackground(objectId, new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    object.remove(fieldName);

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ((IParseQueryResult) context).onParseQuerySuccess(true);
                            } else {
                                ((IParseQueryResult) context).onParseQueryFail(e);
                            }
                        }
                    });
                } else {
                    // something went wrong
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method for update a object in the table by object Id.
     *
     * @param context   Context for getting the callback.
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to update.
     * @param mValues   Values in HashMap with key and values for update.
     */
    public void onUpdateObjectByIdPQuery(@NonNull final IParseQueryResult context, @NonNull String tableName, @NonNull String objectId, final HashMap<String, Object> mValues) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        // Retrieve the object by id
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        object.put(key, value);
                    }
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ((IParseQueryResult) context).onParseQuerySuccess(true);
                            } else {
                                ((IParseQueryResult) context).onParseQueryFail(e);
                            }
                        }
                    });
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method is used for updating the parse user table.
     *
     * @param context
     * @param mValues
     */
    public void onUpdateParseUser(final Context context, final HashMap<String, Object> mValues) {
        ParseUser user = ParseUser.getCurrentUser();
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            user.put(key, value);
        }
        user.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String key = "";
                    Object value = "";
                    for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                        key = entry.getKey();
                        value = entry.getValue();
                    }
                    String valueJson = "{'status':true,'key':" + key + ",'value':'" + value + "'}";
                    ((IParseQueryResult) context).onParseQuerySuccess(valueJson);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
        user.saveEventually(new SaveCallback() {
            public void done(ParseException e) {

            }
        });
    }

    /**
     * Method for delete the object from the database based on the object id.
     *
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to delete.
     */
    public void onDeletePQuery(@NonNull final IParseQueryResult iParseQueryResult, @NonNull final String tableName, @NonNull String objectId,
                               @NonNull String timeInMillies) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromLocalDatastore();
        if (TextUtils.isEmpty(objectId)) {
            query.whereEqualTo("created_time_millis", timeInMillies);
        } else {
            query.whereEqualTo("objectId", objectId);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final List<ParseObject> list1 = list;
                        final ParseObject object = list.get(0);
                        // Release any objects previously pinned for this query.
                        ParseObject.unpinAllInBackground(object.getObjectId(), new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                                    return;
                                } else {
                                    String valueJson = "{'code':200,'msg':'success'}";
                                    iParseQueryResult.onParseQuerySuccess(valueJson);
                                }
                            }
                        });
                        object.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                } else {
                    // something went wrong
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });


    }


    /**
     * Method for update a object in the table by field.
     *
     * @param iParseQueryResult Interface for Callback
     * @param tableName         Table name on which need to execute the fetch query.
     * @param whereClause       Table fields name
     * @param argsValues        Arguments values
     * @param mValues           Values in HashMap with key and values for update.
     */
    public void onUpdateObjectByFieldPQuery(final IParseQueryResult iParseQueryResult, String tableName, List<String> whereClause, List<String> argsValues, final HashMap<String, Object> mValues) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        if (whereClause.size() == argsValues.size()) {
            for (int i = 0; i < whereClause.size(); i++) {
                query.whereEqualTo(whereClause.get(i), argsValues.get(i));
            }
        }
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    final ParseObject object = scoreList.get(0);
                    for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        object.put(key, value);
                    }
                    object.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                String key = "";
                                Object value = "";
                                for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                                    key = entry.getKey();
                                    value = entry.getValue();
                                }
                                String valueJson = "{'status':true,'key':" + key + ",'value':'" + value + "'}";
                                iParseQueryResult.onParseQuerySuccess(valueJson);
                            } else {
                                iParseQueryResult.onParseQueryFail(e);
                            }
                        }
                    });
                    object.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Logger.error("SAved...");
                            } else {
                                Logger.error("Failed...");
                            }
                        }
                    });
                   /* object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                        }
                    });*/
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });

    }

    public void onLogoutPUser(final IParseQueryResult iParseQueryResult) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String valueJson = "{'code':201,'key': 'logout','value':true}";
                    iParseQueryResult.onParseQuerySuccess(valueJson);
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method is used for fetching the weekly points.
     *
     * @param context   Interface to callback
     * @param tableName Table name for fetching the data
     * @param startTime Start time from where fetch the data
     * @param endTime   End time
     */
    public void onFetchWeeklyPoints(final Context context, final String tableName, long startTime, long endTime) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo("date_in_millis", startTime);
        query.whereLessThan("date_in_millis", endTime);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Logger.e("List:-->" + list.size());
                    int userPoints = 0;
                    for (int i = 0; i < list.size(); i++) {
                        userPoints += list.get(i).getInt("points");
                    }
                    MySharedPreference.getInstance(context).setWeeklyPoints(userPoints);
                    String json = "{'code':200,'table_name':'weekly_points'}";
                    ((IParseQueryResult) context).onParseQuerySuccess(json);
                } else {
                    e.printStackTrace();
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }


    /**
     * Method is used for fetching the weekly points.
     *
     * @param context   Interface to callback
     * @param tableName Table name for fetching the data
     * @param startTime Start time from where fetch the data
     * @param endTime   End time
     */
    public void onFetchTotalPoints(final Context context, final String tableName, long startTime, long endTime) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo("date_in_millis", startTime);
        query.whereLessThan("date_in_millis", endTime);
        if (MySharedPreference.getInstance(context).getPointsSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Logger.e("List:-->" + list.size());
                    if (!MySharedPreference.getInstance(context).getPointsSyncDatabase()) {
                        ParseObject.pinAllInBackground(list, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    MySharedPreference.getInstance(context).setPointsSyncDatabase(true);
                                }
                            }
                        });

                    }

                    int userPoints = 0;
                    int todayPoints = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getString("date").equalsIgnoreCase(AppUtils.getCurrentDate()))
                            todayPoints += list.get(i).getInt("points");
                        else
                            userPoints += list.get(i).getInt("points");
                    }
                    MySharedPreference.getInstance(context).setWeeklyPoints(userPoints + todayPoints);
                    MySharedPreference.getInstance(context).setTodayPoints(todayPoints);
                    MySharedPreference.getInstance(context).setLastSyncDate(AppUtils.getCurrentDate());
                    String json = "{'status':200,'code':200,'table_name':'weekly_points'}";
                    ((IParseQueryResult) context).onParseQuerySuccess(json);
                } else {
                    e.printStackTrace();
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method is used for fetching the task list from the database.
     *
     * @param iParseQueryResult
     * @param tableName
     */
    public void onFetchTasksList(final IParseQueryResult iParseQueryResult, final String tableName, long startTime) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo("date_in_millis", startTime);
        query.addDescendingOrder("created_time_millis");
        if (MySharedPreference.getInstance().getTaskSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                MySharedPreference.getInstance().setTaskSyncDatabase(true);
                            }
                        }
                    });
                    ((IParseQueryResult) iParseQueryResult).onParseQuerySuccess(list);
                } else {
                    e.printStackTrace();
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });
    }


    /**
     * Method is used for fetching the task list from the database.
     *
     * @param iParseQueryResult
     * @param tableName
     */
    public void onFetchGoalsList(final IParseQueryResult iParseQueryResult, final String tableName, long startTime) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereGreaterThanOrEqualTo("end_time", startTime);
        onFetchGoalsStatusList(null, AppConstants.USERS_GOALS_STATUS);
        if (MySharedPreference.getInstance().getGoalsSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                MySharedPreference.getInstance().setGoalsSyncDatabase(true);
                            }
                        }
                    });
                    ((IParseQueryResult) iParseQueryResult).onParseQuerySuccess(list);
                } else {
                    e.printStackTrace();
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * Method is used for sync the goals status with the database.
     *
     * @param iParseQueryResult
     * @param tableName
     */
    public void onFetchGoalsStatusList(final IParseQueryResult iParseQueryResult, final String tableName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        if (MySharedPreference.getInstance().getGoalsSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
//                                MySharedPreference.getInstance().setGoalsSyncDatabase(true);
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method is used for switching the task from today to tomorrow etc
     *
     * @param iParseQueryResult
     * @param currentDateInMillies
     * @param objectID
     */
    public void onUpdateTasksID(final IParseQueryResult iParseQueryResult, final String tableName, final long currentDateInMillies, final String objectID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("created_time_millis", currentDateInMillies);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        final ParseObject object = scoreList.get(0);
                        object.setObjectId(objectID);
                        object.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    String response = "{'code':200,'table_name':" + tableName + ",'purpose':'update_task_id'," +
                                            "'object_id':'" + objectID + "','created_time_millis':" + currentDateInMillies + " } ";
                                    iParseQueryResult.onParseQuerySuccess(response);
                                    AppConstants.mTaskList.remove(currentDateInMillies + "");
                                    AppConstants.mGoalsList.remove(currentDateInMillies + "");
                                } else

                                {
                                    iParseQueryResult.onParseQueryFail(e);
                                }
                            }
                        });
                    }
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });
    }

    /**
     * @param iParseQueryResult
     * @param tableName
     * @param mValues
     */
    public void onSaveTaskPQuery(final IParseQueryResult iParseQueryResult, final String tableName, final HashMap<String, Object> mValues) {
        final ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.put("user_id", ParseUser.getCurrentUser());
//        parseObject.setObjectId(System.currentTimeMillis()+"");
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Logger.error("Object ID:--" + parseObject.getObjectId());
                onUpdateTasksID(iParseQueryResult, tableName, parseObject.getLong("created_time_millis"), parseObject.getObjectId());
            }
        });
        parseObject.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String response = "";

                    if (tableName.equalsIgnoreCase(AppConstants.USERS_TASKS)) {
                        AppConstants.mTaskList.add(parseObject.getString("created_time_millis"));
                        Logger.error("Object ID:--" + parseObject.getObjectId());
                        response = "{'code':200,'table_name':" + tableName + ",'purpose':'add_task'," +
                                "'object_id':'" + parseObject.getObjectId() + "','task_name':'" +
                                mValues.get("name") + "','points':" + mValues.get("points") + ",'created_time_millis':" + parseObject.getLong("created_time_millis") + ",'date_in_millis':" + parseObject.getLong("date_in_millis") + " }";
                    } else if (tableName.equalsIgnoreCase(AppConstants.USERS_GOALS)) {
                        AppConstants.mGoalsList.add(parseObject.getString("created_time_millis"));
                        response = "{'code':200,'table_name':'" + tableName + "','purpose':'add_goal'," +
                                "'object_id':'" + parseObject.getObjectId() + "','name':'" +
                                mValues.get("name") + "','created_time_millis':" + parseObject.getLong("created_time_millis") +
                                ",'date_in_millis':" + parseObject.getLong("date_in_millis") + ",'start_time':" + mValues.get("start_time") + ",'end_time':"
                                + mValues.get("end_time") + ",'no_of_days':" + mValues.get("no_of_days") + " }";
                    } else {
                        response = "{'code':200,'table_name':" + tableName + "}";
                    }

                    ((IParseQueryResult) iParseQueryResult).onParseQuerySuccess(response);
                } else {
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });

    }

    /**
     * Method for update a object in the table by object Id.
     *
     * @param context   Context for getting the callback.
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to update.
     * @param mValues   Values in HashMap with key and values for update.
     */
    public void onUpdateTaskDetailsPQuery(@NonNull final IParseQueryResult context, @NonNull String tableName,
                                          @NonNull String objectId, long currentDateInMillies,
                                          final HashMap<String, Object> mValues) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        if (objectId != null && !objectId.equalsIgnoreCase("null")) {
            query.whereEqualTo("objectId", objectId);
        }
        query.whereEqualTo("created_time_millis", currentDateInMillies);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject object = scoreList.get(0);
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            object.put(key, value);
                        }
                        object.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ((IParseQueryResult) context).onParseQuerySuccess(true);
                                } else {
                                    ((IParseQueryResult) context).onParseQueryFail(e);
                                }
                            }
                        });
                        object.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    } else {
                        ((IParseQueryResult) context).onParseQueryFail(e);
                    }
                } else {
                    context.onParseQueryFail(e);
                }
            }
        });


    }


    /**
     * Method is used for switching the task from today to tomorrow etc
     *
     * @param iParseQueryResult
     * @param id
     * @param currentDateInMillies
     * @param updatedValue
     */
    public void onUpdateTasksList(final IParseQueryResult iParseQueryResult, String id, long currentDateInMillies, final long updatedValue) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.USERS_TASKS);
        if (id != null && !id.equalsIgnoreCase("null")) {
            query.whereEqualTo("objectId", id);
        }
        query.whereEqualTo("created_time_millis", currentDateInMillies);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        final ParseObject object = scoreList.get(0);
                        object.put("date_in_millis", updatedValue);
                        object.put("status", AppConstants.STATUS_ARRAY[1]);
                        object.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                } else {
                                    iParseQueryResult.onParseQueryFail(e);
                                }
                            }
                        });
                        object.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });
    }


    /**
     * Method is used for switching the task from today , tomorrow  to Later etc
     *
     * @param iParseQueryResult
     * @param id
     * @param status
     */
    public void onUpdateLaterTasksList(final IParseQueryResult iParseQueryResult, final String id, final long currentDateInMillies, final String status) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.USERS_TASKS);
        if (id != null && !id.equalsIgnoreCase("null")) {
            query.whereEqualTo("objectId", id);
        }
        query.whereEqualTo("created_time_millis", currentDateInMillies);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    final ParseObject object = scoreList.get(0);
                    object.put("status", status);
                    object.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                            } else {
                                iParseQueryResult.onParseQueryFail(e);
                            }
                        }
                    });
                    object.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                        }
                    });
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });
    }

    public void onCompleteTask(final IParseQueryResult iParseQueryResult, String id, long currentDateInMillies, final int points) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.USERS_TASKS);
        if (id != null && !id.equalsIgnoreCase("null")) {
            query.whereEqualTo("objectId", id);
        }
        query.whereEqualTo("created_time_millis", currentDateInMillies);

        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        final ParseObject object = scoreList.get(0);
                        object.put("status", AppConstants.STATUS_ARRAY[0]);
                        object.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    HashMap<String, Object> mValues = new HashMap<String, Object>();
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    String date = sdf.format(new Date());
                                    mValues.put("date", date);
                                    try {
                                        SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
                                        Date dateM = sdfM.parse(date);
                                        mValues.put("date_in_millis", dateM.getTime()); //TODO
                                    } catch (java.text.ParseException ex) {
                                        ex.printStackTrace();
                                        mValues.put("date_in_millis", System.currentTimeMillis()); //TODO
                                    }
                                    mValues.put("points_type", "task");
                                    mValues.put("points", points);
                                    ParseUtils.getInstance().onSavePointsPQuery(iParseQueryResult, AppConstants.USERS_POINTS, mValues);
                                } else {
                                    iParseQueryResult.onParseQueryFail(e);
                                }
                            }
                        });
                        object.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                } else {
                    iParseQueryResult.onParseQueryFail(e);
                }
            }
        });
    }


    public void setGoalCompletedPQuery(final IParseQueryResult context, final String tableName,
                                       final HashMap<String, Object> mValues, final String tagValue, final boolean isRefersh) {
        ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.put("user_id", ParseUser.getCurrentUser());
        parseObject.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String response = "";
                    response = "{'code':200,'table_name':" + tableName + ",'tag_value':'" + tagValue + "','is_refresh':" + isRefersh + ",'type':'add','date_in_millis':" + ((long) mValues.get("date_in_millis")) + "}";
                    ((IParseQueryResult) context).onParseQuerySuccess(response);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

    }


    public void getGoalsStatus(final IParseQueryResult context, String tableName, ArrayList<String> values, ArrayList<Long> mValuesLong) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
//        query.whereContainedIn("goal_id", values);
        query.whereContainedIn("goal_created_time", mValuesLong);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    ((IParseQueryResult) context).onParseQuerySuccess(scoreList);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });

    }


    /**
     * Method for delete the object from the database based on the object id.
     *
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to delete.
     */
    public void onDeleteGoalsPQuery(@NonNull final IParseQueryResult iParseQueryResult, @NonNull final String tableName,
                                    @NonNull final String objectId,
                                    @NonNull long timeInMillies, final String tagValue) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.fromLocalDatastore();
        if (TextUtils.isEmpty(objectId) || objectId.equals("null")) {
            query.whereEqualTo("created_time_millis", timeInMillies);
        } else {
            query.whereEqualTo("objectId", objectId);
        }
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final List<ParseObject> list1 = list;
                        final ParseObject object = list.get(0);

                        ParseObject.unpinAllInBackground(object.getObjectId(), new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                                    return;
                                } else {
                                    deleteGoalsStatus(iParseQueryResult, AppConstants.USERS_GOALS_STATUS, objectId, tagValue);
                                }
                            }
                        });
                        object.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    } else {
//                        String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
//                        iParseQueryResult.onParseQuerySuccess(valueJson);
                    }
                } else {
                    // something went wrong
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });


    }

    public void deleteGoalsStatus(final IParseQueryResult iParseQueryResult, String tableName, String values, final String tagValue) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("goal_id", values);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final List<ParseObject> list1 = list;
                        final ParseObject object = list.get(0);
                        ParseObject.unpinAllInBackground(object.getObjectId(), new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                                    return;
                                } else {
                                    String valueJson = "{'code':200,'msg':'success','table_name':'" +
                                            AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
                                    iParseQueryResult.onParseQuerySuccess(valueJson);
                                }
                            }
                        });
                        object.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
//                                String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
//                                iParseQueryResult.onParseQuerySuccess(valueJson);
                            }
                        });
                    } else {
                        String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
                        iParseQueryResult.onParseQuerySuccess(valueJson);
                    }
                } else {
                    // something went wrong
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });

    }


    public void deleteGoalsDailyStatus(final IParseQueryResult iParseQueryResult, final String tableName, final long values, String goalId, final String tagValue, final boolean isRefersh) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("date_in_millis", values);
        query.whereEqualTo("goal_id", goalId);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final List<ParseObject> list1 = list;
                        final ParseObject object = list.get(0);
                        ParseObject.unpinAllInBackground(object.getObjectId(), new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                                    return;
                                } else {
                                    String valueJson = "{'code':200,'msg':'success','table_name':'" + tableName +
                                            "','tag_value':'" + tagValue + "','is_refresh':" + isRefersh +
                                            ",'type':'delete','date_in_millis':" + values + "}";
                                    iParseQueryResult.onParseQuerySuccess(valueJson);
                                }
                            }
                        });
                        object.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
//                                String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
//                                iParseQueryResult.onParseQuerySuccess(valueJson);
                            }
                        });
                    } else {
                        String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "','tag_value':'" + tagValue + "'}";
                        iParseQueryResult.onParseQuerySuccess(valueJson);
                    }
                } else {
                    // something went wrong
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });

    }


    /**
     * Method for update a object in the table by object Id.
     *
     * @param context   Context for getting the callback.
     * @param tableName Table Name(Object Class Name)
     * @param objectId  Object ID which need to update.
     * @param mValues   Values in HashMap with key and values for update.
     */
    public void onUpdateGoalsDetailsPQuery(@NonNull final IParseQueryResult context, @NonNull String tableName,
                                           @NonNull String objectId, long currentDateInMillies,
                                           final HashMap<String, Object> mValues) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        if (objectId != null && !objectId.equalsIgnoreCase("null")) {
            query.whereEqualTo("objectId", objectId);
        }
        query.whereEqualTo("created_time_millis", currentDateInMillies);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject object = scoreList.get(0);
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            object.put(key, value);
                        }
                        object.pinInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ((IParseQueryResult) context).onParseQuerySuccess(true);
                                } else {
                                    ((IParseQueryResult) context).onParseQueryFail(e);
                                }
                            }
                        });
                        object.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    } else {
                        ((IParseQueryResult) context).onParseQueryFail(e);
                    }
                } else {
                    context.onParseQueryFail(e);
                }
            }
        });


    }


    /**
     * Method is used for saving the WinStreak Data on Server
     *
     * @param context
     * @param tableName
     * @param mValues
     */
    public void setWinStreakPQuery(final IParseQueryResult context, final String tableName,
                                   final HashMap<String, Object> mValues) {
        ParseObject parseObject = new ParseObject(tableName);
        for (Map.Entry<String, Object> entry : mValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            parseObject.put(key, value);
        }
        parseObject.put("user_id", ParseUser.getCurrentUser());
        parseObject.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String response = "";
                    response = "{'code':200,'table_name':" + tableName + ",'created_time':" + ((long)mValues.get("date_in_millis")) + ",'type':'add'}";
                    ((IParseQueryResult) context).onParseQuerySuccess(response);
                } else {
                    ((IParseQueryResult) context).onParseQueryFail(e);
                }
            }
        });
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

    }

    /**
     * Method is used for saving the WinStreak Data on Server
     *
     * @param tableName
     * @param mValues
     */
    public void removeWinStreakPQuery(final IParseQueryResult iParseQueryResult, final String tableName,
                                      final long mValues) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.whereEqualTo("date_in_millis", mValues);
        query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final List<ParseObject> list1 = list;
                        final ParseObject object = list.get(0);
                        ParseObject.unpinAllInBackground(object.getObjectId(), new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e != null) {
                                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                                    return;
                                } else {
                                    String valueJson = "{'code':200,'msg':'success','table_name':'" + tableName +
                                            "','created_time':" + mValues +",'type':'delete'}";
                                    iParseQueryResult.onParseQuerySuccess(valueJson);
                                }
                            }
                        });
                        object.deleteEventually(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                            }
                        });
                    } else {
                        String valueJson = "{'code':200,'msg':'success','table_name':'" + AppConstants.USERS_GOALS + "'}";
                        iParseQueryResult.onParseQuerySuccess(valueJson);
                    }
                } else {
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });

    }

    /**
     * Method is used for fetching the winstreak list from the database.
     *
     * @param iParseQueryResult
     * @param tableName
     */
    public void onFetchWinStreakList(final IParseQueryResult iParseQueryResult, final String tableName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("user_id", ParseUser.getCurrentUser());
        query.addAscendingOrder("date_in_millis");
        if (MySharedPreference.getInstance().getWinStreakSyncDatabase()) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!MySharedPreference.getInstance().getWinStreakSyncDatabase()) {
                        ParseObject.pinAllInBackground(list, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    MySharedPreference.getInstance().setWinStreakSyncDatabase(true);
                                }
                            }
                        });
                    }
                    ((IParseQueryResult) iParseQueryResult).onParseQuerySuccess(list);
                } else {
                    e.printStackTrace();
                    ((IParseQueryResult) iParseQueryResult).onParseQueryFail(e);
                }
            }
        });
    }

}
