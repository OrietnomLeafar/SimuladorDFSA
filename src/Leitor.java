
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
		

		while(!todasIdentificadas(tags)){
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
				slots = new int[estimativa][qntdeTags];
			}
			System.out.println(info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" ");
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

	public int EomLee(){
		return 0;
	}


}
