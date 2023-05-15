// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncRequest implements Serializable {

	private String fn;
	private String userName;
	private String password;
	private Integer siteId;
	@JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
	private LocalDateTime coolDate;
	private Integer id;

	//

	public SyncRequest() {
		super();
		userName = System.getProperty("xy.parman.user", "");
		password = System.getProperty("xy.parman.password", "");
		siteId = Integer.valueOf(System.getProperty("xy.parman.site", "1"));
	}

	private static final long serialVersionUID = 1L;

	//

	public String getFn() {
		return fn;
	}

	public void setFn(String fn) {
		this.fn = fn;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public LocalDateTime getCoolDate() {
		return coolDate;
	}

	public void setCoolDate(LocalDateTime coolDate) {
		this.coolDate = coolDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
