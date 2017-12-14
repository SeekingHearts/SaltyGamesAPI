package main.me.aaron;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Messages {
	
	private static final String BUNDLE_NAME = "main.me.aaron.messages";
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();
	
	private Messages() {}
	
	public static String getString(String key, Locale locale) {
		try {
			final ResourceBundle res = BUNDLES.computeIfAbsent(locale, (l) -> {
				try {
					return ResourceBundle.getBundle(BUNDLE_NAME, l);
				} catch (MissingResourceException e) {
					return RESOURCE_BUNDLE;
				}
			});
			return res.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}
