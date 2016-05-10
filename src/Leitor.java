
public class Leitor {
	/*
	 * 10% DE PERDA
	 */
	public static void main(String[] args) {
		Arquivo arqLB = new Arquivo("Parametros.in", "CpDadosLB.out");
		Arquivo arqEL = new Arquivo("Parametros.in", "CpDadosEL.out");
		Arquivo arqP1 = new Arquivo("Parametros.in", "CpDadosP1.out");
		Arquivo arqP2 = new Arquivo("Parametros.in", "CpDadosP2.out");
		Arquivo arqP3 = new Arquivo("Parametros.in", "CpDadosP3.out");
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
		int colisoesQuadro = 1;
		int sucessosQuadro = 0;
		int vaziosQuadro = 0;

		int lidas = 0;

		int countTentativas = 0;
		int metodo = 1; //indica qual estimador sera usado: 1= lower bound, 2 = eom-lee, 3 = o metodo do prof

		while(qntdeTags <= 1000){
			while(countTentativas < qntdeTentativas){

				tags = inicializar(qntdeTags);
				slots = new int[estimativaI][qntdeTags];
				
				while(colisoesQuadro > 0){
					colisoesQuadro = 0;
					sucessosQuadro = 0;
					vaziosQuadro = 0;
					
					info[3] += estimativa;

					//neste for um numero aleatorio eh atribuido a cada tag nao lida e ela eh alocada num slot
					for (int i = 0; i < tags.length; i++) {
						if(!tags[i].foiLida){
							tags[i].rand(1, 100);
							int p = tags[i].r;
							tags[i].rand(1, estimativa);
							
							if(p > 9) {
								alocarTag(slots, tags[i], tags.length);
							}
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
								lidas += 1;
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
					colidiram = false;
					slots = new int[estimativa][qntdeTags];
				}
				colisoesQuadro = 1;

				if(metodo == 1){
					arqLB.println(lidas+" "+info[1]+" "+info[2]+" "+info[3]+" "+(qntdeTags - lidas));
				}else if(metodo == 2){
					arqEL.println(lidas+" "+info[1]+" "+info[2]+" "+info[3]+" "+(qntdeTags - lidas));
				}else if(metodo == 3){
					arqP1.println(lidas+" "+info[1]+" "+info[2]+" "+info[3]+" "+(qntdeTags - lidas));
				}else if(metodo == 4){
					arqP2.println(lidas+" "+info[1]+" "+info[2]+" "+info[3]+" "+(qntdeTags - lidas));
				}else{
					arqP3.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" "+(qntdeTags - lidas));
				}

				info = new int [4];
				estimativa = estimativaI;

				countTentativas ++;
				lidas = 0;
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

