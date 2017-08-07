package dbOperations;


import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="regressionBean")
@SessionScoped
public class RegressionBean {

	private double[] pvData;
	private double[] tvData;
	private String pvSelected;
	private String tvSelected;
	private boolean dataType;
	private List<String> variableList;
	private List<String> dataRow;
	private double intercept;
	private double interceptStandardError;
	private double tStatistic;
	private double interceptPValue;
	private double slope;
	private double slopeStandardError;
	private double tStatisticPredictor;
	private double pValuePredictor;
	private double standardErrorModel;
	private double rSquare;
	private double rSquareAdjusted;
	private double predictorDF;
	private double regressionSumSquares;
	private double meanSquare;
	private double fValue;
	private double pValue;
	private double residualErrorDF;
	private double sumSquaredErrors;
	private double meanSquareError;
	private double totalDF;
	
	
	
	private boolean renderTable;
	private String equation;	
	private String regressError;

	
	
	
	//private static final String RcolumnNames [] = {"Dataset","Predictor Var","Target Var","No. of Obs.","Slope","Intercept","Standard Error","Correlation Coefficient"};
	
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		this.equation = equation;
	}
	public List<String> getDataRow() {
		return dataRow;
	}
	public void setDataRow(List<String> dataRow) {
		this.dataRow = dataRow;
	}
	public List<String> getVariableList() {
		return variableList;
	}
	public void setVariableList(List<String> variableList) {
		this.variableList = variableList;
	}
	public boolean isRenderTable() {
		return renderTable;
	}
	public void setRenderTable(boolean renderTable) {
		this.renderTable = renderTable;
	}
	public String getRegressError() {
		return regressError;
	}
	public void setRegressError(String regressError) {
		this.regressError = regressError;
	}
	public double getIntercept() {
		return intercept;
	}
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public boolean getDataType() {
		return dataType;
	}
	public void setDataType(boolean dataType) {
		this.dataType = dataType;
	}
	public void setPvSelected(String pvSelected) {
		this.pvSelected = pvSelected;
	}
	public void setTvSelected(String tvSelected) {
		this.tvSelected = tvSelected;
	}
	public double[] getPvData() {
		return pvData;
	}
	public void setPvData(double[] pvData) {
		this.pvData = pvData;
	}
	public double[] getTvData() {
		return tvData;
	}
	public void setTvData(double[] tvData) {
		this.tvData = tvData;
	}
	public String getPvSelected() {
		return pvSelected;
	}
	public String getTvSelected() {
		return tvSelected;
	}    
	public double getInterceptStandardError() {
		return interceptStandardError;
	}
	public void setInterceptStandardError(double interceptStandardError) {
		this.interceptStandardError = interceptStandardError;
	}
	public double gettStatistic() {
		return tStatistic;
	}
	public void settStatistic(double tStatistic) {
		this.tStatistic = tStatistic;
	}
	public double getInterceptPValue() {
		return interceptPValue;
	}
	public void setInterceptPValue(double interceptPValue) {
		this.interceptPValue = interceptPValue;
	}
	public double getSlopeStandardError() {
		return slopeStandardError;
	}
	public void setSlopeStandardError(double slopeStandardError) {
		this.slopeStandardError = slopeStandardError;
	}
	public double gettStatisticPredictor() {
		return tStatisticPredictor;
	}
	public void settStatisticPredictor(double tStatisticPredictor) {
		this.tStatisticPredictor = tStatisticPredictor;
	}
	public double getpValuePredictor() {
		return pValuePredictor;
	}
	public void setpValuePredictor(double pValuePredictor) {
		this.pValuePredictor = pValuePredictor;
	}
	public double getStandardErrorModel() {
		return standardErrorModel;
	}
	public void setStandardErrorModel(double standardErrorModel) {
		this.standardErrorModel = standardErrorModel;
	}
	public double getrSquare() {
		return rSquare;
	}
	public void setrSquare(double rSquare) {
		this.rSquare = rSquare;
	}
	public double getrSquareAdjusted() {
		return rSquareAdjusted;
	}
	public void setrSquareAdjusted(double rSquareAdjusted) {
		this.rSquareAdjusted = rSquareAdjusted;
	}
	public double getPredictorDF() {
		return predictorDF;
	}
	public void setPredictorDF(double predictorDF) {
		this.predictorDF = predictorDF;
	}
	public double getRegressionSumSquares() {
		return regressionSumSquares;
	}
	public void setRegressionSumSquares(double regressionSumSquares) {
		this.regressionSumSquares = regressionSumSquares;
	}
	public double getMeanSquare() {
		return meanSquare;
	}
	public void setMeanSquare(double meanSquare) {
		this.meanSquare = meanSquare;
	}
	public double getfValue() {
		return fValue;
	}
	public void setfValue(double fValue) {
		this.fValue = fValue;
	}
	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	public double getResidualErrorDF() {
		return residualErrorDF;
	}
	public void setResidualErrorDF(double residualErrorDF) {
		this.residualErrorDF = residualErrorDF;
	}
	public double getSumSquaredErrors() {
		return sumSquaredErrors;
	}
	public void setSumSquaredErrors(double sumSquaredErrors) {
		this.sumSquaredErrors = sumSquaredErrors;
	}
	public double getMeanSquareError() {
		return meanSquareError;
	}
	public void setMeanSquareError(double meanSquareError) {
		this.meanSquareError = meanSquareError;
	}
	public double getTotalDF() {
		return totalDF;
	}
	public void setTotalDF(double totalDF) {
		this.totalDF = totalDF;
	}

	

}
