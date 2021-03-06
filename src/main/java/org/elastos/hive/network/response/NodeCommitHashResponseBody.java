package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class NodeCommitHashResponseBody extends HiveResponseBody {
    @SerializedName("commit_hash")
    private String commitHash;

    public String getCommitHash() {
        return this.commitHash;
    }
}
