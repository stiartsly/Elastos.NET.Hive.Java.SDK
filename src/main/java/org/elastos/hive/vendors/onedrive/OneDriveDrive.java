package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;
import org.elastos.hive.vendors.connection.Model.HeaderConfig;
import org.elastos.hive.vendors.onedrive.network.Model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.network.Model.DriveResponse;
import org.elastos.hive.vendors.onedrive.network.Model.FileOrDirPropResponse;
import org.elastos.hive.vendors.onedrive.network.OneDriveApi;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

final class OneDriveDrive extends Drive {
	private final AuthHelper authHelper;
	private Drive.Info driveInfo;

	OneDriveDrive(Drive.Info driveInfo, AuthHelper authHelper) {
		this.driveInfo = driveInfo;
		this.authHelper = authHelper;
	}

	@Override
	public DriveType getType() {
		return DriveType.oneDrive;
	}

	@Override
	public String getId() {
		return driveInfo.get(Drive.Info.driveId);
	}

	@Override
	public Drive.Info getLastInfo() {
		return driveInfo;
	}

	@Override
	public CompletableFuture<Drive.Info> getInfo() {
		return getInfo(new NullCallback<Drive.Info>());
	}

	@Override
	public CompletableFuture<Drive.Info> getInfo(Callback<Drive.Info> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> getInfo(padding, callback));
	}

	private CompletableFuture<Drive.Info> getInfo(Void padding, Callback<Drive.Info> callback) {
		CompletableFuture<Drive.Info> future = new CompletableFuture<Drive.Info>();
		if (callback == null)
			callback = new NullCallback<Drive.Info>();

		try {
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL, config);
			Call call = oneDriveApi.getInfo();
			call.enqueue(new DriveDriveCallback(null, future , callback , Type.GET_INFO));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
		return future;
	}

	@Override
	public CompletableFuture<Directory> getRootDir() {
		return getRootDir(new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getRootDir(Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> getRootDir(padding, callback));
	}

	private CompletableFuture<Directory> getRootDir(Void padding, Callback<Directory> callback) {
		if (callback == null)
			callback = new NullCallback<Directory>();

		return getDirectory("/", callback);
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName) {
		return createDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> createDirectory(String pathName, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> createDirectory(padding, pathName, callback));
	}

	private CompletableFuture<Directory> createDirectory(Void padding, String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Path name must be absulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException e = new HiveException("Impossible to create root directory");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String urlPath;
		String name;

		int pos = pathName.lastIndexOf("/");
		if (pos == 0) {
			name = pathName.replace("/", "");
			urlPath = "/root/children";
		} else {
			String parentPath = pathName.substring(0, pos);
			name = pathName.substring(pos + 1);
			urlPath = "/root:/"+parentPath+":/children";
		}

		CreateDirRequest createDirRequest = new CreateDirRequest(name);
		try {
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL, config);
			Call call = oneDriveApi.createDir(urlPath , createDirRequest);
			call.enqueue(new DriveDriveCallback(pathName, future , callback , Type.CREATE_DIR));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
		return future;
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName) {
		return getDirectory(pathName, new NullCallback<Directory>());
	}

	@Override
	public CompletableFuture<Directory> getDirectory(String pathName, Callback<Directory> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> getDirectory(padding, pathName, callback));
	}

	private CompletableFuture<Directory> getDirectory(Void padding, String pathName, Callback<Directory> callback) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (callback == null)
			callback = new NullCallback<Directory>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Path name must be absulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		String fullPath;
		if (pathName.equals("/"))
			fullPath = OneDriveConstance.ROOT ;
		else
			fullPath = OneDriveConstance.ROOT+":"+pathName;

		try {
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL ,config);
			Call call = oneDriveApi.getFileOrDirProp(fullPath);
			call.enqueue(new DriveDriveCallback(pathName, future , callback , Type.GET_DIR));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}
		return future;
	}

	@Override
	public CompletableFuture<File> createFile(String pathName) {
		return createFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> createFile(String pathName, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> createFile(padding, pathName, callback));
	}

	private CompletableFuture<File> createFile(Void padding, String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!pathName.startsWith("/")) {
			HiveException ex = new HiveException("Path name must be absulte path");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException ex = new HiveException("Impossible to create root directory as file");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		try {
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL, config);
			Call call = oneDriveApi.createFile(pathName);
			call.enqueue(new DriveDriveCallback(pathName, future , callback , Type.CREATE_FILE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<File> getFile(String pathName) {
		return getFile(pathName, new NullCallback<File>());
	}

	@Override
	public CompletableFuture<File> getFile(String pathName, Callback<File> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> getFile(padding, pathName, callback));
	}

	private CompletableFuture<File> getFile(Void padding, String pathName, Callback<File> callback) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (callback == null)
			callback = new NullCallback<File>();

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Path name must be absulte path");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (pathName.equals("/")) {
			HiveException ex = new HiveException("Impossible to open root directory as file");
			callback.onError(ex);
			future.completeExceptionally(ex);
			return future;
		}

		String fullPath;
		if (pathName.equals("/"))
			fullPath = OneDriveConstance.ROOT ;
		else
			fullPath = OneDriveConstance.ROOT+":"+pathName;

		try {
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL, config);
			Call call = oneDriveApi.getFileOrDirProp(fullPath);
			call.enqueue(new DriveDriveCallback(pathName, future , callback , Type.GET_FILE));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path) {
		return getItemInfo(path, new NullCallback<ItemInfo>());
	}

	@Override
	public CompletableFuture<ItemInfo> getItemInfo(String path, Callback<ItemInfo> callback) {
		return authHelper.checkExpired()
				.thenCompose(padding -> getItemInfo(padding, path, callback));
	}

	private CompletableFuture<ItemInfo> getItemInfo(Void padding, String pathName, Callback<ItemInfo> callback) {
		CompletableFuture<ItemInfo> future = new CompletableFuture<ItemInfo>();

		if (callback == null)
			callback = new NullCallback<ItemInfo>();

		if (pathName == null || pathName.isEmpty()) {
			HiveException e = new HiveException("Empty path name");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		if (!pathName.startsWith("/")) {
			HiveException e = new HiveException("Need a absolute path to get a file or directory's item info.");
			callback.onError(e);
			future.completeExceptionally(e);
			return future;
		}

		try {
			String fullPath;
			if (pathName.equals("/"))
				fullPath = OneDriveConstance.ROOT ;
			else
				fullPath = OneDriveConstance.ROOT+":"+pathName;
			
			HeaderConfig headerConfig = new HeaderConfig.Builder()
					.authToken(authHelper.getToken())
					.build();
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.headerConfig(headerConfig)
					.build();
			OneDriveApi oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class,
					OneDriveConstance.ONE_DRIVE_API_BASE_URL, config);
			Call call = oneDriveApi.getFileOrDirProp(fullPath);
			call.enqueue(new DriveDriveCallback(pathName, future , callback , Type.GET_ITEMINFO));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			callback.onError(e);
			future.completeExceptionally(e);
		}

		return future;
	}
	
	private class DriveDriveCallback implements retrofit2.Callback{
		private final String pathName;
		private final CompletableFuture future;
		private final Callback callback;
		private final Type type ;

		public DriveDriveCallback(String pathName, CompletableFuture future, Callback callback , Type type) {
			this.pathName = pathName;
			this.future = future;
			this.callback = callback;
			this.type = type ;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() == 401) {
				authHelper.getToken().expired();
				HiveException e = new HiveException("Server Error: " + response.message());
				this.callback.onError(e);
				future.completeExceptionally(e);
				return;
			}

			if (response.code() != 200 && response.code() != 201) {
				HiveException ex = new HiveException("Server Error: " + response.message());
				this.callback.onError(ex);
				future.completeExceptionally(ex);
				return;
			}

			switch (type){
				case GET_INFO:
					DriveResponse driveResponse= (DriveResponse) response.body();

					HashMap<String, String> attrs = new HashMap<>();
					attrs.put(Drive.Info.driveId, driveResponse.getId());
					// TODO:

					Drive.Info driveInfo = new Drive.Info(attrs);
					this.callback.onSuccess(driveInfo);
					future.complete(driveInfo);
					break;

				case CREATE_DIR:
				case GET_DIR: {
					FileOrDirPropResponse dirResponse= (FileOrDirPropResponse) response.body();

					if (dirResponse.getFolder() == null) {
						HiveException e = new HiveException("This is not a folder");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> dirAttrs = new HashMap<>();
					dirAttrs.put(Directory.Info.itemId, dirResponse.getId());
					// TODO:

					Directory.Info dirInfo_ = new Directory.Info(dirAttrs);
					OneDriveDirectory directory = new OneDriveDirectory(pathName,dirInfo_,authHelper);
					this.callback.onSuccess(directory);
					future.complete(directory);
					break;
				}
				case CREATE_FILE:
				case GET_FILE: {
					FileOrDirPropResponse filePropResponse= (FileOrDirPropResponse) response.body();

					if (filePropResponse.getFolder() !=null) {
						HiveException e = new HiveException("This is not a file");
						this.callback.onError(e);
						future.completeExceptionally(e);
						return;
					}

					HashMap<String, String> fileAttrs = new HashMap<>();
					fileAttrs.put(File.Info.itemId, filePropResponse.getId());
					// TODO:

					File.Info fileInfo = new File.Info(fileAttrs);
					OneDriveFile file = new OneDriveFile(pathName, fileInfo, authHelper);
					this.callback.onSuccess(file);
					future.complete(file);
					break;
				}
				case GET_ITEMINFO: {
					FileOrDirPropResponse itemResponse= (FileOrDirPropResponse) response.body();

					HashMap<String, String> itemAttrs = new HashMap<>();
					itemAttrs.put(ItemInfo.itemId, itemResponse.getId());
					if (itemResponse.getFolder() != null) {
						itemAttrs.put(ItemInfo.type, "directory");
					}
					else {
						itemAttrs.put(ItemInfo.type, "file");
					}

					itemAttrs.put(ItemInfo.size, Integer.toString(itemResponse.getSize()));
					itemAttrs.put(ItemInfo.name, itemResponse.getName());

					ItemInfo info = new ItemInfo(itemAttrs);
					this.callback.onSuccess(info);
					future.complete(info);
					break;
				}
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.callback.onError(e);
			future.completeExceptionally(e);
		}
	}

	private enum Type{
		GET_INFO, CREATE_DIR , CREATE_FILE , GET_DIR , GET_FILE, GET_ITEMINFO
	}
}
