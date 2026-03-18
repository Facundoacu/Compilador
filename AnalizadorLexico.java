import java.util.*;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
public class AnalizadorLexico {
    private LectorDeTexto lector;
    public TablaSimbolos tablaSimbolos;
    private int[][] matrizTransicion;
    private int[][] matrizSemantica;
    private Parser parser;

    public AnalizadorLexico(Parser par, String archEjecutable) {
        String Path = "TablaSimbolos.txt";
        this.parser = par;
        TablaSimbolos TS = new TablaSimbolos(Path);
        this.tablaSimbolos = TS;
        this.lector = new LectorDeTexto(archEjecutable);
        String matrizTransiciones = "matrizTransicion.txt";
        String matrizAccionesSemanticas = "MatrizDeAccionesSemanticas.txt";
        this.matrizSemantica = leerArchivoComoMatriz(matrizAccionesSemanticas);
        this.matrizTransicion = leerArchivoComoMatriz(matrizTransiciones);
    }

    public int getNroLinea (){
        return lector.getNroLinea();
    }

    public int asignarValorChar(char caracter) {
        switch (caracter) {

            case '*':
            case '/':
            case '(':
            case ')':
            case ',':
            case ';':
            case '[':
            case ']':
                return 0;
            case ':':
            case '!':
                return 1;
            case '>':
            case '<':
                return 2;
            case '{':
                return 3;
            case '#':
                return 4;
            case '_':
                return 5;
            case '=':
                return 6;
            case '}':
                return 7;
            case ' ':
            case '\t':
                return 8;
            case '\n':
                return 9;
            case '.':
                return 10;
            case '0':
                return 11;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return 12;
            case 'x':
            case 'X':
                return 13;
            case 'd':
            case 'D':
                return 14;
            case 'a':
            case 'A':
            case 'b':
            case 'B':
            case 'c':
            case 'C':
            case 'e':
            case 'E':
            case 'f':
            case 'F':
                return 15;
            case '?':
                return 16;
            case '@':
                return 17;
            case '–':
            case '-':
            case '+':
                return 18;
            default:
                if (Character.isLetter(caracter)) {
                    return 19;
                } else {
                    System.out.println("Caracter no válido: " + caracter);
                    return -1;
                }
        }
    }

    public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena,  Character caracter) {

        switch (accionSemantica) {
            case 1:
                AccionesSemantica.AS1 instanciaAS1 = AccionesSemantica.AS1.obtenerInstancia(lector);
                instanciaAS1.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 2:
                AccionesSemantica.AS2 instanciaAS2 = AccionesSemantica.AS2.obtenerInstancia(lector,parser);
                instanciaAS2.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 3:
                AccionesSemantica.AS3 instanciaAS3 = AccionesSemantica.AS3.obtenerInstancia(lector);
                instanciaAS3.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 4:
                AccionesSemantica.AS4 instanciaAS4 = AccionesSemantica.AS4.obtenerInstancia(lector);
                instanciaAS4.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 5:
                AccionesSemantica.AS5 instanciaAS5 = AccionesSemantica.AS5.obtenerInstancia(lector);
                instanciaAS5.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 6:
                AccionesSemantica.AS6 instanciaAS6 = AccionesSemantica.AS6.obtenerInstancia(lector,parser);
                instanciaAS6.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 7:
                AccionesSemantica.AS7 instanciaAS7 = AccionesSemantica.AS7.obtenerInstancia(lector);
                instanciaAS7.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 8:
                AccionesSemantica.AS8 instanciaAS8 = AccionesSemantica.AS8.obtenerInstancia(lector);
                instanciaAS8.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 9:
                AccionesSemantica.AS9 instanciaAS9 = AccionesSemantica.AS9.obtenerInstancia(lector);
                instanciaAS9.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 10:
                AccionesSemantica.AS10 instanciaAS10 = AccionesSemantica.AS10.obtenerInstancia(lector);
                instanciaAS10.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 11:
                AccionesSemantica.AS11 instanciaAS11 = AccionesSemantica.AS11.obtenerInstancia(lector);
                instanciaAS11.ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                break;
            case 12:
                break;
            default:

                break;
        }
    }

    public String tokenToString(int token, String cadena) {
        switch (token) {
            case 257:
                return "Sentencia if";
            case 258:
                return "THEN";
            case 259:
                return "ELSE";

            case 260:
                return "BEGIN";
            case 261:
                return "END";
            case 262:
                return "END_IF";
            case 263:
                return "OUTF";
            case 264:
                return "TYPEDEF";
            case 265:
                return "Funcion";
            case 266:
                return "RET";
            case 267:
                return "STRING";
            case 268:
                return "REPEAT";
            case 269:
                return "WHILE";
            case 270:
                return "GOTO";
            case 271:
                return "Identificador:" + " '"+ cadena + "'";
            case 272:
                if(cadena.toUpperCase().equals("LONGINT"))
                    return "LONGINT";
                else
                    return  "Constante entera: " +cadena;
            case 273:
                if(cadena.toUpperCase().equals("HEXA"))
                    return "HEXA";
                else
                    return "Constante entera (hexadecimal): " + cadena;
            case 274:
                return "Cadena: " + cadena.substring(1,cadena.length()-1);
            case 275:
                if(cadena.toUpperCase().equals("DOUBLE"))
                    return "DOUBLE";
                else
                    return  "Constante flotante: " +cadena;
            case 276:
                return "TOD";
            case 277:
                return "STRUCT";
            case 278:
                return "ASIGNACION";
            case 279:
                return "DISTINTO";
            case 280:
                return "MENOR_IGUAL";
            case 281:
                return "MAYOR_IGUAL";
            case 282:
                return "ETIQUETA: " + cadena;
            case 283:
                return "LOWER_THAN_ELSE";
            case 256:
                return "YYERRCODE";
            default:
                return "'" + cadena + "'";

        }
    }

    public int yylex() {
        int valorToken = -1;
        int estado = 0;
        int estadoAnt = 0;
        StringBuilder cadena = new StringBuilder();
        Character caracter = null;
        while (estado != -1){ //estado final es -1 y error es -2
            estadoAnt = estado;
            caracter = lector.nuevoCaracter();
            int valorChar = asignarValorChar(caracter);
            //consulta la tabla de transicion para saber a que estado movernos
            if(valorChar != -1) { //Si es un caracter valido
                estado = matrizTransicion[estado][valorChar];

                if (estado == -2) {
                    System.out.println("El caracter:" + caracter + " es invalido en la linea: "+ this.getNroLinea());
                    estado = estadoAnt; //Estado anterior si se ejecuta alguna accion semantica que retroceda la columna en el lector
                }
                else {
                    int accionSemantica = matrizSemantica[estadoAnt][valorChar];
                    ejecutarAccionSemantica(accionSemantica, cadena, caracter);
                }
            }
        }
        int token = tablaSimbolos.determinarTokenValor(cadena.toString());
        if (token == 271)
                parser.yylval= new ParserVal(cadena.toString().toUpperCase());
        else
            parser.yylval= new ParserVal(cadena.toString());
        if(!cadena.equals(""))
            System.out.print(tokenToString(token , cadena.toString())+ "\n");

        return token;

    }


    public int[][] leerArchivoComoMatriz(String nombreArchivo) {
        int[][] matriz = null;

        // Usar getResourceAsStream para acceder al archivo dentro del .jar
        try (InputStream inputStream = getClass().getResourceAsStream("/" + nombreArchivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            // Verificar si el archivo fue encontrado
            if (inputStream == null) {
                System.out.println("No se encontró el archivo: " + nombreArchivo);
                return null; // Salir si no se encuentra el archivo
            }

            // Leer todas las líneas del archivo
            String linea;
            int numFilas = 0;
            int numColumnas = 0;

            // Determinar el número de filas y columnas
            while ((linea = br.readLine()) != null) {
                numFilas++;
                String[] columnas = linea.split("\\s+"); // Separar por espacios
                numColumnas = columnas.length; // Suponer que todas las filas tienen el mismo número de columnas
            }

            // Inicializar la matriz
            matriz = new int[numFilas][numColumnas];

            // Volver al principio del archivo para llenar la matriz
            br.close(); // Cerramos el BufferedReader de arriba
            try (BufferedReader br2 = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + nombreArchivo)))) {
                int filaActual = 0;

                while ((linea = br2.readLine()) != null) {
                    String[] columnas = linea.split("\\s+");
                    for (int columnaActual = 0; columnaActual < columnas.length; columnaActual++) {
                        matriz[filaActual][columnaActual] = Integer.parseInt(columnas[columnaActual]);
                    }
                    filaActual++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return matriz;
    }



}

