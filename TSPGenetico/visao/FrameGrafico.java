package visao;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgap.Gene;
import org.jgap.IChromosome;

import controle.GA.Alelo;

public class FrameGrafico extends JFrame {

	private JFreeChart jfc;
	
	public FrameGrafico(String titulo, IChromosome dados){
		this.setTitle(titulo);
		this.setSize(640, 430);
		
		final XYSeriesCollection collection=this.criaDados(dados);		
		this.criaGrafico(collection);
	}
	
	private XYSeriesCollection criaDados(IChromosome dados) {
		XYSeries series1 = new XYSeries("Rota",false,true);
		
		Gene[] genes=dados.getGenes();
		for(int i=0; i<genes.length;i++){
			Alelo alelo=(Alelo)genes[i].getAllele();
			series1.add(alelo.getPos_x(), alelo.getPos_y());
		}
		//Coloca o primeiro novamente para terminar o ciclo
		Alelo alelo=(Alelo)genes[0].getAllele();
		series1.add(alelo.getPos_x(), alelo.getPos_y());

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series1);
        
		return data;
	}

	private void criaGrafico(XYSeriesCollection data) {
		jfc = ChartFactory.createXYLineChart(
                this.getTitle(),      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                data,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
            );
			jfc.setBackgroundPaint(Color.white);
			
			final XYPlot plot = jfc.getXYPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	        renderer.setSeriesLinesVisible(0, true);
	        renderer.setSeriesShapesVisible(0, true);
	        plot.setRenderer(renderer);

	        // change the auto tick unit selection to integer units only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        // OPTIONAL CUSTOMISATION COMPLETED.
	
	}
	
	private JPanel createPanel()
	{
		return new ChartPanel(jfc);
	}
	
	public void Show()
	{
		setContentPane(createPanel());
		setVisible(true);
	}
}