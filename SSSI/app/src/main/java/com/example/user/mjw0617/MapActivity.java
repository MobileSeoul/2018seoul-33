package com.example.user.mjw0617;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;




import java.util.List;
import java.util.Locale;


import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {




    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        findViewById(R.id.menuBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        LinearLayout button1 = (LinearLayout) findViewById(R.id.button1);
//        LinearLayout button2 = (LinearLayout) findViewById(R.id.button2);
//        LinearLayout button3 = (LinearLayout) findViewById(R.id.button3);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(getApplicationContext(), "구글지도", Toast.LENGTH_SHORT).show();  //프래그먼트 교체
//                getFragmentManager().beginTransaction().replace(R.id.map, new fragment1()).commit();
//            }
//        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(getApplicationContext(), "학교목록", Toast.LENGTH_SHORT).show();
//
//                getFragmentManager().beginTransaction().replace(R.id.map, new fragment2()).commit();
//            }
//        });
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(getApplicationContext(), "앱 정보", Toast.LENGTH_SHORT).show();
//                getFragmentManager().beginTransaction().replace(R.id.map, new fragment3()).commit();
//            }
//        });



        Log.d(TAG, "onCreate");
        mActivity = this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }



    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady :");

        //1:38

        LatLng SN = new LatLng(37.584782,126.9663373);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SN);
        markerOptions.title("서울농학교");
        markerOptions.snippet("서울시 종로구");
        googleMap.addMarker(markerOptions);

        LatLng SM = new LatLng(37.5597287,126.952197);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(SM);
        markerOptions2.title("서울맹학교");
        markerOptions2.snippet("서울시 종로구");
        googleMap.addMarker(markerOptions2);

        LatLng HW = new LatLng(37.5715606,126.9010935);

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(HW);
        markerOptions3.title("한국우진학교");
        markerOptions3.snippet("서울시 마포구");
        googleMap.addMarker(markerOptions3);


        LatLng SK = new LatLng(37.5750422,126.9852427);

        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(SK);
        markerOptions4.title("서울경운학교");
        markerOptions4.snippet("서울시 종로구");
        googleMap.addMarker(markerOptions4);


        LatLng KJ = new LatLng(37.5373224,127.0896819);

        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(KJ);
        markerOptions5.title("서울광진학교");
        markerOptions5.snippet("서울시 광진구");
        googleMap.addMarker(markerOptions5);


        LatLng JM = new LatLng(37.4617174,126.9145067);

        MarkerOptions markerOptions6 = new MarkerOptions();
        markerOptions6.position(JM);
        markerOptions6.title("서울정문학교");
        markerOptions6.snippet("서울시 관악구");
        googleMap.addMarker(markerOptions6);


        LatLng JM2 = new LatLng(37.633838,127.0708754);

        MarkerOptions markerOptions7 = new MarkerOptions();
        markerOptions7.position(JM2);
        markerOptions7.title("서울정민학교");
        markerOptions7.snippet("서울시 노원구");
        googleMap.addMarker(markerOptions7);



        LatLng JE = new LatLng(37.5143523,127.052214);

        MarkerOptions markerOptions8 = new MarkerOptions();
        markerOptions8.position(JE);
        markerOptions8.title("서울정애학교");
        markerOptions8.snippet("서울시 강남구");
        googleMap.addMarker(markerOptions8);

        LatLng JI = new LatLng(37.6308929,127.0096593);

        MarkerOptions markerOptions9 = new MarkerOptions();
        markerOptions9.position(JI);
        markerOptions9.title("서울정인학교");
        markerOptions9.snippet("서울시 강북구");
        googleMap.addMarker(markerOptions9);

        LatLng JJ = new LatLng(37.4982041,126.8201155);

        MarkerOptions markerOptions10 = new MarkerOptions();
        markerOptions10.position(JJ);
        markerOptions10.title("서울정진학교");
        markerOptions10.snippet("서울시 구로구");
        googleMap.addMarker(markerOptions10);


        LatLng GH = new LatLng(37.5371841,127.1203734);

        MarkerOptions markerOptions11 = new MarkerOptions();
        markerOptions11.position(GH);
        markerOptions11.title("광성하늘빛학교");
        markerOptions11.snippet("서울시 송파구");
        googleMap.addMarker(markerOptions11);

        LatLng GH2 = new LatLng(37.4999602,126.924258);

        MarkerOptions markerOptions12 = new MarkerOptions();
        markerOptions12.position(GH2);
        markerOptions12.title("광성해맑음학교");
        markerOptions12.snippet("서울시 송파구");
        googleMap.addMarker(markerOptions12);

        LatLng GN = new LatLng(37.548039,126.8502883);

        MarkerOptions markerOptions13 = new MarkerOptions();
        markerOptions13.position(GN);
        markerOptions13.title("교남학교");
        markerOptions13.snippet("서울시 강서구");
        googleMap.addMarker(markerOptions13);

        LatLng DNL = new LatLng(37.4603774,127.0899904);

        MarkerOptions markerOptions14 = new MarkerOptions();
        markerOptions14.position(DNL);
        markerOptions14.title("다니엘학교");
        markerOptions14.snippet("서울시 서초구");
        googleMap.addMarker(markerOptions14);

        LatLng MA = new LatLng(37.4859121,127.080016);

        MarkerOptions markerOptions15 = new MarkerOptions();
        markerOptions15.position(MA);
        markerOptions15.title("밀알학교");
        markerOptions15.snippet("서울시 강남구");
        googleMap.addMarker(markerOptions15);

        LatLng SD = new LatLng(37.6443368,127.071264);

        MarkerOptions markerOptions16 = new MarkerOptions();
        markerOptions16.position(SD);
        markerOptions16.title("서울동천학교");
        markerOptions16.snippet("서울시 노원구");
        googleMap.addMarker(markerOptions16);

        LatLng SM2 = new LatLng(37.5940359,126.9887457);

        MarkerOptions markerOptions17 = new MarkerOptions();
        markerOptions17.position(SM2);
        markerOptions17.title("서울명수학교");
        markerOptions17.snippet("서울시 성북구");
        googleMap.addMarker(markerOptions17);


        LatLng SS = new LatLng(37.4992689,126.9436139);

        MarkerOptions markerOptions18 = new MarkerOptions();
        markerOptions18.position(SS);
        markerOptions18.title("서울삼성학교");
        markerOptions18.snippet("서울시 동작구");
        googleMap.addMarker(markerOptions18);

        LatLng EH = new LatLng(37.62514,127.0265464);

        MarkerOptions markerOptions19 = new MarkerOptions();
        markerOptions19.position(EH);
        markerOptions19.title("서울애화학교");
        markerOptions19.snippet("서울시 강북구");
        googleMap.addMarker(markerOptions19);

        LatLng IK = new LatLng(37.6947813,127.04024);

        MarkerOptions markerOptions20 = new MarkerOptions();
        markerOptions20.position(IK);
        markerOptions20.title("서울인강학교");
        markerOptions20.snippet("서울시 도봉구");
        googleMap.addMarker(markerOptions20);


        LatLng SBDR = new LatLng(37.4874741,126.8235598);

        MarkerOptions markerOptions21 = new MarkerOptions();
        markerOptions21.position(SBDR);
        markerOptions21.title("성베드로학교");
        markerOptions21.snippet("서울시 구로구");
        googleMap.addMarker(markerOptions21);

        LatLng SDSR = new LatLng(37.5747447,126.9630233);

        MarkerOptions markerOptions22 = new MarkerOptions();
        markerOptions22.position(SDSR);
        markerOptions22.title("수도사랑의학교");
        markerOptions22.snippet("서울시 종로구");
        googleMap.addMarker(markerOptions22);

        LatLng YSJ = new LatLng(37.5629452,126.929721);

        MarkerOptions markerOptions23 = new MarkerOptions();
        markerOptions23.position(YSJ);
        markerOptions23.title("연세대학교재활학교");
        markerOptions23.snippet("서울시 서대문구");
        googleMap.addMarker(markerOptions23);


        LatLng EP = new LatLng(37.6061032,126.9038966);

        MarkerOptions markerOptions24 = new MarkerOptions();
        markerOptions24.position(EP);
        markerOptions24.title("은평대영학교");
        markerOptions24.snippet("서울시 은평구");
        googleMap.addMarker(markerOptions24);


        LatLng JM3 = new LatLng(37.5462948,127.1609247);

        MarkerOptions markerOptions25 = new MarkerOptions();
        markerOptions25.position(JM3);
        markerOptions25.title("주몽학교");
        markerOptions25.snippet("서울시 강동구");
        googleMap.addMarker(markerOptions25);


        LatLng HKKH = new LatLng(37.5582652,127.156814);

        MarkerOptions markerOptions26 = new MarkerOptions();
        markerOptions26.position(HKKH);
        markerOptions26.title("한국구화학교");
        markerOptions26.snippet("서울시 강동구");
        googleMap.addMarker(markerOptions26);

        LatLng HKYY = new LatLng(37.4819688,127.1303184);

        MarkerOptions markerOptions27 = new MarkerOptions();
        markerOptions27.position(HKYY);
        markerOptions27.title("한국육영학교");
        markerOptions27.snippet("서울시 송파구");
        googleMap.addMarker(markerOptions27);

        LatLng HB = new LatLng(37.6281078,127.0136357);

        MarkerOptions markerOptions28 = new MarkerOptions();
        markerOptions28.position(HB);
        markerOptions28.title("한빛맹학교");
        markerOptions28.snippet("서울시 강북구");
        googleMap.addMarker(markerOptions28);


        GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String markerId = marker.getId();
                Toast.makeText(MapActivity.this, "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
            }
        };

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SN));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;
    }


    @Override
    protected void onStart() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }



            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }
//    public void displayPicture(View v) {
//        int id = v.getId();
//        LinearLayout layout = (LinearLayout) v.findViewById(id);
//        String tag = (String) layout.getTag();
//
//        Intent it = new Intent(this, favorite.class);
//        it.putExtra("it_tag", tag);
//        startActivity(it);
//    }


}

