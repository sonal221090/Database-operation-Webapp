package dataImport;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;


@ManagedBean(name="databean")
@SessionScoped
public class DataBean {
	/**
	 * 
	 */
	private String fileType;
	private Part file;
	private String fileLabel;
	private String datasetLabel;
	private String fileFormat;
	private String headerRow;
	private String errorMessage;
	private String tableName;
	private boolean showtable;
	private Integer rowCount;
	private Integer badRowCount;
	private Integer columnCount;
	private String fileName;
	private Long fileSize;
	private String fileContentType;
	private String filePath;
	private String status;
	private boolean interim=false;
	private boolean report=false;
	
	private List<List<String>> uploadedmetaData;
	private List<List<Double>> uploadedData;

	/*~~~~~~~~~~~~~~~~~Getters and Setters~~~~~~~~~~~~~~~~~~~*/


	public String getTableName() {
		return tableName;
	}
	public List<List<String>> getUploadedmetaData() {
		
	
		return uploadedmetaData;
	}
	public void setUploadedmetaData(List<List<String>> uploadedmetaData) {
		
		if (this.uploadedmetaData == null) {
            this.uploadedmetaData = new ArrayList<>();
        }
		this.uploadedmetaData = uploadedmetaData;
	}
	public List<List<Double>> getUploadedData() {
		return uploadedData;
	}
	public void setUploadedData(List<List<Double>> rows) {
		
		if (this.uploadedData == null) {
            this.uploadedData = new ArrayList<>();
        }
		this.uploadedData = rows;
	}
	
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public boolean isShowtable() {
		return showtable;
	}
	public void setShowtable(boolean showtable) {
		this.showtable = showtable;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileContentType() {
		return fileContentType;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Part getFile() {
		return file;
	}
	public void setFile(Part file) {
		this.file = file;
	}
	public String getFileLabel() {
		return fileLabel;
	}
	public void setFileLabel(String fileLabel) {
		this.fileLabel = fileLabel;
	}
	public String getDatasetLabel() {
		return datasetLabel;
	}
	public void setDatasetLabel(String datasetLabel) {
		this.datasetLabel = datasetLabel;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getHeaderRow() {
		return headerRow;
	}
	public void setHeaderRow(String headerRow) {
		this.headerRow = headerRow;
	}
	
	public Integer getBadRowCount() {
		return badRowCount;
	}
	public void setBadRowCount(Integer badRowCount) {
		this.badRowCount = badRowCount;
	}
	public boolean isInterim() {
		return interim;
	}
	public void setInterim(boolean interim) {
		this.interim = interim;
	}
	public boolean isReport() {
		return report;
	}
	public void setReport(boolean report) {
		this.report = report;
	}
	
	




}
