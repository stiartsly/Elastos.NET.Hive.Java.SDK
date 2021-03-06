package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class ScriptInsertExecutableBody {
    @SerializedName("collection")
    private String collection;
    @SerializedName("document")
    private KeyValueDict document;
    @SerializedName("options")
    private KeyValueDict options;

    public ScriptInsertExecutableBody(String collection, KeyValueDict document) {
        this(collection, document, null);
    }

    public ScriptInsertExecutableBody(String collection, KeyValueDict document, KeyValueDict options) {
        this.collection = collection;
        this.document = document;
        this.options = options;
    }

}
