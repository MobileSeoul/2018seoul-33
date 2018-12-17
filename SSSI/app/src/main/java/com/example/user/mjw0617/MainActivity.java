package com.example.user.mjw0617;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    private Adapter adapter;
    private ListView listView;
    private ArrayList<DTO> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        adapter = new Adapter();
        listView = (ListView) findViewById(R.id.listView);
        findViewById(R.id.menuBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        data = new ArrayList<>();

        String strUrl = "http://openapi.seoul.go.kr:8088/74797259546a773138364a786d7858/xml/SchulInfoSpcl/1/29/";
        DownloadWebpageTask task = new DownloadWebpageTask();
        task.execute(strUrl);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {

            String schoolName = null;
            String address = null;
            String tel = null;
            String dspsn = null;
            String homepage = null;
            String fond = null;

            boolean bSet_schoolName = false;
            boolean bSet_address = false;
            boolean bSet_tel = false;
            boolean bSet_dspsn = false;
            boolean bSet_homepage = false;
            boolean bSet_fond = false;

            DTO dto;


            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    dto = new DTO();
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("SCHUL_NM"))
                            bSet_schoolName = true;
                        if (tag_name.equals("HMPG"))
                            bSet_homepage = true;
                        if (tag_name.equals("FOND"))
                            bSet_fond = true;
                        if (tag_name.equals("DSPSN"))
                            bSet_dspsn = true;
                        if (tag_name.equals("ADRES"))
                            bSet_address = true;
                        if (tag_name.equals("TELNO"))
                            bSet_tel = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet_schoolName) {
                            schoolName = xpp.getText();
                            bSet_schoolName = false;
                        }
                        if (bSet_homepage) {
                            homepage = xpp.getText();
                            bSet_homepage = false;
                        }
                        if (bSet_fond) {
                            fond = xpp.getText();
                            bSet_fond = false;
                        }
                        if (bSet_dspsn) {
                            dspsn = xpp.getText();
                            bSet_dspsn = false;
                        }
                        if (bSet_address) {
                            address = xpp.getText();
                            bSet_address = false;
                        }
                        if (bSet_tel) {
                            tel = xpp.getText();
                            bSet_tel = false;
                            dto.setName(schoolName);
                            dto.setTel(tel);
                            dto.setHomepage(homepage);
                            dto.setDspsn(dspsn);
                            dto.setAddress(address);
                            dto.setFond(fond);
                            adapter.addItem(dto);
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        listView.setAdapter(adapter);

                        /**
                         디테일로 넘어가는 intent
                         */
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                String tName = ((DTO)adapter.getItem(position)).getName(); //추가사항
//
//                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                                intent.putExtra("tag_name", tName); //추가사항
//                                startActivity(intent);
//                            }
//                        });
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }
    }
}