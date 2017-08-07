package dbOperations;


import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import database.LoginBean;
import database.loginController;
import graphs.GraphBean;

import java.sql.Connection;

@ManagedBean(name="operationcontroller", eager=true)
@SessionScoped
public class OperationController {



	loginController loginController=new loginController();


	@ManagedProperty(value = "#{loginbean}")
	LoginBean loginbean;



	@ManagedProperty(value = "#{operationBean}")
	OperationBean operationBean;


	@ManagedProperty(value = "#{statisticsBean}")
	StatisticsBean statisticsBean;

	@ManagedProperty(value = "#{regressionBean}")
	RegressionBean regressionBean;
	
	@ManagedProperty(value = "#{graphbean}")
	GraphBean graphbean;



	public loginController getLoginController() {
		return loginController;
	}

	public void setLoginController(loginController loginController) {
		this.loginController = loginController;
	}

	public GraphBean getGraphbean() {
		return graphbean;
	}

	public void setGraphbean(GraphBean graphbean) {
		this.graphbean = graphbean;
	}

	public RegressionBean getRegressionBean() {
		return regressionBean;
	}

	public void setRegressionBean(RegressionBean regressionBean) {
		this.regressionBean = regressionBean;
	}
	public StatisticsBean getStatisticsBean() {
		return statisticsBean;
	}

	public void setStatisticsBean(StatisticsBean statisticsBean) {
		this.statisticsBean = statisticsBean;
	}



	public LoginBean getLoginbean() {
		return loginbean;
	}

	public void setLoginbean(LoginBean loginbean) {
		this.loginbean = loginbean;
	}




	public OperationBean getOperationBean() {
		return operationBean;
	}

	public void setOperationBean(OperationBean operationBean) {
		this.operationBean = operationBean;
	}

	public List<String> getTableNamesBySchema() throws Exception {

		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = loginController.establishConnection(loginbean);
			DatabaseMetaData metaData = connection.getMetaData();
			List<String> list = new ArrayList<>();
			resultSet = metaData.getTables(null, null, "%", null);
			//resultSet.beforeFirst();
			if (resultSet != null) {
				while (resultSet.next()) {
					list.add(resultSet.getString("TABLE_NAME"));
				}
			}
			System.out.println("********************" +list+ "************************");
			return list;
		}
		finally {
			if(resultSet!=null){
				resultSet.close();
				connection.close();
			}

		}

	}


	public List<String> getColumnsByTable(LoginBean loginbean, String tableName) throws Exception {

		Connection connection = null;
		ResultSet resultSet = null;

		try {

			connection = loginController.establishConnection(loginbean);
			DatabaseMetaData metaData = connection.getMetaData();
			List<String> list = new ArrayList<>();

			resultSet = metaData.getColumns(null, null, tableName, null);
			resultSet.beforeFirst();
			if (resultSet != null) {
				while (resultSet.next()) {
					list.add(resultSet.getString("COLUMN_NAME"));
				}
			}
			return list;

		} finally {
			resultSet.close();
			connection.close();
		}
	}

	public List<String> getInputColumnsByTable(LoginBean loginbean, String tableName,String type) throws Exception {

		Connection connection = null;
		ResultSet resultSet = null;

		try {

			connection = loginController.establishConnection(loginbean);
			DatabaseMetaData metaData = connection.getMetaData();
			List<String> list = new ArrayList<>();

			resultSet = metaData.getColumns(null, null, tableName, null);
			resultSet.beforeFirst();
			if (resultSet != null) {
				while (resultSet.next()) {

					if(type.equalsIgnoreCase("computed") && resultSet.getString("COLUMN_NAME").contains("_COMPUTED"))
					{
						list.add(resultSet.getString("COLUMN_NAME"));
					}
					else if(type.equalsIgnoreCase("input") && !resultSet.getString("COLUMN_NAME").contains("_COMPUTED"))
					{
						list.add(resultSet.getString("COLUMN_NAME"));
					}


				}
			}
			return list;

		} finally {
			resultSet.close();
			connection.close();
		}
	}




	public List<List<String>> executeSelectQuery(LoginBean loginbean) throws Exception {

		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		String query="";
		String columns="";

		operationBean.setDataRows(null);
		operationBean.setColumnHeader(null);

		try {

			connection = loginController.establishConnection(loginbean);
			System.out.println("In select query method 1");


			if(operationBean.getColumnsSelected().size()!=0 && !operationBean.isTable())
			{
				for(int i=0;i<operationBean.getColumnsSelected().size();i++)
				{

					operationBean.getColumnsSelected().get(i);
					columns=columns+operationBean.getColumnsSelected().get(i);

					if (i!=operationBean.getColumnsSelected().size()-1)
					{
						columns=columns+",";
					}

				}

				query="Select "+columns+" from "+operationBean.getTableSelected()+";";
				System.out.println(query);
			}

			else
			{
				query="Select * from "+operationBean.getTableSelected()+";";	
			}

			System.out.println(query);

			operationBean.setQuery(query);
			statement = connection.prepareStatement(query);
			System.out.println("In select query method 2" + query );
			resultSet = statement.executeQuery();

			/************************ */
			ResultSetMetaData  rsmd = resultSet.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String datatype = rsmd.getColumnTypeName(i);


				System.out.println("Data type of selected column is :" +datatype);


				if(datatype.equalsIgnoreCase("INT") || datatype.equalsIgnoreCase("BIGINT")|| datatype.equalsIgnoreCase("REAL")|| datatype.equalsIgnoreCase("FLOAT")|| datatype.equalsIgnoreCase("DOUBLE"))
				{
					System.out.println("Valid Data Type");
					statisticsBean.setDtypeFlag(true);
				}
				else
				{
					System.out.println("InValid Data Type");
					statisticsBean.setDtypeFlag(false);

				}
			}



			System.out.println("In select query method 3");
			System.out.println("1. Column Header: " + operationBean.getColumnHeader());
			operationBean.setColumnHeader(getColumnName(resultSet));
			System.out.println("After setting Column Header: " + operationBean.getColumnHeader());

			while (resultSet.next()) {
				List<String> rowData = new ArrayList<>();
				for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
					rowData.add(String.valueOf(resultSet.getObject(i)));
				}
				operationBean.setRow(rowData);
			}
		} finally {
			resultSet.close();
			statement.close();
			connection.close();
		}

		operationBean.setOperationError(query);
		operationBean.setRowCount(operationBean.getDataRows().size());
		operationBean.setColumnCount(operationBean.getColumnHeader().size());

		return operationBean.dataRows;
	}

	public void executeUpdateQuery(LoginBean loginbean, String query) throws Exception {

		Statement statement = null;
		Connection connection = null;
		boolean updateStatus = false;

		operationBean.setDataRows(null);
		operationBean.setColumnHeader(null);

		System.out.println(operationBean.getQuery());

		if (!(query.toUpperCase().startsWith("UPDATE") || query.toUpperCase().startsWith("INSERT")
				|| query.toUpperCase().startsWith("CREATE")
				|| query.toUpperCase().startsWith("DELETE"))) {

			operationBean.setOperationError("Query is not a valid Update Query. Please enter a correct one.");
			return;
		}

		try {

			connection = loginController.establishConnection(loginbean);
			statement = connection.createStatement();
			System.out.println("Inside correct query block: " + operationBean.getQuery());
			updateStatus= statement.executeUpdate(query) > -1 ? true : false;
			System.out.println(updateStatus);

			if (updateStatus) {
				operationBean.setOperationError("Table successfully updated!");
			} else {
				operationBean.setOperationError("Unable to Update the table !");
			}

		} finally {
			statement.close();
			connection.close();
		}
	}
	public void executeDropQuery(LoginBean loginbean)  {

		Statement statement = null;
		Connection connection = null;
		String query="";
		boolean dropStatus = false;
		operationBean.setColumnHeader(null);
		operationBean.setDataRows(null);
		operationBean.setColumnCount(null);
		operationBean.setRowCount(null);


		try {
			connection = loginController.establishConnection(loginbean);
			query="Drop table " + operationBean.getTableSelected() + ";" ;
			System.out.println(query);

			operationBean.setQuery(query);
			statement = connection.createStatement();
			System.out.println("In drop query method 2");
			dropStatus = statement.executeUpdate(query) > -1 ? true : false;
			System.out.println(dropStatus);


			operationBean.setOperationError("Table Successfully dropped !");




			System.out.println("In drop query method 3");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			operationBean.setOperationError("Unable to drop the selected table! \n"+e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			operationBean.setOperationError("Unable to drop the selected table! \n"+e);
		}

		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void getTables(){

		System.out.println("********Hi Tables***************");
		try{
			//tablelist = getTableNamesBySchema();
			this.operationBean.setTableList(getTableNamesBySchema());
			operationBean.setTabFlag(true);
			operationBean.setColFlag(false);
		}catch(Exception e) {
			operationBean.setOperationError("Error! Database tables could not be retrieved. PLease try again!");
		}	
	}

	public void getColumns(){

		System.out.println("********Hi Columns***************");

		if(operationBean.getTableSelected()!="" && operationBean.getTableSelected()!=null)
		{
			try{

				this.operationBean.setColumnList(getColumnsByTable(loginbean,operationBean.getTableSelected()));
				operationBean.setColFlag(true);
			}catch(Exception e) {
				operationBean.setOperationError("Columns of selected table could not be retrieved. Following exception occured: "+e);
			}


		}
		else 
			operationBean.setOperationError("Please select a valid table in order to display the column(s)");

	}

	public void getInputColumns(){

		operationBean.setComputationError("");

		if(operationBean.getTableSelected()!="" && operationBean.getTableSelected()!=null)
		{
			try{

				this.operationBean.setSourceColumnList(getInputColumnsByTable(loginbean,operationBean.getTableSelected(),"input"));
				this.operationBean.setDestinationColumnList(getInputColumnsByTable(loginbean,operationBean.getTableSelected(),"computed"));
				if(this.operationBean.getDestinationColumnList().isEmpty()){
					operationBean.setComputationError("No Computable columns in the selected table");
				}


			}catch(Exception e) {

				operationBean.setComputationError("Columns of selected table could not be retrieved. Following exception occured: "+e);
			}


		}
		else 
			operationBean.setComputationError("Please select a valid table in order to display the column(s)");

	}





	/*****************Regression**********/
	public void getVariableList(){

		if(operationBean.getTableSelected()!="" && operationBean.getTableSelected()!=null)
		{
			try{
				this.regressionBean.setVariableList(getColumnsByTable(loginbean,operationBean.getTableSelected()));
				statisticsBean.setRegression(true);
			}catch(Exception e) {
				regressionBean.setRegressError("Columns of selected table could not be retrieved. Following exception occured: "+e );
			}
		}
		else 
			regressionBean.setRegressError("Please select a valid table in order to display the column(s)");
	}




	/************Regression *************/


	private List<String> getColumnName(ResultSet resultSet) throws Exception {
		List<String> columnList = new ArrayList<>();

		for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) 
			columnList.add(resultSet.getMetaData().getColumnName(i));

		return columnList;
	}

	public void getTableContents(){

		operationBean.setTable(true);  //check used to identify its a select * from table query
		System.out.println("*******In table content method***************");
		try{

			this.operationBean.setDataRows(executeSelectQuery(loginbean));

		}catch(NullPointerException e)
		{
			operationBean.setOperationError("The Table is empty");
			operationBean.setColumnCount(null);
			operationBean.setRowCount(null);
		}


		catch(Exception e) {
			operationBean.setOperationError("Error! Table could not be displayed. !"+ e);
		}
		finally{
			operationBean.setTable(false);
		}
	}

	public void getColumnContents(){

		System.out.println("*******In column content method***************");
		try{
			this.operationBean.setDataRows(executeSelectQuery(loginbean));
		}catch(Exception e) {
			operationBean.setOperationError("Error! Selected column(s) could not be displayed.!");
		}	

	}

	public void getQueryExecutionMethod(){

		//operationBean.setQuery(null);



		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		String query=operationBean.getQuery();
		/*String columns="";*/
		operationBean.setColumnCount(null);
		operationBean.setRowCount(null);
		operationBean.setDataRows(null);
		operationBean.setColumnHeader(null);
		operationBean.setOperationError(null);



		try {
			connection = loginController.establishConnection(loginbean);


			System.out.println(query);

			operationBean.setQuery(query);
			statement = connection.prepareStatement(query);
			System.out.println("In select query method 2");


			if(query.toUpperCase().contains("INSERT") || query.toUpperCase().contains("UPDATE") ||query.toUpperCase().contains("DROP") )
			{

				statement.executeUpdate();
				operationBean.setOperationError("Update query successfully executed");

			}
			else
			{
				resultSet = statement.executeQuery();




				if(resultSet!=null){
					operationBean.setColumnHeader(getColumnName(resultSet));
				}


				while (resultSet.next()) {
					List<String> rowData = new ArrayList<>();
					for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
						rowData.add(String.valueOf(resultSet.getObject(i)));
					}
					operationBean.setRow(rowData);
				}


			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			operationBean.setOperationError("Please enter a valid Query. The database triggered the following error \n"+e);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			operationBean.setOperationError(e.getClass().getSimpleName());


		}

		finally {

			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
		if(operationBean.getDataRows()!=null && operationBean.getColumnHeader()!=null)
		{
			operationBean.setRowCount(operationBean.getDataRows().size());
			operationBean.setColumnCount(operationBean.getColumnHeader().size());


			try{
				this.operationBean.setDataRows(operationBean.dataRows);
			}catch(Exception e) {
				operationBean.setOperationError("Error! Table could not be displayed. Please try again!");
				System.out.println(e.getClass().getSimpleName());

				operationBean.setOperationError(e.getClass().getSimpleName());
			}
			finally{
			}


		}}


	public void getDropQuery()
	{
		try{
			System.out.println("Redirect to Drop table method method");
			executeDropQuery(loginbean);
		}catch(Exception e) {
			System.out.println(e.getClass().getSimpleName());
			operationBean.setOperationError("Invalid query. The database triggered the following error \n"+e);
		}        
	}



	public void clearContent()
	{
		operationBean.setColumnCount(null);
		operationBean.setColumnHeader(null);
		operationBean.setColumnList(null);
		operationBean.setColumnsSelected(null);
		operationBean.setDataRows(null);
		operationBean.setOperationError(null);
		operationBean.setQuery(null);
		operationBean.setRow(null);
		operationBean.setRowCount(null);
		operationBean.setTableList(null);
		operationBean.setTableSelected(null);

		statisticsBean.setDataRows(null);
		statisticsBean.setError("");
		statisticsBean.setRenderTable(false);
		regressionBean.setVariableList(null);
		regressionBean.setRegressError(null);
		statisticsBean.setRegression(false);

		regressionBean.setEquation(null);
		regressionBean.setRenderTable(false);
		regressionBean.setPvSelected(null);
		regressionBean.setTvSelected(null);


		regressionBean.setEquation(null);
		regressionBean.setIntercept(0.0d);
		regressionBean.setInterceptStandardError(0.0d);
		regressionBean.settStatistic(0.0d);
		regressionBean.setInterceptPValue(0.0d);
		regressionBean.setSlope(0.0d);
		regressionBean.setSlopeStandardError(0.0d);
		regressionBean.settStatisticPredictor(0.0d);
		regressionBean.setpValuePredictor(0.0d);
		regressionBean.setStandardErrorModel(0.0d);
		regressionBean.setrSquare(0.0d);
		regressionBean.setrSquareAdjusted(0.0d);
		regressionBean.setPredictorDF(0.0d);
		regressionBean.setRegressionSumSquares(0.0d);
		regressionBean.setMeanSquare(0.0d);
		regressionBean.setfValue(0.0d);
		regressionBean.setpValue(0.0d);
		regressionBean.setResidualErrorDF(0.0d);
		regressionBean.setSumSquaredErrors(0.0d);
		regressionBean.setMeanSquareError(0.0d);
		regressionBean.setTotalDF(0.0d);

		regressionBean.setDataRow(null);
		regressionBean.setPvData(null);
		regressionBean.setTvData(null);
		regressionBean.setPvSelected(null);
		regressionBean.setTvSelected(null);
		regressionBean.setRenderTable(false);
		regressionBean.setDataType(false);
		regressionBean.setVariableList(null);
		regressionBean.setDataRow(null);
		
		graphbean.setGraphPathName(null);
	}



	public String generateDescriptiveStatistics() {
		//renderStats = false;   
		//                String dataRow [] = null;   

		statisticsBean.setRenderTable(true);
		statisticsBean.setError("");


		try{
			//operationBean.setTable(true);
			operationBean.setDataRows(executeSelectQuery(loginbean));


		}
		catch(NullPointerException e)
		{
			statisticsBean.setError("The Table is empty ! Please select a table.");
		}
		catch(Exception e) {
			statisticsBean.setError("Error! Selected column(s) could not be displayed.!"+e);
		}        

		List<String> headerList=new ArrayList<>();
		List<List<String>> dataList=new ArrayList<>();

		boolean errorflag=false;


		try {
			headerList = operationBean.getColumnHeader();
			dataList = operationBean.getDataRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			statisticsBean.setError("The table is empty.");
			errorflag=true;
		} 


		if(headerList==null || errorflag)
		{
			statisticsBean.setError("The table is empty.");
			return "Statistics";
		}
		else
		{

			System.out.println(dataList.size());
			int nObservation = dataList.size();
			//System.out.println(nObservation);
			double dataValues [] = null;  
			String data = null;      
			List<String> temp;
			List<List<String>> localrows=new ArrayList<>();

			/*if(headerList!=null)
				{*/

			if(statisticsBean.isDtypeFlag())
			{
				for(int i=0; i<headerList.size(); i++) {  
					//statisticsBean = new  StatisticsBean(); 
					//statisticsBean.setDataset(statisticsBeanFile.getDatasetLabel()); 

					temp =  new ArrayList<String>();

					//if(operationBean.getColumnsSelected().get(i).g

					dataValues = new double [nObservation];        
					for(int r = 0; r < nObservation; r++) {
						data = dataList.get(r).get(i);
						//data = dataList.get(r)[i];     
						dataValues[r] = Double.parseDouble(data);
					}   

					statisticsBean.setDataset(operationBean.getTableSelected());
					statisticsBean.setVariable(headerList.get(i)); 
					statisticsBean.setNumberObs(nObservation);
					statisticsBean.setMinValue(StatUtils.min(dataValues));
					statisticsBean.setQ1(StatUtils.percentile(dataValues, 25.0));
					statisticsBean.setMedian(StatUtils.percentile(dataValues, 50.0));
					statisticsBean.setQ3(StatUtils.percentile(dataValues, 75.0));
					statisticsBean.setMaxValue(StatUtils.max(dataValues));
					statisticsBean.setMean(StatUtils.mean(dataValues));   
					statisticsBean.setVariance(StatUtils.variance(dataValues));   
					statisticsBean.setStandardDeviation(Math.sqrt(statisticsBean.getVariance()));        
					statisticsBean.setIqr(statisticsBean.getQ3()-statisticsBean.getQ1());  
					statisticsBean.setRange(statisticsBean.getMaxValue() - statisticsBean.getMinValue());

					temp.add(statisticsBean.getDataset());
					temp.add(statisticsBean.getVariable());
					temp.add(""+statisticsBean.getNumberObs());
					temp.add(""+statisticsBean.getMinValue());
					temp.add(""+statisticsBean.getQ1());
					temp.add(""+statisticsBean.getMedian());
					temp.add(""+statisticsBean.getQ3());
					temp.add(""+statisticsBean.getMaxValue());
					temp.add(""+statisticsBean.getMean());
					temp.add(""+statisticsBean.getVariance());
					temp.add(""+statisticsBean.getStandardDeviation());
					temp.add(""+statisticsBean.getIqr());
					temp.add(""+statisticsBean.getRange());

					localrows.add(temp);

					System.out.println(temp);

				}

				System.out.println("Setting render table to true");
				statisticsBean.setDataRows(localrows);
				statisticsBean.setRenderTable(true);
				return "Statistics";  

			}
			else
			{

				System.out.println("categorical not allowed");
				statisticsBean.setError("Categorical variables are not allowed !");
				statisticsBean.setRenderTable(false);

				return "Statistics";
			}

		}
	} 




	public void getcomputeReturns(){

		System.out.println("The columns that are selected are "+ operationBean.getColumnsSelected().get(0));


		getColumnContents();
		if(statisticsBean.isDtypeFlag()){
			if(operationBean.getRowCount()>0){


				System.out.println(operationBean.getDataRows().get(0));

				//				computedValues=computeLograthims(operationBean.getDataRows().get(0));
				//					



				Connection connection = null;
				ResultSet rs = null;
				String query="";
				PreparedStatement prepStmt=null ;

				operationBean.setDataRows(null);
				operationBean.setColumnHeader(null);


				if(operationBean.getColumnsSelected().size()!=0 && !operationBean.isTable())
				{




					try {
						connection = loginController.establishConnection(loginbean);			
						query="Select * from "+ operationBean.getTableSelected()+";";	




						operationBean.setQuery(query);

						prepStmt = connection.prepareStatement(query);


						System.out.println("In select query method 2");
						System.out.println(query);
						rs = prepStmt.executeQuery();


						//				prepStmt.executeUpdate();



						int i=0;
						double newAge=0;
						double previous=0;

						while(rs.next()){
							//Retrieve by column name

							if(i==0){

								newAge=0.00d;
								previous=Math.log((Double.parseDouble(operationBean.getDataRows().get(0).get(i))));
								System.out.println(previous);

							}

							else
							{



								newAge = Math.log(Double.parseDouble(operationBean.getDataRows().get(0).get(i)) - previous);

								System.out.println(newAge);
								previous=Math.log(Double.parseDouble(operationBean.getDataRows().get(0).get(i)));
							}

							rs.updateDouble(operationBean.getDestinationColumnSelected(), newAge );
							rs.updateRow();
							i++;
						}
						operationBean.setComputationError("The values have been successfully added");
						System.out.println("I have created history");
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
				else
				{
					operationBean.setComputationError("The Source Table is Empty");
				}
			}
			else{
				operationBean.setComputationError("The source Datatype is non-numeric");
			}


		}

	}


	/****************Prashansa :::: Regression***************/

	public void regressionComputation(double[] x, double[] y){

		SimpleRegression regression= new SimpleRegression();
		for(int i=0;i<x.length;i++){

			System.out.println("Printing values of TV: "+x[i]);
			regression.addData(x[i], y[i]);
		}

		int totalDF = x.length-1;


		TDistribution tDistribution = new TDistribution(totalDF);
		double intercept = regression.getIntercept();
		double interceptStandardError = regression.getInterceptStdErr();
		double tStatistic = 0;
		int predictorDF = 1;
		int residualErrorDF = totalDF - predictorDF;
		double rSquare = regression.getRSquare();
		double rSquareAdjusted = rSquare - (1 - rSquare)/(totalDF - predictorDF - 1);
		if(interceptStandardError!=0){
			tStatistic = (double)intercept/interceptStandardError;
		}
		double interceptPValue =
				(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatistic));
		double slope = regression.getSlope();
		double slopeStandardError = regression.getSlopeStdErr();
		double tStatisticPredictor = 0;
		if(slopeStandardError != 0) {
			tStatisticPredictor = (double)slope/slopeStandardError;
		}
		double pValuePredictor =
				(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatisticPredictor));
		double standardErrorModel = Math.sqrt(StatUtils.variance(y))*(Math.sqrt(1-rSquareAdjusted));
		double regressionSumSquares = regression.getRegressionSumSquares();
		double sumSquaredErrors = regression.getSumSquaredErrors();
		double totalSumSquares = regression.getTotalSumSquares();
		double meanSquare = 0;
		if(predictorDF!=0) {
			meanSquare = regressionSumSquares/predictorDF;
		}
		double meanSquareError = 0;
		if(residualErrorDF != 0) {
			meanSquareError = (double)(sumSquaredErrors/residualErrorDF);
		}
		double fValue = 0;
		if(meanSquareError != 0) {
			fValue = meanSquare/meanSquareError;
		}

		double pValue = (1 - new FDistribution(predictorDF, residualErrorDF).cumulativeProbability(fValue));

		String s1=regressionBean.getPvSelected();
		String s2=regressionBean.getTvSelected();

		String regressionEquation = "" +s2+ "=" + intercept + "+ (" + slope + ") " +s1+ "";


		regressionBean.setEquation(regressionEquation);
		regressionBean.setIntercept(intercept);
		regressionBean.setInterceptStandardError(interceptStandardError);
		regressionBean.settStatistic(tStatistic);
		regressionBean.setInterceptPValue(interceptPValue);
		regressionBean.setSlope(slope);
		regressionBean.setSlopeStandardError(slopeStandardError);
		regressionBean.settStatisticPredictor(tStatisticPredictor);
		regressionBean.setpValuePredictor(pValuePredictor);
		regressionBean.setStandardErrorModel(standardErrorModel);
		regressionBean.setrSquare(rSquare);
		regressionBean.setrSquareAdjusted(rSquareAdjusted);
		regressionBean.setPredictorDF(predictorDF);
		regressionBean.setRegressionSumSquares(regressionSumSquares);
		regressionBean.setMeanSquare(meanSquare);
		regressionBean.setfValue(fValue);
		regressionBean.setpValue(pValue);
		regressionBean.setResidualErrorDF(residualErrorDF);
		regressionBean.setSumSquaredErrors(sumSquaredErrors);
		regressionBean.setMeanSquareError(meanSquareError);
		regressionBean.setTotalDF(totalDF);

	}







	public double[] getVariableColValues(LoginBean loginbean, String selectedVariable) {

		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;
		String query="";		
		List<Double> dataList = new ArrayList<>();

		operationBean.setDataRows(null);
		operationBean.setColumnHeader(null);

		System.out.println(regressionBean.getPvSelected()+" -> Predictor varaiable selected");

		String datatype="";
		try {
			connection = loginController.establishConnection(loginbean);
			query="Select "+selectedVariable+" from "+operationBean.getTableSelected()+";";

			System.out.println(query);
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			ResultSetMetaData  rsmd = resultSet.getMetaData();
			datatype = rsmd.getColumnTypeName(1);
		} 
		catch(NullPointerException e)
		{
			regressionBean.setRegressError("No columns selected as predictor and/or target variable. "+e);
		}
		catch (SQLException e) {

			regressionBean.setRegressError("Error!"+e);

			e.printStackTrace();
		} catch (Exception e) {

			regressionBean.setRegressError("Error!"+e);
		}



		if(datatype.equalsIgnoreCase("INT") || datatype.equalsIgnoreCase("BIGINT")|| datatype.equalsIgnoreCase("REAL")|| datatype.equalsIgnoreCase("FLOAT")|| datatype.equalsIgnoreCase("DOUBLE"))
		{
			System.out.println("Valid Data Type");
			regressionBean.setDataType(true);
		}
		else
		{
			System.out.println("InValid Data Type");
			regressionBean.setDataType(false);
		}

		double data;
		try {
			resultSet.beforeFirst();
			while (resultSet.next()) {
				data = Double.parseDouble(resultSet.getString(selectedVariable));
				System.out.println(data);
				dataList.add(data);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		System.out.println("Printing the dataList");

		/*System.out.println(dataList);*/
		System.out.println(dataList.size());

		double[] dummy=new double[dataList.size()];
		System.out.println("size of list: "+dummy.length);
		int i=0;

		for  (Double d : dataList) {
			dummy[i]=d;
			i++;
		}
		for (double d : dummy) {
			System.out.println(d);
		}
		return dummy;
	}

	public void regressionAnalysis()
	{
		/*String predictor = regressionBean.getPvSelected();
		String target = regressionBean.getTvSelected();*/

		int l1= 0;
		int l2 =0;


		statisticsBean.setDataRows(null);
		statisticsBean.setError(null);

		try{
			System.out.println(regressionBean.getPvSelected());
			System.out.println(regressionBean.getTvSelected());
			
			regressionBean.setPvData(getVariableColValues(loginbean, regressionBean.getPvSelected()));
			regressionBean.setTvData(getVariableColValues(loginbean, regressionBean.getTvSelected()));
		}
		catch(NullPointerException e)
		{
			regressionBean.setRegressError("No columns selected as predictor and/or target variable. "+e);
		}
		catch(Exception e) {
			regressionBean.setRegressError("Error!"+e);
		}  

		double[] predValues = null;
		double[] targValues = null;

		try {
			l1= regressionBean.getPvData().length;
			l2 = regressionBean.getTvData().length;

			predValues = new double[l1];
			targValues = new double[l2];
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(regressionBean.getDataType()){

			System.out.println("PRINT THE PV DATA");


			try {
				for(int i=0;i<l1; i++)
				{
					predValues[i] = regressionBean.getPvData()[i];
					System.out.println(predValues[i]);
				}

				for(int i=0;i<l2; i++)
				{
					targValues[i] = regressionBean.getTvData()[i];
					System.out.println(targValues[i]);
				}


				regressionComputation(predValues, targValues);
				//regressionBean.setEquation(regressionBean.toString());
				regressionBean.setRenderTable(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				regressionBean.setRenderTable(false);
				regressionBean.setPvData(null);
				regressionBean.setTvData(null);
				regressionBean.setRegressError("The table is empty.");
			}					



		}

		else
		{
			regressionBean.setRenderTable(false);
			regressionBean.setPvData(null);
			regressionBean.setTvData(null);
			regressionBean.setRegressError("Categorical variables are not allowed ! Please select a numeric variable.");
		}
	}



}