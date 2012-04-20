package br.com.ant.system.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.com.ant.system.controller.EstatisticaColetor;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Estatistica;

public class ChartUtil {
	private static ChartUtil	instance;

	public static ChartUtil getInstance() {
		if (instance == null) {
			instance = new ChartUtil();
		}

		return instance;
	}

	public void createTempoTotalExecucao(Set<EstatisticaColetor> estatisticas) {

		// Create a simple XY chart
		XYSeries series = new XYSeries("Formiga");

		JFrame frame = new JFrame();

		for (EstatisticaColetor e : estatisticas) {
			series.add(e.getId(), e.getTempoExecucao());
		}

		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart("Tempo total de Execu��o", "Execu��o", "Tempo (ms)", dataset, PlotOrientation.VERTICAL, true, true, false);
		frame.getContentPane().add(new ChartPanel(chart));

		frame.setPreferredSize(new Dimension(600, 600));
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setMaximumSize(new Dimension(600, 600));
		frame.setVisible(true);

		try {
			ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}

	public void createCaminhoPercorrido(EstatisticaColetor estatisticaColetor) {

		// Create a simple XY chart

		JFrame frame = new JFrame();

		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();

		Map<Cidade, List<Estatistica>> mapformigasEstatisticas = new HashMap<Cidade, List<Estatistica>>();
		for (Estatistica e : estatisticaColetor.getEstatisticas()) {
			if (mapformigasEstatisticas.containsKey(e.getCidadeInicial())) {
				mapformigasEstatisticas.get(e.getCidadeInicial()).add(e);
			} else {
				List<Estatistica> lista = new ArrayList<Estatistica>();
				lista.add(e);

				mapformigasEstatisticas.put(e.getCidadeInicial(), lista);
			}
		}

		Set<Cidade> cidades = mapformigasEstatisticas.keySet();
		for (Cidade c : cidades) {
			// for (int i = 0; i < 2; i++) {
			List<Estatistica> list = (List<Estatistica>) mapformigasEstatisticas.get(c);

			XYSeries series = new XYSeries(c.getNome());
			dataset.addSeries(series);

			for (Estatistica e : list) {
				if (e.getFormigaId() == 1) {
					series.add(e.getIteracao(), e.getDistanciaPercorrida());
				}
			}
		}

		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart("", "Itera��o", "Distancia (Km)", dataset, PlotOrientation.VERTICAL, true, true, false);
		frame.getContentPane().add(new ChartPanel(chart));

		frame.setPreferredSize(new Dimension(600, 600));
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setMaximumSize(new Dimension(600, 600));
		frame.setVisible(true);

		try {
			ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}
}
