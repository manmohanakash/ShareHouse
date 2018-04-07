package com.example.akash.sharehouse.helpers;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {

    private AppConfig appConfig;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        saveRegistrationToken(refreshedToken);
    }

    public void saveRegistrationToken(String refreshedToken) {
    }
}
