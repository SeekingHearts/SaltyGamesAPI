package main.me.aaron;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Messages {

	/** the resource bundle name. */
	private static final String BUNDLE_NAME = "me.aaron.messages"; //$NON-NLS-1$

	/** the default resource bundle; used as fallback. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/** the bundles by locales. */
	private static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();

	/**
	 * hidden constructor.
	 */
	private Messages() {
	}

	/**
	 * Returns the localized string
	 * 
	 * @param key
	 *            string key
	 * @param locale
	 *            locale to be used.
	 * @return localized string.
	 */
	public static String getString(String key, Locale locale) {
		try {
			final ResourceBundle res = BUNDLES.computeIfAbsent(locale, (l) -> {
				try {
					return ResourceBundle.getBundle(BUNDLE_NAME, l);
				} catch (@SuppressWarnings("unused") MissingResourceException ex) {
					return RESOURCE_BUNDLE;
				}
			});
			return res.getString(key);
		} catch (@SuppressWarnings("unused") MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}