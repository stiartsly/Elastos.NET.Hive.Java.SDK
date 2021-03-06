package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.utils.DateUtil;

import java.math.BigDecimal;

/**
 * The class to represent the information of File or Folder.
 */
public class FileInfo {
	@SerializedName("type")
	private String type;
	@SerializedName("name")
	private String name;
	@SerializedName("size")
	private int size;
	@SerializedName("last_modify")
	private double lastModify;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setLastModify(double lastModify) {
		this.lastModify = lastModify;
	}

	public String getLastModified() {
		long timeStamp = BigDecimal.valueOf(lastModify).multiply(new BigDecimal(1000)).longValue();
		return DateUtil.getCurrentEpochTimeStamp(timeStamp);
	}
}
