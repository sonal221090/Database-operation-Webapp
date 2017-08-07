package database;

import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


import java.sql.Connection;
import java.sql.DriverManager;


import java.io.IOException;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;

@ManagedBean
@SessionScoped
public class loginController {



		 @ManagedProperty(value = "#{loginbean}")
		 LoginBean loginbean;
		 


		public LoginBean getloginbean() {
			return loginbean;
		}



		public void setloginbean(LoginBean loginbean) {
			this.loginbean = loginbean;
		}


		boolean connStatus=false;
		
		public String validate() {
						
	        String returnPage = "index" ;
	      
/*	        if (loginbean.getUsername().equals(USER_NAME)
	                && loginbean.getPassword().equals(PASSWORD)) {
*/
	           	connStatus = authenticate();
	        	if(connStatus)
	        	{
	        		
	        		returnPage = "navigation"; 
	        		loginbean.setLoggedIn(true);
	        		loginbean.setLoginError("");
	        	}
	        	else{

	        		//Do nothing the error messages have already been set.
	        	}
	        		
	        	 return returnPage;
	           
	        } 
	       
	   
			
		
		
		
		 public Connection establishConnection(LoginBean lb) throws Exception { 
		        
		        // Evaluate JDBC Driver & URL from user Input
		        String url = "";
		        String jdbcDriver="";
		        
		        System.out.println("Host");
		        System.out.println(lb.getHost());
		        System.out.println("Schema");
		        System.out.println(lb.getSchema());
		        
		        
		        switch(lb.getDbms().toLowerCase()){
		            case "mysql": 
		                jdbcDriver ="com.mysql.jdbc.Driver"; 
		                url = "jdbc:mysql://" + lb.getHost() + ":3306/" + lb.getSchema() + "?useSSL=false";
		                break;

		            case "db2": 
		                jdbcDriver ="com.ibm.db2.jcc.DB2Driver";
		                url = "jdbc:db2://" + lb.getHost() + ":50000/" + lb.getSchema() + "?useSSL=false";
		                break;

		            case "oracle": 
		                jdbcDriver ="oracle.jdbc.driver.OracleDriver";
		                url = "jdbc:oracle:thin:@" + lb.getHost() + ":1521:" + lb.getSchema() + "?useSSL=false";
		                break;

		            default:
		                jdbcDriver=null;
		                url=null;
		                break;
		        }

		        Class.forName(jdbcDriver);
		        System.out.println(DriverManager.getConnection(url, lb.getUsername(), lb.getPassword()));
		        
		        return DriverManager.getConnection(url, lb.getUsername(), lb.getPassword());
		    }
		
		
		
		
		
		
		
			
			public boolean authenticate() {

		        boolean status = false;
		        //databaseAccess = new DatabaseAccess();
		        Connection conn = null;

		        try {
		        	conn = establishConnection(loginbean);
		        	System.out.println("*************Check3****************");
		        	status = conn.isValid(10);
		            System.out.println("*************" +status+ "****************");
		        } catch (ClassNotFoundException e) {
		        	
		        	 System.err.println("Driver not found !" + e.getMessage());
		        	 
		        	 loginbean.setLoginError("Driver not found !" + e.getMessage());
		        	 
		        } catch (SQLException e) { 
		        	 System.err.println("Connection to database not established" + e.getMessage()
		                    + "Error Code:" + e.getErrorCode() + "SQL State:" + e.getSQLState());
		        	 
		        	 loginbean.setLoginError("Connection to database not established  \n "+ e.getMessage());
		        	 
		        } catch (Exception e) {
		        	
		        	loginbean.setLoginError("Please enter valid login credentials");
		        	 System.err.println("Cannot Login to Database" + e.getMessage());
		        } finally {
		            try {
		       
		            	
	           	conn.close();
		            	
		            	
		            } catch (Exception e) {
		            	 System.err.println("Connection cannot be closed" + e.getMessage());
		            }
		        }

		        return status;
		    }
		
			public void logout() throws IOException {
			    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			    ec.invalidateSession();
			    ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
			}
		
		
	
}
