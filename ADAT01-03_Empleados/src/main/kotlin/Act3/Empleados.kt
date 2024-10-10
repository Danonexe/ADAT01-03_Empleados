package Act3

import org.w3c.dom.*
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun EmpleadosLeer(ficheroEmpresa: Path): MutableMap<String, Map<String, String>> {
    val datos: MutableMap<String, Map<String, String>> = mutableMapOf()

    val br = BufferedReader(FileReader(ficheroEmpresa.toFile()))
    br.use { reader ->

        reader.readLine()

        reader.forEachLine { linea ->

            val columnas = linea.split(",")

            val ID = columnas[0]
            val Apellido = columnas[1]
            val Departamento = columnas[2]
            val Salario = columnas[3]

            // Crear un mapa con los datos del empresario
            val datosEmpresario = mapOf(
                "ID" to ID,
                "Apellido" to Apellido,
                "Departamento" to Departamento,
                "Salario" to Salario
            )

            datos[ID] = datosEmpresario
        }
    }

    return datos
}

fun EscribirXML(datos:MutableMap<String, Map<String, String>>){
    //1º Instanciar la clase document builderFactory//
    val factory : DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    //instaciar la clase document builder//
    val builder : DocumentBuilder = factory.newDocumentBuilder()
    //Opcional(Especificar funciones) contiene los metodos para crear un Document
    val imp: DOMImplementation = builder.getDOMImplementation()
    //2º Procedemos a crear un Document vacio (namespaceURI qualifiedName,doctype)
    val document: Document = imp.createDocument(null,"empleados",null)
    //por cada dato creamos un empleado
    for ((ID, datos) in datos) {
        val Empleado: Element = document.createElement("empleado")
        //atributo
        Empleado.setAttribute("id", datos["ID"])
        document.documentElement.appendChild(Empleado)
        val Apellido1 : Element =document.createElement("apellido")
        val Departamento1: Element =document.createElement("departamento")
        val Salario1: Element =document.createElement("salario")
        val textoApellido1: Text =document.createTextNode(datos["Apellido"])
        val textoDepartamento1: Text =document.createTextNode(datos["Departamento"])
        val textoSalario1: Text =document.createTextNode(datos["Salario"])
        //Unimos
        Apellido1.appendChild(textoApellido1)
        Departamento1.appendChild(textoDepartamento1)
        Salario1.appendChild(textoSalario1)
        Empleado.appendChild(Apellido1)
        Empleado.appendChild(Departamento1)
        Empleado.appendChild(Salario1)
    }
    //escribir en el archivo
    val source: Source = DOMSource(document)
    val result: StreamResult = StreamResult(Path.of("src/main/resources/empleadosXML.xml").toFile())
    val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
    //Para indentarXML correctamente
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    //Por ultimo realizamos la transformación
    transformer.transform(source, result)
}

fun MostrarEmpleados(ficheroXML: Path){
    val dbf= DocumentBuilderFactory.newInstance()
    val db= dbf.newDocumentBuilder()

    val document = db.parse(ficheroXML.toFile())

    val root: Element = document.documentElement
    root.normalize()

    val listaNodos: NodeList = root.getElementsByTagName("empleado")

    for (i:Int in 0 until listaNodos.length) {
        val nodo: Node = listaNodos.item(i)
        if(nodo.nodeType == Node.ELEMENT_NODE) {
            val nodoElemento: Element = nodo as Element

            val elementoApellido: NodeList = nodoElemento.getElementsByTagName("apellido")
            val elementoDepartamento: NodeList = nodoElemento.getElementsByTagName("departamento")
            val elementoSalario: NodeList = nodoElemento.getElementsByTagName("salario")

            val textoContenidoApellido: String = elementoApellido.item(0).textContent
            val textoContenidoDepartamento: String = elementoDepartamento.item(0).textContent
            val textoContenidoSalario: Double = elementoSalario.item(0).textContent.toDouble()

            //imprimo
            println("ID ${i + 1}, Apellido: ${textoContenidoApellido}, Departamento: ${textoContenidoDepartamento}, Salario: ${textoContenidoSalario}")
        }
    }
}

fun EditarEmpleados(id:Int, salario:Double, datos:MutableMap<String, Map<String, String>>) {
    val empleado = datos[id.toString()]

    if (empleado != null) {
        val empleadoAlterado = empleado.toMutableMap()
        empleadoAlterado["Salario"] = salario.toString()
        datos[id.toString()] = empleadoAlterado
        EscribirXML(datos)
    }
}