/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendor.connection;

import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.vault.network.VaultApi;
import org.elastos.hive.vendor.vault.network.VaultAuthApi;

public class ConnectionManager {

    private static VaultAuthApi vaultAuthApi;
    private static VaultApi hivevaultApi;

    private static String hivevaultBaseUrl;
    private static String authBaseUrl;

    private static BaseServiceConfig hivevaultConfig = new BaseServiceConfig.Builder().build() ;
    private static BaseServiceConfig authConfig = new BaseServiceConfig.Builder().build();

    public static VaultApi getHiveVaultApi() {
        if(hivevaultApi == null) {
            hivevaultApi = BaseServiceUtil.createService(VaultApi.class,
                    ConnectionManager.hivevaultBaseUrl, ConnectionManager.hivevaultConfig);
        }
        return hivevaultApi;
    }

    public static void resetAuthApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
        vaultAuthApi = null;
        updateAuthConfig(baseServiceConfig);
        updateAuthBaseUrl(baseUrl);
    }

    public static VaultAuthApi getVaultAuthApi() {
        if(vaultAuthApi == null) {
            vaultAuthApi = BaseServiceUtil.createService(VaultAuthApi.class,
                    ConnectionManager.authBaseUrl, ConnectionManager.authConfig);
        }
        return vaultAuthApi;
    }

    private static void updateHiveVaultConfig(BaseServiceConfig hivenvaultConfig) {
        ConnectionManager.hivevaultConfig = hivenvaultConfig;
    }

    private static void updateAuthConfig(BaseServiceConfig authConfig) {
        ConnectionManager.authConfig = authConfig;
    }

    private static void updateHiveVaultBaseUrl(String hivevaultBaseUrl) {
        ConnectionManager.hivevaultBaseUrl = hivevaultBaseUrl;
    }

    private static void updateAuthBaseUrl(String authBaseUrl) {
        ConnectionManager.authBaseUrl = authBaseUrl;
    }

    public static void resetHiveVaultApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
        hivevaultApi = null;
        updateHiveVaultBaseUrl(baseUrl);
        updateHiveVaultConfig(baseServiceConfig);
    }

}