package app.lifewin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;


public class ImageDisplay implements IParseQueryResult{
    private ImageView userProfile;
    private Context mContext;
    public void scaleAndLoadBitmap(Context context, String imagePath, ImageView mImageView) {
        userProfile=null;
        mContext=context;
        if (imagePath.startsWith(Environment.getExternalStorageDirectory().toString())) {
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            mImageView.setImageBitmap(bmp);
        } else if (imagePath.startsWith("http://files.parsetfss.com/")) {
            userProfile=mImageView;
            ParseUser mParseUser= ParseUser.getCurrentUser();
            ParseUtils.getInstance().onRetrieveImageFromParse(ImageDisplay.this, mParseUser.getParseFile("user_image"));
        }else if(!TextUtils.isEmpty(imagePath)){
            Picasso.with(context).load(imagePath).into(mImageView);
        }
    }



    @Override
    public void onParseQuerySuccess(Object obj) {
        if (obj instanceof Bitmap && userProfile!=null) {
            userProfile.setImageBitmap((Bitmap) obj);
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        if (obj instanceof ParseException) {
            Toast.makeText(mContext, ((ParseException) obj).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
