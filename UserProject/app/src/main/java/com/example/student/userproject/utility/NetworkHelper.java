package com.example.student.userproject.utility;

import com.example.student.userproject.R;
import com.example.student.userproject.model.DataObject;
import com.example.student.userproject.model.NotificationData;
import com.example.student.userproject.service.MyFirebaseInstanceIDService;
import com.example.student.userproject.service.MyFirebaseMessagingService;
import com.google.gson.Gson;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkHelper {

    private static final String SERVER_KEY = "AAAAXOlepJ4:APA91bG49WQvvZ4YDaT_Wf_n7cLXwhjjwoo0rVzuw-r3cIEE9FC2WvimfMEmxEK1aw7rdkIkMwPyDk0_BxWWf3XuCIDSnWKqE8v_4hHNpQQb85yCgmcH2mgC5lSJjcScGRRdBRkknFhI";

    public void sendNotificationRequest(){
        Gson gson = new Gson();
        NotificationData notificationData = new NotificationData();
        DataObject dataObject = new DataObject();
        dataObject.setTitle("Arman Jan");
        dataObject.setIcon(R.drawable.ic_favorite_border_black_24dp);
        dataObject.setMessage("Hello,can i call you?");
        notificationData.setData(dataObject);
        notificationData.setTo("eN0GGYY6at0:APA91bGsq-YyKeV9yKwAHC9WzHz-sK14f8YdJgprejtt5HbWYIv867-tWsDIFtRRklDCzLX3-sUnG0VdcPbxjILjou_zO7QLrXRtXCYb9_6kWuxFxBPFD2JcqNAV9P0wgNis5e59t_uZ");

        String json = gson.toJson(notificationData);
        String url = "https://fcm.googleapis.com/fcm/send";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "key=" + SERVER_KEY)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
    }

