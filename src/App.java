import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class App extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private boolean elitismo=false;
	
	private Individuo[] poblacionActual;
	private double[] vectorFitness;
	
	private Individuo maximoTotal;
	// private Individuo maximoIndividuo;
	// private Individuo minimoIndividuo;
	// private double promedio;
	private double sumatoriaPuntuaciones=0;
	
	private int tamañoPoblacion=0;
	
	private WebEngine webEngine;

	public static final String[] PROVINCIAS={
		"Buenos Aires - Cdad. de Bs. As."
		,"Córdoba - Córdoba"
		,"Corrientes - Corrientes"
		,"Formosa - Formosa"
		,"Buenos Aires - La Plata"
		,"La Rioja - La Rioja"
		,"Mendoza - Mendoza"
		,"Neuquén - Neuquén"
		,"Entre Ríos - Paraná"
		,"Misiones - Posadas"
		,"Chubut - Rawson"
		,"Chaco - Resistencia"
		,"Santa Cruz - Río Gallegos"
		,"Catamarca - San Fernando del Valle de Catamarca"
		,"Tucumán - San Miguel de Tucumán"
		,"Jujuy - San Salvador de Jujuy"
		,"Salta - Salta"
		,"San Juan - San Juan"
		,"San Luis - San Luis"
		,"Santa Fe - Santa Fe"
		,"La Pampa - Santa Rosa"
		,"Santiago del Estero - Santiago del Estero"
		,"Tierra del Fuego, Antártida e Islas del Atlántico Sur - Ushuaia"
		,"Río Negro - Viedma"
	};
	/* public static int[][] DISTANCIAS ={
		
		{0,646,792,933,53,986,985,989,375,834,1127,794,2082,979,1080,1334,1282,1005,749,393,579,939,2373,799}
		,{646,0,677,824,698,340,466,907,348,919,1321,669,2281,362,517,809,745,412,293,330,577,401,2618,1047}
		,{993.3,855.7,0.0,2168.3,867.6,20.2,556.7,175.9,850.0,1329.5,1.008,1432.6,339.9,1.856,1728.9,824.5,1350.6,1177.7,3304.1,546.0,622.8,3875.8,785.1}
		,{1361,1898.2,2168.3,0.0,1451.5,2262.7,1662.1,2334.8,2357.5,840.8,1820.9,1572.0,2346.4,744.0,506.2,2332.0,1666.9,1344.9,1161.8,1630.5,1895.0,1733.5,2017.9}
		,{753.4,439.8,868.4,1450.2,0.0,888.3,401.6,1034.8,899.1,610.4,449.4,655.2,1152.5,1138.0,1199.0,873.6,577.9,401.6,2586.1,370.0,436.6,3157.8,}
	}; */
	public static final Integer[][] DISTANCIAS_RAW ={
			/*23	22	21... */
		/*1*/{799,2373,939,579,393,749,1005,1282,1334,1080,979,2082,794,1127,834,375,989,985,986,53,933,792,646}
		/*2*/,{1047,2618,401,577,330,293,412,745,809,517,362,2281,669,1321,919,348,907,466,340,698,824,677}
		/*3*/,{1527,3131,535,1136,498,969,1039,719,742,633,691,2819,13,1845,291,500,1534,1131,814,830,157}
		,{1681,3284,629,1293,654,1117,1169,741,750,703,793,2974,161,1999,263,656,1690,1269,927,968}
		,{789,2350,991,602,444,795,1053,1333,1385,1132,1030,2064,833,1116,857,427,1005,1029,1038}
		,{1311,2821,311,834,640,435,283,533,600,330,149,2473,802,1548,1098,659,1063,427}
		,{1019,2435,713,586,775,235,152,957,1023,756,569,2081,1121,1201,1384,790,676}
		,{479,1762,1286,422,1049,643,824,1591,1658,1370,1182,1410,1529,543,1709,1053}
		,{1030,2635,566,642,19,574,757,906,959,707,622,2320,498,1345,658}
		,{1624,3207,827,1293,664,1200,1306,992,1007,924,980,2914,305,1951}
		,{327,1300,1721,745,1349,1113,1340,2054,2120,1827,1647,975,1843}
		,{1526,3130,523,1132,495,961,1029,706,729,620,678,2818}
		,{1294,359,2677,1712,2325,2046,2231,2997,3063,2773,2587}
		,{1391,2931,166,915,602,540,430,410,477,189}
		,{1562,3116,141,1088,689,727,612,228,293}
		,{1855,3408,414,1382,942,1017,874,67}
		,{1790,3341,353,1316,889,950,808}
		,{1141,2585,583,686,740,284}
		,{882,2392,643,412,560}
		,{1035,2641,547,641}
		,{477,2044,977}
		,{1446,3016}
		,{1605}
	};
	public static HashMap<Set<Integer>,Integer> distancias = new HashMap<>();

	public int obtenerDistanciaEntre(int a,int b){
		/* if(a==b)
			return 0; */
		HashSet<Integer> set = new HashSet<>();
		set.add(a);
		set.add(b);
		return distancias.get(set);
	}

  // Función que sirve para evaluar el desempeño de cada individuo.
	private double objetivo(Individuo individuo){
		int recorrido=0;
		int ultimoDestino=individuo.cromosoma.get(individuo.cromosoma.size()-1);
		for(int provincia: individuo.cromosoma){
			recorrido+=obtenerDistanciaEntre(ultimoDestino,provincia);
			ultimoDestino=provincia;
		}
		// El inverso de la cantidad de miles de kilómetros.
		// Los recorridos tienen probabilidades relativas proporcionales.
		// (Si un recorrido es el doble de largo que el otro, tiene la mitad del fitness.)
		return 1000/recorrido;
	}
	
	public static void main(String[] args) throws Exception {
		int n=PROVINCIAS.length-1;
		for(int i=0;i<n;i++){
			for(int j=0,to=n-i;j<to;j++){
				Set<Integer> s = new HashSet<>();
				s.add(i);
				s.add(n-j);
				distancias.put(s,DISTANCIAS_RAW[i][j]);
			}
		}

		// launch(args);
		new App();
	}
	
	// @Override
	// public void start(Stage primaryStage) throws Exception {
	public App(){

		// TODO remove
		// System.out.println(obtenerDistanciaEntre(0,1));
		// System.out.println(obtenerDistanciaEntre(21,22));
		// System.out.println(obtenerDistanciaEntre(23,22));

		// System.exit(0);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JFXPanel jfxPanel = new JFXPanel();
		add(jfxPanel);
		
		Platform.runLater(() -> {
			WebView webView = new WebView();
			jfxPanel.setScene(new Scene(webView));
			webEngine = webView.getEngine();
			webEngine.load(getClass().getResource("res/index.html").toString());
			
			App self=this;
			webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
        @Override
        public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
					if (newState == State.SUCCEEDED) {
						JSObject window = (JSObject) webEngine.executeScript("window");
						window.setMember("app", self);
					}
				}
      });
		});

		// Preparación de la ventana.
		setIconImage(new ImageIcon(getClass().getResource("/res/Logo AG.png")).getImage());
		setTitle("Algoritmos Genéticos - TP3");
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width*3/4,screenSize.height*3/4);
		setLocation(screenSize.width/8, screenSize.height/8);
		setVisible(true);
	}
	
  // Función para iniciar la simulación.
	private void reiniciar(){
		sumatoriaPuntuaciones=0;
		// Generación de la primera población aleatoria.
		poblacionActual=new Individuo[tamañoPoblacion];
		for(int i=0;i<tamañoPoblacion;i++){
			int[] cromosoma=new int[App.PROVINCIAS.length];
			
			for(int j=0;j<App.PROVINCIAS.length;j++)
				cromosoma[j]=j;

			Collections.shuffle(Arrays.asList(cromosoma));
				
			Individuo newIndividuo=new Individuo(cromosoma);
			
			poblacionActual[i]=newIndividuo;
			
			double fitness=objetivo(poblacionActual[i]);
			poblacionActual[i].valorFuncionObjetivo=fitness;
			sumatoriaPuntuaciones+=fitness;
		}
		
		ordenarPoblacion(poblacionActual);
		// calcularMinMaxPro();
	}
	
  // Función para realizar cada generación de la simulación.
	private void nuevaGeneracion(){
		
		vectorFitness=new double[tamañoPoblacion];
		for(int j=0;j<tamañoPoblacion;j++)
			vectorFitness[j]=poblacionActual[j].valorFuncionObjetivo/sumatoriaPuntuaciones;
		
		sumatoriaPuntuaciones=0;

		//Individuo[] nuevaPoblacion=nuevaGeneracionPorRuleta();
		
		int cantidadPares=tamañoPoblacion/2;
		Individuo[] nuevaPoblacion=new Individuo[tamañoPoblacion];

		if(elitismo){
			// // Reemplazamos el último par por los mejores individuos. (Recuerde que las poblaciones están ordenadas.)
			// Guardamos el 20% de la población total, de ser impar se le resta en 1. Y se pasa a la siguiente generacion
			int tamañoReducido=(int)Math.floor(tamañoPoblacion*.2);

			if (tamañoReducido%2 == 1)
				tamañoReducido--;
			
			for (int i = 0; i < tamañoReducido; i++){
				cantidadPares -= tamañoReducido%2;
				nuevaPoblacion[tamañoPoblacion-i-1]=poblacionActual[i].crearClon();
				sumatoriaPuntuaciones+=poblacionActual[i].valorFuncionObjetivo;
			}
		}

		for(int j=0;j<cantidadPares;j++){
			
			// Aplicación de selección.
			int j1=j*2,j2=j1+1;
			Individuo individuo1=poblacionActual[elegirIndicePorRuleta(vectorFitness)].crearClon()
				,individuo2=poblacionActual[elegirIndicePorRuleta(vectorFitness)].crearClon();
	
			// Aplicación de crossover. (Se encarga la clase Individuo)
			if(individuo1.equals(individuo2) || Math.random()>.75){
				nuevaPoblacion[j1]=individuo1;
				nuevaPoblacion[j2]=individuo2;
			}else{
				Individuo[] hijos=individuo1.crossover(individuo2);
				nuevaPoblacion[j1]=hijos[0];
				nuevaPoblacion[j2]=hijos[1];
			}
			
			// Aplicación de mutación.
			nuevaPoblacion[j1].aplicarMutacion();
			nuevaPoblacion[j2].aplicarMutacion();
			
			// Cálculo del valor objetivo. (Ver método objetivo.)
			double valorObjetivo1=objetivo(nuevaPoblacion[j1])
				,valorObjetivo2=objetivo(nuevaPoblacion[j2]);
			
			nuevaPoblacion[j1].valorFuncionObjetivo=valorObjetivo1;
			nuevaPoblacion[j2].valorFuncionObjetivo=valorObjetivo2;
			
			// Sumatoria de todos los resultados de la función objetivo de la generación (sirve para el promedio y la próxima selección).
			sumatoriaPuntuaciones+=valorObjetivo1+valorObjetivo2;
		}

		ordenarPoblacion(nuevaPoblacion);
		poblacionActual=nuevaPoblacion;
		
		// calcularMinMaxPro();
	}

	private int elegirIndicePorRuleta(double[] vectorFitness, int evitar) {
		// En este método evitamos uno de los índices.
		double totalSumaVector=0;
		for(int i=0,til=vectorFitness.length;i<til;i++){
			if(i==evitar)
				continue;
			totalSumaVector+=vectorFitness[i];
		}
		totalSumaVector=Math.random()*totalSumaVector;

		vectorFitness[evitar]=vectorFitness[vectorFitness.length-1];
		return elegirIndicePorRuleta(Arrays.copyOf(vectorFitness, vectorFitness.length-1),totalSumaVector);
	}
	
	private int elegirIndicePorRuleta(double[] vectorFitness){
		return elegirIndicePorRuleta(vectorFitness, 1.0);
	}

	private int elegirIndicePorRuleta(double[] vectorFitness, double totalSumaVector){
		double acc=0,selector=Math.random()*totalSumaVector;
		// No hay forma de que la probabilidad (selector) sea mayor a 1, y la suma (acc) va a llegar a 1 en algun momento.
		// Por lo que este for va a en algún momento terminar con un elegido.
		for(int l=0;l<vectorFitness.length;l++){
			acc+=vectorFitness[l];
			if(acc>selector)
				return l;
		}
		// Aún así, a veces por división de punto flotante, la suma no es exactamente igual a 1 y el número aleatorio puede entrar en ese margen de error.
		// Por lo que en ese caso, elegimos el último.
		// Técnicamente le estamos asignando el resto de la probabilidad a un cromosoma aleatorio, pero es una probabilidad insignificante.
		// Esto ocurrió en nuestras simulaciones como máximo en un individuo de cada 600 generaciones, pudiendo no aparecer por miles de generaciones.
		return vectorFitness.length-1;
	}

	// Ordenamos la población para facilitar el cálculo del máximo, mínimo y promedio.
	private void ordenarPoblacion(Individuo[] poblacion){
		Arrays.sort(poblacion);
	}

  // Función que calcula Mínimo, Máximo y Promedio de cada generación.
  // Solo calcula el promedio, ya que los individuos se encuentran ordenados en la población.
	// private void calcularMinMaxPro(){
	// 	maximoIndividuo=poblacionActual[0];
	// 	minimoIndividuo=poblacionActual[tamañoPoblacion-1];
	// 	// promedio=sumatoriaPuntuaciones/tamañoPoblacion;
	// 	// if(maximoIndividuo>)
	// }

	// API para el frontend.

	private void mandarGeneracionActual(){
		// TODO
		double mediano=tamañoPoblacion%2==0?
			(poblacionActual[tamañoPoblacion/2].valorFuncionObjetivo+poblacionActual[tamañoPoblacion/2+1].valorFuncionObjetivo)/2
			:poblacionActual[tamañoPoblacion/2].valorFuncionObjetivo;
			// TODO check si mejor y peor son correctos
		StringBuilder JSCommand=new StringBuilder("proximaGeneracion({peor:"+poblacionActual[tamañoPoblacion-1].valorFuncionObjetivo+",med:"+mediano+",mejor:"+poblacionActual[0].valorFuncionObjetivo+",individuos:[");
		
		String[] poblacionAsJSON=new String[tamañoPoblacion];
		for (int i = 0; i < tamañoPoblacion; i++)
			poblacionAsJSON[i]=poblacionActual[i].toJSONObject();
		
		JSCommand.append(String.join(",",poblacionAsJSON)+"]});");
		webEngine.executeScript(JSCommand.toString());
	}

	public void iniciarSimulacion(int cantidadIndividuos, int tipoSeleccion,boolean conElitismo){
		// TODO
		/*tamañoPoblacion=cantidadIndividuos%2==0?
			cantidadIndividuos
			:cantidadIndividuos-1;
		seleccionPorRango=tipoSeleccion==2;
		elitismo=conElitismo;
		reiniciar();

		mandarGeneracionActual();*/
	}

	public void siguienteGeneracion(){
		nuevaGeneracion();
		mandarGeneracionActual();
	}

}