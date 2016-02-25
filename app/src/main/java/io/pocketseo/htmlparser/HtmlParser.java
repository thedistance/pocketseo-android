/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.htmlparser;

import android.support.annotation.NonNull;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.pocketseo.HtmlData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pharris on 19/02/16.
 */
public class HtmlParser {

    private final OkHttpClient mOkHttpClient;

    public HtmlParser(OkHttpClient client){
        mOkHttpClient = client;
    }

    public static HtmlData theDistanceMetaData(){
        return new HtmlData() {
            @Override
            public String getPageTitle() {
                return "App Developers UK | Mobile App Development | The Distance, York";
            }

            @Override
            public String getCanonicalUrl() {
                return "https://thedistance.co.uk/";
            }

            @Override
            public String getMetaDescription() {
                return "We are award winning UK app developers UK who develop mobile app development solutions for IOS & Android for B2C, B2B & Enterprise. Call York team today.";
            }

            @Override
            public List<String> getH1TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                testData.add("The Yorkshire & UK leading mobile app developers");
                return testData;
            }

            @Override
            public List<String> getH2TagList() {
                ArrayList<String > testData = new ArrayList<String>();
                for(String s: new String[]{"Mobile App Consultancy",
                        "Mobile App Development",
                        "Mobile App UI/UX",
                        "Trusted By",
                        "OUR TOOLS",
                        "PLATFORMS",
                        "TELL US YOUR APP IDEA"}){
                    testData.add(s);
                }
                return testData;
            }

            @Override
            public boolean isSsl() {
                return true;
            }

            @Override
            public String getFinalUrl() {
                return "http://thedistance.co.uk/";
            }

            @Override
            public Date getDateChecked() {
                return new Date();
            }
        };
    }

    /**
     * Get HTML data by requesting the specified URL, then parsing the response content
     * @param url
     * @return
     */
    public HtmlParser.HtmlDataImpl getHtmlData(String url) throws ParserError {
        InputStream is = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            boolean ssl;
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                int code = response.code();
                if (code < 200 || code >= 400) {
                    throw new ParserError(String.format(Locale.US, "Server responded with status code %d", code));
                }
                url = response.request().url().toString();
                ssl = response.request().isHttps();
                is = response.body().byteStream();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ParserError(e.getMessage());
            }


            if(null == is){
                throw new ParserError("Cannot retrieve data");
            }

            HtmlDataImpl response = (HtmlDataImpl) parseData(is);
            response.parseDate = new Date();
            response.ssl = ssl;
            response.finalUrl = url;
            return response;
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public HtmlData parseData(@NonNull InputStream htmlStream) throws ParserError {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");

            final HtmlDataImpl data = new HtmlDataImpl();
            ContentHandler handler = new DefaultHandler() {

                boolean inTitle = false;
                boolean inHead = false;
                boolean inH1= false;
                boolean inH2= false;

                StringBuilder inProgress;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    // ...
                    if(qName.equals("head")){
                        inHead = true;
                    } else if(inHead && qName.equals("title")){
                        inTitle = true;
                        inProgress = new StringBuilder();
                    } else if(qName.toLowerCase().equals("h1")) {
                        inH1 = true;
                        inProgress = new StringBuilder();
                    } else if(qName.toLowerCase().equals("h2")) {
                        inH2 = true;
                        inProgress = new StringBuilder();
                    } else if(inHead && qName.toLowerCase().equals("meta")) {
                        String name = attributes.getValue("name");
                        if (null != name) name = name.toLowerCase();
                        if ("description".equals(name)) {
                            data.meta = attributes.getValue("content");
                        }
                    } else if(inHead && qName.toLowerCase().equals("link")) {
                        String rel = attributes.getValue("rel");
                        if(null != rel) rel = rel.toLowerCase();
                        if("canonical".equals(rel)){
                            data.canonicalUrl = attributes.getValue("href");
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.toLowerCase().equals("head")){
                        inHead = false;
                    } else if(qName.toLowerCase().equals("title")){
                        data.title = inProgress.toString();
                        inTitle = false;
                        inProgress = null;
                    } else if(qName.toLowerCase().equals("h1")) {
                        data.h1.add(inProgress.toString());
                        inH1 = false;
                        inProgress = null;
                    } else if(qName.toLowerCase().equals("h2")) {
                        data.h2.add(inProgress.toString());
                        inH2 = false;
                        inProgress = null;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if(inTitle){
                        inProgress.append(ch, start, length);
                    } else if(inH1) {
                        inProgress.append(ch, start, length);
                    } else if(inH2) {
                        inProgress.append(ch, start, length);
                    }
                }
            };
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(htmlStream));
            return data;
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            throw new ParserError("Error reading HTML");
        }
    }


    public static class HtmlDataImpl implements HtmlData {
        String title = "";
        List<String> h1 = new ArrayList<>(15);
        List<String> h2 = new ArrayList<>(15);
        String meta = "";
        Date parseDate;
        String canonicalUrl;
        String finalUrl;
        boolean ssl;

        @Override
        public String getPageTitle() {
            return title;
        }

        @Override
        public String getCanonicalUrl() {
            return canonicalUrl;
        }

        @Override
        public String getMetaDescription() {
            return meta;
        }

        @Override
        public List<String> getH1TagList() {
            return h1;
        }

        @Override
        public List<String> getH2TagList() {
            return h2;
        }

        @Override
        public boolean isSsl() {
            return ssl;
        }

        @Override
        public String getFinalUrl() {
            return finalUrl;
        }

        @Override
        public Date getDateChecked() {
            return parseDate;
        }
    }

    public static class ParserError extends Exception {

        public ParserError(String message) {
            super(message);
        }
    }
}
