import java.awt.*;
import java.math.BigDecimal;

public abstract class AccionesSemantica {
    //protected String Token;
    protected LectorDeTexto lector;
    public AccionesSemantica(LectorDeTexto lector) {
        this.lector = lector;

    }

    public abstract void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter);

    public static class AS1 extends AccionesSemantica {
        private static AS1 instancia;

        private AS1(LectorDeTexto lector) {
            super(lector);
        }

        public static AS1 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS1(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            cadena.append(caracter);
            //System.out.println("Letra que inserta "+"'"+cadena+"'");
        }
    }

    public static class AS2 extends AccionesSemantica {
        private static AS2 instancia;
        protected Parser parser;
        private AS2(LectorDeTexto lector, Parser par) {
            super(lector);
            parser = par;
        }

        public static AS2 obtenerInstancia(LectorDeTexto lector, Parser par) {
            if (instancia == null) {
                instancia = new AS2(lector, par);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            if (cadena.length() > 15) {

                parser.yywarning( "WARNING: Supera el limite indicado para los id en la linea: " + lector.getNroLinea() + " en la columna: " + lector.getColumna() + "\n" + " se trunco el nombre de la variable");
                // Truncar el StringBuilder a 15 caracteres
                cadena.setLength(15); // Establece la longitud máxima de la cadena a 15
            }
            if(!caracter.equals('?')) {
                lector.retrocederCaracter();

            }
        }
    }

    public static class AS3 extends AccionesSemantica {
        private static AS3 instancia;

        private AS3(LectorDeTexto lector) {
            super(lector);
        }

        public static AS3 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS3(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            //informar linea se esperaba un igual y llego otro caracter
            System.out.println("ERROR: Se esperaba un simbolo '=' en la linea: " + lector.getNroLinea() + " en la columna: " + lector.getColumna() +" y se obtuvo el simbolo: "+ caracter);
            if(!caracter.equals('?')) {
                lector.retrocederCaracter();

            }
        }
    }

    //TODO: SE OCUPA CON EL ? creo
    public static class AS4 extends AccionesSemantica {
        private static AS4 instancia;

        private AS4(LectorDeTexto lector) {
            super(lector);
        }

        public static AS4 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS4(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            //System.out.println("");
            System.out.println(cadena);
            //System.out.println("");
            cadena.setLength(0);
        }
    }

    public static class AS5 extends AccionesSemantica {
        private static AS5 instancia;

        private AS5(LectorDeTexto lector) {
            super(lector);
        }

        public static AS5 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS5(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            // Convertir el StringBuilder a String
            String numeroEnCadena = cadena.toString();
            // Convertir la cadena a un número entero
            Long numero = Long.decode(numeroEnCadena);
            long maxValorAbsoluto = 2147483648L;
            if (numero > maxValorAbsoluto) { //TODO: como hacemos con el negativo?
                System.out.println("ERROR: Supera el limite indicado para los longint en la linea: "
                        + lector.getNroLinea() + " en la columna: " + lector.getColumna());
            }

            if(!caracter.equals('?')) {
                lector.retrocederCaracter();
            }
        }
    }


    public static class AS6 extends AccionesSemantica {
        private static AS6 instancia;
        protected Parser parser;
        private AS6(LectorDeTexto lector, Parser parser) {
            super(lector);
            this.parser = parser;
        }


        public static AS6 obtenerInstancia(LectorDeTexto lector, Parser par) {
            if (instancia == null) {
                instancia = new AS6(lector,par);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            //informa error ya que se esperaba un numero, inserta el caracter
            String numeroStr = cadena.toString();
            if(!numeroStr.matches("-?\\d+(\\.\\d+)?d[+-–]?\\d+$")){
            if (numeroStr.matches("^-?\\d+(\\.\\d+)?d[+-–]?$")) {
                parser.yywarning("WARNING: Exponente faltante después de " + cadena + ". Se asume el exponente (1) en la línea: " + lector.getNroLinea());
                cadena.append("1");
            }else if(numeroStr.matches("^-?\\d+(\\.)$")){
                parser.yywarning("WARNING: Parte decimal faltante despues del " + cadena + " Se asume el valor (1) en la línea: " + lector.getNroLinea());
                cadena.append("1");
            }
            }


            if(!caracter.equals('?')) {
             lector.retrocederCaracter();
            }
        }

    }

    public static class AS7 extends AccionesSemantica {
        private static AS7 instancia;

        private AS7(LectorDeTexto lector) {
            super(lector);
        }

        public static AS7 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS7(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            // Retroceder un carácter
            if(!caracter.equals('?')) {
                lector.retrocederCaracter();
            }
        }
    }


    public static class AS8 extends AccionesSemantica {
        private static AS8 instancia;

        private AS8(LectorDeTexto lector) {
            super(lector);
        }

        public static AS8 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS8(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            //se esperaba un numero o una letra A,B,C,D,E,F , y inserto en la linea
            System.out.println("ERROR: Se esperaba un digito o una letra de la A-F en la linea: " + lector.getNroLinea()
                                + " en la columna: " + lector.getColumna() +" y se obtuvo el simbolo: "+"'"+ caracter+"'");
            if(!caracter.equals('?')) {
                lector.retrocederCaracter();
            }
        }
    }

    public static class AS9 extends AccionesSemantica {
        private static AS9 instancia;

        private AS9(LectorDeTexto lector) {
            super(lector);
        }

        public static AS9 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS9(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            // se esperaba un # para comentario, pero no llego, inserta el ultimo caracter
            System.out.println("ERROR: Se esperaba un simbolo '#' en la linea: " + lector.getNroLinea()
                    + " en la columna: " + lector.getColumna() +" y se obtuvo el simbolo: "+ "'"+ caracter+"'");
            lector.retrocederCaracter();
        }

    }

    public static class AS10 extends AccionesSemantica {
        private static AS10 instancia;

        private AS10(LectorDeTexto lector) {
            super(lector);
        }

        public static AS10 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS10(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            // Convertir el número hexadecimal a long
            String hexa = cadena.toString();
            if (hexa.startsWith("0x")) {
                hexa = hexa.substring(2);
            }

            try {
                // Convertimos el string a long especificando que la base es 16 (hexadecimal)
                long num = Long.parseLong(hexa, 16);
                long maxValorAbsoluto = 2147483648L;
                // Verificación del rango
                if (num > maxValorAbsoluto) {
                    System.out.println("ERROR: Supera el limite indicado para los HEXA en la linea: "
                            + lector.getNroLinea() + " en la columna: " + lector.getColumna());
                }
            } catch (NumberFormatException e) {
                System.out.println("Formato de número inválido.");
                // Retroceder un carácter

            }
            if (!caracter.equals('?')) {
                lector.retrocederCaracter();
            }
        }

    }
    public static class AS11 extends AccionesSemantica {
        private static AS11 instancia;

        private AS11(LectorDeTexto lector) {
            super(lector);
        }

        public static AS11 obtenerInstancia(LectorDeTexto lector) {
            if (instancia == null) {
                instancia = new AS11(lector);
            }
            return instancia;
        }

        public void ejecutarAccionSemantica(int accionSemantica, StringBuilder cadena, Character caracter) {
            cadena.append(caracter);
            String c= cadena.toString();
            String result = cadena.toString().replaceAll("[\\t\\n]", "");
            result = result.replaceAll("\\s+", " ");

            cadena.setLength(0);  // Limpiamos el contenido del StringBuilder
            cadena.append(result);
        }
    }

}

