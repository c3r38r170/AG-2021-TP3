//mock API
if(!app)
	var app={
		siguienteGeneracion:function(){
			proximaGeneracion({
				individuos:new Array(10).fill().map((el,i)=>({
					recorrido:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0,16,17,18,19,20,21,22,23].sort(()=>Math.random()-.5)
					,longitud:Math.round(Math.random()*5419669)
				}))
				,peor:1000
				,med:5000
				,mejor:40000
			});
		}
		,iniciarSimulacion:function(){
			proximaGeneracion({
				individuos:new Array(10).fill().map((el,i)=>({
					fitness:0.7
					,recorrido:[23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,1,2,3,4,5,0].sort(()=>Math.random()-.5)
					,longitud:Math.random(Math.random()*5419669)
				}))
				,peor:1000
				,med:5000
				,mejor:40000
			});
		}
	};

var nombresProvincias=[
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
];

var coordenadas=[
	[-58.3712, -34.6083], //Bs.AS
	[-64.183, -31.417], // Cordoba
	[-58.8403, -27.4711], //Corrientes
	[-58.1781, -26.1775], //Formosa
	[-57.9544, -34.9214], //La plata
	[-66.8558, -29.4131], //La rioja
	[-68.8472, -32.8903], //Mendoza
	[-68.0744, -38.9516], //Neuquen
	[-60.5300, -31.7331], // Entre rios
	[-54.5333, -26.8666], // Misiones
	[-65.1000, -43.3000], //Chubut
	[-58.9866, -27.4513], //Chaco
	[-68.815, -48.8238], //Santa cruz
	[-65.7792, -28.4686], //Catamarca 
	[-65.2169, -26.8198], //Tucuman
	[-65.2945, -24.1894], //Jujuy 
	[-64.5, -24.55], //Salta
	[-68.5253, -31.5372], //San Juan
	[-66.35, -33.3], //San Luis
	[-60.6917, -31.6406], //Santa fe
	[-64.2999, -36.6333], //La Pampa
	[-64.2614, -27.7951], //Stgo. Del Estero
	[-68.3044, -54.8072], //Tierra del fuego
	[-62.9973, -40.8133] //Rio Negro
];

/*
TODO crear objeto mapa con mapa y layers
{
	mapa:Map
	,layers:{
		unica: Layer
		,mejors: Layer
		...
	}
} */
var layer;

// manipulación de mapas
function mostrarIndividuo(generacionID,individuoID){
	let individuo=generaciones[+generacionID].individuos[+individuoID];

	gEt('individuo-recorrido-valor').innerText=individuo.longitud;

	mapaIndividuo.removeLayer(layer);
	let recorrido =individuo.recorrido.map(el=>ol.proj.fromLonLat(coordenadas[el]));
	layer = new ol.layer.Vector({
		source: new ol.source.Vector({
			features: [
				new ol.Feature({
					geometry: new ol.geom.LineString([...recorrido,recorrido[0]]),
					name: "Line",
				}),
			],
		})
		,style:new ol.style.Style({
			fill: new ol.style.Fill({
					color: 'white'
			}),
			stroke: new ol.style.Stroke({
					color: '#319FD3',
					width: 1
			})
		})
	});
	mapaIndividuo.addLayer(layer);

}

// TODO
// cambiarRecorrido(mapa,layer)

//important methods
var gEt=id=>document.getElementById(id);
var qS=selector=>document.querySelectorAll(selector);
var mejorHastaAhora;
var generaciones=[];
/*formato de la generación:
{
	individuos:[
		{
			fitness:
			,cromosoma:
			,valorDecimal:
		}
	]
	,peor:
	,med:
	,mejor:
}
*/
function proximaGeneracion(generacion){
	// generaciones
	/* let tempIndividuos=[];
	for(let individuo of generacion.individuos){
		tempIndividuos.push(individuo);
		tempIndividuos[tempIndividuos.length-1]=.recorrido.map(el=>coordenadas[el]);
	} */
	// generacion.individuos=generacion.individuos.map(el=>coordenadas[el]);
	generaciones.push(generacion);

	if(!mejorHastaAhora || generacion.individuos[0].longitud<mejorHastaAhora.longitud)
		actualizarMejorHastaAhora(generacion.individuos[0]);

	let nuevaGeneracion=document.createElement('DIV');
	nuevaGeneracion.classList.add('generaciones-n');
	nuevaGeneracion.dataset.id=generaciones.length-1;
	nuevaGeneracion.innerHTML=`
<div class="generaciones-n-individuos">
	<span class="generaciones-n-individuos-header">Individuo</span>
	<span class="generaciones-n-individuos-header">Longitud del recorrido</span>
	${generacion.individuos.reduce((acc,el,i)=>acc+='<div class=generaciones-n-individuo>'+[
			i+1
			,el.longitud
		].reduce((ac,e)=>ac+`<span>${e}</span>`,'')+'</div>','')}
</div>
<div class="generaciones-n-resumen">
	<span>${generaciones.length}</span>
	<span>${generacion.peor}</span>
	<span>${generacion.med}</span>
	<span>${generacion.mejor}</span>
</div>
`;
	gEt('generaciones').append(nuevaGeneracion);
}

function actualizarMejorHastaAhora(individuo){
	mejorHastaAhora=individuo;
	
}

//onload
addEventListener('DOMContentLoaded',()=>{
	//TODO quitar, jQuery está prohibido
	/* $('table').bootstrapTable({
		data: tableData
	});

	$('#modalAjuste').modal('show'); */

	gEt('modal-iniciar').onclick=()=>{
		generaciones=[];
		for(let generacion of [...qS('.generaciones-n')])
			generacion.remove();
		app.iniciarSimulacion(
			gEt('modal-individuos').value
			,qS('[name="ruleta"]:checked')[0].value
			,gEt('modal-elitismo').checked
		);
	};

	gEt('controles-siguiente').onclick=()=>{
		for(let i=0,to=gEt('controles-pasos').value;i<to;i++)
			app.siguienteGeneracion();
	}

	gEt('generaciones').onclick=e=>{
		let t=e.target;
		let resumen=t.closest('.generaciones-n-resumen');
		if(resumen){
			resumen.previousElementSibling.classList.toggle('generaciones-n-individuos-shown');
		}else{
			let individuo=t.closest('.generaciones-n-individuo');
			if(individuo)
				mostrarIndividuo(t.closest('.generaciones-n').dataset.id,individuo.firstElementChild.innerText-1);
		}
	}

	// TODO esto es parte del modal
	gEt('modal-rango').onclick=()=>{
		let elitismo=gEt('modal-elitismo');
		elitismo.checked=false;
		elitismo.disabled=true;
	}

	gEt('modal-ruleta').onclick=()=>{
		gEt('modal-elitismo').disabled=false;
	}
});

// Pruebas de OpenLayers

function pruebaDeCoordenadas(){
	// var lines = new ol.geom.LineString(coordenadas)
	// .transform('EPSSG:3857', mapaGeneraciones.getView().getProjection());

var layer = new ol.layer.Vector({
  source: new ol.source.Vector({
    features: [
      // new ol.Feature({
      //   geometry: new ol.geom.LineString(coordenadas.map(el=>ol.proj.fromLonLat(el))).transform('EPSG:3857', mapaGeneraciones.getView().getProjection()),
      //   name: "Line",
      // }),
      new ol.Feature({
        geometry: new ol.geom.LineString(coordenadas.map(el=>ol.proj.fromLonLat(el))),
        name: "Line",
      }),
    ],
  })
	,style:new ol.style.Style({
    fill: new ol.style.Fill({
      	color: 'white'
    }),
    stroke: new ol.style.Stroke({
      	color: '#319FD3',
      	width: 1
    })
	})
});
mapaGeneraciones.addLayer(layer);
}


function pointMapTo(coords){
	coords=ol.proj.fromLonLat(coords);
	// mapaGeneraciones.setCoordinates(coords);
	mapaGeneraciones.getView().setCenter(coords);
}