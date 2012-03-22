/**
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Estatistica;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.NumberUtil;

/**
 * Classe responsavel pela coletas de estaticas da execucao do algoritmo.
 * 
 * @author j.duarte
 * 
 */
public class EstatisticasControler {

	private List<Caminho>					melhorCaminho	= new LinkedList<Caminho>();
	private double							menorCaminhoPercorrido;
	private Long							menorTempo;
	private long							horarioInicial;
	private long							horarioFinal;

	Logger									logger			= Logger.getLogger(this.getClass());
	private List<Estatistica>				estatisticas	= new ArrayList<Estatistica>();

	private static EstatisticasControler	instance;

	public static EstatisticasControler getInstance() {
		if (instance == null) {
			instance = new EstatisticasControler();
		}

		return instance;
	}

	private EstatisticasControler() {
	}

	public void coletarEstatisticas(Formiga formiga) {
		this.addEstatistica(formiga);

		if (menorCaminhoPercorrido == 0 || formiga.getDistanciaPercorrida() < menorCaminhoPercorrido) {
			menorCaminhoPercorrido = formiga.getDistanciaPercorrida();

			// Copiando a o caminho percorrido para o melhor caminho.
			melhorCaminho.addAll(formiga.getTrajetoCidades());

			// Setando o menor tempo.
			menorTempo = (formiga.getTempoFinal() - formiga.getTempoInicial());
		}
	}

	private void addEstatistica(Formiga formiga) {
		Estatistica estatistica = new Estatistica();
		estatistica.setFormigaId(formiga.getId());
		estatistica.setDistanciaPercorrida(formiga.getDistanciaPercorrida());
		estatistica.setTempoGasto(formiga.getTempoFinal() - formiga.getTempoInicial());

		estatistica.getCaminhoPercorrido().addAll(formiga.getTrajetoCidades());
		estatisticas.add(estatistica);
	}

	public long getTempoExecucao() {
		return horarioFinal - horarioInicial;
	}

	public List<Caminho> getMelhorCaminho() {
		return melhorCaminho;
	}

	public double getMenorCaminhoPercorrido() {
		return menorCaminhoPercorrido;
	}

	public Long getMenorTempo() {
		return menorTempo;
	}

	public List<Estatistica> getEstatisticas() {
		return estatisticas;
	}

	public void setHorarioFinal(long horarioFinal) {
		this.horarioFinal = horarioFinal;
	}

	public void setHorarioInicial(long horarioInicial) {
		this.horarioInicial = horarioInicial;
	}

	public long getHorarioFinal() {
		return horarioFinal;
	}

	public long getHorarioInicial() {
		return horarioInicial;
	}

	public void loggerEstatisticas() {
		logger.info("***********************************************************************************************");
		logger.info("**************************************Estatisticas*******************************************");
		logger.info("***********************************************************************************************");
		logger.info("Menor Tempo: " + menorTempo + " ms");
		logger.info("Menor caminho: " + NumberUtil.getInstance().doubleToString(menorCaminhoPercorrido));
		logger.info("Melhor trajeto: " + Arrays.toString(melhorCaminho.toArray()));
		logger.info("Quantidade de solucoes encontradas: " + estatisticas.size());
		logger.info("***********************************************************************************************");
		for (Estatistica e : estatisticas) {
			logger.info("FormigaID: " + e.getFormigaId());
			logger.info("Tempo Gasto: " + e.getTempoGasto() + " ms");
			logger.info("Distancia Percorrida: " + NumberUtil.getInstance().doubleToString(e.getDistanciaPercorrida()));
			logger.info("Trajeto: " + Arrays.toString(e.getCaminhoPercorrido().toArray()));
			logger.info("***********************************************************************************************");
		}
	}

}