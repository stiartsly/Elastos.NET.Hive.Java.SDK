package org.elastos.hive;

public class AuthToken {
	private String accessToken;
	private long expiresTime;
	private String tokenType;

	public AuthToken(String accessToken, long expiresTime, String tokenType) {
		this.accessToken = accessToken;
		this.expiresTime = expiresTime;
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public String getTokenType() {
		return tokenType;
	}

	public boolean isExpired() {
		long currentSeconds = System.currentTimeMillis() / 1000;
		return currentSeconds >= expiresTime;
	}
}
