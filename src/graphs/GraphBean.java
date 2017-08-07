package graphs;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;



@ManagedBean(name="graphbean")
@SessionScoped
public class GraphBean {

	private String selectedPlot;
	private String graphPathName;
	private boolean renderPlot;

	public String getSelectedPlot() {
		return selectedPlot;
	}
	public void setSelectedPlot(String selectedPlot) {
		this.selectedPlot = selectedPlot;
	}

	public String getGraphPathName() {
		return graphPathName;
	}
	public void setGraphPathName(String graphPathName) {
		this.graphPathName = graphPathName;
	}

	public boolean isRenderPlot() {
		return renderPlot;
	}
	public void setRenderPlot(boolean renderPlot) {
		this.renderPlot = renderPlot;
	}
}
