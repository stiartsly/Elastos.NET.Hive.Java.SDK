package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.*;
import org.elastos.hive.did.DApp;
import org.elastos.hive.did.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This is used for representing 3rd-party application.
 */
public class TestData {
	private static TestData instance = null;

	private DIDApp userDid;
	private DApp appInstanceDid;
	private NodeConfig nodeConfig;
	private AppContext context;

	public static TestData getInstance() throws HiveException, DIDException {
		if (instance == null) instance = new TestData();
		return instance;
	}

	private TestData() throws HiveException, DIDException {
		Logger.hive();
		init();
	}

	public void init() throws HiveException, DIDException {
		//TODO set environment config
		String fileName = null;
		switch (EnvironmentType.DEVELOPING) {
			case DEVELOPING:
				fileName = "Developing.conf";
				break;
			case PRODUCTION:
				fileName = "Production.conf";
				break;
			case LOCAL:
				fileName = "Local.conf";
				break;
		}

		ClientConfig clientConfig = ClientConfig.deserialize(Utils.getConfigure(fileName));
		AppContext.setupResolver(clientConfig.resolverUrl(), "data/didCache");

		DummyAdapter adapter = new DummyAdapter();
		ApplicationConfig applicationConfig = clientConfig.applicationConfig();
		appInstanceDid = new DApp(applicationConfig.name(), applicationConfig.mnemonic(), adapter, applicationConfig.passPhrase(), applicationConfig.storepass());

		UserConfig userConfig = clientConfig.userConfig();
		userDid = new DIDApp(userConfig.name(), userConfig.mnemonic(), adapter, userConfig.passPhrase(), userConfig.storepass());

		nodeConfig = clientConfig.nodeConfig();

		//初始化Application Context
		context = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return System.getProperty("user.dir") + File.separator + "data/store" + File.separator + nodeConfig.storePath();
			}

			@Override
			public DIDDocument getAppInstanceDocument() {
				try {
					return appInstanceDid.getDocument();
				} catch (DIDException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				return CompletableFuture.supplyAsync(() -> {
					try {
						Claims claims = JwtUtil.getBody(jwtToken);
						if (claims == null)
							throw new HiveException("Invalid jwt token as authorization.");
						return appInstanceDid.createToken(appInstanceDid.createPresentation(
								userDid.issueDiplomaFor(appInstanceDid),
								claims.getIssuer(),
								(String) claims.get("nonce")), claims.getIssuer());
					} catch (Exception e) {
						throw new CompletionException(new HiveException(e.getMessage()));
					}
				});
			}
		}, nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public AppContext getAppContext() {
		return this.context;
	}

	public String getOwnerDid() {
		return nodeConfig.ownerDid();
	}

	public String getProviderAddress() {
		return nodeConfig.provider();
	}

	public Vault newVault() {
		return new Vault(context, nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public BackupService getBackupService() {
		BackupService bs = this.newVault().getBackupService();
		bs.setupContext(new BackupContext() {
			@Override
			public String getType() {
				return null;
			}

			@Override
			public String getParameter(String key) {
				if ("targetDid".equals(key)) {
					return nodeConfig.targetDid();
				} else if ("targetHost".equals(key)) {
					return nodeConfig.targetHost();
				}
				return null;
			}

			@Override
			public CompletableFuture<String> getAuthorization(String srcDid, String targetDid, String targetHost) {
				return CompletableFuture.supplyAsync(() -> {
					try {
						return userDid.issueBackupDiplomaFor(srcDid, targetHost, targetDid).toString();
					} catch (DIDException e) {
						throw new CompletionException(new HiveException(e.getMessage()));
					}
				});
			}
		});
		return bs;
	}

	private enum EnvironmentType {
		DEVELOPING,
		PRODUCTION,
		LOCAL
	}
}
