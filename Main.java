//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.Scanner;


public class Main {
    static AnalizadorLexico lex = null;
    static Parser par = null;

    public static void main(String[] args) {
        try {
            String path;
            path = "";

            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingresar el path de un codigo fuente: ");
            scanner = new Scanner(System.in);
            // Leer una línea de texto
            //path = "src/CodigoFuente3.txt";
            path = scanner.nextLine();
            System.out.println("Texto ingresado: " + path);

            Parser par = new Parser(false);
            AnalizadorLexico lex = new AnalizadorLexico(par, path);
            par.run(lex);
            lex.tablaSimbolos.imprimir();
            System.out.println("Fin de compilación");
            //par.imprimirPolacaFunciones();

            if(!par.polaca.isEmpty() || !par.polacaFunciones.isEmpty()) {
                par.imprimirPolaca();
                par.imprimirPolacaFunciones();
                GeneradorCodigo gen = new GeneradorCodigo(par.polaca, lex.tablaSimbolos, par.polacaFunciones, path, par.errores);
                String codigoGenerado ="";
                if (!par.error)
                   codigoGenerado = gen.generarCodigo();
                if(!par.warnings.isEmpty()) {
                    System.out.println("");
                    System.out.println("------------ IMPRIMIENDO WARNINGS DETECTADOS ------------");
                    par.imprimir(par.warnings);
                    System.out.println("");
                }
                if(!gen.errores.isEmpty()) {
                    System.out.println("");
                    System.out.println("------------ IMPRIMIENDO ERRORES DETECTADOS ------------");
                    par.imprimir(gen.errores);
                    System.out.println("");
                } else
                    System.out.println("Archivo ASM generado correctamente en: " +codigoGenerado);

            }
        } catch (Exception var2) {
            System.err.println("Error durante la ejecución: " + var2.getMessage());
        }

    }
}





