package com.ushahidi.swiftriver.core.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

public class TextUtil {

	/**
	 * Given a phrase (string), generates and returns a URL "slug" The phrase is
	 * first normalized and any whitespaces are replaced with hyphens
	 * ("-")
	 * 
	 * @param phrase
	 * @return
	 */
	public static String getURLSlug(String phrase) {
		if (phrase == null || phrase.trim().length() == 0)
			return "";

		String nonWhitespace = Pattern.compile("[^\\w-]").matcher(phrase)
				.replaceAll("-");
		String normalized = Normalizer.normalize(nonWhitespace, Form.NFD);
		String slug = Pattern.compile("[\\s]").matcher(normalized)
				.replaceAll("");

		return slug.toLowerCase();
	}

}
