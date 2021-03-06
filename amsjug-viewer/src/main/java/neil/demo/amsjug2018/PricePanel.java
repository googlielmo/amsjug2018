package neil.demo.amsjug2018;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDate;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 * <p>A panel containing a dynamic chart. The chart is updated with
 * Bitcoin/US Dollar prices whenever the {@link #update} method is
 * called. But it all happens quick so it might appear as if it
 * is only painted once.
 * </p>
 */
@SuppressWarnings("serial")
public class PricePanel extends JPanel {

    private final TimeSeries[] timeSeries;
	
	/**
	 * <p>Create a time-series panel with the X-Axis for
	 * the date and the Y-Axis for the price of Bitcoin
	 * </p>
	 */
	public PricePanel() {
	    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

		// Initialise all three cached prices to zero
	    this.timeSeries = new TimeSeries[Constants.PANEL_LINES.length];
		for (int i=0 ; i<Constants.PANEL_LINES.length; i++) {
			this.timeSeries[i] = new TimeSeries(Constants.PANEL_LINES[i]);
			timeSeriesCollection.addSeries(this.timeSeries[i]);
		}

        // Chart title within the panel
	    String title = Constants.BITCOIN_CODE + "/" + Constants.US_DOLLARS_CODE
	    		+ " from " + Constants.BTC_USD_FILENAME_FROM_DATE;

	    // Create a chart with the title and axis labels
	    JFreeChart jFreeChart = ChartFactory.createTimeSeriesChart(title,
                Constants.PANEL_X_AXIS,
                Constants.PANEL_Y_AXIS,
                timeSeriesCollection);

	    // Configure Y-Axis on chart, the BTC/USD exchange rate, up to $20,000
	    ValueAxis yAxis = jFreeChart.getXYPlot().getRangeAxis();
	    yAxis.setRange(new Range(0d, 20_000d));
	    
	    // Allow scrolling
	    jFreeChart.getXYPlot().setDomainPannable(true);
	    jFreeChart.getXYPlot().setRangePannable(true);

	    // Colours for average, 50-point, 200-point respectively
	    XYPlot xyPlot = jFreeChart.getXYPlot();
	    XYItemRenderer xYItemRenderer = xyPlot.getRenderer();
	    xYItemRenderer.setSeriesPaint(0, Color.BLUE);
	    xYItemRenderer.setSeriesPaint(1, Color.RED);
	    xYItemRenderer.setSeriesPaint(2, Color.MAGENTA);

	    // Add the chart to the panel
	    this.setLayout(new BorderLayout());
	    this.add(new ChartPanel(jFreeChart));
	}
	
	/**
	 * <p>Update the panel with a new price on a date, from
	 * current, 50-point or 200-point averages.
	 * </p>
	 * 
	 * @param name Current, 50 point average or 200 point average
	 * @param rate In US Dollars, the price of Bitcoin
	 * @param day The day that price occurred
	 */
    public void update(String name, double rate, LocalDate localDate) {
		Day day = new Day(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());

    	// Add the relevant price into the time series array
    	for (int i=0 ; i < Constants.PANEL_LINES.length; i++) {
    		if (Constants.PANEL_LINES[i].equals(name)) {
				this.timeSeries[i].add(new TimeSeriesDataItem(day, rate));
     		}
    	}
    }
}
