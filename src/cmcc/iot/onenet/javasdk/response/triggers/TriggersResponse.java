package cmcc.iot.onenet.javasdk.response.triggers;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TriggersResponse {
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("ds_id")
	private String ds_id;
	
	@JsonProperty("ds_uuids")
	private List<String> dsuuids;
	
	@JsonProperty("dev_ids")
	private List<String> dev_ids;
	
	@JsonProperty("url")
	private String url;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("threshold")
	private Object threshold;
	
	@JsonProperty("invalid")
	private boolean invalid;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	
	@JsonProperty("create_time")
	private Date createtime;
	
	@JsonProperty("target_type")
	private Date targettype;
	
	
	
	public String getDs_id() {
		return ds_id;
	}
	public void setDs_id(String ds_id) {
		this.ds_id = ds_id;
	}
	public List<String> getDev_ids() {
		return dev_ids;
	}
	public void setDev_ids(List<String> dev_ids) {
		this.dev_ids = dev_ids;
	}
	public void setThreshold(Object threshold) {
		this.threshold = threshold;
	}
	public Date getTargettype() {
		return targettype;
	}
	public void setTargettype(Date targettype) {
		this.targettype = targettype;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getDsuuids() {
		return dsuuids;
	}
	public void setDsuuids(List<String> dsuuids) {
		this.dsuuids = dsuuids;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getThreshold() {
		return threshold;
	}
	public boolean isInvalid() {
		return invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}


}
