package com.botcrator.stage;

import com.botcrator.FacebookData;
import com.botcrator.WebRegisterInstance;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class GetFacebookDataStage extends StageImpl {
    private Random random = new Random();
    private FacebookData facebookData = new FacebookData();

    public GetFacebookDataStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws Exception {
        //10 tries
        for (int i = 0; i < 30; i++) {
            //dbr can be a random number from 1 to 99999999 (10^8-1)
            int dbr = random.nextInt(99999998) + 1;

            //db_id can be a random number from 3 to 11
            int dbId = random.nextInt(8) + 3;

            try {
                //Get user data
                URLConnection urlConnection = new URL("http://app.thefacesoffacebook.com/php/select_one_fbid_from.php?dbr=" + dbr + "&table=fbusers" + dbId).openConnection();

                long fbId = Long.parseLong(IOUtils.toString(urlConnection.getInputStream()));
                facebookData.setId(fbId);

                getUserName();

                try {
                    getProfileImage();
                } catch (Exception ignored) {
                }

                wri.setFacebookData(facebookData);
                break;
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }

    private void getProfileImage() throws IOException, JSONException {
        //Get data
        URLConnection urlConnection = new URL("http://graph.facebook.com/" + facebookData.getId() + "/picture?width=300&height=300&redirect=false").openConnection();

        //Get json
        JSONObject root = new JSONObject(new JSONTokener(new InputStreamReader(urlConnection.getInputStream())));

        facebookData.setImageURL(root.getJSONObject("data").getString("url"));
    }


    private void getUserName() throws Exception {
        //Get data
        URLConnection urlConnection = new URL("http://graph.facebook.com/" + facebookData.getId()).openConnection();

        //Get json
        JSONObject jsonObject = new JSONObject(new JSONTokener(new InputStreamReader(urlConnection.getInputStream())));

        //Check username
        if (jsonObject.isNull("username")) throw new Exception("Doesn't have username");

        String username = jsonObject.getString("username");

        //Max 23+2 char
        if (username.length() >= 23)
            username = username.substring(0, 23);

        //replace all non-word char
        username = username.replaceAll("\\W", "");

        //do have number in the username?
        if (username.matches("^.*\\d.*")) {

            //Replace numbers with random numbers
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < username.length(); i++) {
                char ch = username.charAt(i);
                if (Character.isDigit(ch)) {
                    builder.append(Integer.toString(random.nextInt(9)));
                    continue;
                }
                builder.append(ch);
            }

            username = builder.toString();
        } else {
            //Add random numbers at the end
            username += random.nextInt(99);
        }

        facebookData.setUsername(username);
    }
}
