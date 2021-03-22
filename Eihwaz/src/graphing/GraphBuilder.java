package graphing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.PiePlot;
import orient.Orient2;

public class GraphBuilder {
	// will read from file to retrieve the data for the graph
	// should use the graphing api to draw the graph to be used by the graph saver
	// graph api -> graph -> GraphSaver
	
	//private boolean add;

	public GraphBuilder() {
		
	}
	
	public List<List<String>> getLinesFromCSV(File csvFile) throws FileNotFoundException{
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(csvFile)) {
		    while (scanner.hasNextLine()) {
		        records.add(getDataFromLine(scanner.nextLine()));
		    }
		}
		
		if(Orient2.DEBUG)
			for(List<String> r : records)
				for(String s : r)
					System.out.println(s);
		
		return records;
	}
	
	public List<String> getDataFromLine(String line){
		List<String> values = new ArrayList<String>();
	    try (Scanner rowScanner = new Scanner(line)) {
	        rowScanner.useDelimiter(",");
	        while (rowScanner.hasNext()) {
	            values.add(rowScanner.next());
	        }
	    }
	    return values;
	}
	
	// https://www.tutorialspoint.com/javafx/2dshapes_rectangle.htm
	
	// TODO: for the bar charts
	// http://jcharts.sourceforge.net/
	// https://developers.google.com/chart/interactive/docs/gallery/barchart
	// https://developers.google.com/chart/interactive/docs/printing
	// http://jcharts.sourceforge.net/
	// https://github.com/eseifert/gral/wiki/gallery
	// jCharts, no
	// use GRAL!
	
	// You can set the Font and Paint used for the Values on the ValueLabelRenderer Object. 
	
	// http://jcharts.sourceforge.net/samples/bar.html
	//---to make this plot horizontally, pass true to the AxisProperties Constructor
	public void generateBarChart() {
		
	}
	
	public byte[] exportChart() throws IOException {
        BufferedImage bImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bImage.getGraphics();
        DrawingContext context = new DrawingContext(g2d);
        BarPlot plot = new BarPlot(); //getPlot();
        plot.draw(context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DrawableWriter wr = DrawableWriterFactory.getInstance().get("image/jpeg");
        wr.write(plot, baos, 800, 600);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
	}
	
	// TODO: for the pie charts
	// https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/pie-chart.htm
	// JavaFX
	
}
