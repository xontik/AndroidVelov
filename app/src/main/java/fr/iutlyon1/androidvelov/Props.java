package fr.iutlyon1.androidvelov;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class Props {

    private static Map<Context, Props> instances = new HashMap<>();
    private Properties properties;

    private Props(Context context) {
        try {
            final InputStream propertiesStream = context.getAssets().open("app.properties");
            properties = new Properties();
            properties.load(propertiesStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Props getInstance(Context context) {
        if (!instances.containsKey(context)) {
            instances.put(context, new Props(context));
        }
        return instances.get(context);
    }
}
