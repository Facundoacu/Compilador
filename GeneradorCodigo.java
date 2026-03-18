
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class GeneradorCodigo {

    private  Stack<String> pila ;
    private ArrayList<String> polaca;
    private ArrayList<ArrayList<String>> funciones;
    private  StringBuilder codigoAssembler ;
    private boolean[]  registros;
    private boolean[] registrosDouble;
    private String[]   tipoRegistros;
    private int posicionPolaca = 0;
    private TablaSimbolos tablaSimbolos;
    private static ArrayList<Integer> posicionEtiqueta = new ArrayList<Integer>();
    private String ultimaComparacion = ""; //esto para guardar en la comparacion si es un salto condicional o incondicional etc
    private static String auxMem = "aux_mem_2bytes";
    private static int auxiliares = 0;
    private static boolean saltoEtiqueta = false;
    private static int cantImpresiones = 0;
    private int cant_if = 0;
    private String path;
    private String nombreDeFuncion = "";
    private int contDivision = 0;
    public ArrayList<String> errores;
    public GeneradorCodigo(ArrayList<String> codigoPolaca, TablaSimbolos tabla, ArrayList<ArrayList<String>> funciones, String path, ArrayList<String> erroresParser) throws IOException {
        this.path = path;
        polaca = codigoPolaca;
        pila = new Stack<>();
        codigoAssembler = new StringBuilder();
        registros = new boolean[4];
        registrosDouble = new boolean[7];
        tipoRegistros = new String[4];
        tablaSimbolos = tabla;
        this.funciones = new ArrayList<>(funciones);
        this.errores = new ArrayList<>(erroresParser);
    }
    public void setPosicionPolaca(Integer pos){
        posicionPolaca= pos;
    }
    public boolean hacerAccion(String caracter) {
        String auxiliar = caracter;
        if(caracter.matches("^RET@.*"))
            caracter = "RET";
        switch (caracter) {

            case "*":
                operar("IMUL");
                return false;
            case "/":
                operar("DIV");
                return false;
            case ":=":
                if (pila.size() > 1)
                    generarAsignacion();
                return false;
            case ">":
            case "<":
            case "<=":
            case ">=":
            case "!=":
                generarCondicion(caracter);
                return false;
            case "–":
            case "-":
                operar("SUB");
                return false;
            case "+":
                operar("ADD");
                return false;
            case "#BI":
                generarSalto("JMP");
                return  false;
            case "#BF": //Este caso no es necesario creo lo tomamos por la default?
                //generarSalto(ultimaComparacion);
                return false;
            case "TOD":
                generarConversion();
                return false;
            /*case "#BI GOTO":
                generarSaltoEtiqueta("JMP");
                return false;*/
            case "RET":
                asignarRetorno(auxiliar);
                return false;
            case "CALL":
                String nombre_funcion = pila.pop();
                String ret = nombre_funcion;
                // Buscar el índice del primer '@'
                int firstAt = nombre_funcion.indexOf('@');
                if (firstAt != -1) {
                    // Extraer la primera parte (hasta el primer '@') y el resto
                    String firstPart = nombre_funcion.substring(0, firstAt);
                    String remainingParts = nombre_funcion.substring(firstAt);
                    // Construir el nuevo string reordenado
                     ret = "RET" + remainingParts + "@" + firstPart;
                }
                codigoAssembler.append("CALL " + nombre_funcion + "\n");
               // System.out.println("nombre_ fun: " + nombre_funcion +  "RET@"+nombreDeFuncion);
                pila.push(ret);
                return false;
            case "OUTF":
                generarImpresion();
                return false;
            default:
                if (saltoEtiqueta){
                    generarEtiqueta();
                    posicionEtiqueta.remove(0);
                    saltoEtiqueta=false;
                }
                if(caracter.matches("^:[a-zA-Z0-9]+$")){
                    caracter = caracter.substring(1);
                    codigoAssembler.append(caracter + ":").append("\n");
                    return false;}
                else
                    return true; //cuando retorna true agrega a la pila
        }

    }

    public String generarCodigo() {
        // Define la estructura base del archivo assembler

        codigoAssembler.append(".386                  ; Especifica la arquitectura del procesador (386 o superior)\n");
        codigoAssembler.append(".model flat, stdcall  ; Modelo de memoria y convenciones de llamada\n");
        codigoAssembler.append("option casemap:none   ; Sensibilidad a mayúsculas/minúsculas\n\n");
        codigoAssembler.append("include\\masm32\\include\\masm32rt.inc").append("\n");
        codigoAssembler.append("includelib \\masm32\\lib\\kernel32.lib\n");
        codigoAssembler.append("includelib \\masm32\\lib\\user32.lib\n\n");
        codigoAssembler.append("includelib \\masm32\\lib\\masm32.lib\n\n");
        codigoAssembler.append("dll_dllcrt0 PROTO C" ).append("\n");
        codigoAssembler.append("printf PROTO C : VARARG").append("\n");
        codigoAssembler.append("\n");
        codigoAssembler.append(".data                 ; Segmento de datos\n\n");
        codigoAssembler.append("@varAuxMax dd ?\n");
        codigoAssembler.append("@varAuxMin dd ?\n");
        codigoAssembler.append("@varAuxMaxDouble DQ ?\n");
        codigoAssembler.append("@varAuxMinDouble DQ ?\n");
        codigoAssembler.append("@varAuxRangoDouble DQ ?\n");
        codigoAssembler.append("@varDivCero dd 00h \n");
        codigoAssembler.append(auxMem + " dw ? ; Variable de 2 bytes no inicializada").append("\n");
        generarDataSeccion();
        imprimirContenido();
        codigoAssembler.append("\n");
        codigoAssembler.append(".code                 ; Segmento de código\n");
        codigoAssembler.append("\n");
        //GENERAMOS EL CONTROL DEL RANGO PARA ENTEROS
        codigoAssembler.append("ControlarRangoEntero:"+ "\n");
        codigoAssembler.append("JO OverflowEntero"+ "\n");
        codigoAssembler.append("JC OverflowEntero"+ "\n");
        codigoAssembler.append("RET" + "\n");

        codigoAssembler.append("ControlarRangoEnterito:"+ "\n");
        codigoAssembler.append("; Comparar con el maximo" + "\n");
        codigoAssembler.append("CMP edx, @varAuxMax" + "\n");
        codigoAssembler.append("JG OverflowEntero" + "\n");
        codigoAssembler.append("; Comparar con el minimo" + "\n");
        codigoAssembler.append("CMP edx, @varAuxMin" + "\n");
        codigoAssembler.append("JL OverflowEntero" + "\n");
        codigoAssembler.append("RET" + "\n");
        codigoAssembler.append("\n");
        codigoAssembler.append("OverflowEntero:" + "\n");
        codigoAssembler.append("invoke StdOut, addr ERROR_RANGO" +"\n");
        //codigoAssembler.append("invoke MessageBox, NULL, addr ERROR_RANGO, addr ERROR_RANGO, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append("\n");
        //GENERAMOS EL CONTROL DEL RANGO PARA DOUBLE
        codigoAssembler.append("ControlarRangoDouble:" + "\n");
        codigoAssembler.append("; Comparar con el limite maximo" + "\n");
        codigoAssembler.append("FLD @varAuxRangoDouble" + "\n");
        codigoAssembler.append("FLD @varAuxMaxDouble" + "\n");
        codigoAssembler.append("FCOM" + "\n");
        codigoAssembler.append("FSTSW aux_mem_2bytes" + "\n");
        codigoAssembler.append("MOV AX, aux_mem_2bytes" + "\n");
        codigoAssembler.append("SAHF" + "\n");
        codigoAssembler.append("JB OverflowDouble" + "\n");
        codigoAssembler.append("; Comparar con el limite minimo" + "\n");
        codigoAssembler.append("FLD @varAuxRangoDouble" + "\n");
        codigoAssembler.append("FLD @varAuxMinDouble" + "\n");
        codigoAssembler.append("FCOM" + "\n");
        codigoAssembler.append("FSTSW aux_mem_2bytes" + "\n");
        codigoAssembler.append("MOV AX, aux_mem_2bytes" + "\n");
        codigoAssembler.append("SAHF" + "\n");
        codigoAssembler.append("JA OverflowDouble" + "\n");
        codigoAssembler.append("; Retornar si está dentro del rango" + "\n");
        codigoAssembler.append("FINIT"+ "\n");
        codigoAssembler.append("FLD @varAuxRangoDouble" + "\n");
        codigoAssembler.append("RET" + "\n");
        codigoAssembler.append("\n");
        codigoAssembler.append("OverflowDouble: " + "\n");
        codigoAssembler.append("invoke StdOut, addr ERROR_RANGO" +"\n");
        //codigoAssembler.append("invoke MessageBox, NULL, addr ERROR_RANGO, addr ERROR_RANGO, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        //codigoAssembler.append("DivisionPorCero: " +"\n");
        //codigoAssembler.append("invoke MessageBox, NULL, addr ERROR_DIVISION_POR_CERO, addr ERROR_DIVISION_POR_CERO, MB_OK\n");
        //codigoAssembler.append("invoke ExitProcess, 0\n");
        //CARGAMOS FUNCIONES
        Integer polacaActual =0;
        for (int i =0; i < funciones.size(); i++) {
            ArrayList<String> funcion = funciones.get(i);
            posicionPolaca = 0;
            polacaActual=0;
            //cargamos el nombre de la funcion como etiqueta
            //System.out.println("Nombre de la funcion:" + funcion.get(0));
            nombreDeFuncion= funcion.get(0);
            codigoAssembler.append(funcion.get(0) + ":\n");
            codigoAssembler.append("push ebp ; Save the old base pointer value").append("\n");
            codigoAssembler.append("mov ebp, esp ; Set the new base pointer value").append("\n");
            codigoAssembler.append("sub esp, 4 ; Make room for one 4-byte local variable").append("\n");
            codigoAssembler.append("push edi ; Save the values of registers that the function").append("\n");
            codigoAssembler.append("push esi ; will modify. This function uses EDI and ESI)").append("\n");
            for (int j =1; j < funcion.size(); j++) {
                if (funcion.size() <= j + 1) {
                    if (funcion.get(j).matches("^[a-zA-Z0-9]+@$") &&
                            !funcion.get(j).equals("#BI") &&
                            posicionEtiqueta.isEmpty()) {
                        posicionEtiqueta.add(j);
                        saltoEtiqueta = true;
                    }
                }
                try {
                    //@Divir@Sumar@Main
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (funcion.size() > j + 1) {
                    if (funcion.get(j).matches("^[a-zA-Z0-9]+@$") &&
                            !funcion.get(j + 1).equals("#BI")) {
                        posicionEtiqueta.add(j);
                    }

                    if (funcion.get(j + 1).equals("#BI") &&
                            !posicionEtiqueta.isEmpty()) {
                        saltoEtiqueta = true;
                    }
                }

                if (hacerAccion(funcion.get(j)))
                    pila.push(funcion.get(j));
                posicionPolaca++;
                polacaActual++;
            }
        }

        codigoAssembler.append("start:\n");
        codigoAssembler.append("FINIT \n");
        setPosicionPolaca(posicionPolaca-polacaActual); // chequear pero creo que se desfaza con los datos de la ultima funcion no hay
        // que resetear pero si rrestarle para q no se desface
        // Agrega cada instrucción a partir del segmento .code
        for (int k = 0; k < polaca.size(); k++) {
            if (polaca.size() <=  k+1) {
                if (polaca.get(k).matches(("^[a-zA-Z0-9]+@$")) && (!polaca.get(k).equals("#BI")) && (posicionEtiqueta.isEmpty())) {
                    posicionEtiqueta.add(k);
                    saltoEtiqueta = true;
                }
            }
            if (polaca.size() > k+1){
                if (polaca.get(k).matches(("^[a-zA-Z0-9]+@$")) && (!polaca.get(k + 1).equals("#BI"))) {

                    posicionEtiqueta.add(k);
                }

                if (polaca.get(k + 1).equals("#BI") && (!posicionEtiqueta.isEmpty())) {
                    //posicionPolaca= posicionEtiqueta.get(0);
                    saltoEtiqueta = true;
                }

            }

            if(hacerAccion(polaca.get(k)))
                pila.push(polaca.get(k));
            posicionPolaca++;
        }

        // Finaliza el archivo assembler con la directiva END
        codigoAssembler.append("\nEND start");
        if (errores.isEmpty()) {
            String sinExtension = path.contains(".") ? path.substring(0, path.lastIndexOf(".")) : path;
            String archivoRuta = sinExtension + ".asm";

            // Escribir el contenido en el archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoRuta))) {
                writer.write(codigoAssembler.toString());
                //System.out.println("Archivo ASM generado correctamente en: " + archivoRuta);
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
            }
            if (errores.isEmpty())
                return (archivoRuta);
            else
                return null;
        }
        else return null;

    }

    public void generarDataSeccion() {
        codigoAssembler.append("ERROR_DIVISION_POR_CERO DB \"ERROR: Se intento dividir por cero\", 10, 0\n\n");
        codigoAssembler.append("ERROR_RANGO DB \"ERROR: Fuera de rango\", 10, 0\n\n");

        for (Map.Entry<String , DatosTablaSimbolos> entrada : tablaSimbolos.tabla.entrySet()) {
            String nombre = entrada.getKey();
            String tipo = entrada.getValue().getTipo();
            String estructura = entrada.getValue().getEstructura();
            String uso = entrada.getValue().getUso();
            if(uso == null || !uso.equals("Nombre de tipo")) {
                if (!nombre.matches("^[+-]?\\d+$") && !nombre.matches("^-?0[xX][0-9a-fA-F]+$")) {
                    if (!nombre.matches("^[+-–]?\\d+\\.\\d+([dD][+-–]?\\d+)?$")) {
                        if ("LONGINT".equals(tipo) || (estructura != null && estructura.equals("LONGINT"))) {
                            codigoAssembler.append("_" + nombre).append(" dd ?\n"); // Enteros largos (32 bits)
                        } else if ("DOUBLE".equals(tipo) || (estructura != null && estructura.equals("DOUBLE"))) {
                            codigoAssembler.append("_" + nombre).append(" DQ ?\n"); // Punto flotante (32 bits)
                        }
                    } else {
                        String nombreVar = nombre.replace(".", "@");
                        nombreVar = nombreVar.replace("+", "");
                        nombreVar = nombreVar.replace("-", "m");
                        codigoAssembler.append("DOUBLE" + nombreVar + " DQ " + nombre).append("\n");
                    }
                }
            }
        }
    }
    private void generarImpresion() {
        //System.out.println("IMPRESION");
        String imprimir = pila.pop();
        //System.out.println("imprmir: " + imprimir);
        String firstChar = imprimir.substring(0, 1);
        //IMPRIMIR CADENAS MULTILINEA
        if (firstChar.equals("{")) {
            String outf = "OUTF" + cantImpresiones;
            cantImpresiones++;
            imprimir = imprimir.substring(1, imprimir.length() - 1); /* Elimina los "{}"*/
            String nuevaLinea = outf + " DB '" + imprimir + "', 10, 0";
            int inicioData = codigoAssembler.indexOf(".data");
            int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
            codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
            codigoAssembler.append("invoke printf, addr " + outf +"\n");
        } else {//INT/HEX  - varInt - regInt - varDouble - RegDouble - 2.0 + 2.0

            if (imprimir.matches("^(eax|ebx|ecx|edx)$")) //IMPRIMIR REGISTRO DE ENTERO
                codigoAssembler.append("invoke printf, cfm$(\"%d\\n\"), " + imprimir).append("\n");
            else {
                DatosTablaSimbolos dato = tablaSimbolos.getDato(imprimir);
                    if ((dato != null) && dato.getTipo().equals("LONGINT")) {
                        //IMPRIMIR CONSTANTE ENTERA O VARIABLE ENTERA
                        if(dato.getUso()!= null) //VARIABLE
                            codigoAssembler.append("invoke printf, cfm$(\"%d\\n\"), _" + imprimir).append("\n");
                        else //CONSTANTE
                            codigoAssembler.append("invoke printf, cfm$(\"%d\\n\"), " + imprimir).append("\n");
                    }
                    else{ //IMPRIMIR DOUBLE
                        if (dato != null) {
                            //Variable double sola
                            if (dato.getTipo().equals("DOUBLE") && dato.getUso() != null)
                                codigoAssembler.append("invoke printf, cfm$(\"%.20Lf\\n\"), _" + imprimir).append("\n");
                            else {
                            //Inmediato double
                                String nombreVar = imprimir.replace(".", "@");
                                nombreVar = nombreVar.replace("+", "");
                                nombreVar = nombreVar.replace("-", "m");
                                codigoAssembler.append("invoke printf, cfm$(\"%.20Lf\\n\")," + "DOUBLE" +nombreVar).append("\n");
                            }
                        } else { //Registro double
                            String varAux = generarAux();
                             //creo una variable auxiliar para el caso de una expresion
                            codigoAssembler.append("FSTP " + varAux).append("\n"); //guardo el tope de la pila en la variable
                            codigoAssembler.append("invoke printf, cfm$(\"%.20Lf\\n\"), " + varAux).append("\n");
                        }
                    }
            }
        }
    }
    /*private void  generarSaltoEtiqueta(String salto){
        String op1= pila.pop();
        //System.out.println("op1: " + op1);
        if(!op1.equals("PENDIENTE")){
            //System.out.println("ME METI con: " + op1);
            codigoAssembler.append(salto + " L" + polaca.get(posicionPolaca - 2)).append("\n");
        }
        pila.push(op1);
    }*/
    private void generarEtiqueta(){
        codigoAssembler.append(polaca.get(posicionEtiqueta.get(0)) + ":").append("\n");
    }
    private String ocuparRegistroDouble (){
        boolean encontrado = false;
        int i = 0;
        while(!encontrado){
            if(!registrosDouble[i]){
                registrosDouble[i] = true;
                encontrado = true;
                String aux = "st" + "(" + i + ")";
                return aux;
            }
            i++;
        }
        return null;
    }
    private String ocuparRegistro(boolean suma){

        if(!registros[2] && suma){
            registros[2] = true;
            return "ecx";
        }
        if(!registros[1] && suma){
            registros[1] = true;
            return "ebx";
        }
        if(!registros[0]){
            registros[0] = true;
            return "eax";
        }
        if(!registros[3]){
            registros[3] = true;
            return "edx";
        }
        return null;
    }

    private  void generarCondicion(String operador) {
        //System.out.println("operador en condicion: " + operador);
        String op2 = pila.pop();
        String op1 = pila.pop();
        //System.out.println("op1: " + op1);
        //System.out.println("op2: " + op2);
        boolean op1reg = false;
        boolean op2reg = false;
        boolean op1regDouble = false;
        boolean op2regDouble = false;
        boolean varAux1 = false;
        boolean varAux2 = false;
        boolean asignacionDouble = false;
        boolean asignacionDouble2 = false;
        String tipoOp1 = "";
        String tipoOp2 = "";
        //CONTROLAMOS SI SON REGISTROS st
        if (op1.matches("st\\([0-7]\\)"))
            op1regDouble = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2regDouble = true;
        if (op1.matches("^@aux\\d+$"))
            varAux1 = true;
        if (op2.matches("^@aux\\d+$"))
            varAux2 = true;
        if (op1.matches("^(eax|ebx|ecx|edx)$"))
            op1reg = true;
        if (op2.matches("^(eax|ebx|ecx|edx)$"))
            op2reg = true;
        if (op1regDouble) {
            asignacionDouble = true;
        } else if (varAux1) {
            asignacionDouble = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op1reg){
                tipoOp1 = tablaSimbolos.getDato(op1).getTipo();
                String estrucOp1 = tablaSimbolos.getDato(op1).getEstructura();
                if (tipoOp1.equals("DOUBLE") || (estrucOp1 != null && estrucOp1.equals("DOUBLE")))
                asignacionDouble = true;}
        }

        if (op2regDouble) {
            asignacionDouble2 = true;
        } else if (varAux2) {
            asignacionDouble2 = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op2reg){
                tipoOp2 = tablaSimbolos.getDato(op2).getTipo();
                String estrucOp2 = tablaSimbolos.getDato(op2).getEstructura();
                if (tipoOp2.equals("DOUBLE") || (estrucOp2 != null && estrucOp2.equals("DOUBLE")))
                asignacionDouble2 = true;}
        }
        if (asignacionDouble && asignacionDouble2) {
            pila.push(op1);
            pila.push(op2);
            generarCondicionDouble(operador);
        } else {
            if (asignacionDouble || asignacionDouble2) {
                errores.add("ERROR: los tipos de la comparacion son invalidos");
                codigoAssembler.append("invoke ExitProcess, 0\n");
            } else {
                if (op1reg && op2reg) //SON DOS REGISTROS
                    codigoAssembler.append("CMP " + op1 + ", " + op2).append("\n");
                if (op1reg && !op2reg) {
                    String tipo = tablaSimbolos.getDato(op2).getTipo();

                    if (op2.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if (op2.startsWith("-")) {
                            op2 = op2.substring(3);
                            op2 = "-0" + op2 + "h";
                        } else {
                            op2 = op2.substring(2);
                            op2 = "0" + op2 + "h";

                        }
                    }

                    if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                        op2 = "_" + op2;
                    codigoAssembler.append("CMP " + op1 + ", " + op2).append("\n");
                }
                if (!op1reg && op2reg) {
                    String tipo = tablaSimbolos.getDato(op1).getTipo();
                    if (op1.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {
                        if(op1.startsWith("-")) {
                            op1 = op1.substring(3);
                            op1 = "-0" + op1 + "h";
                        } else {
                            op1 = op1.substring(2);
                            op1 = "0" + op1 + "h";
                        }
                    }
                    if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                        op1 = "_" + op1;
                    codigoAssembler.append("CMP " + op1 + ", " + op2).append("\n");
                }
                if (!op1reg && !op2reg) {
                    String tipo2 = tablaSimbolos.getDato(op2).getTipo();
                    String tipo = tablaSimbolos.getDato(op1).getTipo();

                    //CONVERTIMOS DE HEXA A ENTERO
                    if (op1.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if(op1.startsWith("-")) {
                            op1 = op1.substring(3);
                            op1 = "-0" + op1 + "h";
                        } else {
                            op1 = op1.substring(2);
                            op1 = "0" + op1 + "h";
                        }
                    }
                    if (op2.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if(op2.startsWith("-")) {
                            op2 = op2.substring(3);
                            op2 = "-0" + op2 + "h";
                        } else {
                            op2 = op2.substring(2);
                            op2 = "0" + op2 + "h";
                        }
                    }

                    if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                        op1 = "_" + op1;
                    if (!(tipo2 == "HEXA") && !(tipo2 == "DOUBLE") && !(tipo2 == "LONGINT")) //si es una var agrego _ adelante
                        op2 = "_" + op2;
                    String reg = ocuparRegistro(true);
                    codigoAssembler.append("MOV " + reg + ", " + op1).append("\n");
                    codigoAssembler.append("CMP " + reg + ", " + op2).append("\n");
                    liberarRegistro(reg);
                }
                if(cant_if == 0 )
                    posicionPolaca++;
                cant_if++;//++++
                switch (operador) {
                    case "!=":
                        codigoAssembler.append("JE " + "L" + polaca.get(posicionPolaca)).append("\n");
                        //ultimaComparacion = "JNE";
                        break;
                    case ">=":
                        codigoAssembler.append("JL " + "L" + polaca.get(posicionPolaca)).append("\n");
                        //ultimaComparacion = "JGE";
                        break;
                    case ">":
                        codigoAssembler.append("JLE " + "L" + polaca.get(posicionPolaca)).append("\n");
                        break;
                    case "<=":
                        codigoAssembler.append("JG " + "L" + polaca.get(posicionPolaca)).append("\n");
                        //ultimaComparacion = "JLE";
                        break;
                    case "<":
                        codigoAssembler.append("JGE " + "L" + polaca.get(posicionPolaca)).append("\n");
                        //ultimaComparacion = "JL";
                        break;
                    case "=": //esta?
                        codigoAssembler.append("JNE " + "L" + polaca.get(posicionPolaca)).append("\n");
                        //ultimaComparacion = "JE";
                        break;
                    default:
                        throw new RuntimeException("Operador de comparación no reconocido: " + operador);
                }
                //pila.push("CMP_READY");
            }
        }
    }

    public void generarCondicionDouble(String operador){
        String op2 = pila.pop(); // Segundo operando
        String op1 = pila.pop(); // Primer operando
        boolean op1reg = false;
        boolean op2reg = false;
        boolean varAux1 = false;
        boolean varAux2 = false;
        String operacion = operador;
        if (op1.matches("st\\([0-7]\\)"))
            op1reg = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2reg = true;
        if (op1.matches("^@aux\\d+$"))
            varAux1 = true;
        if (op2.matches("^@aux\\d+$"))
            varAux2 = true;
        if (!op2reg) {
            if(!varAux2){
                String tipo = tablaSimbolos.getDato(op2).getTipo();
                if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                    op2 = "_" + op2;
                else{
                    op2 = op2.replace(".","@");
                    op2 = op2.replace("+","");
                    op2 = op2.replace("-","m");
                    op2 = "DOUBLE" + op2;
                }
            }
        }
        if (!op1reg) {
            if(!varAux1) {
                String tipo = tablaSimbolos.getDato(op1).getTipo();
                if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                    op1 = "_" + op1;
                else{
                    op1 = op1.replace(".","@");
                    op1 = op1.replace("+","");
                    op1 = op1.replace("-","m");
                    op1 = "DOUBLE" + op1;
                }
            }
        }

        if(!op1reg)
            codigoAssembler.append("FLD " + op1).append("\n");
        if(!op2reg)
            codigoAssembler.append("FLD " + op2).append("\n");
        codigoAssembler.append("FCOM").append("\n");
        codigoAssembler.append("FSTSW " + auxMem).append("\n");
        codigoAssembler.append("MOV AX, " + auxMem).append("\n");
        codigoAssembler.append("SAHF").append("\n");
        if(cant_if == 0 )
            posicionPolaca++;
        cant_if++;
        String etiqueta = "L" + polaca.get(posicionPolaca); // se desfasa
        //System.out.println(etiqueta);
        switch (operador) {
            case "!=":
                //posicionPolaca++;
                etiqueta= "L" + polaca.get(posicionPolaca-1);
                codigoAssembler.append("JE "  + etiqueta).append("\n");
                //ultimaComparacion = "JNE";
                break;
            case ">=":
                //posicionPolaca++;
                codigoAssembler.append("JA "  + etiqueta).append("\n");
                //ultimaComparacion = "JGE";
                break;
            case ">":
                //posicionPolaca++;
                codigoAssembler.append("JAE " + etiqueta).append("\n");
                break;
            case "<=":
                //posicionPolaca++;
                codigoAssembler.append("JB " + etiqueta).append("\n");
                //ultimaComparacion = "JLE";
                break;
            case "<":
                //posicionPolaca++;
                codigoAssembler.append("JBE "+ etiqueta).append("\n");
                //ultimaComparacion = "JL";
                break;
            case "=": //esta?
                etiqueta= "L" + polaca.get(posicionPolaca-1);
                codigoAssembler.append("JNE "  + etiqueta).append("\n");
                //ultimaComparacion = "JE";
                break;
            default:
                throw new RuntimeException("Operador de comparación no reconocido: " + operador);
        }
    }


    private void generarSalto(String salto) {
        String direccion = pila.pop(); // Etiqueta de destino la ultima de la polaca
        if (direccion.matches("^[a-zA-Z0-9]+@$")){
            codigoAssembler.append(salto).append(" "+ direccion ).append("\n");
            //generarEtiqueta();
        }else{
        //System.out.println("DIRECCION: " + direccion);
        codigoAssembler.append(salto).append(" L").append(direccion).append("\n");
        // Limpiamos la última comparación
        ultimaComparacion = "";}
    }

    private void liberarRegistro (String registro){
        switch (registro) {
            case "eax" : {
                    registros[0] = false;
                break;
            }
            case "ebx" : {
                    registros[1] = false;
                break;
            }
            case "ecx" : {
                    registros[2] = false;
                break;
            }
            case "edx" : {
                    registros[3] = false;
                break;
            }
        }
    }

    private void liberarRegistroDouble (int i){
        registrosDouble[i] = false;

    }

    public String getTipoRegistro(String registro){
        switch (registro) {
            case "eax" :
                return tipoRegistros[0];
            case "ebx" :
                return tipoRegistros[1];

            case "ecx" :
                return tipoRegistros[2];

            case "edx" :
                return tipoRegistros[3];
            default:
                //System.out.println("registro no valido");
                return null;
        }
    }

    public void setTipoRegistro(String registro , String tipo){
        switch (registro) {
            case "eax" :
                tipoRegistros[0] = tipo;
                break;
            case "ebx" :
                tipoRegistros[1] = tipo;
                break;
            case "ecx" :
                tipoRegistros[2] = tipo;
                break;
            case "edx" :
                tipoRegistros[3] = tipo;
                break;
            default:
                //System.out.println("registro no valido");
                break;
        }
    }

    private void generarConversion(){
        //System.out.println("TOD");
        String varAux = "@aux" + auxiliares;
        auxiliares++;
        String nuevaLinea = varAux + " DQ ?";
        int inicioData = codigoAssembler.indexOf(".data");
        int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
        codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
        String pop = pila.pop();
        //CONVIERTO DE HEXA A ENTERO
        if (pop.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {
            long decimalValue;
            if (pop.matches("^[-]0[Xx][0-9a-fA-F]+$")) {
                decimalValue = Long.parseLong(pop.substring(3), 16);
                decimalValue = decimalValue * (-1);
            }else
                decimalValue = Long.parseLong(pop.substring(2), 16);
            //System.out.println("conversion de: "+ pop +" a :"+decimalValue);
            pop = String.valueOf(decimalValue) ;
        }
        if(!pop.matches("^[+-]?\\d+$") && !pop.equals("eax") && !pop.equals("ebx") && !pop.equals("ecx") && !pop.equals("edx")) {
            //ES UNA VARIABLE
            if(pop.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$"))
                errores.add("ERROR: se quiere convertir una variable double con TOD");
            else
                codigoAssembler.append("FILD " + pop).append("\n");
        }else {
            String varAux2 = "@aux" + auxiliares;
            auxiliares++;
            nuevaLinea = varAux2 + " dd ?";
            inicioData = codigoAssembler.indexOf(".data");
            finalLineaData = codigoAssembler.indexOf("\n", inicioData);
            codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
            codigoAssembler.append("MOV " + varAux2 +", " + pop).append("\n");
            codigoAssembler.append("FILD " + varAux2).append("\n");
            liberarRegistro(pop);
        }
        codigoAssembler.append("FST " + varAux).append("\n");
        pila.push(varAux);

    }

    private void generarErrorDivCero(String etiquetaError) {
        contDivision++;
        codigoAssembler.append("JNE " + etiquetaError + contDivision + "\n");
        codigoAssembler.append("invoke StdOut, addr ERROR_DIVISION_POR_CERO" +"\n");
        //codigoAssembler.append("invoke MessageBox, NULL, addr ERROR_DIVISION_POR_CERO, addr ERROR_DIVISION_POR_CERO, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append(etiquetaError + contDivision + ":\n");
    }

    private void generarErrorOverflow(String etiquetaError) {
        //codigoAssembler.append("JNE" + etiquetaError + "\n");  // Salta si  hay overflow
        codigoAssembler.append("invoke StdOut, addr ERROR_OVERFLOW_PRODUCTO" +"\n");
        //codigoAssembler.append("invoke MessageBox, NULL, addr ERROR_OVERFLOW_PRODUCTO, addr ERROR_OVERFLOW_PRODUCTO, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append(etiquetaError + ":\n");
    }


    private void chequeoDeRangoEnterito(String min, String max, String resultado) {
        codigoAssembler.append("MOV edx, " + resultado + "\n");
        codigoAssembler.append("MOV @varAuxMin, " + min + "\n");
        codigoAssembler.append("MOV @varAuxMax, "+ max + "\n");
        codigoAssembler.append("CALL ControlarRangoEnterito" + "\n");

    }
    private void chequeoDeRango() {
        codigoAssembler.append("CALL ControlarRangoEntero" + "\n");
    }

    private void operar (String operacion) {
        //System.out.println("operacion: " + operacion);
        String min = "80000000h";
        String max = "7FFFFFFFh";
        String op2 = pila.pop();

        String op1 = pila.pop();
        //System.out.println("op1: " + op1);
        //System.out.println("op2: " + op2);
        boolean op1reg = false;
        boolean op2reg = false;
        boolean op1regDouble = false;
        boolean op2regDouble = false;
        boolean varAux1 = false;
        boolean varAux2 = false;
        boolean asignacionDouble = false;
        boolean asignacionDouble2 = false;
        String tipoOp1 = "";
        String tipoOp2 = "";
        //CONTROLAMOS SI SON REGISTROS st
        if (op1.matches("st\\([0-7]\\)"))
            op1regDouble = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2regDouble = true;
        if (op1.matches("^@aux\\d+$"))
            varAux1 = true;
        if (op2.matches("^@aux\\d+$"))
            varAux2 = true;
        if (op1.matches("^(eax|ebx|ecx|edx)$"))
            op1reg = true;
        if (op2.matches("^(eax|ebx|ecx|edx)$"))
            op2reg = true;
        if (op1regDouble) {
            asignacionDouble = true;
        } else if (varAux1) {
            asignacionDouble = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op1reg){
                tipoOp1 = tablaSimbolos.getDato(op1).getTipo();
                String estrucOp1 = tablaSimbolos.getDato(op1).getEstructura();
                if (tipoOp1.equals("DOUBLE") || (estrucOp1 != null && estrucOp1.equals("DOUBLE")))
                    asignacionDouble = true;
            }
        }

        if (op2regDouble) {
            asignacionDouble2 = true;
        } else if (varAux2) {
            asignacionDouble2 = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op2reg){
                tipoOp2 = tablaSimbolos.getDato(op2).getTipo();
                String estrucOp2 = tablaSimbolos.getDato(op2).getEstructura();
                if (tipoOp2.equals("DOUBLE") || (estrucOp2 != null && estrucOp2.equals("DOUBLE")))
                    asignacionDouble2 = true;
            }
        }

        if (asignacionDouble && asignacionDouble2) {
            pila.push(op1);
            pila.push(op2);
            operarDouble(operacion);
        } else {

            if(asignacionDouble || asignacionDouble2){
                errores.add("ERROR: los tipos con los que se quiere operar son invalidos");
                System.out.println("ERROR: los tipos con los que se quiere operar son invalidos");
                codigoAssembler.append("invoke ExitProcess, 0\n");
            }
            else {
                if (operacion.equals("DIV")) {
                    String op3 ="";
                    if (op2reg) {
                        codigoAssembler.append("CMP " + op2 + ", 0\n");  // Comparo op2 con 0 si es un registro
                        generarErrorDivCero("_ERROR_DIV_ZERO");
                    } else //comparo si op2 es 0 pero si es una variable
                    {
                        String tipo = tablaSimbolos.getDato(op2).getTipo();
                        if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                            op3 = "_" + op2;
                        else{
                            String reg = ocuparRegistro(true);
                            codigoAssembler.append("MOV " + reg +", "+ op2).append("\n");
                            op3 = reg;
                        }
                    }
                    codigoAssembler.append("CMP " + op3 + ", 0\n");  // Comparo op2 con 0 si es un registro
                    generarErrorDivCero("_ERROR_DIV_ZERO");
                    liberarRegistro(op3);
                }


                //chequeamos los distintos casos
                if (op1reg && op2reg) {
                    if(operacion != "DIV") {
                        codigoAssembler.append(operacion + " " + op1 + ", " + op2).append("\n"); //Registro operacion registro
                        pila.push(op1);
                    }
                    else {
                        registros[0] = true;
                        setTipoRegistro("eax", "LONGINT");
                        if(op1 != "eax")
                            liberarRegistro(op1);
                        codigoAssembler.append("MOV EAX, " + op1).append("\n");
                        codigoAssembler.append("MOV EBX, " + op2).append("\n");
                        codigoAssembler.append("MOV EDX, 0").append("\n");
                        codigoAssembler.append("DIV EBX").append("\n");
                        pila.push("eax");
                    }
                    if (operacion == "IMUL") {
                        chequeoDeRango();
                    }

                    liberarRegistro(op2);

                }
                if (op1reg && !op2reg) {
                    String tipo = tablaSimbolos.getDato(op2).getTipo();
                    String op2Uso = tablaSimbolos.getDato(op2).getUso();
                    if (op2.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if (op2.startsWith("-")) {
                            op2 = op2.substring(3);
                            op2 = "-0" + op2 + "h";
                        } else {
                            op2 = op2.substring(2);
                            op2 = "0" + op2 + "h";

                        }
                    }

                    if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                    op2 = "_" + op2;

                    if(!operacion.equals("DIV")) {
                        codigoAssembler.append(operacion + " " + op1 + ", " + op2).append("\n"); //Registro operacion registro
                        pila.push(op1);
                    }
                    else {
                        String reg = "";

                            if(!op1.equals("eax")){
                                reg = ocuparRegistro(true);
                                codigoAssembler.append("MOV " + reg + ", eax").append("\n");
                                codigoAssembler.append("MOV " + "eax" + ", " + op1).append("\n");
                                liberarRegistro(op1);
                                op1 = "eax";
                                if (pila.contains("eax"))
                                    pila.setElementAt(reg,pila.size()-pila.search("eax"));
                            }
                            if(registros[3]){
                                String reg1 = ocuparRegistro(true);
                                codigoAssembler.append("MOV " + reg1 + ", edx").append("\n");
                                if (pila.contains("edx"))
                                    pila.setElementAt(reg1,pila.size()-pila.search("edx"));
                            }
                            registros[0] = true;
                            codigoAssembler.append("CDQ").append("\n"); //Extiende el signo de EAX hacia EDX (prepara EDX:EAX)
                            if(op2Uso == null) {
                                String varAux = "@aux" + auxiliares;auxiliares++;
                                String nuevaLinea = varAux + " DD ?";
                                int inicioData = codigoAssembler.indexOf(".data");
                                int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
                                codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
                                codigoAssembler.append("MOV " + varAux + ", " + op2).append("\n");
                                op2 = varAux;
                            }
                            codigoAssembler.append("IDIV " + op2).append("\n");
                            pila.push("eax");
                            liberarRegistro("EDX");
                        }
                    if (operacion == "IMUL") {
                        chequeoDeRango();
                    }


                }
                if (!op1reg && op2reg) {
                    String tipo = tablaSimbolos.getDato(op1).getTipo();

                    if (op1.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if(op1.startsWith("-")) {
                            op1 = op1.substring(3);
                            op1 = "-0" + op1 + "h";
                        } else {
                            op1 = op1.substring(2);
                            op1 = "0" + op1 + "h";
                        }
                    }

                    if (!(tipo == "HEXA") && !(tipo == "DOUBLE") && !(tipo == "LONGINT")) //si es una var agrego _ adelante
                        op1 = "_" + op1;
                    if (operacion == "ADD" || operacion == "MUL") {
                        codigoAssembler.append(operacion + " " + op2 + ", " + op1).append("\n"); //Const/variable (ADD/MUL) registro
                        pila.push(op2);
                        if (operacion == "IMUL") {
                            chequeoDeRango();
                        }
                    } else {
                        String reg = "";
                        if (operacion.equals("DIV")){
                            if(op2.equals("eax")){
                                reg = ocuparRegistro(true);
                                codigoAssembler.append("MOV "+ reg+", EAX").append("\n");
                                op2 = reg;
                            }
                            else
                            if(registros[0]){
                                String reg1 = ocuparRegistro(true);
                                codigoAssembler.append("MOV " + reg1 + ", eax").append("\n");
                                if (pila.contains("eax"))
                                    pila.setElementAt(reg1,pila.size()-pila.search("eax"));
                            }
                            if(registros[3]){
                                String reg1 = ocuparRegistro(true);
                                codigoAssembler.append("MOV " + reg1 + ", edx").append("\n");
                                if (pila.contains("edx"))
                                    pila.setElementAt(reg1,pila.size()-pila.search("edx"));
                            }
                            registros[0] = true;
                            codigoAssembler.append("MOV eax, " + op1).append("\n");
                            codigoAssembler.append("CDQ").append("\n"); //Extiende el signo de EAX hacia EDX (prepara EDX:EAX)
                            codigoAssembler.append("IDIV " + op2).append("\n");
                            pila.push("eax");
                            liberarRegistro("EDX");
                            liberarRegistro(op2);
                        }
                        else{
                            reg = ocuparRegistro(true);
                            codigoAssembler.append("MOV " + reg + ", " + op1).append("\n"); //Const/var a registro
                            codigoAssembler.append(operacion + " " + reg + ", " + op2).append("\n"); //Const/var (DIV/SUB) registro
                            pila.push(reg);
                        }
                    }
                    //liberarRegistro(op2);
                }
                if (!op1reg && !op2reg) { //Variable OPERACION variable
                    String reg = "";
                    if (operacion == "ADD" || operacion == "SUB")
                        reg = ocuparRegistro(true);
                    else
                        reg = ocuparRegistro(false);

                    tipoOp1 = tablaSimbolos.getDato(op1).getTipo();

                    tipoOp2 = tablaSimbolos.getDato(op2).getTipo();

                    String op1Uso = tablaSimbolos.getDato(op1).getUso();

                    String op2Uso = tablaSimbolos.getDato(op2).getUso();

                    if (op1.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if(op1.startsWith("-")) {
                            op1 = op1.substring(3);
                            op1 = "-0" + op1 + "h";
                        } else {
                            op1 = op1.substring(2);
                            op1 = "0" + op1 + "h";
                        }
                    }
                    if (op2.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if (op2.startsWith("-")) {
                            op2 = op2.substring(3);
                            op2 = "-0" + op2 + "h";
                        } else {
                            op2 = op2.substring(2);
                            op2 = "0" + op2 + "h";

                        }
                    }

                    setTipoRegistro(reg, tipoOp1);
                    //System.out.println("registro: " + reg + "tipo:" + getTipoRegistro(reg));
                    if (!(tipoOp1 == "HEXA") && !(tipoOp1 == "DOUBLE") && !(tipoOp1 == "LONGINT")) //si es una var agrego _ adelante
                        op1 = "_" + op1;
                    if (!(tipoOp2 == "HEXA") && !(tipoOp2 == "DOUBLE") && !(tipoOp2 == "LONGINT")) //si es una var agrego _ adelante
                        op2 = "_" + op2;
                    if(!operacion.equals("DIV")) {
                        codigoAssembler.append("MOV " + reg + ", " + op1).append("\n"); //variable/const a registro
                        codigoAssembler.append(operacion + " " + reg + ", " + op2).append("\n"); //Registro operacion const/variable
                        pila.push(reg);
                    }
                    else {
                        liberarRegistro(reg);
                        if(registros[0]){
                            String reg1 = ocuparRegistro(true);
                            codigoAssembler.append("MOV " + reg1 + ", eax").append("\n");
                            if (pila.contains("eax"))
                                pila.setElementAt(reg1,pila.size()-pila.search("eax"));
                        }
                        if(registros[3]){
                            String reg1 = ocuparRegistro(true);
                            codigoAssembler.append("MOV " + reg1 + ", edx").append("\n");
                            if (pila.contains("edx"))
                                pila.setElementAt(reg1,pila.size()-pila.search("edx"));
                        }


                        registros[0] = true;
                        codigoAssembler.append("MOV eax, " + op1).append("\n");
                        codigoAssembler.append("CDQ").append("\n"); //Extiende el signo de EAX hacia EDX (prepara EDX:EAX)

                        if(op2Uso == null) {
                            String varAux = "@aux" + auxiliares;auxiliares++;
                            String nuevaLinea = varAux + " DD ?";
                            int inicioData = codigoAssembler.indexOf(".data");
                            int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
                            codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
                            codigoAssembler.append("MOV " + varAux + ", " + op2).append("\n");
                            op2 = varAux;
                        }
                        codigoAssembler.append("IDIV " + op2).append("\n");
                        pila.push("eax");
                        liberarRegistro("EDX");
                    }
                    if (operacion == "IMUL"){
                        chequeoDeRango();
                    }
                }
            }
        }
    }



    private void operarDouble (String operacion) {
        //System.out.println("DOUBLE");
        String op2 = pila.pop();
        String op1 = pila.pop();
        boolean op1reg = false;
        boolean op2reg = false;
        String tipoOp1 = "";
        if (op1.matches("st\\([0-7]\\)"))
            op1reg = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2reg = true;
        //System.out.println("Auxiliar par afloat " + op1);
        if (!op1reg) {
            if (op1.matches("^@aux\\d+$"))
                tipoOp1 = "DOUBLE";
            else{
                tipoOp1 = tablaSimbolos.getDato(op1).getTipo();
                if (!(tipoOp1 == "DOUBLE")) //si es una var agrego _ adelante
                    op1 = "_" + op1;
                else {
                    op1 = op1.replace(".", "@");
                    op1 = op1.replace("+","");
                    op1 = op1.replace("-","m");
                    op1 = "DOUBLE" + op1;
                }
            }
        }
        String tipoOp2 = "";
        if (!op2reg) {
            if(op2.matches("^@aux\\d+$"))
                tipoOp2 = "DOUBLE";
            else{
                tipoOp2 = tablaSimbolos.getDato(op2).getTipo();
                if (!(tipoOp2 == "DOUBLE")){
                    op2 = "_" + op2;
                }
                else {
                    op2 = op2.replace(".","@");
                    op2 = op2.replace("+","");
                    op2 = op2.replace("-","m");
                    op2 = "DOUBLE" + op2;
                }
            }
        }
        // codigoAssembler.append("FLD " + op1).append("\n"); // aca guardo el primer operador en la pila de los float ST(0) := + Op1 (la inicializo);
        if (operacion != "DIV") {
            if(!op1reg)
                codigoAssembler.append("FLD " + op1).append("\n");
            if(!op2reg)

                codigoAssembler.append("FLD " + op2).append("\n");
        }
        // no hay que chear que este en un registro ya que siempre que tengas que operar entre double siempre o es una variable aux o directamente lo guardamos
        // en la pila de float osea no tendriamos por que trabajar con registros tenemso de ST(0) a ST(7) onda si es un float lo guardamos directamente ahi ?
        // diria que en la pila comun van a estar los @Aux
        switch (operacion){
            case "ADD":
                //esto creo q seria asi pero ni idea con los registros pero bueno
                codigoAssembler.append("FADD").append("\n"); // realizo ST(1):= ST(1) + ST(0);

                pila.push("st(0)");
                break;
            case "SUB":
                //esto creo q seria asi pero ni idea con los registros pero bueno
                codigoAssembler.append("FSUB").append("\n"); // realizo ST(1):= ST(1) - ST(0);
                pila.push("st(0)");
                break;
            case "DIV":
                String varAux2 = generarAux();
                String aux;
                //System.out.println("size: " + pila.size());
                if(!pila.isEmpty()) {
                    aux = pila.firstElement();
                    if (aux.equals("st(0)")) {
                        //System.out.println("Variablw");
                        String varAux3 = generarAux();
                        codigoAssembler.append("FSTP " + varAux3).append("\n");
                        pila.pop();
                        pila.push(varAux3);
                    }
                }


                codigoAssembler.append("FLDZ").append("\n"); //cargo en ST(0):= 0.0
                codigoAssembler.append("FLD " + op2 ).append("\n");
                codigoAssembler.append("FCOM ").append("\n"); //genero la comparacion lo guarda en la FPU
                codigoAssembler.append("FNSTSW " + auxMem).append("\n"); // Guardar el registro de estado de la FPU (resultado de la comparacion) en Aux en mem
                codigoAssembler.append("MOV AX," + auxMem).append("\n"); //muevo ese valor a AX
                codigoAssembler.append("SAHF\n"); // Transferir el estado guardado a los indicadores de la CPU
                generarErrorDivCero("_ERROR_DIV_ZERO");
                codigoAssembler.append("FLD " + op2).append("\n");
                codigoAssembler.append("FLD " + op1).append("\n"); // Cargar op1 en la pila FPU
                codigoAssembler.append("FDIV ").append("\n"); // realizo ST(1):= ST(1) / ST(0);
                codigoAssembler.append("FSTP " + varAux2).append("\n");
                //System.out.println("Sobrevivi a una division");
                pila.push(varAux2);

                break;
            case "IMUL":
                codigoAssembler.append("FMUL").append("\n");
                String varAux = "@aux" + auxiliares;
                auxiliares++;
                String nuevaLinea = varAux + " DQ ?";
                int inicioData = codigoAssembler.indexOf(".data");
                int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
                codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
                codigoAssembler.append("FSTP " + varAux).append("\n");
                pila.push(varAux);
                break;

            default:
                break;
        }

    }

    private void asignarRetorno(String aux) {
        //AUX ES LA VARIABLE RETORNO DE LA FUNCION
        String resultado = pila.pop();
        //System.out.println("imprimo el resultado: " + resultado);
        //System.out.println("imprimo aux: "+aux);
        String tipoRetorno = tablaSimbolos.getDato(aux).getTipo();
        String tipoResultado = tablaSimbolos.getDato(resultado).getTipo();
        if(tipoResultado.equals(tipoRetorno)) {
            if (resultado.matches("^(eax|ebx|ecx|edx)$"))
                codigoAssembler.append("MOV _" + aux + ", " + resultado + "\n");
            else if (resultado.matches("^[+-]?\\d+$"))
                codigoAssembler.append("MOV _" + aux + ", " + resultado + "\n");
            else if (resultado.matches("^[+-–]?\\d+\\.\\d+([dD][+-–]?\\d+)?$")) {
                resultado = resultado.replace(".", "@");
                resultado = resultado.replace("+", "");
                resultado = resultado.replace("-", "m");
                resultado = "DOUBLE" + resultado;
                codigoAssembler.append("FLD " + resultado + "\n");
                codigoAssembler.append("FSTP _" + aux + "\n");
            } else {
                String tipo = tablaSimbolos.getDato(resultado).getTipo();
                if (tipo.equals("LONGINT")) {
                    String reg = ocuparRegistro(true);
                    codigoAssembler.append("MOV " + reg + ", _" + resultado + "\n");
                    codigoAssembler.append("MOV _" + aux + ", " + reg + "\n");
                } else {
                    codigoAssembler.append("FLD _" + resultado + "\n");
                    codigoAssembler.append("FSTP _" + aux + "\n");
                }
            }
            codigoAssembler.append("pop esi ; Recover register values" +  "\n");
            codigoAssembler.append("pop edi " +  "\n");
            codigoAssembler.append("mov esp, ebp ; Deallocate local variables " +  "\n");
            codigoAssembler.append("pop ebp ; Restore the caller's base pointer value " +  "\n");



            codigoAssembler.append("RET" + "\n");
        } else
            errores.add("ERROR: El retorno" +" no coincide con el tipo de la funcion " + aux.substring(aux.lastIndexOf('@') + 1));
    }

    private String generarAux(){
        String varAux = "@aux" + auxiliares;
        auxiliares++;
        String nuevaLinea = varAux + " DQ ?";
        int inicioData = codigoAssembler.indexOf(".data");
        int finalLineaData = codigoAssembler.indexOf("\n", inicioData);
        codigoAssembler.insert(finalLineaData + 1, nuevaLinea + "\n");
        return varAux;
    }

    private void generarAsignacion() {
        //System.out.println("ASIGNACION");
        String op1 = pila.pop();
        String op2 = pila.pop();
        //System.out.println("op1: " + op1);
        //System.out.println("op2: " + op2);
        boolean op1reg = false;
        boolean op2reg = false;
        boolean op1regDouble = false;
        boolean op2regDouble = false;
        boolean varAux1 = false;
        boolean varAux2 = false;
        boolean asignacionDouble = false;
        boolean asignacionDouble2 = false;
        String tipoOp1 = "";
        String tipoOp2 = "";
        //CONTROLAMOS SI SON REGISTROS st
        if (op1.matches("^(eax|ebx|ecx|edx)$"))
            op1reg = true;
        if (op2.matches("^(eax|ebx|ecx|edx)$"))
            op2reg = true;
        if (op1.matches("st\\([0-7]\\)"))
            op1regDouble = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2regDouble = true;
        if (op1.matches("^@aux\\d+$"))
            varAux1 = true;
        if (op2.matches("^@aux\\d+$"))
            varAux2 = true;

        if (op1regDouble) {
                asignacionDouble = true;
        } else if (varAux1) {
            asignacionDouble = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op1reg){
            tipoOp1 = tablaSimbolos.getDato(op1).getTipo();
            String estrucOp1 = tablaSimbolos.getDato(op1).getEstructura();

            if (tipoOp1.equals("DOUBLE") || (estrucOp1 != null && estrucOp1.equals("DOUBLE")))
                asignacionDouble = true;}

        }

        if (op2regDouble) {
                asignacionDouble2 = true;
        } else if (varAux2) {
            asignacionDouble2 = true;
        } //POR AHORA TODAS LAS VAR AUX SON DOUBLE SINO HABRIA QUE CHEQUEAR
        else {
            if(!op2reg){
                tipoOp2 = tablaSimbolos.getDato(op2).getTipo();
                String estrucOp2 = tablaSimbolos.getDato(op2).getEstructura();

                if (tipoOp2 != null && (tipoOp2.equals("DOUBLE") || (estrucOp2 != null && estrucOp2.equals("DOUBLE")))) {
                    asignacionDouble2 = true;
                }
            }
        }

        if (asignacionDouble && asignacionDouble2) {
            pila.push(op2);
            pila.push(op1);
            generarAsignacionDouble();
        } else {
            String registro = "";
            if (asignacionDouble || asignacionDouble2) {
                errores.add("ERROR: los tipos para la asignacion/invocacion son invalidos");
                codigoAssembler.append("invoke ExitProcess, 0\n");
            } else {
                if (!op1reg) {
                    tipoOp1 = tablaSimbolos.getDato(op1).getTipo();

                    //CONVIERTO DE HEXA A ENTERO
                    if (op1.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if(op1.startsWith("-")) {
                            op1 = op1.substring(3);
                            op1 = "-0" + op1 + "h";
                        } else {
                            op1 = op1.substring(2);
                            op1 = "0" + op1 + "h";
                        }
                    }

                    if (!(tipoOp1 == "HEXA") && !(tipoOp1 == "DOUBLE") && !(tipoOp1 == "LONGINT")) //si es una var agrego _ adelante
                        op1 = "_" + op1;
                }
                if (!op2reg) {
                    tipoOp2 = tablaSimbolos.getDato(op2).getTipo();

                    //CONVIERTO DE HEXA A ENTERO
                    if (op2.matches("^[+-]?0[Xx][0-9a-fA-F]+$")) {

                        if (op2.startsWith("-")) {
                            op2 = op2.substring(3);
                            op2 = "-0" + op2 + "h";
                        } else {
                            op2 = op2.substring(2);
                            op2 = "0" + op2 + "h";

                        }
                    }
                    if (!(tipoOp2 == "HEXA") && !(tipoOp2 == "DOUBLE") && !(tipoOp2 == "LONGINT")) { //si es una var agrego _ adelante
                        //System.out.println("op2 es var: " + op2);
                        op2 = "_" + op2;
                    }

                }
                else {
                    tipoOp2 = getTipoRegistro(op2);
                }



                //System.out.println("op1: " +op1 + " op2: " + op2);
                //System.out.println("TIPO1: " + tipoOp1 + " TIPO2: " + tipoOp2);
                if (!tipoOp1.equals("") && !tipoOp1.equals("HEXA") && !tipoOp1.equals("DOUBLE") && !tipoOp1.equals("LONGINT")) {
                    //System.out.println("SUBTIPO");
                    int indiceDosPuntos = op1.indexOf('@'); // Busca la posición del '@'
                    String resultado = "";
                    if (indiceDosPuntos != -1) {
                        resultado = op1.substring(indiceDosPuntos); // Extrae desde el ':'
                    }

                    //String subTipoABuscar = tipoOp1+resultado;
                    //String subtipo = tablaSimbolos.getDato(tipoOp1 + resultado).getTipo();
                    chequeoSubtiposValido(tipoOp1 + resultado, op2);
                    if(!op1reg && !op2reg){
                        registro = ocuparRegistro(true);
                        liberarRegistro(registro);
                        codigoAssembler.append("MOV " + registro + ", " + op2).append("\n"); //Asigno el op2 al registro intermedio
                        codigoAssembler.append("MOV " + op1 + ", " + registro).append("\n"); //Asigno el registro a op1
                    } else
                        codigoAssembler.append("MOV " + op1 + ", " + op2).append("\n"); //Asigno el op2 al op1
                    liberarRegistro(op2);
                } else{

                    if(!op1reg && !op2reg){
                        registro = ocuparRegistro(true);
                        liberarRegistro(registro);
                        codigoAssembler.append("MOV " + registro + ", " + op2).append("\n"); //Asigno el op2 al registro intermedio
                        codigoAssembler.append("MOV " + op1 + ", " + registro).append("\n"); //Asigno el registro a op1
                    }
                    else
                        codigoAssembler.append("MOV " + op1 + ", " + op2).append("\n"); //Asigno el op2 al op1

                    liberarRegistro(op2);
                } /*else {
                    errores.add("ERROR: los tipos para la asignacion son invalidos");
                    codigoAssembler.append("invoke ExitProcess, 0\n");
                }*/
            }
        }
    }
    /*
    * FLD - Carga un valor de punto flotante desde memoria al registro de la FPU.
    FSTP - Almacena un valor de la FPU en memoria y lo saca de la pila de la FPU.*/

    private void generarAsignacionDouble(){
        //System.out.println("DOUBLE");
        String op1 = pila.pop();
        String op2 = pila.pop();
        boolean op1reg = false;
        boolean op2reg = false;
        //CONTROLAMOS SI SON REGISTROS
        if (op1.matches("st\\([0-7]\\)"))
            op1reg = true;
        if (op2.matches("st\\([0-7]\\)"))
            op2reg = true;
        String tipoOp1 = "";
        //SI NO SON REGISTROS LE AGREGAMOS ADELANTE "_"
        if (!op1reg) {
            if(op1.matches("^@aux\\d+$"))
                tipoOp1 = "DOUBLE";
            else{
                tipoOp1 = tablaSimbolos.getDato(op1).getTipo();
                if (!(tipoOp1 == "HEXA") && !(tipoOp1 == "DOUBLE") && !(tipoOp1 == "LONGINT")) //si es una var agrego _ adelante
                    op1 = "_" + op1;
                else {
                    op1 = op1.replace(".","@");
                    op1 = op1.replace("+","");
                    op1 = op1.replace("-","m");
                    op1 = "DOUBLE" + op1;
                }
            }
        }
        String tipoOp2 = "";
        if (!op2reg) {
            if(op2.matches("^@aux\\d+$"))
                tipoOp2 = "DOUBLE";
            else{
                tipoOp2 = tablaSimbolos.getDato(op2).getTipo();
                if (!(tipoOp2 == "HEXA") && !(tipoOp2 == "DOUBLE") && !(tipoOp2 == "LONGINT")){ //si es una var agrego _ adelante
                    //System.out.println("op2 es var: "+op2);
                    op2 = "_" + op2;
                }
                else {
                    op2 = op2.replace(".","@");
                    op2 = op2.replace("+","");
                    op2 = op2.replace("-","m");
                    op2 = "DOUBLE" + op2;

                }
            }
        }
        if (!tipoOp1.equals("") && !tipoOp1.equals("HEXA") && !tipoOp1.equals("DOUBLE") && !tipoOp1.equals("LONGINT")) {
            //System.out.println("SUBTIPO");
            int indiceDosPuntos = op1.indexOf('@'); // Busca la posición del ':'
            String resultado = "";
            if (indiceDosPuntos != -1) {
                resultado = op1.substring(indiceDosPuntos); // Extrae desde el ':'
            }
            String subtipo = tablaSimbolos.getDato(tipoOp1+resultado).getTipo();
            chequeoSubtiposValido(tipoOp1+resultado, op2);
            //if(!op2reg)
              //  codigoAssembler.append("FLD " + op2).append("\n");
            codigoAssembler.append("FSTP " + op1).append("\n");
        } else{
            if(!op2reg)
                codigoAssembler.append("FLD " + op2).append("\n"); //Asigno el op2 al op1
            codigoAssembler.append("FSTP " + op1).append("\n");
            if (op2reg) {
                int i = Integer.parseInt(op2.replaceAll("\\D", ""));
                liberarRegistroDouble(i);
            }
            if (op1reg) {
                int i = Integer.parseInt(op1.replaceAll("\\D", ""));
                liberarRegistroDouble(i);
            }
        }
    }

    private void chequeoSubtiposValido(String subtipo, String asignado){
        String limInf = tablaSimbolos.getDato(subtipo).getlimiteINF();
        String limSup = tablaSimbolos.getDato(subtipo).getlimiteSup();
        String tipoSubtipo = tablaSimbolos.getDato(subtipo).getTipo();
        //USO COMPARACION PARA LOS TIPO ENTEROS
        if (tipoSubtipo.equals("LONGINT") || tipoSubtipo.equals("HEXA")) {
            chequeoDeRangoEnterito(limInf, limSup, asignado);
        } else {

            limInf = limInf.replace(".","@");
            limInf = limInf.replace("+","");
            limInf = limInf.replace("-","m");
            limInf = "DOUBLE" + limInf;

            limSup = limSup.replace(".","@");
            limSup = limSup.replace("+","");
            limSup = limSup.replace("-","m");
            limSup = "DOUBLE" + limSup;
            //System.out.println("limite superior: " + limSup + " LIMITE INFERIOR: " + limInf);
            //GENERO COMPARACION PARA LOS TIPOS DOUBLE

            if (!asignado.matches("st\\([0-7]\\)"))
                codigoAssembler.append("FLD "+ asignado + "\n");
            codigoAssembler.append("FSTP @varAuxRangoDouble" + "\n");
            codigoAssembler.append("FLD "+ limInf + "\n");
            codigoAssembler.append("FSTP @varAuxMinDouble" + "\n");
            codigoAssembler.append("FLD "+ limSup + "\n");
            codigoAssembler.append("FSTP @varAuxMaxDouble "+ "\n");
            codigoAssembler.append("CALL ControlarRangoDouble" + "\n");
        }

    }


    private static boolean esNumero(String str) {
        try {
            Integer.parseInt(str); // Intenta convertir a entero
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void imprimirContenido(){
        if ( codigoAssembler == null || codigoAssembler.length() == 0) {
            //System.out.println("El StringBuilder está vacío o es nulo.");
        } else {
           // em.out.println(codigoAssembler.toString());
        }
    }
}