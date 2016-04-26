
public class Leitor {

	public static void main(String[] args) {

		Arquivo arqLB = new Arquivo("Parametros.in", "DadosLB.out");
		Arquivo arqEL = new Arquivo("Parametros.in", "DadosEL.out");
		Estimador estimador = new Estimador();

		int qntdeTags = arqLB.readInt();
		int estimativaI = arqLB.readInt();
		int qntdeTentativas = arqLB.readInt();
		int estimativa = estimativaI;

		boolean[] tags = new boolean[qntdeTags];//array que diz quais tags foram reconhecidas

		int[][] slots = new int[estimativaI][qntdeTags];//array pra alocar tags nos slots

		int r; //num aleatorio

		/*
		 * a variavel info vai armazenar as quantidades de slots vazios, colisoes, sucessos e slots totais
		 * info[0] = sucessos/ info[1] = colisoes/ info[2] = vazios/ info[3] = slots
		 * */
		int[] info = new int [4];
		boolean colidiram = false;
		int colisoesQuadro = 0;
		int sucessosQuadro = 0;

		int countTentativas = 0;
		int count = 0;
		while(estimativaI <=128){//esse while vai rodar duas vezes: p/ 64 e p/ 128 slots iniciais
			
			while(qntdeTags <= 1000){//esse vai rodar 10 vzs: p/ 100,200,...,1000 tags

				while(countTentativas < qntdeTentativas){//esse vai rodar de acordo com um parâmetro do arquivo

					//Lower Bound
					/*while(!todasIdentificadas(tags)){//esse vai rodar até que o lower bound identifique todas as tags
						info[3] += estimativa;

						//neste for um numero aleatorio eh atribuido a cada tag nao lida e ela eh alocada num slot
						for (int tag = 0; tag < tags.length; tag++) {
							
							if(!tags[tag]){     //se a tag ja foi lida (true) nao e preciso gerar um numero pra ela

								r = rand(1,estimativa)-1;     //sorteia um numero aleatorio dentre os possiveis
								alocarTag(slots, tag, r, qntdeTags);     //aloca a tag em um slot
							}
						}

						//for para verificar sucessos, colisoes e vazios
						for (int i = 0; i < estimativa; i++) {
							if(slots[i][0] == 0){
								info[2] += 1;

							}else if(slots[i][0] > 0){

								if(slots[i][1] > 0){
									colidiram = true;
									info[1] += 1;
									colisoesQuadro +=1;
								}else{
									info[0] += 1;
									tags[slots[i][0]-1] = true;
								}
							}
						}

						if(colidiram){
							estimativa = estimador.LowerBound(colisoesQuadro);
							colisoesQuadro = 0;
							colidiram = false;
							slots = new int[estimativa][qntdeTags];
						}

					}
					arqLB.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
					
					//resetando variáveis para utilizar o Eom-Lee
					tags = new boolean[qntdeTags];	
					slots = new int[estimativaI][qntdeTags];
					info = new int [4];
					estimativa = estimativaI;
					*/
					//Eom-Lee
					while(!todasIdentificadas(tags)){//esse vai rodar até que o Eom-Lee identifique todas as tags
						info[3] += estimativa;

						//neste for um numero aleatorio eh atribuido a cada tag nao lida e ela eh alocada num slot
						for (int tag = 0; tag < tags.length; tag++) {
							if(!tags[tag]){     //se a tag ja foi lida (true) nao e preciso gerar um numero pra ela

								r = rand(1,estimativa)-1;     //sorteia um numero aleatorio dentre os possiveis
								alocarTag(slots, tag, r, qntdeTags);     //aloca a tag em um slot
							}
						}

						//for para verificar sucessos, colisoes e vazios
						for (int i = 0; i < estimativa; i++) {
							if(slots[i][0] == 0){
								info[2] += 1;

							}else if(slots[i][0] > 0){

								if(slots[i][1] > 0){
									colidiram = true;
									info[1] += 1;
									colisoesQuadro += 1;
								}else{
									sucessosQuadro += 1;
									info[0] += 1;
									tags[slots[i][0]-1] = true;
								}
							}
						}
						System.out.println("loop aqui? "+ count+++"      "+estimativa +"          "+colisoesQuadro);
						if(colidiram){
							estimativa = estimador.EomLee(colisoesQuadro, sucessosQuadro, estimativa);// FALTAR IMPLEMENTAR O EOM-LEE
							colisoesQuadro = 0;
							sucessosQuadro = 0;
							colidiram = false;
							slots = new int[estimativa][qntdeTags];
						}
						//System.out.println("loop aqui? "+ count++);
					}
					arqEL.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
					
					tags = new boolean[qntdeTags];	
					slots = new int[estimativaI][qntdeTags];
					info = new int [4];
					estimativa = estimativaI;
					
					countTentativas ++;
					System.out.println("------------------------ "+countTentativas);
				}

				qntdeTags += 100;
				tags = new boolean[qntdeTags];
				colidiram = false;
				slots = new int[estimativaI][qntdeTags];
				countTentativas = 0;
				
			}
			//resetando os parâmetros para refazer os testes com 128 slots iniciais
			qntdeTags = 100;
			tags = new boolean[qntdeTags];
			colidiram = false;
			estimativaI *= 2;
			slots = new int[estimativaI][qntdeTags];
			

		}
		System.out.println("FIM");


	}


	static void alocarTag(int[][] slots, int tag, int valor, int tagsTotais){
		for (int i = 0; i < tagsTotais; i++) {
			if(slots[valor][i]==0){
				slots[valor][i] = tag+1;
				return;
			}
		}
	}

	static int rand(int Str, int End) {
		return (int) Math.ceil(Math.random() * (End  - Str + 1)) - 1 + Str;
	}

	static boolean todasIdentificadas(boolean[] tags){
		for (int i = 0; i < tags.length; i++) {
			if(tags[i] == false){
				return false;
			}
		}
		return true;
	}
}

class Estimador{

	//retorna a estimativa segundo o lower bound
	public int LowerBound(int colisoes){
		return colisoes*2;
	}

	public int EomLee(int colisoes,int sucessos, int quadro){
		int novaEstimativa = 0;
		
		double e = Math.E;
		double gamaANT = 2;
		double gamaATU = 0, betaK, umSobreBetaK, numerador, denominador = 0;
		double diferenca =0;
		
		do{
			
			betaK = quadro/((gamaANT*colisoes) + sucessos);
			umSobreBetaK = 1/betaK;
			
			numerador = 1 - Math.pow(e, umSobreBetaK*-1);
			denominador = betaK*(1 - ((1 + umSobreBetaK)* Math.pow(e, umSobreBetaK*-1)));
			
			gamaATU = numerador/denominador;
			
			diferenca = (gamaANT - gamaATU)*(-1);
			/*System.out.println((gamaANT - gamaATU)*(-1));
			System.out.println(gamaANT+"   <<<<<<<ANTERIOR");
			System.out.println(gamaATU+"   <<<<<<<ATUAL\n");*/
			gamaANT = gamaATU;
			
		
		}while(!(diferenca < 0.001));
		
		novaEstimativa = (int) (gamaATU * colisoes);
		
		return novaEstimativa;
	}


}
