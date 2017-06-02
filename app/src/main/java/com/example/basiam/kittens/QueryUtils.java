package com.example.basiam.kittens;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/**
 * Helper methods related to requesting and receiving kitten data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static String ns = "";
    private static String TAG = "QueryUtils";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Kitten} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Kitten> extractKittens(String stringUrl) {

        URL url = createUrl(stringUrl);
        InputStream xmlResponse = null;
        try {
            xmlResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Kitten> kittens = extractFeatureFromXml(xmlResponse);


        return kittens;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }
    private static InputStream makeHttpRequest(URL url) throws IOException {
        InputStream xmlResponse = null;

        // If the URL is null, then return early.
        if (url == null) {
            return xmlResponse;
        }

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                xmlResponse = urlConnection.getInputStream();
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the kitten JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return xmlResponse;
    }


    /**
     * Return an {@link Kitten} object by parsing out information
     * about the first kitten from the input kittenXML string.
     */
    private static List<Kitten> extractFeatureFromXml(InputStream kittenXML) {
        // If the JSON string is empty or null, then return early.

        List<Kitten> kittenList = new ArrayList<Kitten>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(kittenXML, null);
            parser.nextTag();
            kittenList = readFeed(parser);
            kittenXML.close();

        } catch (XmlPullParserException |  IOException e) {
            Log.e(LOG_TAG, "Problem parsing the kitten JSON results", e);
        }
        return kittenList;
    }

    private static List<Kitten> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Kitten> kittenList = new ArrayList<Kitten>();

        parser.require(XmlPullParser.START_TAG, ns, "response");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals("data")) {
                parser.next();
                parser.next();
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    if (parser.getName().equals("image")) {
                        kittenList.add(readEntry(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return kittenList;
    }

    private static Kitten readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "image");

        String url = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("url")) {
                url = readUrl(parser);
            } else {
                skip(parser);
            }
        }
        return new Kitten(url);
    }

    private static String readUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "url");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "url");
        return title;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
