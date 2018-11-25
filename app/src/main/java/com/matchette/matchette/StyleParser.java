package com.matchette.matchette;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StyleParser {

    private static final String ns = null;

    public List<Style> parse(InputStream in, Context context) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            Log.d("XMLparsing", "pull parser initiated");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            Log.d("XMLparsing", "input stream passed");
            parser.nextTag();
            return readStyles(parser, context);
        } finally {
            in.close();
        }
    }

    private List<Style> readStyles(XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        List<Style> styles = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "styles");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("style")) {
                styles.add(readStyle(parser, context));
            } else {
                skip(parser);
            }
        }
        return styles;
    }

    private Style readStyle (XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "style");
        String name = null;
        String ridName = null;
        float weight = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("name")) {
                name = readName(parser);
            } else if (xmlName.equals("asset")) {
                ridName = readRidName(parser);
            } else if (xmlName.equals("weight")) {
                weight = readWeight(parser);
            } else {
                skip(parser);
            }
        }

        int rid = context.getResources().getIdentifier(ridName, "drawable", context.getPackageName());
        return new Style(name, rid, weight);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readRidName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "asset");
        String ridName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "asset");
        return ridName;
    }

    private float readWeight(XmlPullParser parser) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "weight");
        String weight = readText(parser);
        Log.d("Weight", weight);
        parser.require(XmlPullParser.END_TAG, ns, "weight");
        return Float.parseFloat(weight);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
