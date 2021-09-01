import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Individuo implements Comparable<Individuo>{
	private static final double PROBABILIDAD_DE_MUTACIÓN=0.05;
	
	ArrayList<Integer> cromosoma=new ArrayList<>();
	int longitudDelCromosoma;
	double valorFuncionObjetivo;
	boolean inicioFijo=false;
	
	public Individuo(int[] cromosoma){
		for(int i :cromosoma)
			this.cromosoma.add(i);
		this.longitudDelCromosoma = cromosoma.length;
	}
	
	public Individuo[] crossover(Individuo pareja){
		if(this.equals(pareja)){
			return new Individuo[]{
				this.crearClon()
				,this.crearClon()
			};
		}

		List<List<Integer>> ciclos=new ArrayList<>();
		List<Boolean> indicesVisitados=new ArrayList<>();
		Collections.fill(indicesVisitados, false);
		
		while(indicesVisitados.contains(true)){
			ciclos.add(new ArrayList<>());
			int cicloActual=ciclos.size()-1;
			int primerIndice=indicesVisitados.indexOf(false)
				,indiceActual=primerIndice;
			do{
				ciclos.get(cicloActual).add(indiceActual);
				indicesVisitados.set(indiceActual, true);
				indiceActual=this.cromosoma.indexOf(pareja.cromosoma.get(indiceActual));
			}while(indiceActual!=primerIndice);
		}
		
    // Crear 2 nuevos cromosomas.
		int[] cromosomaNuevo1=new int[longitudDelCromosoma]
			,cromosomaNuevo2=new int[longitudDelCromosoma];
		
		Individuo p1=this;
		Individuo p2=pareja;
		for(List<Integer> ciclo:ciclos){
			for(Integer i:ciclo){
				cromosomaNuevo1[i]=p1.cromosoma.get(i);
				cromosomaNuevo2[i]=p2.cromosoma.get(i);
			}
			Individuo tmp=p1;
			p1=p2;
			p2=tmp;
		}

		return new Individuo[]{
			new Individuo(cromosomaNuevo1)
			,new Individuo(cromosomaNuevo2)
		};
	}
	
	public boolean aplicarMutacion(){
		if(Math.random()<Individuo.PROBABILIDAD_DE_MUTACIÓN){
			int gen1=Utils.randomIntBetween(0, this.longitudDelCromosoma);
			int gen2=Utils.randomIntBetween(0, this.longitudDelCromosoma);
			
			// Método de Mutación: swapping
			int temp=cromosoma.get(gen1);
			cromosoma.set(gen1,cromosoma.get(gen2));
			cromosoma.set(gen2,cromosoma.get(temp));
			
			return true;
		} else return false;
	}

	public Individuo crearClon(){
		Individuo clon=null;
		try {
			clon = (Individuo)this.clone();
			clon.valorFuncionObjetivo=valorFuncionObjetivo;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// Individuo(Arrays.copyOf(cromosoma,cromosoma.length));
		return clon;
	}

	public String toJSONObject() {
		StringBuilder sb = new StringBuilder("[");
		for(int valorGen : cromosoma){
			sb.append(valorGen+",");
		}
		return sb.toString()+"]";
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Recorrido:");
		for(int valorGen : cromosoma){
			sb.append("\n\t"+App.provincias[valorGen]);
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(Individuo otro) {
		for (int i=0,to=otro.longitudDelCromosoma;i<to;i++)
			if(otro.cromosoma.get(i)!=this.cromosoma.get(i))
				return otro.cromosoma.get(i)-this.cromosoma.get(i);
		return 0;
	}

	@Override
	public boolean equals(Object otro){
		//TODO hacer directamente otro=(Individuo)otro;
		Individuo otroComoIndividuo=((Individuo)otro);
		for (int i=0,to=otroComoIndividuo.longitudDelCromosoma;i<to;i++)
			if(otroComoIndividuo.cromosoma.get(i)!=this.cromosoma.get(i))
				return false;
		return true;
	}
}
