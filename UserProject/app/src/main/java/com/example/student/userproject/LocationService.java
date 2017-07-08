package com.example.student.userproject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;


@SuppressWarnings("MissingPermission")
public class LocationService extends Service {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intent = new Intent("LOCATION_UPDATE");
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("lng", location.getLongitude());
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
        // TODO check is it necessary and request network provider when gps is not available
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, mLocationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
