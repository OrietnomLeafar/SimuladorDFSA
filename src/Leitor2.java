
public class Leitor2 {
	/*
	 * 0% DE PERDA
	 */
	public static void main(String[] args) {
		Arquivo arqLB = new Arquivo("Parametros.in", "DadosLB.out");
		Arquivo arqEL = new Arquivo("Parametros.in", "DadosEL.out");
		Arquivo arqP1 = new Arquivo("Parametros.in", "DadosP1.out");
		Arquivo arqP2 = new Arquivo("Parametros.in", "DadosP2.out");
		Arquivo arqP3 = new Arquivo("Parametros.in", "DadosP3.out");
		Estimador estimador = new Estimador();

		int qntdeTags = arqLB.readInt();
		int estimativaI = arqLB.readInt();
		int qntdeTentativas = arqLB.readInt();
		int estimativa = estimativaI;

		Tag[] tags = new Tag[qntdeTags];//array que diz quais tags foram reconhecidas

		tags = inicializar(qntdeTags);

		int[][] slots = new int[estimativaI][qntdeTags];//array pra alocar tags nos slots

		int[] info = new int [4];
		boolean colidiram = false;
		int colisoesQuadro = 0;
		int sucessosQuadro = 0;
		int vaziosQuadro = 0;

		int countTentativas = 0;
		int metodo = 1; //indica qual estimador sera usado: 1= lower bound, 2 = eom-lee, 3 = o metodo do prof

		while(qntdeTags <= 1000){
			while(countTentativas < qntdeTentativas){

				tags = inicializar(qntdeTags);
				slots = new int[estimativaI][qntdeTags];
				
				while(!todasIdentificadas(tags)){
					info[3] += estimativa;

					//neste for um numero aleatorio eh atribuido a cada tag nao lida e ela eh alocada num slot
					for (int i = 0; i < tags.length; i++) {
						if(!tags[i].foiLida){
							tags[i].rand(1, estimativa);
							alocarTag(slots, tags[i], tags.length);
						}
					}
					//for para verificar sucessos, colisoes e vazios
					for (int i = 0; i < estimativa; i++) {
						if(slots[i][0] == 0){
							info[2] += 1;
							vaziosQuadro += 1;

						}else if(slots[i][0] > 0){

							if(slots[i][1] > 0){
								colidiram = true;
								info[1] += 1;
								colisoesQuadro += 1;
							}else{
								sucessosQuadro += 1;
								info[0] += 1;
								tags[slots[i][0]-1].foiLida = true;
							}
						}
					}

					
					System.out.println("col: "+colisoesQuadro + " suc: "+sucessosQuadro + " lidas: "+ info[0]);
					if(colidiram){
						switch(metodo){
						case 1:
							estimativa = estimador.LowerBound(colisoesQuadro);

							break;

						case 2:
							estimativa = estimador.EomLee(colisoesQuadro, sucessosQuadro, estimativa);

							break;

						case 3:
							estimativa = estimador.p1(estimativa, colisoesQuadro, sucessosQuadro, vaziosQuadro);
							
							
							break;
							
						case 4:
							estimativa = estimador.p2(vaziosQuadro, estimativa, colisoesQuadro);
							break;
							
						case 5:
							estimativa = estimador.p3(colisoesQuadro, sucessosQuadro, estimativa);
							break;
						}


					}
					System.out.println("ESTIMATIVA " + estimativa+"\n");
					colisoesQuadro = 0;
					sucessosQuadro = 0;
					vaziosQuadro = 0;
					colidiram = false;
					slots = new int[estimativa][qntdeTags];
				}

				if(metodo == 1){
					arqLB.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
				}else if(metodo == 2){
					arqEL.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
				}else if(metodo == 3){
					arqP1.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
				}else if(metodo == 4){
					arqP2.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
				}else{
					arqP3.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
				}

				info = new int [4];
				estimativa = estimativaI;

				countTentativas ++;
			}
			
			qntdeTags += 100;
			countTentativas = 0;
			if(qntdeTags > 1000 && metodo < 5 ){
				qntdeTags = 100;
				metodo += 1;
				System.out.println("agr eh o metodo "+metodo);
				switch(metodo){
				case 2:
					arqLB.close();
					break;
				case 3:
					arqEL.close();
					break;
				case 4:
					arqP1.close();
					break;
				case 5:
					arqP2.close();
					break;
				}
			}
			
		}
		arqP3.close();
		System.out.println("FIM");
	}

	static void alocarTag(int[][] slots, Tag tag, int tagsTotais){
		int s = tag.r;
		for (int i = 0; i < tagsTotais; i++) {
			if(slots[s][i] == 0){
				slots[s][i] = tag.id;
				return;
			}
		}
	}

	static Tag[] inicializar(int qntdeTags){
		Tag[] t = new Tag[qntdeTags];

		for (int i = 0; i < t.length; i++) {
			t[i]= new Tag(false, i+1);
		}

		return t;
	}

	static boolean todasIdentificadas(Tag[] tags){
		for (int i = 0; i < tags.length; i++) {
			if(tags[i].foiLida == false){
				return false;
			}
		}
		return true;
	}
}

class Tag{
	boolean foiLida;
	int id;
	int r;

	public Tag(boolean lida, int id){
		foiLida = lida;
		this.id = id;
	}

	void rand(int Str, int End) {
		r = (int) Math.ceil(Math.random() * (End  - Str + 1)) - 1 + Str;
		r -= 1;
		//System.out.println(r);
	}
}

class Estimador{

	//retorna a estimativa segundo o lower bound
	public int LowerBound(int colisoes){
		return colisoes*2;
	}

	public int EomLee(int colisoes,int sucessos, int quadro){
		double novaEstimativa = 0;

		double gamaANT = 2;
		double gamaATU = 0, betaK = 0;
		double diferenca =0;
		
		do{

			betaK = quadro/((gamaANT* (double) colisoes) + (double) sucessos);

			gamaATU = (1.0 - Math.exp((-1.0)/betaK)) / (betaK * (1.0 - (1.0 + 1.0/betaK) * Math.exp((-1.0)/betaK)));

			diferenca = (gamaANT - gamaATU)*(-1);
			/*System.out.println((gamaANT - gamaATU)*(-1));
			System.out.println(gamaANT+"   <<<<<<<ANTERIOR");
			System.out.println(gamaATU+"   <<<<<<<ATUAL\n");*/

			gamaANT = gamaATU;

		}while(!(diferenca < 0.001));

		novaEstimativa = (gamaATU * colisoes);
		//System.out.println("prox quadro: "+novaEstimativa +"\n\n");
		return (int) Math.round(novaEstimativa);
	}

	public int p1(int quadro, int colisoes, int sucessos, int vazios){
		
		double L = quadro;
		double alfa = (L - 1)/L;
		double col = colisoes;
		double suc = sucessos;
		double en = col + suc;
		
		int ret = 0;
		if(vazios == 0) {
			ret = LowerBound(colisoes);
		} else {
			double n = (Math.log10(1-(1 - alfa)*en))/Math.log10(alfa);
			ret = (int) Math.ceil(n);
		}
		
		
		return ret;
	}
	
	public int p2(int vazios, int quadro, int colisoes){
		double V = vazios;
		double L = quadro;
		double col = colisoes;
		double n = 0;
		
		if(vazios != 0){		
			n = (Math.log10(V/L))/Math.log10(1-(1/L));
		}else{
			n = 2* col;
		}
		return (int) Math.ceil(n);
	}
	
	
	public int p3(int colisoes, int sucessos, int quadro){
		double L = quadro;
		double col = colisoes;
		double suc = sucessos;
		double n;
		
		int ret =0;
		if(col != L){
			n = EomLee(colisoes, sucessos, quadro);
		}else{
			n = (12.047)*(L -1)+ 2;
			n = Math.ceil(n-suc);
		}
		ret = (int) n;
		
		return ret;
	}
}
