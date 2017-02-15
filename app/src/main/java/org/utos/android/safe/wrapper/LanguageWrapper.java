package org.utos.android.safe.wrapper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * Created by zachariah.davis on 2/3/17.
 */
public class LanguageWrapper extends ContextWrapper {

    public LanguageWrapper(Context base) {
        super(base);
    }

    @SuppressWarnings("deprecation") public static ContextWrapper wrap(Context context, String language) {
        Configuration config = context.getResources().getConfiguration();
        if (!language.equals("")) {
            //            Locale locale;
            //
            //            if (language.contains("_")) {
            //                Log.d("LanguageWrapper", "YES");
            //                String[] separated = language.split("_");
            //                Log.d("LanguageWrapper", separated[0]);
            //                Log.d("LanguageWrapper", separated[1].substring(1));
            //                locale = new Locale(separated[0], separated[1].substring(1));
            //            } else {
            //                Log.d("LanguageWrapper", "NO");
            //                locale = new Locale(language);
            //            }
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale);
            } else {
                setSystemLocaleLegacy(config, locale);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale);
                context = context.createConfigurationContext(config);
            } else {
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        }
        return new LanguageWrapper(context);
    }

    @SuppressWarnings("deprecation") public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N) public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation") public static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N) public static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }

}
