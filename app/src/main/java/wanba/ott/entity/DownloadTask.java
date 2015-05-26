package wanba.ott.entity;

import java.util.Date;

import android.net.Uri;

public class DownloadTask {

	private Uri downloadURI;
	private String appId;
	private String name;
	private String desc;
	private String status;
	private Date createTime;
	private Long downloadId;
	private Long appSize;

	public DownloadTask() {
		super();
	}

	public DownloadTask(String appId, Uri downloadURI, String name,
			String desc, Long appSize) {
		super();
		this.downloadURI = downloadURI;
		this.name = name;
		this.desc = desc;
		this.appSize = appSize;
		this.appId = appId;
	}

	public Long getAppSize() {
		return appSize;
	}

	public void setAppSize(Long appSize) {
		this.appSize = appSize;
	}

	public Long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(Long downloadId) {
		this.downloadId = downloadId;
	}

	public Uri getDownloadURI() {
		return downloadURI;
	}

	public void setDownloadURI(Uri downloadURI) {
		this.downloadURI = downloadURI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}
