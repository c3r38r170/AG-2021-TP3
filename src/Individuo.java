import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Individuo implements Comparable<Individuo>,Cloneable {
	private static final double PROBABILIDAD_DE_MUTACIÓN=.05;
	
	ArrayList<Integer> cromosoma=new ArrayList<>();
	int longitudDelCromosoma;
	double valorFuncionObjetivo;
	int longitud=0;
	boolean inicioFijo=false;
	
	public Individuo(int[] cromosoma){
		for(int i :cromosoma)
			this.cromosoma.add(i);
		this.longitudDelCromosoma = cromosoma.length;
		calcularLongitud();
	}

	public Individuo(ArrayList<Integer> cromosoma){
		this.cromosoma =cromosoma;
		longitudDelCromosoma =cromosoma.size();
		calcularLongitud();
	}

	private void calcularLongitud(){
		int ultimoDestino=cromosoma.get(cromosoma.size()-1);
		for(int provincia: cromosoma){
			longitud+=App.obtenerDistanciaEntre(ultimoDestino,provincia);
			ultimoDestino=provincia;
		}
	}
	
	public Individuo[] crossover(Individuo pareja){
		if(this.equals(pareja)){
			return new Individuo[]{
				this.crearClon()
				,this.crearClon()
			};
		}

		List<List<Integer>> ciclos=new ArrayList<>();
		List<Boolean> indicesVisitados=new ArrayList<> (Collections.nCopies(longitudDelCromosoma, false));

		while(indicesVisitados.contains(false)){
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
			int primerGen=inicioFijo?1:0;
			int gen1=Utils.randomIntBetween(primerGen, this.longitudDelCromosoma);
			int gen2=Utils.randomIntBetween(primerGen, this.longitudDelCromosoma);
			
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
			e.printStackTrace();
		}// I.ndividuo(Arrays.copyOf(cromosoma,cromosoma.length));
		return clon;
	}

	public String toJSONObject() {
		// TODO ver si no es 1000/
		StringBuilder sb = new StringBuilder("{\"longitud\":"
			// +(new DecimalFormat("##.#######")).format(valorFuncionObjetivo/1000)
			+/* (int)(longDouble*100.0)( */longitud/* /1000) */
			+",\"recorrido\":[");
		for(int valorGen : cromosoma){
			sb.append(valorGen+",");
		}
		return sb.toString()+"]}";
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Recorrido:");
		for(int valorGen : cromosoma){
			sb.append("\n\t"+App.PROVINCIAS[valorGen]);
		}
		return sb.toString();
	}
	
	// Este método se usa para ordenar de mejor a peor.
	@Override
	public int compareTo(Individuo otro) {
		double resta=otro.valorFuncionObjetivo-this.valorFuncionObjetivo;
		return (int)(resta/Math.abs(resta));
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
