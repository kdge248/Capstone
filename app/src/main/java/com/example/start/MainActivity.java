package com.example.start;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private TextView whido;
    private TextView gyeongdo;
    private Button button_reset;
    private TextView tvTemp;
    private TextView tvHumidity;

    WeatherRepo weatherRepo= new WeatherRepo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //허가1
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        whido = (TextView) findViewById(R.id.whido);
        gyeongdo = (TextView) findViewById(R.id.gyeongdo);
        button_reset = (Button) findViewById(R.id.button_reset);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);


        //객체 참조
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //위치 리스너 작성
        final LocationListener gpsLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longtitude = location.getLongitude();
                double latitude = location.getLatitude();

                gyeongdo.setText(String.format("위도 : %.4f", longtitude));
                whido.setText(String.format("경도 : %.4f", latitude));

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //가장 최근의 위치를 가지고 옴
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

                double longtitude = location.getLongitude();
                double latitude = location.getLatitude();

                gyeongdo.setText(String.format("위도 : %.4f", longtitude));
                whido.setText(String.format("경도 : %.4f", latitude));

                try{
                    //서울시청의 위도와 경도
                    String lon = String.valueOf(latitude);  //경도
                    String lat = String.valueOf(longtitude);   //위도

                    //OpenAPI call하는 URL
                    String urlstr = "http://api.openweathermap.org/data/2.5/weather?"
                            + "&lat="+lat+"&lon="+lon
                            +"&appidb=f352c536f5948fbd7d81903f2c7a309a";

                    URL url = new URL(urlstr);
                    BufferedReader bf;
                    String line;
                    String result="";

                    //날씨 정보를 받아온다.
                    bf = new BufferedReader(new InputStreamReader(url.openStream()));

                    //버퍼에 있는 정보를 문자열로 변환.
                    while((line=bf.readLine())!=null){
                        result=result.concat(line);
                        //System.out.println(line);
                    }

                    //문자열을 JSON으로 파싱
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObj = (JSONObject) jsonParser.parse(result);

                    //지역 출력
                    tvTemp.setText(String.format("지역 : " + jsonObj.get("name")));

                    //날씨 출력
                    JSONArray weatherArray = (JSONArray) jsonObj.get("weather");
                    JSONObject obj = (JSONObject) weatherArray.get(0);
                    System.out.println("날씨 : "+obj.get("main"));

                    //온도 출력(절대온도라서 변환 필요)
                    JSONObject mainArray = (JSONObject) jsonObj.get("main");
                    double ktemp = Double.parseDouble(mainArray.get("temp").toString());
                    double temp = ktemp-273.15;

                    tvTemp.setText(String.format("온도 : %.2f",temp));

                    bf.close();
                }catch(Exception e){
                    //tvTemp.setText(String.format("온도 : %.2f",-1));
                }
            }
        });
    }

}