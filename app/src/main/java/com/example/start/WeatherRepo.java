package com.example.start;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class WeatherRepo {
    @SerializedName("result")
    Result result;
    @SerializedName("weather")
    weather weather;

    public class Result {
        @SerializedName("message") String message;
        @SerializedName("code") String code;

        public String getMessage() {return message;}
        public String getCode() {return code;}
    }

    public class weather {

        public List<hourly> hourly = new ArrayList<>();
        public List<hourly> getHourly() {return hourly;}

        public class hourly {
            @SerializedName("sky") Sky sky;
            @SerializedName("precipitation") precipitation precipitation;
            @SerializedName("temperature") temperature temperature;
            @SerializedName("wind") wind wind;

            public class Sky{
                @SerializedName("name") String name;
                @SerializedName("code") String code;

                public String getName() {return name;}
                public String getCode() {return code;}
            }

            public class precipitation{ // 강수 정보
                @SerializedName("sinceOntime") String sinceOntime; // 강우
                @SerializedName("type") String type; //0 :없음 1:비 2: 비/눈 3: 눈

                public String getSinceOntime() {return sinceOntime;}
                public String getType() {return type;}
            }
            public class temperature{
                @SerializedName("tc") String tc; // 현재 기온

                public String getTc() {return tc;}
            }
            public class wind{ // 바람
                @SerializedName("wdir") String wdir;
                @SerializedName("wspd") String wspd;

                public String getWdir() {return wdir;}
                public String getWspd() {return wspd;}
            }
            @SerializedName("humidity") String humidity;
            public String getHumidity() {
                return humidity;
            }
            public Sky getSky() {return sky;}
            public hourly.precipitation getPrecipitation() {return precipitation;}
            public hourly.temperature getTemperature() {return temperature;}
            public hourly.wind getWind() {return wind;}
        }
    }
    public Result getResult() {return result;}
    public weather getWeather() {return weather;}

    public interface WeatherApiInterface {
        @Headers({"Accept: application/json","appKey:4aaf9041-99fe-49be-b922-67cbbfbb04af"})
        @GET("weather/current/hourly")
        Call<WeatherRepo> get_Weather_retrofit(@Query("version") int version, @Query("lat") String lat, @Query("lon") String lon);
    }

}
