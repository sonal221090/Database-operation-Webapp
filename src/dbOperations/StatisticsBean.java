package dbOperations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="statisticsBean")
@SessionScoped
public class StatisticsBean {

	private String dataset; 
	private String variable; 
	private int numberObs;  
	private double minValue; 
	private double q1; 
	private double median; 
	private double q3; 
	private double maxValue; 
	private double mean;  
	private double variance; 
	private double standardDeviation;  
	private double iqr;  
	private double range;    
	private boolean renderStats;
	private String error;
	public List<List<String>> dataRows;
	private boolean renderTable=false;
	private boolean regression=false;
	
	
	
	
	

	private boolean dtypeFlag;
	private static final String columnNames [] = {"Dataset","Variable","No. of Obs.","MinValue","Q1","Median","Q3","MaxValue","Mean","Variance","StandardDeviation","Iqr","Range"   };
	
	public boolean isRegression() {
		return regression;
	}
	public void setRegression(boolean regression) {
		this.regression = regression;
	}
	public boolean isRenderTable() {
		return renderTable;
	}
	public void setRenderTable(boolean renderTable) {
		this.renderTable = renderTable;
	}
	public List<List<String>> getDataRows() {
		return dataRows;
	}
	public void setDataRows(List<List<String>> dataRows) {
		
		 if (this.dataRows == null) {
	            this.dataRows = new ArrayList<>();
	        }
		
		this.dataRows = dataRows;
	}
	public boolean isRenderStats() {
		return renderStats;
	}
	public void setRenderStats(boolean renderStats) {
		this.renderStats = renderStats;
	}
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public int getNumberObs() {
		return numberObs;
	}
	public void setNumberObs(int numberObs) {
		this.numberObs = numberObs;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public double getQ1() {
		return q1;
	}
	public void setQ1(double q1) {
		this.q1 = q1;
	}
	public double getMedian() {
		return median;
	}
	public void setMedian(double median) {
		this.median = median;
	}
	public double getQ3() {
		return q3;
	}
	public void setQ3(double q3) {
		this.q3 = q3;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getVariance() {
		return variance;
	}
	public void setVariance(double variance) {
		this.variance = variance;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	public double getIqr() {
		return iqr;
	}
	public void setIqr(double iqr) {
		this.iqr = iqr;
	}
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	public  String[] getColumnNames() {
		return columnNames;
	}
	

	

	public boolean isDtypeFlag() {
		return dtypeFlag;
	}
	public void setDtypeFlag(boolean dtypeFlag) {
		this.dtypeFlag = dtypeFlag;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	

	
}
