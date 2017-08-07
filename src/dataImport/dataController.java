package dataImport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import database.LoginBean;
import database.loginController;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


@ManagedBean
@SessionScoped
public class dataController {

	@ManagedProperty(value = "#{databean}")
	DataBean databean;

	loginController logincontroller=new loginController();

	@ManagedProperty(value = "#{loginbean}")
	LoginBean loginbean;

	ResultSetMetaData tempMetaData; // To be used to determine the row type while parsing CSV in order to handle VARCHAR2

	public DataBean getDatabean() {
		return databean;
	}

	public LoginBean getLoginbean() {
		return loginbean;
	}

	public void setLoginbean(LoginBean loginbean) {
		this.loginbean = loginbean;
	}

	public void setDatabean(DataBean databean) {
		this.databean = databean;
	}

	public String processFile() {

		String status="interim";

		databean.setErrorMessage(""); // Clearing the error message for each upload

		if(isfileValid()){

			try {
				File tempDataFile = new File(getPath());


				FileUtils.copyInputStreamToFile(
						new BufferedInputStream(databean.getFile().getInputStream()), tempDataFile);
				databean.setFilePath(tempDataFile.getAbsolutePath());
				databean.setFileSize(tempDataFile.length());	

				if(databean.getFileType().equals("metadata")){

					List<List<String>> rows;

					if(databean.getFileFormat().equals("xls"))
						rows = transformExcelMeta();

					else
					{
						rows = transformMetaFile();
					}


					databean.setColumnCount(rows.get(0).size());
					databean.setRowCount(rows.size());
					databean.setFileContentType(databean.getFileFormat());
					databean.setUploadedmetaData(rows); // To be used for db insert


				}

				else{

					if(databean.getFileFormat().equals("xls"))


					{	
						List<List<Double>> rows;
						rows = transformExcelData();
						databean.setColumnCount(rows.get(0).size());
						databean.setRowCount(rows.size());
						databean.setStatus("File Imported Successfully");
						databean.setFileContentType(databean.getFileFormat());
						databean.setErrorMessage("File is good");
						databean.setUploadedData(rows);// To be used for db insert
						status="interim";
					}
					else
					{

						List<List<Double>> rows;
						rows = transformFile();
						System.out.println("File was transformed");
						databean.setColumnCount(rows.get(0).size());
						databean.setRowCount(rows.size());
						databean.setStatus("File Imported Successfully");
						databean.setFileContentType(databean.getFileFormat());
						databean.setErrorMessage("File is good");
						databean.setUploadedData(rows);// To be used for db insert

						status="interim";
					}
				}

			} catch (Exception e) {
				status="failure";
				databean.setErrorMessage("Please upload a valid file"+e);

			}
			finally{

				databean.setInterim(true);
			}
		}
		return status;

	}


	public String uploadSwitch()
	{
		if(databean.getFileType().equals("metadata")){
			uploadMetaData();

		}
		else{
			uploadData();
		}

		databean.setReport(true);
		return "report";

	}


	public void uploadMetaData()

	{

		List<List<String>> rows=databean.getUploadedmetaData();

		if(databean.getFileType().equals("metadata")){

			String sql = "CREATE TABLE IF NOT EXISTS s17g308_"+databean.getFileLabel() +"(\n";

			if(rows.isEmpty())
			{
				databean.setStatus("There rows were empty. Could not create the table");
				System.out.println("There was an error in creating the table");
				databean.setStatus("There was an error pasring the file. Please verify the file type( csv,excel) again");
			}
			else
			{
				if (!rows.get(0).isEmpty()) {
					boolean goodData=true;
					for (int i = 0; i < rows.size(); i++) {

						if (databean.getHeaderRow().toLowerCase().equals("y") && i == 0
								&& !databean.getFileFormat().equals("xls")) {

							//Skipping the header
						} else {


							try {
								
								sql = sql + " " + rows.get(i).get(0);
								if( rows.get(i).get(2).equalsIgnoreCase("computed")){
									
									sql=sql+"_COMPUTED";
									}
									


								if(rows.get(i).get(1).equalsIgnoreCase("String"))
										
								{
									sql = sql + " " +"varchar(255)";
								}
								else
								{
									sql = sql + " " + rows.get(i).get(1);
								}

								if (i != rows.size() - 1) {
									sql = sql + ",\n";
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								goodData=false;
								break;
							}



						}

					}
					sql = sql + ");";

					if(goodData){


						try
						{ 
							Connection conn = logincontroller.establishConnection(loginbean);
							Statement stmt = conn.createStatement();
							// create a new table

							stmt.execute(sql);
							System.out.println(sql);
							System.out.println("Table Created");
							databean.setStatus("The table has been created successfully");
						}catch(Exception e){
							System.out.println(sql);
							System.out.println(databean.getHeaderRow().toLowerCase());
							System.out.println("There was an error in creating the table"+e);
							databean.setStatus("There was an error creating the table");}
					}
					else
					{
						databean.setStatus("There was an error parsing the file. Please verify the file type(csv,excel) again");}
				}
			}


		}
	}








	public void uploadData()

	{
		List<List<Double>> rows=databean.getUploadedData();

		try {
			Connection conn = logincontroller.establishConnection(loginbean);
			Statement stmt = conn.createStatement();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+databean.getTableName());

			ResultSetMetaData rsMetaData = rs.getMetaData();

			int numberOfColumns = rsMetaData.getColumnCount();


			System.out.println("The row size is"+rows.size());

			String sql = "Insert into "+databean.getTableName() +"(";

			for (int i = 1; i <= numberOfColumns; i++) {
				if(rsMetaData.getColumnName(i).contains("_COMPUTED"))
				{
					//Ignoring the computed columns
				}
				else
				{
					sql=sql+rsMetaData.getColumnName(i);
				}		

				if(i==numberOfColumns){
					if(sql.endsWith(","))
					{
						sql = sql.substring(0, sql.length()-1);
					}
					sql=sql+")\n Values(";
					
				}
				else
				{
					
					if(rsMetaData.getColumnName(i).contains("_COMPUTED"))
					{
						//Ignoring the computed columns
					}
					else
					{
					sql=sql+",";
					}
				}
			}

			int validcols=validColumns();
			for(int j=0;j<rows.size();j++){


				if(databean.getHeaderRow().toLowerCase().equals("y")&& j==0 && !databean.getFileFormat().equals("xls")){

					System.out.println("*************************************");

					System.out.println("skipping the header");
					System.out.println("*************************************");

				}
				else{
					

					for(int k=0;k<validcols;k++ ){

						sql=sql+rows.get(j).get(k);

						if(k==validcols-1){

						}else{
							sql=sql+",";

						}

					}

					if(j==rows.size()-1){

					}else
					{
						sql= sql+ "),(";
					}
				}
			}

			sql= sql+ ");";

			try
			{ 

				// create a new table
				stmt.execute(sql);

				databean.setStatus("Rows added successfully");
			}catch(Exception e){
				System.out.println(sql);
				System.out.println(databean.getHeaderRow().toLowerCase());
				System.out.println("There was an error in creating the table"+e);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block

			databean.setStatus("There was an error while adding the values to the datbase"+e);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			databean.setStatus("There was an error while adding the values to the datbase"+e);
		}


	}





	private boolean isfileValid() {

		if (databean.getFile() == null || databean.getFile().getSize() < 1) {

			databean.setErrorMessage("Please upload a valid file");
			System.out.println(databean.getFile().getSize());
			System.out.println(databean.getFile());

			return false;
		}

		else {
			return true;

		}
	}


	// Create a path for the file to be stored
	private String getPath() throws IOException {
		StringBuilder path = new StringBuilder(((ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext()).getRealPath(""));
		path.append("/WebContent/data-import/");
		path.append(databean.getFileType()).append("/");       
		path.append(databean.getFileLabel()).append(".");
		path.append(databean.getFileFormat());
		return path.toString();
	}


	public  List<List<String>> transformExcelMeta() throws IOException{

		List<List<String>> rows= new ArrayList<>();

		// Prepare the file stats
		final File tempFile =
				File.createTempFile("temp" + databean.getFileLabel(), databean.getFileFormat());
		tempFile.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(databean.getFile().getInputStream(), out);
		}

		FileInputStream fis = new FileInputStream(tempFile);

		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet ws = wb.getSheetAt(0);

		int rowNum = ws.getLastRowNum()+1;
		int colNum = ws.getRow(0).getLastCellNum();

		System.out.println(rowNum);
		System.out.println(colNum);




		/*	List<String> data = new ArrayList<>();*/

		List<List<String>> Datarow = new ArrayList<>();
		int badData=0;

		for (int i=0; i<rowNum; i++){

			List<String> data = new ArrayList<>();

			HSSFRow row = ws.getRow(i);
			data.clear();
			
			

			for (int j=0; j<colNum; j++){
				HSSFCell cell = row.getCell(j);
				String value = cellToString(cell);
				data.add(value); 		
			}

			if(databean.getHeaderRow().toLowerCase().equals("y")&& i==0){
				//Ignore the header
			}
			else
			{
				
					try {
						Datarow.add(data);
						System.out.println(data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						badData=badData+1;
					}
				
				
			}


		}
		
		databean.setBadRowCount(badData);
		System.out.println(Datarow);
		return Datarow;
	}

	public static String cellToString (HSSFCell cell){

		int type;
		Object result = null;
		type = cell.getCellType();

		switch(type) {


		case 0://numeric value in excel
			result = cell.getNumericCellValue();
			break;
		case 1: //string value in excel
			result = cell.getStringCellValue();
			break;
		case 2: //boolean value in excel
			result = cell.getBooleanCellValue ();
			break;

		case 3: //Cell type is blank
			result = "";
			break;
		case 4: //boolean value in excel
			result = cell.getBooleanCellValue ();
			break;
		case 5: //Error
			result = cell.getStringCellValue();
			break;


		default:

			System.out.println("There are not support for type with id ["+type+"] of cell");

		}

		return result.toString();
	}


	public  List<List<Double>> transformExcelData() throws IOException{

		boolean baddata=false;
		int badcount=0;

		// Prepare the file stats
		final File tempFile =
				File.createTempFile("temp" + databean.getFileLabel(), databean.getFileFormat());
		tempFile.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(databean.getFile().getInputStream(), out);
		}

		FileInputStream fis = new FileInputStream(tempFile);

		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet ws = wb.getSheetAt(0);

		int rowNum = ws.getLastRowNum()+1;
		int colNum = ws.getRow(1).getLastCellNum();

		System.out.println(rowNum);
		System.out.println(colNum);




		/*	List<String> data = new ArrayList<>();*/

		List<List<Double>> Datarow = new ArrayList<>();

		for (int i=0; i<rowNum; i++){
			baddata=false;
			List<Double> data = new ArrayList<>();

			HSSFRow row = ws.getRow(i);
			data.clear();

			for (int j=0; j<colNum; j++){
				HSSFCell cell = row.getCell(j);
				try{

					Double value = Double.parseDouble(cellToString(cell));
					data.add(value); 	

				}
				catch(Exception e)
				{
					if(databean.getHeaderRow().toLowerCase().equals("y")&& i==0)
					{
						//expected error as the headers are supposed to be text.
					}
					else
					{
						System.out.println(e);
						baddata=true;
						badcount=badcount+1;
					}

				}




			}

			if(databean.getHeaderRow().toLowerCase().equals("y")&& i==0){
				//Ignore the header
				System.out.println("inside the header");
			}
			else
			{
				if(baddata==false)
				{
					Datarow.add(data);
					System.out.println("Printing Data");
					System.out.println(data);
				}


			}


		}

		System.out.println("Datarow"+ Datarow);
		databean.setBadRowCount(badcount);

		try {
			wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Datarow;
	}
	
	
	public int validColumns()
	{	
		int columncount=0;
		try {
			Connection conn = logincontroller.establishConnection(loginbean);
			Statement stmt = conn.createStatement();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+databean.getTableName());
			ResultSetMetaData rsMetaData = rs.getMetaData();
			tempMetaData=rsMetaData;
			int numberOfColumns = rsMetaData.getColumnCount();
			
		
			for (int i = 1; i <= numberOfColumns; i++) {
				if(rsMetaData.getColumnName(i).contains("_COMPUTED"))
				{
					
				}
				else
				{
					columncount=columncount+1;
				}
			}
			
			conn.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return columncount;
	}

	public  Double cellToDouble (HSSFCell cell){

		int type;
		Object result = null;
		type = cell.getCellType();

		switch(type) {


		case 0://numeric value in excel
			result = cell.getNumericCellValue();
			break;
		case 1: //string value in excel
			result = cell.getStringCellValue();
			break;
		case 2: //boolean value in excel
			result = cell.getBooleanCellValue ();
			break;

		case 3: //Cell type is blank
			result = "";
			break;
		case 4: //boolean value in excel
			result = cell.getBooleanCellValue ();
			break;
		case 5: //Error
			result = cell.getStringCellValue();
			break;


		default:

			System.out.println("There are not support for type with id ["+type+"] of cell");

		}

		return Double.parseDouble(result.toString());
	}


	//Convert the user uploaded file into Iterable<CSVRecord> so that it can be easily read now
	private Iterable<CSVRecord> parseFileByType()
			throws FileNotFoundException, IOException {

		// Prepare the file stats
		final File tempFile =
				File.createTempFile("temp" + databean.getFileLabel(), databean.getFileFormat());
		tempFile.deleteOnExit();

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(databean.getFile().getInputStream(), out);
		}

		Iterable<CSVRecord> records = evaluateHeader().parse(new FileReader(tempFile));
		tempFile.delete();


		return records;
	}

	// Add the indicator to CSV format weather to skip header or not
	private CSVFormat evaluateHeader() {
		return evaluateFileExtension(databean.getFileFormat()).withSkipHeaderRecord(
				"Y".equalsIgnoreCase(databean.getHeaderRow()) ? true : false);
	}

	// Based on file extension return CSVFormat
	private CSVFormat evaluateFileExtension(String extension) {
		CSVFormat format = null;
		switch (extension) {
		case "csv":
			format = CSVFormat.DEFAULT;
			break;

		case "xls":
			format = CSVFormat.EXCEL;
			break;

		case "txt":
			format = CSVFormat.TDF;
			break;
		}
		return format;
	}



	// Converting the file to  calculate rowCount
	public List<List<Double>> transformFile()
			throws FileNotFoundException, IOException {
		Iterable<CSVRecord> records = parseFileByType();


		List<List<Double>> Datarow = new ArrayList<>();
		int maxcol=validColumns();
		System.out.println("Max Coulms are"+maxcol);
		int counter=0;
		int badrowCounter=0;
		boolean badrow=false;
		

		for (CSVRecord record : records) {
			Iterator<String> recordString = record.iterator();
			List<Double> row = new ArrayList<>();
			String number=new String();
			
			badrow=false;
			counter=0;
			while (recordString.hasNext()) {
				
				
				try {
					if(counter<maxcol){
						number = recordString.next();
						System.out.println(number);
						row.add(Double.parseDouble(number.toString()));
						
					}
					
				} catch (NumberFormatException e) {
					if(counter<maxcol){
						try{
							if(tempMetaData.getColumnTypeName(counter+1).equalsIgnoreCase("varchar"))
							{
								System.out.println(number);
								row.add(0.00d);
							}
							else{
								badrow=true;
							}
							
							
						
						}
						catch(Exception f){
							
						}
						
						
									
					}
					
				}
				finally{
					counter=counter++;
				}
			}
			
				if(badrow){
					badrowCounter++;
					System.out.println("Bad Row");
				}
				else
				{
					Datarow.add(row);
				}
				
			
			
		}

		databean.setBadRowCount(badrowCounter);
		System.out.println("Bad Rows are"+badrowCounter);
		return Datarow;
	}


	// Converting the file to  calculate rowCount
	public List<List<String>> transformMetaFile()
			throws FileNotFoundException, IOException {

		Iterable<CSVRecord> records = parseFileByType();

		// Each row of data has been represented as a list
		// So all data becomes a List-of-List
		List<List<String>> energyRows = new ArrayList<>();
		boolean baddata=false;
		int badcount=0;
		for (CSVRecord record : records) {
			Iterator<String> recordString = record.iterator();
			List<String> row = new ArrayList<>();

			while (recordString.hasNext()) {

				baddata=false;

				try {
					row.add(recordString.next());
				} catch (NumberFormatException e) {

					baddata=true;
					badcount=badcount+1;

				}
			}

			if(baddata==false) {

				energyRows.add(row);

			}

		}
		databean.setBadRowCount(badcount);




		return energyRows;
	}


	public void sTable(){

		databean.setShowtable(true);
	}

	public void hTable(){

		databean.setShowtable(false);
	}



}
