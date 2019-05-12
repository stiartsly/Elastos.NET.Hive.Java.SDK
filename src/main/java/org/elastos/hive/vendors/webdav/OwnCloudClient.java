package org.elastos.hive.vendors.webdav;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.Authenticator;
import org.elastos.hive.ClientInfo;
import org.elastos.hive.DriveType;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.HiveException;
import org.elastos.hive.Result;

public final class OwnCloudClient extends Client {
	private static Client clientInstance;

	private OwnCloudClient(OwnCloudParameter parameter) {
		// TODO;
	}

	public static Client createInstance(OwnCloudParameter parameter) {
		if (clientInstance == null) {
			clientInstance = new OwnCloudClient(parameter);
		}
		return clientInstance;
	}

	public static Client getInstance() {
		return clientInstance;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.ownCloud;
	}

	@Override
	public synchronized void login(Authenticator authenticator) throws HiveException {
		// TODO;
	}

	@Override
	public synchronized void logout() throws HiveException {
		// TODO;
	}

	@Override
	public ClientInfo getLastInfo() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<ClientInfo>> getInfo() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<ClientInfo>> getInfo(Callback<ClientInfo> callback) {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<Drive>> getDefaultDrive() {
		// TODO
		return null;
	}

	@Override
	public CompletableFuture<Result<Drive>> getDefaultDrive(Callback<Drive> callback) {
		// TODO
		return null;
	}
}