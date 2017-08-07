package dbOperations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name="operationBean", eager=true)
@SessionScoped
public class OperationBean implements Serializable {

		
	    private static final long serialVersionUID = 1L;

	    private String tableSelected;
	    private List<String> columnsSelected;
	    private List<String> tableList;
	    private List<String> columnList;
	    private List<String> columnHeader;
	    public List<List<String>> dataRows;
	    private Integer rowCount;
	    private Integer columnCount;
	    private String operationError;
	    private String query;
	    private boolean isTable;
	    private List<String> SourceColumnList;
	    private List<String> DestinationColumnList;
	    private String SourceColumnSelected;
	    private String DestinationColumnSelected;
	    private String ComputationError;
	    private boolean tabFlag=false;
		private boolean colFlag=false;
	    
	    
		public boolean isTabFlag() {
			return tabFlag;
		}
		public void setTabFlag(boolean tabFlag) {
			this.tabFlag = tabFlag;
		}
		public boolean isColFlag() {
			return colFlag;
		}
		public void setColFlag(boolean colFlag) {
			this.colFlag = colFlag;
		}
		public String getComputationError() {
			return ComputationError;
		}
		public void setComputationError(String computationError) {
			ComputationError = computationError;
		}
		public String getSourceColumnSelected() {
			return SourceColumnSelected;
		}
		public void setSourceColumnSelected(String sourceColumnSelected) {
			SourceColumnSelected = sourceColumnSelected;
		}
		public String getDestinationColumnSelected() {
			return DestinationColumnSelected;
		}
		public void setDestinationColumnSelected(String destinationColumnSelected) {
			DestinationColumnSelected = destinationColumnSelected;
		}
		public List<String> getSourceColumnList() {
			return SourceColumnList;
		}
		public void setSourceColumnList(List<String> sourceColumnList) {
			SourceColumnList = sourceColumnList;
		}
		public List<String> getDestinationColumnList() {
			return DestinationColumnList;
		}
		public void setDestinationColumnList(List<String> destinationColumnList) {
			DestinationColumnList = destinationColumnList;
		}
		public boolean isTable() {
			return isTable;
		}
		public void setTable(boolean isTable) {
			this.isTable = isTable;
		}
		public String getTableSelected() {
			return tableSelected;
		}
		public void setTableSelected(String tableSelected) {
			this.tableSelected = tableSelected;
		}
		
		
		public List<String> getColumnsSelected() {
			return columnsSelected;
		}
		public void setColumnsSelected(List<String> columnListSelected) {
			this.columnsSelected = columnListSelected;
		}
		public List<String> getTableList() {
			return tableList;
		}
		public void setTableList(List<String> tableList) {
			this.tableList = tableList;
		}
		public List<String> getColumnList() {
			return columnList;
		}
		public void setColumnList(List<String> columnList) {
			this.columnList = columnList;
		}
		public List<String> getColumnHeader() {
			return columnHeader;
		}
		public void setColumnHeader(List<String> columnHeader) {
			this.columnHeader = columnHeader;
		}
		public List<List<String>> getDataRows() {
			return dataRows;
		}
		public void setDataRows(List<List<String>> dataRows) {
			this.dataRows = dataRows;
		}
		public Integer getRowCount() {
			return rowCount;
		}
		public void setRowCount(Integer rowCount) {
			this.rowCount = rowCount;
		}
		public Integer getColumnCount() {
			return columnCount;
		}
		public void setColumnCount(Integer columnCount) {
			this.columnCount = columnCount;
		}
		public String getOperationError() {
			return operationError;
		}
		public void setOperationError(String operationError) {
			this.operationError = operationError;
		}
		 public String getQuery() {
			return query;
		}
		public void setQuery(String query) {
			this.query = query;
		}
		public void setRow(List<String> row) {
		        if (this.dataRows == null) {
		            this.dataRows = new ArrayList<>();
		        }
		        this.dataRows.add(row);
		    }
		
}
