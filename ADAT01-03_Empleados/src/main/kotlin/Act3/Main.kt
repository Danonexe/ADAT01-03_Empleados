package Act3

import java.nio.file.Path

fun main(){
    val raiz = Path.of("src")
    val ficheroArchivo = raiz.resolve("main").resolve("resources").resolve("empleados.csv")
    //map empleados
    val mapEmpleados = EmpleadosLeer(ficheroArchivo)
    //crear el XML
    EscribirXML(mapEmpleados)
    val ficheroXML = raiz.resolve("main").resolve("resources").resolve("empleadosXML.xml")
    EditarEmpleados(1,777.0,mapEmpleados)
    MostrarEmpleados(ficheroXML)
}