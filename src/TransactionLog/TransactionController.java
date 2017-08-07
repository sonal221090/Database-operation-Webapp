package TransactionLog;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TransactionController {



	public boolean createTable()
	{

		String sqlQuery = "Create table if not exists s17g308_logs (LogID INT(6)" +
				" NOT NULL AUTO_INCREMENT , User char(50) not null, " +
				"LoginTime timestamp null, LogoutTime timestamp null, " +
				"IPAddress char(50), SessionID char(50), PRIMARY KEY (LogID)) " +
				"ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";

		FacesContext fCtx = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
		String sessionId = session.getId(); 

		HttpServletRequest request =
				(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null) 
			ipAddress = request.getRemoteAddr();
		
		String logintime=currTime();
		return false;

	}


	


	public String currTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return  sdf.format(cal.getTime()) ;
	}

}
