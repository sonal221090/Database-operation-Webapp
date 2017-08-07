package graphs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import dbOperations.*;

@ManagedBean(name="graphcontroller", eager=true)
@SessionScoped
public class graphController {


	@ManagedProperty(value = "#{graphbean}")
	GraphBean graphbean;

	@ManagedProperty(value = "#{regressionBean}")
	RegressionBean regressionBean;

	@ManagedProperty(value = "#{operationBean}")
	OperationBean operationBean;
	
	@ManagedProperty(value = "#{operationcontroller}")
	OperationController operationcontroller;
	
	
	
	

	public RegressionBean getRegressionBean() {
		return regressionBean;
	}


	public void setRegressionBean(RegressionBean regressionBean) {
		this.regressionBean = regressionBean;
	}


	public GraphBean getGraphbean() {
		return graphbean;
	}


	public void setGraphbean(GraphBean graphbean) {
		this.graphbean = graphbean;
	}


	public RegressionBean getRegressionbean() {
		return regressionBean;
	}


	public void setRegressionbean(RegressionBean regressionBean) {
		this.regressionBean = regressionBean;
	}


	public OperationBean getOperationBean() {
		return operationBean;
	}


	public void setOperationBean(OperationBean operationBean) {
		this.operationBean = operationBean;
	}


	public OperationController getOperationcontroller() {
		return operationcontroller;
	}


	public void setOperationcontroller(OperationController operationcontroller) {
		this.operationcontroller = operationcontroller;
	}


	/*************** Pie Chart*************/
	public JFreeChart createPieChart(double[] dataValues) {
		
		DefaultPieDataset data = new DefaultPieDataset();

		int countA = 0; 
		int countB = 0;
		int countC = 0;
		int countD = 0;
		
		double median = StatUtils.percentile(dataValues, 50.0);
		double q1 = StatUtils.percentile(dataValues, 25.0);
		double q3 = StatUtils.percentile(dataValues, 75.0);


		for (int i = 0; i < dataValues.length; i++) {

			if (dataValues[i] <= q1) {
				countA++;
			} else if (q1 < dataValues[i] && dataValues[i] <= median) {
				countB++;
			} else if (median < dataValues[i] && dataValues[i] <= q3) {
				countC++;
			} else {
				countD++;
			}
		}

		data.setValue("Less than Q1", countA);
		data.setValue("Between Q1 and Q2", countB);
		data.setValue("Between Q2 and Q3", countC);
		data.setValue("Greater than Q3", countD);

		JFreeChart chart = ChartFactory.createPieChart("Pie Chart", data, true, true, false);
		return chart;

	}


	/********************** Bar Chart****************/
	public static JFreeChart createBarChart(double[] dataValues) {
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		int countA = 0;
		int countB = 0;
		int countC = 0;
		int countD = 0;
		
		double median = StatUtils.percentile(dataValues, 50.0);
		double q1 = StatUtils.percentile(dataValues, 25.0);
		double q3 = StatUtils.percentile(dataValues, 75.0);		

		for (int i = 0; i < dataValues.length; i++) {

			if (dataValues[i] <= q1) {
				countA++;
			} else if (q1 < dataValues[i] && dataValues[i] <= median) {
				countB++;
			} else if (median < dataValues[i] && dataValues[i] <= q3) {
				countC++;
			} else {
				countD++;
			}
		}

		data.addValue(countA, "Less than Q1", "C1");
		data.addValue(countB, "Between Q1 and Q2", "C2");
		data.addValue(countC, "Between Q2 and Q3", "C3");
		data.addValue(countD, "Greater than Q3", "C4");

		JFreeChart chart = ChartFactory.createBarChart3D("Bar Chart", "Category", "Value", data, PlotOrientation.VERTICAL,
				true, true, false);

		return chart;
	}

	/******************** Scatter Plot************/
	public static JFreeChart createScatterPlotChart(String predColumnName, String targColumnName, double[] predValues, double[] targValues) {

		XYSeries series = new XYSeries("Scatter plot of "+ predColumnName + " & " + targColumnName);


		if(predValues.length > targValues.length)
		{
			for(int i=0;i<predValues.length;i++)
			{

				series.add(predValues[i], targValues[i]);
			}
		}
		else
		{
			for(int i=0;i<targValues.length;i++)
			{

				series.add(predValues[i], targValues[i]);
			}
		}



		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);   

		JFreeChart chart = ChartFactory.createScatterPlot(
				"Scatter Plot", predColumnName, targColumnName,
				data,
				PlotOrientation.VERTICAL,
				true, true, false);

		return chart;
	}

	
	/******************Time Series******************/

	public static JFreeChart createTimeSeriesChart(String tableName,String predColumnName, String targColumnName, double[] values1,
			double[] values2) {
	
		XYSeries series1 = new XYSeries(predColumnName);
		XYSeries series2 = new XYSeries(targColumnName);

		double[] predictorArray = new double[values1.length];
		for (int i = 0; i < values1.length; i++) {
			predictorArray[i] = (double) values1[i];
			series1.add(i + 1, (double) values1[i]);
		}
		double[] targetArray = new double[values2.length];
		for (int i = 0; i < values2.length; i++) {
			targetArray[i] = (double) values2[i];
			series2.add(i + 1, (double) values2[i]);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series1);
		data.addSeries(series2);

		JFreeChart chart = ChartFactory.createXYLineChart("Time - Series Plot", "X", "Y", data, 
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips32
				false // urls
				);

		return chart;
}




	public String generateGraph(){
		
		System.out.print("Inside Generate GRAPH");
		graphbean.setRenderPlot(true);

		List<String> headerList=new ArrayList<>();
		List<List<String>> dataList=new ArrayList<>();
		
		String data = null;

		operationcontroller.getColumnContents();
		
		
		headerList = operationBean.getColumnHeader();
		dataList = operationBean.getDataRows();
		
		
		
		int nObservation = 0;
		
	
				
		try {
			nObservation=	dataList.size();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		double[] dataValues = new double [nObservation];
		
		if(graphbean.getSelectedPlot().equalsIgnoreCase("Pie Chart") ||graphbean.getSelectedPlot().equalsIgnoreCase("Bar Chart") ){
			
			
			
			try {
				for(int i=0; i<headerList.size(); i++) {
					
					for(int r = 0; r < nObservation; r++) {
						data = dataList.get(r).get(i);
						
						dataValues[r] = Double.parseDouble(data);
				
						}


					
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
			
			
			
		}

		Random rand = new Random();

		int  n = rand.nextInt(5000) + 1;
		
		String tableName = operationBean.getTableSelected()+n;

		FacesContext context = FacesContext.getCurrentInstance();
		String path = context.getExternalContext().getRealPath("/temp/");
		String splitFilepath[] = path.split("\\\\");	
		String filePathUpdated ="";				
		for(int i= 0; i<splitFilepath.length;i++)
										
		{								
			String filePathUpdt = splitFilepath[i]+"/";								
			filePathUpdated =filePathUpdated+ filePathUpdt;
										
		}
										
										
		
		try {

			
		
		//	System.out.print(dataValues);
		
			System.out.print(graphbean.getSelectedPlot());
			
		switch(graphbean.getSelectedPlot())
		{
		case "Pie Chart":
			
		{

			JFreeChart pieChart = createPieChart(dataValues);
			
			File out = new File(filePathUpdated + tableName + ".png");
			ChartUtilities.saveChartAsPNG(out, pieChart, 600, 450);
			graphbean.setGraphPathName("/temp/"+ tableName + ".png");
			System.out.print(out.getAbsoluteFile().getPath());
		}
			break;

		case "Bar Chart":
		{
			
			JFreeChart barChart = createBarChart(dataValues);
			File out = new File(filePathUpdated + tableName + ".png");
			ChartUtilities.saveChartAsPNG(out, barChart, 600, 450);
			graphbean.setGraphPathName("/temp/"+ tableName + ".png");
		}
			break;

		case "Scatter Plot":
			
		{
			String predColumnName = regressionBean.getPvSelected();
			String targColumnName = regressionBean.getTvSelected();
			operationcontroller.regressionAnalysis();
			
			System.out.print(predColumnName);
			System.out.print(targColumnName);
			double [] predValues = regressionBean.getPvData();
			double [] targValues = regressionBean.getTvData();
			JFreeChart scatterPlot = createScatterPlotChart(predColumnName, targColumnName, predValues, targValues );
			File out = new File(filePathUpdated + tableName + ".png");
			
			ChartUtilities.saveChartAsPNG(out, scatterPlot, 600, 450);
			graphbean.setGraphPathName("/temp/"+ tableName + ".png");
		}
			break;
		

		case "Time-Series Plot":
		{
			String predColumnName = regressionBean.getPvSelected();
			String targColumnName = regressionBean.getTvSelected();
		
			operationcontroller.regressionAnalysis();
			JFreeChart tsPlot = createTimeSeriesChart(tableName, predColumnName,targColumnName, regressionBean.getPvData(), regressionBean.getTvData());
			File out = new File(filePathUpdated + tableName + ".png");
			ChartUtilities.saveChartAsPNG(out, tsPlot, 600, 450);
			graphbean.setGraphPathName("/temp/"+ tableName + ".png");
		}
			break;
			
			
		}
		
		
		
		
		}
		
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR");
		}
		graphbean.setRenderPlot(true);
		
		
		return "plots";
													
	}


	
	
	
	
}

