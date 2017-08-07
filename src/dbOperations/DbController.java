package dbOperations;



import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import database.LoginBean;
import database.loginController;

import java.sql.Connection;


@ManagedBean(name="dbcontroller", eager=true)
@SessionScoped
public class DbController {
	




	loginController logincontroller=new loginController();


	@ManagedProperty(value = "#{loginbean}")
	LoginBean loginbean;




	public List<String> getTableNamesBySchema() throws Exception {

		Connection connection = null;
		ResultSet resultSet = null;


		try {
			
			
			connection = logincontroller.establishConnection(loginbean);
			DatabaseMetaData metaData = connection.getMetaData();


			List<String> list = new ArrayList<>();


			resultSet = metaData.getTables(null, null, "%", null);
			resultSet.beforeFirst();
			if (resultSet != null) {
				while (resultSet.next()) {
					list.add(resultSet.getString("TABLE_NAME"));
				}
			}
			System.out.println(list);
			return list;

		}


		finally {
			if(resultSet!=null){
				resultSet.close();
				connection.close();
			}

		}
	}

	public List<String> getColumnsByTable(String tableName) throws Exception {

		Connection conn = null;
		ResultSet resultSet = null;

		try {

			conn = logincontroller.establishConnection(loginbean);
			DatabaseMetaData metaData = conn.getMetaData();

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
			conn.close();
		}
	}

	/*	public TableData executeSelectQuery(String query) throws Exception {

		TableData tableData = new TableData();
		PreparedStatement statement = null;
		Connection connection = null;
		ResultSet resultSet = null;

		try {

			connection = getConnection(loginForm);
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();

			tableData.setColumnNames(getColumnNames(resultSet));
			while (resultSet.next()) {
				List<String> rowData = new ArrayList<>();
				for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
					rowData.add(String.valueOf(resultSet.getObject(i)));
				}
				tableData.setRow(rowData);
			}
		} finally {
			resultSet.close();
			statement.close();
			connection.close();
		}

		// set the rowCount, columnCount & the query into the model bean to display
		// this in the UI
		messageModel.setStatus(query);
		messageModel.setRowCount(tableData.getDataRows().size());
		messageModel.setColumnCount(tableData.getColumnNames().size());

		return tableData;
	}

	public boolean executeUpdateQuery(LoginForm loginForm, String query) throws Exception {

		Statement statement = null;
		Connection connection = null;

		try {

			connection = getConnection(loginForm);
			statement = connection.createStatement();
			return statement.executeUpdate(query) > -1 ? true : false;
		} finally {
			statement.close();
			connection.close();
		}
	}
	 */


/*	private List<String> getColumnNames(ResultSet resultSet) throws Exception {
		List<String> returnList = new ArrayList<>();

		for (int i = 1, count = resultSet.getMetaData().getColumnCount(); i <= count; i++) {
			returnList.add(resultSet.getMetaData().getColumnName(i));
		}
		return returnList;
	}
*/

	public loginController getLogincontroller() {
		return logincontroller;
	}

	public void setLogincontroller(loginController logincontroller) {
		this.logincontroller = logincontroller;
	}

	public LoginBean getLoginbean() {
		return loginbean;
	}

	public void setLoginbean(LoginBean loginbean) {
		this.loginbean = loginbean;
	}


}
