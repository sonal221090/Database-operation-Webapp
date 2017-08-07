package TransactionLog;

import java.util.Date;

public class TransactionBean {


	private Date loginTime;
	private Date logoutTime;
	private String IpAddress;
	private String SessionId;
	private String currentSession;



	public String getIpAddress() {
		return IpAddress;
	}
	public void setIpAddress(String ipAddress) {
		IpAddress = ipAddress;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getCurrentSession() {
		return currentSession;
	}
	public void setCurrentSession(String currentSession) {
		this.currentSession = currentSession;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getLogoutTime() {
		return logoutTime;
	}
	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}


}
