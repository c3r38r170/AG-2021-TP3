//mock API
if(!app)
	var app={
		siguienteGeneracion:function(){
			proximaGeneracion({
				individuos:new Array(10).fill().map((el,i)=>({
					fitness:0.7
					,recorrido:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0,16,17,18,19,20,21,22,23]
					,longitud:5419669
				}))
				,min:1000
				,med:5000
				,max:40000
			});
		}
		,iniciarSimulacion:function(){
			proximaGeneracion({
				individuos:new Array(10).fill().map((el,i)=>({
					fitness:0.7
					,recorrido:[23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,1,2,3,4,5,0]
					,longitud:5419669
				}))
				,min:1000
				,med:5000
				,max:40000
			});
		}
	};

//Carga Google Charts
var grafico={chart:null,data:null};
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(()=>{
	grafico={
		chart:new google.visualization.LineChart(document.getElementById('generacionesChart'))
		,data:crearData()
	};
});
function crearData(){
	let data = new google.visualization.DataTable();
	data.addColumn('number', 'Generacion');
	data.addColumn('number', 'Máximo');
	data.addColumn('number', 'Promedio');
	data.addColumn('number', 'Mínimo');
	return data;
}
function actualizarGrafico(){
	grafico.chart.draw(
		grafico.data
		,{
			title: 'Fitness mínima, máxima y promedio de las generaciones.',
			subtitle: '20',
	
			hAxis: {
				title: 'Generación'
			},
			explorer: {
				actions: ['dragToZoom', 'rightClickToReset'],
				axis: 'horizontal'
			},
			vAxis: {
				title: 'Fitness'
				,minValue:0
				,maxValue:1.1
			}
			,
			height: 450,
			width: 600
			,pointSize:5
		}
	);
}

//important methods
var tableData=[];
var gEt=id=>document.getElementById(id);
var qS=selector=>document.querySelectorAll(selector);
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
	,min:
	,pro:
	,max:
}
*/
function proximaGeneracion(generacion){
	// Actualizar gráfico.
	generaciones.push(generacion);
	grafico.data.addRow([
		generaciones.length
		,generacion.max
		,generacion.pro
		,generacion.min
	]);
	actualizarGrafico();

	// generaciones
	let generacionesTabla=gEt('generaciones');
	let nuevaGeneracion=document.createElement('DIV');
	nuevaGeneracion.classList.add('generaciones-n');

	nuevaGeneracion.innerHTML=`
<div class="generaciones-n-individuos">
	<span class="generaciones-n-individuos-header">Individuo</span>
	<span class="generaciones-n-individuos-header">Genoma</span>
	<span class="generaciones-n-individuos-header">Valor decimal</span>
	<span class="generaciones-n-individuos-header">Fitness</span>
	${generacion.individuos.reduce((acc,el,i)=>{
		acc+=[
			i+1
			,el.cromosoma
			,el.valorDecimal
			,el.fitness.toFixed(7)
		].reduce((ac,e)=>ac+`<span>${e}</span>`,'');
		return acc;
	},'')}
</div>
<div class="generaciones-n-resumen">
	<span>${generaciones.length}</span>
	<span>${generacion.min.toFixed(7)}</span>
	<span>${generacion.pro.toFixed(7)}</span>
	<span>${generacion.max.toFixed(7)}</span>
</div>
`;
	generacionesTabla.append(nuevaGeneracion);
}

//onload
addEventListener('DOMContentLoaded',()=>{
	$('table').bootstrapTable({
		data: tableData
	});

	$('#modalAjuste').modal('show');

	gEt('modal-iniciar').onclick=()=>{
		grafico.data=crearData();
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
		if(resumen)
			resumen.previousElementSibling.classList.toggle('generaciones-n-individuos-shown');
	}

	gEt('modal-rango').onclick=()=>{
		let elitismo=gEt('modal-elitismo');
		elitismo.checked=false;
		elitismo.disabled=true;
	}

	gEt('modal-ruleta').onclick=()=>{
		gEt('modal-elitismo').disabled=false;
	}
});