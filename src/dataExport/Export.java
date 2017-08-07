package dataExport;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dataImport.DataBean;
import database.LoginBean;
import database.loginController;


@ManagedBean(name="export")
@SessionScoped
public class Export {


	@ManagedProperty(value = "#{databean}")
	DataBean databean;

	loginController logincontroller=new loginController();

	@ManagedProperty(value = "#{loginbean}")
	LoginBean loginbean;

	//Saving the file to the server location


	public void setDatabean(DataBean databean) {
		this.databean = databean;
	}

	public DataBean getDatabean() {
		return databean;
	}

	public LoginBean getLoginbean() {
		return loginbean;
	}

	public void setLoginbean(LoginBean loginbean) {
		this.loginbean = loginbean;
	}

	
	public void exportCSV()
	{
		try {
			Connection conn;
			Statement stmt=null;
			ResultSet resultSet=null;
			Result result=null;
			ResultSetMetaData rsmd=null;
			int colCount=0;
			
			
			
				
					FacesContext fc = FacesContext.getCurrentInstance();
					ExternalContext ec = fc.getExternalContext();
					FileOutputStream fos = null;
					String path = fc.getExternalContext().getRealPath("/temp");
					File dir = new File(path);
					if(!dir.exists())
						new File(path).mkdirs();
					ec.setResponseCharacterEncoding("UTF-8");
				
					String fileNameBase = databean.getTableName() + ".csv";
					
					
					String fileName = path + "/" + fileNameBase;
					File f = new File(fileName);
					
					
				
					try {
						conn = logincontroller.establishConnection(loginbean);
						stmt = conn.createStatement();
						resultSet = stmt.executeQuery("SELECT * FROM "+databean.getTableName());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
					if(resultSet!=null)
					{
						result = ResultSupport.toResult(resultSet);
						Object [][] sData = result.getRowsByIndex();
						String columnNames [] = result.getColumnNames();
						StringBuffer sb = new StringBuffer();
						try {
							fos = new FileOutputStream(fileName);
							for(int i=0; i<columnNames.length; i++) 
							{
								sb.append(columnNames[i].toString() + ",");
							}
							sb.append("\n");
							fos.write(sb.toString().getBytes());
							for(int i = 0; i < sData.length; i++) {
								sb = new StringBuffer();
								for(int j=0; j<sData[0].length; j++) {
									if(sData[i][j]==null)
									{
										String value2="0";
										value2=value2.replaceAll("[^A-Za-z0-9.]", " . ");
										if(value2.isEmpty())
										{
											value2="0";
										}
										sb.append(value2 + ",");
									}
									else
									{
										String value =sData[i][j].toString();
										if(value.contains(","))
										{
											int index=value.indexOf(",");
											String newValue=value.substring(0, index-1);
											value=newValue+value.substring(index+1,value.length());
										}
										value=value.replaceAll("[^A-Za-z0-9,.]", " ");
										if(value.isEmpty())
										{
											value="0";
										}
										sb.append(value + ",");
									}
								}
								sb.append("\n");
								fos.write(sb.toString().getBytes());
							}
							fos.flush();
							fos.close();
						} catch (FileNotFoundException e) {
						
							
							//Display
							
						} catch (IOException io) {
							
							//Display
						}
						String mimeType = ec.getMimeType(fileName);
						FileInputStream in = null;
						byte b;
						ec.responseReset();
						ec.setResponseContentType(mimeType);
						ec.setResponseContentLength((int) f.length());
						ec.setResponseHeader("Content-Disposition",
								"attachment; filename=\"" + fileNameBase + "\"");
						try {
							in = new FileInputStream(f);
							OutputStream output = ec.getResponseOutputStream();
							while(true) {
								b = (byte) in.read();
								if(b < 0)
									break;
								output.write(b);
							}
						} catch (IOException e) {
							//Display
						}
						finally
						{
							try { 
								in.close(); 
							} catch (IOException e) {
								//Display
							}
						}
						fc.responseComplete();
					} 
					else
					{
						//Display
					}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
				
				
				
			}
			
		
		
	



	public void generateXml() throws ParserConfigurationException, SQLException  {



		Connection conn;
		Statement stmt;
		ResultSet rs;
		ResultSetMetaData rsmd;
		int colCount;
		Document doc=null;
		Element results=null;
		
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		rs = null;
		rsmd = null;
		colCount = 0;
		
		
		try {
			

			 factory = DocumentBuilderFactory.newInstance();
			 builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			results = doc.createElement("Results");
			doc.appendChild(results);
		} catch (DOMException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}





		try {
			conn = logincontroller.establishConnection(loginbean);
			stmt = conn.createStatement();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM "+databean.getTableName());

			rsmd = rs.getMetaData();

			colCount = rsmd.getColumnCount();


		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		while (rs.next()) {
			Element row = doc.createElement("Row");
			results.appendChild(row);
			for (int i = 1; i <= colCount; i++) {
				String columnName="";
				try {
					columnName = rsmd.getColumnName(i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Object value="";
				try {
					value = rs.getObject(i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Element node = doc.createElement(columnName);
				node.appendChild(doc.createTextNode(value.toString()));
				row.appendChild(node);
			}
		}
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		StringWriter sw =  new StringWriter();
		StreamResult sr;
		sr = new StreamResult(sw);
		try {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			
			
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		try {
			transformer.transform(domSource, sr);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		FacesContext context = null;
		HttpServletResponse response = null;
		try {
			context = FacesContext.getCurrentInstance();
			response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+databean.getTableName()+"\"");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		try {
			OutputStream responseOutputStream = response.getOutputStream();

			InputStream fileInputStream = new ByteArrayInputStream(sw.toString().getBytes());

			byte[] bytesBuffer = new byte[2048];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) 
			{
				responseOutputStream.write(bytesBuffer, 0, bytesRead);
			}

			responseOutputStream.flush();

			fileInputStream.close();
			responseOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		context.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.


		System.out.println(sw.toString());

		rs.close();
	}
}
