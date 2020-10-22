package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Vault;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;

public class InstanceTest {
    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Client client;

    @Test
    public void testGetVaultInstance() {
        try {
            String json = TestData.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestData.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);
            client = Client.createInstance(options);

            Vault vault = client.getVault(TestData.OWNERDID).get();
            assertNotNull(vault);
            System.out.println("appId="+vault.getAppId());
            System.out.println("userDid="+vault.getUserDid());
            System.out.println("appInstanceDid="+vault.getAppInstanceDid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }

}