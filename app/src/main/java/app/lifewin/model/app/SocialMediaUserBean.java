package app.lifewin.model.app;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class SocialMediaUserBean implements Parcelable {

    private String  mSocialId;
    private String  mSocialName;
    private String  mUserName;
    private String  mFirstName;
    private String  mLastName;
    private String  mProfilePic;
    private String  mDOB;
    private String  mEmail;
    private String  mGender;
    private String  mSocialType;
    private String  mMobileNo;
    private String  mSocialAccessToken;

    public String getUsername() {
        return mUserName;
    }

    public void setUsername(String username) {
        this.mUserName = username;
    }




    public SocialMediaUserBean(Parcel source) {
        this.mSocialId=source.readString();
        this.mSocialName=source.readString();
        this.mUserName=source.readString();
        this.mFirstName=source.readString();
        this.mLastName=source.readString();
        this.mProfilePic=source.readString();
        this.mDOB=source.readString();
        this.mEmail=source.readString();
        this.mGender=source.readString();
        this.mSocialType=source.readString();
        this.mMobileNo=source.readString();
        this.mSocialAccessToken=source.readString();
    }

    public SocialMediaUserBean() {

    }
    public SocialMediaUserBean(JSONObject obj) throws JSONException {
        if(obj.has("UserId")){
            this.mSocialId=obj.getString("UserId");
        }
        if(obj.has("name")){
            this.mSocialName=obj.getString("name");
        }

        if(obj.has("BirthDate")){
            this.mDOB=obj.getString("BirthDate");
        }

        if(obj.has("Email")){
            this.mEmail=obj.getString("Email");
        }

        if(obj.has("Gender")){
            this.mGender=obj.getString("Gender");
        }
        if(obj.has("Mobile")){
            this.mMobileNo=obj.getString("Mobile");
        }

        if(obj.has("Image")){
            this.mProfilePic=obj.getString("Image");
        }

        if(obj.has("Type")){
            this.mSocialType=obj.getString("Type");
        }

        if(obj.has("Username")){
            this.mSocialType=obj.getString("Username");
        }

    }

    public String getBirthday() {
        return mDOB;
    }

    public void setBirthday(String birthday) {
        this.mDOB = birthday;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        this.mGender = gender;
    }

    public String getId() {
        return mSocialId;
    }

    public void setId(String id) {
        this.mSocialId = id;
    }

    public String getName() {
        return mSocialName;
    }

    public void setName(String name) {
        this.mSocialName = name;
    }

    public String getProfilepic() {
        return mProfilePic;
    }

    public void setProfilepic(String profilepic) {
        this.mProfilePic = profilepic;
    }

    /**
     * Return the social media type
     * @return
     */
    public String getSocialType() {
        return mSocialType;
    }

    public void setSocialType(String type) {
        this.mSocialType = type;
    }

    public void setFirstName(String fName){
        this.mFirstName=fName;
    }
    public String getFirstName() {
        return mFirstName;
    }


    public void setLastName(String lName){
        this.mLastName=lName;
    }

    public String getLastName() {
        return mLastName;
    }



    public void setSocialAccessToken(String socialToken){
        this.mSocialAccessToken=socialToken;
    }

    public String getSocialAccessToken() {
        return mSocialAccessToken;
    }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    public static final Creator<SocialMediaUserBean> CREATOR= new Creator<SocialMediaUserBean>(){
        @Override
        public SocialMediaUserBean createFromParcel(Parcel source) {
            return new SocialMediaUserBean(source);
        }
        @Override
        public SocialMediaUserBean[] newArray(int size) {

            return new SocialMediaUserBean[size];
        }

    };

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(this.mSocialId);
        arg0.writeString(this.mSocialName);
        arg0.writeString(this.mUserName);
        arg0.writeString(this.mFirstName);
        arg0.writeString(this.mLastName);
        arg0.writeString(this.mProfilePic);
        arg0.writeString(this.mDOB);
        arg0.writeString(this.mEmail);
        arg0.writeString(this.mGender);
        arg0.writeString(this.mSocialType);
        arg0.writeString(this.mMobileNo);
        arg0.writeString(this.mSocialAccessToken);
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mMobileNo;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mMobileNo = mobile;
    }
}
