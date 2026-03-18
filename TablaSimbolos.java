import javax.tools.ForwardingFileObject;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.Scanner;
public class TablaSimbolos {
    private Integer token;
    private String atributo;
    private String archivoSimbolos;
    public static Map<String,DatosTablaSimbolos> tabla;
    private static Integer NO_ENCONTRADO = -1;


    public TablaSimbolos(String archivo) {
        this.archivoSimbolos = archivo;
        this.tabla = addTokenTXT();
    }
    public Map<String, DatosTablaSimbolos> addTokenTXT() {
        Map<String, DatosTablaSimbolos> map = new HashMap<>();
        String filePath = archivoSimbolos; // Ruta del archivo

        // Usar InputStream para leer el archivo
        try (InputStream inputStream = getClass().getResourceAsStream("/" + archivoSimbolos);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Usar split con límite -1 para capturar todos los elementos, incluyendo los vacíos
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    try {
                        // Convertir la primera parte en un Integer (valor)
                        Integer value = Integer.parseInt(parts[0].trim());

                        // Determinar el valor según las partes
                        String key;
                        if (parts.length == 3 && parts[1].isEmpty() && parts[2].isEmpty()) {
                            // Si la línea es como "18,,", asignar "," como clave
                            key = ",";
                        } else {
                            // En cualquier otro caso, tomar la segunda parte como clave
                            key = parts[1].trim();
                        }
                        DatosTablaSimbolos datos = new DatosTablaSimbolos(value);
                        // Agregar el par clave-valor al mapa
                        map.put(key, datos);
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir el valor a Integer: " + parts[0]);
                    }
                } else {
                    System.out.println("Línea no válida: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void imprimir(){
        System.out.println();
        System.out.println("------------------------ IMPRIMIENDO TABLA DE SIMBOLOS ------------------------");
        for (Map.Entry<String, DatosTablaSimbolos> entry : this.tabla.entrySet()) {
            String key = entry.getKey();
            DatosTablaSimbolos value = entry.getValue();
            int token = value.getToken();
            if (token == 271 && !key.equals("ID")
            || (token == 273 && !key.equals("HEXA")
            || (token == 282)&& !key.equals("ETIQUETA"))
            || token == 272 && !key.equals("LONGINT")
            || token == 275 && !key.equals("DOUBLE")
            || (token == 266 && !key.equals("RET"))
            || token == 274 && !key.equals("CML"))
            {
                System.out.println("----- " + key + " -----");
                System.out.println("  Token: " + token);

                if (value.getTipo() != null) {
                    System.out.println("  Tipo: " + value.getTipo());
                }
                if (value.getAmbito() != null) {
                    System.out.println("  Ambito: " + value.getAmbito());
                }
                if (value.getUso() != null) {
                    System.out.println("  Uso: " + value.getUso());
                }
                if (value.getEstructura() != null) {
                    System.out.println("  Estructura: " + value.getEstructura());
                }
                if (value.getlimiteINF() != null) {
                    System.out.println("  LimInf: " + value.getlimiteINF());
                }
                if (value.getlimiteSup() != null) {
                    System.out.println("  LimSup: " + value.getlimiteSup());
                }
                if (value.getTipoParametro() != null) {
                    System.out.println("  TipoParametro: " + value.getTipoParametro());
                }
                if (value.getNombreParametro() != null) {
                    System.out.println("  NombreParametro: " + value.getNombreParametro());
                }

                System.out.println(); // Línea en blanco para separar entradas
            }
        }
    }

    public void addToken(String Lexema,Integer identificador, String tipo) {
        if(!tabla.containsKey(Lexema)){
            DatosTablaSimbolos datos = new DatosTablaSimbolos(identificador);
            datos.setTipo(tipo);
            this.tabla.put(Lexema,datos);
        }
    }

    public void addToken(String lexema, DatosTablaSimbolos datos) {
            this.tabla.put(lexema,datos);
    }

    public DatosTablaSimbolos getDato(String lexema){
        //recorro la tabla para buscar el lexema
        for (Map.Entry<String , DatosTablaSimbolos> entry : tabla.entrySet()) {
            if ((entry.getKey().equals(lexema)) || (entry.getKey().equalsIgnoreCase(lexema))) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void setDato(DatosTablaSimbolos dato, String lexema){
        for (Map.Entry<String , DatosTablaSimbolos> entry : tabla.entrySet()) {
            if ((entry.getKey().equals(lexema)) || (entry.getKey().equalsIgnoreCase(lexema))) {
                entry.getValue().seVuelve(dato);
            }
        }
    }
    public boolean existeLexema(String lexema){
        for (Map.Entry<String , DatosTablaSimbolos> entry : tabla.entrySet()) {
            if ((entry.getKey().equals(lexema)) || (entry.getKey().equalsIgnoreCase(lexema))) {
                return true;
            }
        }
        return false;
    }
    public void borrarLexema(String lexema){
        tabla.remove(lexema);
    }


    public int obtenerToken(String lexema) {
        for (Map.Entry<String , DatosTablaSimbolos> entry : tabla.entrySet()) {
            if ((entry.getKey().equals(lexema)) || (entry.getKey().equalsIgnoreCase(lexema))) {
                return entry.getValue().getToken();
            }
        }
        return NO_ENCONTRADO;
    }


    public int determinarTokenValor(String cadena) {
        int valor = this.obtenerToken(cadena);
        if (valor != -1) {
            /*if (cadena.matches("(?i)\\bLONGINT\\b")) {
                cadena = "LONGINT";
            }
            valor = this.obtenerToken(cadena);

             */
            //System.out.println("encontrado");
            return valor;

        } else if (cadena.matches("^0[xX][0-9a-fA-F]+$")) {
            int hexadecimal = obtenerToken("HEXA");
            //addToken(cadena, null, "LONGINT");

            return hexadecimal;

        } else if (cadena.equals("@")){
            return obtenerToken("@");

        } else if (cadena.matches("^^\\d+$")) {
            int digito = obtenerToken("LONGINT");
            return digito;

        } else if (cadena.equals("<=")){
            return obtenerToken("MENOR_IGUAL");

        } else if (cadena.equals(">=")){
            return obtenerToken("MAYOR_IGUAL");

        } else if (cadena.equals("!=")){
            return obtenerToken("DISTINTO");

        } else if (cadena.equals(":=")){
            return obtenerToken("ASIGNACION");

        } else if (cadena.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            cadena = cadena.toUpperCase();
            int identificador = obtenerToken("ID");
            this.addToken(cadena, identificador, null);
            return identificador;

        } else if (cadena.matches("^[a-zA-Z][a-zA-Z0-9_]*@$")){
            int etiqueta = obtenerToken("ETIQUETA");
            this.addToken(cadena, etiqueta, null);
            return etiqueta;

        } else if (cadena.matches("\\{[\\s\\S]*\\}")){
            int cadenaMultilinea = obtenerToken("CML");
            this.addToken(cadena,cadenaMultilinea, null);
            return cadenaMultilinea;

        } else if (cadena.matches("^\\d+\\.\\d+([dD][+-–]?\\d+)?$")) {
            int identificador = obtenerToken("DOUBLE");
            return identificador;
        }
        return NO_ENCONTRADO;

    }
}
