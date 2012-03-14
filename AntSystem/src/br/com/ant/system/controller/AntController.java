package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ant.system.app.AntSystemUtil;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;

public class AntController {
		  private static final int   MAXIMO_INTERACOES = 50;
		  private static final float FEROMONIO_INICIAL = 0.0F;
		  private static final float MINIMO_FEROMONIO  = 2F;

		  private List<Formiga>	  formigas;
		  private PercursoController percursoController;

		  public AntController(List<Formiga> formigas, PercursoController percurso) {
					this.formigas = formigas;
					this.percursoController = percurso;
		  }

		  /**
		   * Inicia o execu��o do algoritmo
		   */
		  public void inicializar() {

					System.out.println("Maximo Interacoes: " + MAXIMO_INTERACOES);
					for (int i = 0; i < MAXIMO_INTERACOES; i++) {
							  for (Formiga formiga : formigas) {
										// Recupera as alternativas para o trajeto de cada formiga
										List<Caminho> alternativas = percursoController.getAlternativas(formiga.getLocalizacaoCidadeAtual());

										// Recupera o melhor trajeto que a formiga pode escolher
										Caminho caminhoEscolhido = escolherPercurso(formiga, alternativas);

										// atualiza a localiza��o atual da formiga e o estado da cidade.
										formiga.addCaminho(caminhoEscolhido);

										// Verifica se a formiga ja percorreu todas as cidades.
										if (percursoController.isAllFinished(formiga)) {
												  // Adiciona Feromonio ao trajeto percorrido pela formiga
												  adicionarFeromonioTrajeto(formiga);
												  System.out.println("Chegou ao destino: ");
												  System.out.println(formiga.toString());
												  this.clearFormiga(formiga);

										}
							  }
					}

					System.out.println("Melhor Trajeto: ");
					System.out.println(Arrays.toString(percursoController.getMelhorTrajeto().toArray()));
					System.out.println("Menor caminho: " + percursoController.getTotalMelhorDistancia());

		  }

		  private void adicionarFeromonioTrajeto(Formiga formiga) {
					// Recupera o trajeto efetuado pela formiga
					List<Caminho> trajetosFormigas = formiga.getTrajetoCidades();
					float distancia = 0;
					for (int i = 0; i < trajetosFormigas.size(); i++) {
							  Caminho caminho = trajetosFormigas.get(i);
							  // Recupera as distancias dos percursos
							  distancia += caminho.getDistancia();

							  // Atualiza o feromonio
							  caminho.getFeromonio().addFeromonio();
					}

					// Atualiza as estatiscas dos melhores caminhos
					if (percursoController.getTotalMelhorDistancia() == 0 || percursoController.getTotalMelhorDistancia() > distancia) {
							  percursoController.setTotalMelhorDistancia(distancia);
							  percursoController.setMelhorTrajeto(formiga.getTrajetoCidades());
					}
		  }

		  public Caminho escolherPercurso(Formiga formiga, List<Caminho> todasAlternativas) {
					Map<Cidade, Caminho> mapCaminhosDisponiveis = new HashMap<Cidade, Caminho>();
					List<Cidade> cidadesDisponiveis = new ArrayList<Cidade>();
					float maiorFeromonio = FEROMONIO_INICIAL;
					Caminho distanciaMaiorFeromonio = null;

					for (Caminho d : todasAlternativas) {
							  if (!formiga.isCidadeVisitada(d.getCidadeDestino())) {
										mapCaminhosDisponiveis.put(d.getCidadeDestino(), d);
										cidadesDisponiveis.add(d.getCidadeDestino());

										if (d.getFeromonio().getFeromonio() >= MINIMO_FEROMONIO && d.getFeromonio().getFeromonio() > maiorFeromonio) {
												  maiorFeromonio = d.getFeromonio().getFeromonio();
												  distanciaMaiorFeromonio = d;
										}

							  }
					}

					Caminho distanciaEscolhida;
					if (distanciaMaiorFeromonio == null) {
							  if (!mapCaminhosDisponiveis.isEmpty()) {
										int aleatorio = AntSystemUtil.getIntance().getAleatorio(0, cidadesDisponiveis.size() - 1);
										distanciaEscolhida = mapCaminhosDisponiveis.get(cidadesDisponiveis.get(aleatorio));
							  } else {
										distanciaEscolhida = todasAlternativas.get(AntSystemUtil.getIntance().getAleatorio(0, todasAlternativas.size() - 1));
							  }
					} else {
							  distanciaEscolhida = distanciaMaiorFeromonio;
					}

					return distanciaEscolhida;
		  }

		  private void clearFormiga(Formiga formiga) {
					Cidade atuaCidade = percursoController.getCidades().get(AntSystemUtil.getIntance().getAleatorio(0, percursoController.getCidades().size() - 1));
					formiga.clear();
					formiga.setLocalizacaoCidadeAtual(atuaCidade);
		  }
}
