import java.io.*;

public class LectorDeTexto {
    private BufferedReader br;
    int nroLinea;
    private String linea;
    private int columna;
    private boolean saltoDeLineaPendiente;

    // Constructor modificado para usar InputStream
    public LectorDeTexto(String nombreArchivo) {
        try {
            InputStream inputStream = new FileInputStream(nombreArchivo);
            System.out.println(inputStream);
            // Verificar si el archivo fue encontrado
            if (inputStream == null) {
                System.out.println("No se encontró el archivo: " + nombreArchivo);
                return; // Salir del constructor si el archivo no se encuentra
            }

            br = new BufferedReader(new InputStreamReader(inputStream));

            this.nroLinea = 0;
            this.getNuevaLinea();
            columna = 0;
        } catch (IOException e) {
            System.out.println("Error al abrir el archivo: " + e.getMessage());
        }
    }

    public int getNroLinea() {
        return nroLinea;
    }

    public int getColumna() {
        return this.columna;
    }

    public void getNuevaLinea() {
        try {
            System.out.println("");
            this.linea = br.readLine();
            nroLinea++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarArchivo() {
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char nuevoCaracter() {
        // Si hay un salto de línea pendiente, devolverlo primero
        if (saltoDeLineaPendiente) {
            saltoDeLineaPendiente = false;
            return '\n'; // Devolvemos el salto de línea explícitamente
        }
        if ((linea == null) || (columna >= linea.length())) {
            if (this.hayLineas()) {
                this.getNuevaLinea();
                this.columna = 0;
                return '\n';
            } else {
                System.out.println("fin de archivo");
                return '?'; // Caracter de fin
            }
        }

        char caracter = linea.charAt(columna);
        columna++;
        return caracter;
    }

    public void retrocederCaracter() {
        if (columna > 0) {
            this.columna--;
        }
    }

    public boolean hayLineas() {
        try {
            br.mark(100); // Marcamos la posición actual
            if (br.readLine() == null) {
                return false; // Si no hay más líneas, retornamos false
            }
            br.reset(); // Volvemos a la posición marcada
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true; // Si hay más líneas, retornamos true
    }
}
