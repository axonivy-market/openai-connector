package com.openai.connector.demo.enums;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.log.Logger;

/**
 * Interface to use with class which have their instance names defined in the
 * CMS.
 *
 * These are typically enumerations.
 */
public interface HasCmsName {
	static final String SLASH = "/";
	static final String ENUMS_CMS_BASE = SLASH + "Enums";

	/**
	 * Get the name of this instance.
	 *
	 * @return name of this instance.
	 */
	String name();

	/**
	 * Return the name entry of the instance.
	 *
	 * @return
	 */
	default String getCmsName() {
		return getCms("name");
	}

	/**
	 * Lookup the entry for an instance in the Ivy Cms.
	 *
	 * If the entry is not found, then return the name of the entry.
	 *
	 * @param entry
	 * @return CMS entry
	 */
	default String getCms(String entry) {
		final String cmsPath = getEntryPath(entry);
		String result = Ivy.cms().co(cmsPath);
		if (StringUtils.isEmpty(result)) {
			Logger.getLogger(HasCmsName.class).warn("Missing CMS entry for '" + cmsPath + "'");
			result = name().replace("_", " ");
		}
		return result;
	}

	/**
	 * Lookup the URL of an entry in the Ivy Cms.
	 *
	 * If the entry is not found, then the name of the entry is returned.
	 *
	 * @param entry
	 * @return
	 */
	default String getCmsUrl(String entry) {
		final String cmsPath = getEntryPath(entry);
		String result = Ivy.cms().cr(cmsPath);
		if (StringUtils.isEmpty(result)) {
			Logger.getLogger(HasCmsName.class).warn("Missing CMS entry for '" + cmsPath + "'");
			result = name();
		}
		return result;
	}

	default String getEntryPath(String entry) {
		return getBasePath() + SLASH + name() + SLASH + entry;
	}

	default String getBasePath() {
		return ENUMS_CMS_BASE + SLASH + getClass().getCanonicalName().replaceAll("[.]", SLASH);
	}
	
}
