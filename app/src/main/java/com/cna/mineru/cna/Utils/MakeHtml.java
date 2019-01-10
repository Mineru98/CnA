package com.cna.mineru.cna.Utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cna.mineru.cna.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MakeHtml extends AppCompatActivity {

    private File myFile;
    private String mTime;
    private WebView mWebView;
    private WebSettings mWebSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setBuiltInZoomControls(true);
        mWebSetting.setSupportZoom(true);

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy_MM_dd", Locale.KOREA );
        Date currentTime = new Date ();
        mTime = mSimpleDateFormat.format ( currentTime );
        try {
            createHtml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mWebView.loadUrl("file:///sdcard/CnA/"+mTime+".html");
    }
    private void createHtml() throws IOException {
        int count = 1;
        File f = new File("sdcard/CnA");
        if (!f.exists()) {
            f.mkdir();
        }

        myFile = new File(mTime + ".html");
        if(myFile.exists()){
            myFile = new File(mTime + "( "+ count +").html");
            count++;
        }

        try{
            StringBuffer sBuff = new StringBuffer("<html lang=\"kr\">");
            sBuff.append("<head>");
            sBuff.append("<meta charset=\"UTF-8\">");
            sBuff.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.5, maximum-scale=2.0, minimum-scale=0.5, user-scalable=yes,target-densitydpi=medium-dpi\">");
            sBuff.append("<title>" + mTime+".html"+"</title>");
            sBuff.append("<style>");
            sBuff.append("body { margin: 0; padding: 0;background-color: grey; font: 12pt \"Tahoma;\"}");
            sBuff.append(" * {box-sizing: border-box;-moz-box-sizing: border-box;}");
            sBuff.append(".page {margin: 1cm auto;width: 21cm;min-height: 29.7cm;padding-top: 10px;padding-bottom: : 10px;padding-left: 20px;padding-right: 20px;background: white;box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);}");
            sBuff.append(".subpage {margin-top: 50px;height: 256mm;line-height:1em;vertical-align: middle;}");
            sBuff.append("@page {size: A4 landscape;margin: 0;}");
            sBuff.append("@media print {.page {margin: 0;border: initial;border-radius: initial;width: initial;min-height: initial;box-shadow: initial;background: initial;page-break-after:always;}}");
            sBuff.append("#left{float: left;width: 45%;display: inline-block;}");
            sBuff.append("#right{float: left;width: 45%;display: inline-block;}");
            sBuff.append("#_1{padding: 10px;height: 450px;width: 350px;border: 0px solid #dddddd;}");
            sBuff.append("#_2{padding: 10px;height: 450px;width: 350px;border: 0px solid #dddddd;}");
            sBuff.append("#_3{padding: 10px;margin-left: -10px;height: 450px;width: 350px;border: 0px solid #dddddd;}");
            sBuff.append("#_4{padding: 10px;margin-left: -10px;height: 450px;width: 350px;border: 0px solid #dddddd;}");
            sBuff.append("#Top_title{text-align: center;font-weight: 500;font-size: 20px;}");
            sBuff.append("#Top_title2{text-align: center;font-weight: 1000;font-size: 40px;}");
            sBuff.append("</style>");
            sBuff.append("</head>");
            sBuff.append("<body>");
            sBuff.append("<div class=\"book\">");
            sBuff.append("<div class=\"page\">");
            sBuff.append("<div class=\"subpage\" id=\"content\">");
            sBuff.append("<h2 id=\"Top_title\">제1회 오답노트 시험 문제지</h2>");
            sBuff.append("<h1 id=\"Top_title2\">오답 영역</h1>");
            sBuff.append("<hr style=\"border: solid 1px black\"></hr>");

            sBuff.append("<div id =\"left\">");
            sBuff.append("<div id=\"_1\">");
            //첫번째 문제
            sBuff.append("<h4>1. 두벡터 a = (1, -2) b = (-1, 4)에 대하여</h4>");
            sBuff.append("</div>");
            sBuff.append("<div id=\"_2\">");
            //두번째 문제
            sBuff.append("<h4>2. 두벡터 a = (1, -2) b = (-1, 4)에 대하여</h4>");
            sBuff.append("</div>");
            sBuff.append("</div>");

            sBuff.append("<div style=\"float: left; width: 10%; margin-top: -18px;\">");
            sBuff.append("<hr width=\"1\" size=\"950\" color=\"black\">");
            sBuff.append("</div>");

            sBuff.append("<div id=\"right\">");
            sBuff.append("<div id=\"_3\">");
            //세번째 문제
            sBuff.append("<h4>3. 두벡터 a = (1, -2) b = (-1, 4)에 대하여</h4>");
            sBuff.append("</div>");
            sBuff.append("<div id=\"_4\">");
            //네번째 문제
            sBuff.append("<h4>4. 두벡터 a = (1, -2) b = (-1, 4)에 대하여</h4>");
            sBuff.append("</div>");
            sBuff.append("</div>");

            sBuff.append("</div>");
            sBuff.append("</div>");
            sBuff.append("</div>");
            sBuff.append("</body>");
            sBuff.append("</html>");
            FileWriter fw = new FileWriter(f + "/" + myFile,false);
            fw.write(sBuff.toString());
            fw.flush();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
