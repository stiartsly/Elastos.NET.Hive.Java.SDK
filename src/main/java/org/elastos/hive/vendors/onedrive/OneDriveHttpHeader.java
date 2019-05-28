package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;

class OneDriveHttpHeader {
	static final String Authorization	= "Authorization";
	static final String ContentType		= "Content-Type";

	static String bearerValue(AuthHelper authHelper) {
		if (authHelper.getToken() != null) {
			return "bearer " + authHelper.getToken().getAccessToken();			
		}
		return "";
	}
}
