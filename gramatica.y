%{
import java.io.*;
import java.util.Stack;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
%}
        //declaracion de tokens a recibir del Analizador Lexico
%token IF THEN ELSE BEGIN END END_IF OUTF TYPEDEF FUN RET STRING REPEAT WHILE GOTO ID LONGINT HEXA CML DOUBLE TOD STRUCT ASIGNACION DISTINTO MENOR_IGUAL MAYOR_IGUAL ETIQUETA

%left '+' '-'
%left '*' '/'
%left '.'
%left ASIGNACION
%nonassoc LOWER_THAN_ELSE
%nonassoc ELSE
%start prog


%%
prog									: ID BEGIN cuerpo END
							            | ID cuerpo END {yyerror("ERROR, falta begin programa principal en la linea: " + lector.getNroLinea());}
                                        | BEGIN END {yyerror("ERROR, falta el ID del programa principal en la linea: " + lector.getNroLinea());}
                                        | ID BEGIN cuerpo {yyerror("ERROR, falta END del programa principal en la linea: " + lector.getNroLinea());}
                                        | ID cuerpo {yyerror("ERROR, falta BEGIN,END del programa principal en la linea: " + lector.getNroLinea());}
                                        | BEGIN cuerpo  {yyerror("ERROR, falta ID,END del programa principal en la linea: " + lector.getNroLinea());}
                                        ;

cuerpo				                    : cuerpo sentencia
                                        | sentencia
                                        ;

sentencia			                    : sentencia_declaracion
                                        | sentencia_ejecucion

                                        ;

sentencia_declaracion			        : tipo lista_variables ";" {

                                               for (String var : listaVariablesTemp) {
                                                   var = var.toUpperCase();
                                                   if(tipoId){
                                                      String padre = verAlcance($1.sval.toUpperCase());

                                                      if(padre != null){
                                                          DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(padre);
                                                          String tipoPadre = dato.getTipo();
                                                          ArrayList<String> variables = dato.getListaVar();
                                                          ArrayList<String> variablesAux = new ArrayList<String>();
                                                          if(tipoPadre.equals("struct")){
                                                            for(String list: variables){
                                                                String varAux = verAlcance(list);
                                                                DatosTablaSimbolos datoVar = lector.tablaSimbolos.getDato(varAux);

                                                                cargarVariable(var+"_"+list, datoVar.getTipo(), "Nombre de variable", null, null, null, null, null, null,datoVar.getToken());

                                                                variablesAux.add(var+"_"+list);
                                                            }

                                                            cargarVariable(var, tipoPadre, "Nombre de variable", null, null, null, null, null, variablesAux,null);
                                                          }
                                                          else{
                                                               cargarVariable(var, $1.sval.toUpperCase(), "Nombre de variable", tipoPadre, null, null, null, null, null, null);
                                                               lector.tablaSimbolos.borrarLexema(var);
                                                          }
                                                      }
                                                   }
                                                   else{
                                                   if ($1.sval.equalsIgnoreCase("DOUBLE") || $1.sval.equalsIgnoreCase("LONGINT"))
                                                       cargarVariable(var, $1.sval.toUpperCase(), "Nombre de variable", null, null, null, null, null,null, null);
                                                   lector.tablaSimbolos.borrarLexema(var);
                                                   }
                                               }
                                               tipoId = false;
                                               esAsignacionMultiple = false;
                                               listaVariablesTemp.clear();
                                        }
							            | tipo lista_variables {yyerror("ERROR, Falta ; en la sentencia de declaracion en la linea: " + lector.getNroLinea());}
                                        | declaracion_funcion
                                        | TYPEDEF ID ASIGNACION tipo "[" factor "," factor "]" ";"
										{											cargarVariable($2.sval, $4.sval, "Nombre de tipo", null,$8.sval,$6.sval, null, null,null, null);
						    			tiposValidos.add($4.sval);
						    			}
                                        | TYPEDEF ID ASIGNACION tipo "[" factor "," factor ";" {yyerror("ERROR, Falta de ']' en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF ID ASIGNACION tipo factor "," factor  "]" ";" {yyerror("ERROR, Falta de '[' en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF ID ASIGNACION tipo  factor "," factor  ";" {yyerror("ERROR, Falta de llaves '[]' en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF ID ASIGNACION tipo "[" "]" ";" {yyerror("ERROR, Falta de rango en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF ASIGNACION tipo "[" factor "," factor "]" ";" {yyerror("ERROR, Falta nombre del tipo definido en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF ID ASIGNACION "[" factor "," factor "]" ";" {yyerror("ERROR, Falta el tipo base en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF STRUCT "<" lista_tipos ">" "(" lista_variables "," ")" ID ";" {ArrayList<String> variables = new ArrayList<>();
                                                                                                         if (listaTipos.size() == listaVariablesTemp.size()) {


                                                                                                                     for (int i = 0; i < listaTipos.size(); i++) {
                                                                                                                         String tipo = listaTipos.get(i);
                                                                                                                         String variable = listaVariablesTemp.get(i);
                                                                                                                         variables.add(variable);
                                                                                                                         cargarVariable(variable, tipo, "Nombre de variable", $10.sval, null, null, null, null, null, null);
                                                                                                                     }
                                                                                                                 } else {
                                                                                                                     yyerror("Error: la cantidad de tipos y variables del struct no coincide");
                                                                                                                 }
														tiposValidos.add($5.sval);
																													 cargarVariable($10.sval, "struct", "Nombre de struct", null, null, null, null, null, variables, null);
                                                                                                                 listaTipos.clear();
                                                                                                                 listaVariablesTemp.clear();
						    			}
                                        | TYPEDEF STRUCT lista_tipos  "(" lista_variables "," ")" ID ";" {yyerror("ERROR, Falta <> en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF "<" lista_tipos ">" "(" lista_variables "," ")" ID ";" {yyerror("ERROR, Falta la palabra STRUCT en la linea: " + lector.getNroLinea());}
                                        | TYPEDEF STRUCT "<" lista_tipos ">"  "(" lista_variables "," ")"  ";" {
                                            for(int i = 0; i< listaTipos.size(); i++){

                                            }
                                            yyerror("ERROR, Falta ID al final de la declaracion en la linea: " + lector.getNroLinea());
                                        }
                                        ;

actualizar_ambito                       : ID {
                                               $$.sval = $1.sval;
                                             if (!esUnaFuncion || esNuevaFuncion) {

                                                        polacaFunciones.add(new ArrayList<>());
                                                        indiceFuncionActual = polacaFunciones.size() - 1;
                                                        pilaIndiceFuncion.push(indiceFuncionActual);
                                                        esNuevaFuncion = false;
                                                    }
                                             else{
                                             polacaFunciones.add(new ArrayList<>());
                                             indiceFuncionActual = polacaFunciones.size() - 1;
                                             pilaIndiceFuncion.push(indiceFuncionActual);
                                             }
                                                    esUnaFuncion = true;
                                            agregarTokenPolaca($1.sval + "@" + ambitoActual);
                                            ambitoActual = ambitoActual + "@" + $1.sval;
					    //polacaFunciones.add(new ArrayList<>());
                                            //        indiceFuncionActual = polacaFunciones.size() - 1;
                                        }
                                        ;

declaracion_funcion	                    : tipo FUN actualizar_ambito "(" parametro ")" BEGIN cuerpo_funcion END
                                        {

                                            String[] parametro = val_peek(4).sval.split(",");
                                            parametro[1] = parametro[1] + "@" + ambitoActual;


                                                        if ($1.sval.equals("LONGINT")) {
                                                            polacaFunciones.get(pilaIndiceFuncion.peek()).add("0");
                                                            lector.tablaSimbolos.addToken("0",272,"LONGINT");
                                                        } else if ($1.sval.equals("DOUBLE")) {
                                                            polacaFunciones.get(pilaIndiceFuncion.peek()).add("0.0");
                                                            lector.tablaSimbolos.addToken("0.0",275,"DOUBLE");
                                                        }
                                                        polacaFunciones.get(pilaIndiceFuncion.peek()).add("RET@" + ambitoActual);
                                            ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                            //polacaIndiceFuncion.get(pilaIndiceFuncion.peek()).add("RET@"+ ambitoActual  + "@" + $3.sval);
                                            String Ret_simbolo = "RET@" + ambitoActual + "@" + $3.sval;
                                            int token = this.RET;
                                            lector.tablaSimbolos.addToken(Ret_simbolo,token,$1.sval);
                                            cargarVariable(val_peek(6).sval, val_peek(8).sval, "Nombre de Funcion", null, null, null, parametro[0], parametro[1],null,null);
                                            /*lector.tablaSimbolos.borrarLexema($3.sval);*/
                                            int Indice= pilaIndiceFuncion.peek();
					    pilaIndiceFuncion.pop();
					    if (!pilaIndiceFuncion.isEmpty()) {
						indiceFuncionActual = pilaIndiceFuncion.peek();
					    }

					    esUnaFuncion = false;
					    esNuevaFuncion = true;
					    //System.out.println("Antes de romperme" + pilaIndiceFuncion.peek());

					    if (funcionesConRetorno.contains(Indice)) {
					    funcionesConRetorno.remove(Indice);
					    }

                                            //indiceFuncionActual = -1;
                                        }
                                        | tipo actualizar_ambito "(" parametro ")" BEGIN cuerpo_funcion END
                                        {   yyerror("ERROR, Falta la declaracion de la palabra reservada FUN en la linea: " + lector.getNroLinea());
                                            //String[] parametro = $4.split(",");
                                            //parametro[1] = parametro[1] + "@" + ambitoActual;
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                            //cargarVariable($2.sval,$1.sval,"Nombre de Funcion", null, null, null, parametro[0], parametro[1]);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        | tipo FUN "(" parametro ")" BEGIN cuerpo_funcion END
                                        {   yyerror("ERROR, Falta el ID de la funcion en la linea: " + lector.getNroLinea());

                                        }
                                        | tipo FUN actualizar_ambito parametro  BEGIN cuerpo_funcion END
                                        {   yyerror("ERROR, Falta de () a la hora de los parametros en la linea: " + lector.getNroLinea());
                                            //cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null, null);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        | tipo FUN actualizar_ambito "("  ")" BEGIN cuerpo_funcion END
                                        {   yyerror("ERROR, Falta de parametros en la FUN en la linea: " + lector.getNroLinea());
                                            //cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        | tipo FUN actualizar_ambito "(" parametro ")"  cuerpo_funcion  END
                                        {   yyerror("ERROR, Falta de BEGIN en la FUN en la linea: " + lector.getNroLinea());
                                            //cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        | tipo FUN actualizar_ambito "(" parametro ")" BEGIN cuerpo_funcion error
                                        {
                                            //cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        | tipo FUN actualizar_ambito "(" parametro ")" BEGIN error  END
                                        {   yyerror("ERROR, Falsa cuerpo_funcion de funcion en la linea: " + lector.getNroLinea());
                                            //cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);
                                            //ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                        }
                                        ;


cuerpo_funcion  	   	                : cuerpo_funcion sentencia {esUnaFuncion = true;}
                                        | sentencia {esUnaFuncion= true;}
                                        ;

sentencia_ejecucion		                : asignacion
                                        | condicion_if
                                        | sentencia_print
                                        | sentencia_while
                                        | invocacion_funcion ";"
                                        | GOTO ETIQUETA ";"
                                        {
              				            if (esUnaFuncion) {

                                            agregarTokenPolaca($2.sval);  // Marca pendiente
                                            agregarTokenPolaca("#BI");
                                            //int posicionPendiente = obtenerPosicionPolacaEnFuncion(indiceFuncionActual) - 2;
                                            //saltosPendientes.putIfAbsent($2.sval, new ArrayList<>());
                                            //saltosPendientes.get($2.sval).add(new int[] {indiceFuncionActual, posicionPendiente});
                                        } else {
                                            // En el programa principal

                                            agregarTokenPolaca($2.sval);
                                            agregarTokenPolaca("#BI");
                                            //int posicionPendiente = obtenerPosicionPolaca() - 2;
                                            //saltosPendientes.putIfAbsent($2.sval, new ArrayList<>());
                                            //saltosPendientes.get($2.sval).add(new int[] {-1, posicionPendiente});  // -1 para indicar el cuerpo principal
                                        }
                                        }
                                        | GOTO ETIQUETA  {yyerror("ERROR, Falta ';' al final de la sentencia en la linea: " + lector.getNroLinea());}
                                        | GOTO  ";" {yyerror("ERROR, falta la ETIQUETA en la linea: " + lector.getNroLinea());}
                                              //| ETIQUETA ";" {yyerror("ERROR, falta el GOTO en la linea: " + lector.getNroLinea());

                                        | ETIQUETA {
                                            DatosTablaSimbolos d = lector.tablaSimbolos.getDato($1.sval);
                                            d.setUso("Nombre de etiqueta");
                                            lector.tablaSimbolos.setDato(d, $1.sval);
                                             if (esUnaFuncion) {
                                                 // En el contexto de una función
                                                agregarTokenPolaca($1.sval);
                                                 /*int posicionEtiqueta = obtenerPosicionPolacaEnFuncion(indiceFuncionActual);

                                                 if (saltosPendientes.containsKey($1.sval)) {
                                                     for (int[] posicionPendiente : saltosPendientes.get($1.sval)) {
                                                         int funcIndex = posicionPendiente[0];
                                                         int index = posicionPendiente[1];
                                                         if (funcIndex == indiceFuncionActual) {
                                                             polacaFunciones.get(funcIndex).set(index, String.valueOf(posicionEtiqueta));
                                                         }
                                                     }
                                                     saltosPendientes.remove($1.sval);
                                                 }*/
                                             } else {
                                                 // En el programa principal
                                                 agregarTokenPolaca($1.sval);
                                                 /*
                                                 int posicionEtiqueta = obtenerPosicionPolaca();

                                                 if (saltosPendientes.containsKey($1.sval)) {
                                                     for (int[] posicionPendiente : saltosPendientes.get($1.sval)) {
                                                         int funcIndex = posicionPendiente[0];
                                                         int index = posicionPendiente[1];
                                                         if (funcIndex == -1) {
                                                             polaca.set(index, String.valueOf(posicionEtiqueta));
                                                         }
                                                     }
                                                     saltosPendientes.remove($1.sval);
                                                 }*/
                                             }
                                        }
                                        | CML
                                        | RET "(" expresion ")" ";"
                                        {
                                         if (!funcionesConRetorno.contains(pilaIndiceFuncion.peek())) {
                                             //agregarTokenPolaca("RET@" + ambitoActual);
                                             polacaFunciones.get(pilaIndiceFuncion.peek()).add("RET@" + ambitoActual);
                                             funcionesConRetorno.add(pilaIndiceFuncion.peek());
                                         }
                                        }
                                        | RET expresion ";" {yyerror ("ERROR, falta parentesis en el ret");}
                                        ;

sentencia_while 	   	                : repeat bloque_sentencia_ejecutable WHILE "(" condicion ")" ";"
                                        {
                                                     if (esUnaFuncion) {
                                                         // Resolver el salto condicional
                                                         int posicionCondicion = pilaFuncion.pop(); // Posición del #BF
                                                         polacaFunciones.get(indiceFuncionActual).set(
                                                             posicionCondicion,
                                                             String.valueOf(polacaFunciones.get(indiceFuncionActual+2).size())
                                                         );

                                                         // Obtener la posición de inicio del bloque
                                                         int posicionInicio = pilaFuncion.pop();

                                                         // Salto incondicional al inicio del bucle
                                                         agregarTokenPolaca(String.valueOf(posicionInicio));
                                                         agregarTokenPolaca("#BI");

                                                         // Etiqueta para el fin del bucle
                                                         agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                     } else {
                                                         // Resolver el salto condicional
                                                         int posicionCondicion = pila.pop(); // Posición del #BF
                                                         polaca.set(posicionCondicion, String.valueOf(polaca.size()+2));

                                                         // Obtener la posición de inicio del bloque
                                                         int posicionInicio = pila.pop();

                                                         // Salto incondicional al inicio del bucle
                                                         agregarTokenPolaca(String.valueOf(posicionInicio));
                                                         agregarTokenPolaca("#BI");

                                                         // Etiqueta para el fin del bucle
                                                         agregarTokenPolaca(":L" + polaca.size());
                                                     }
                                                 }

                                        | repeat bloque_sentencia_ejecutable WHILE "(" condicion ")"  {yyerror("ERROR, falta palabra ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
                                        | repeat bloque_sentencia_ejecutable WHILE "(" ")" ";" {yyerror("ERROR, falta la condicion del WHILE en la linea: " + lector.getNroLinea());}
                                        | repeat bloque_sentencia_ejecutable WHILE  condicion ")" ";" {yyerror("ERROR, falta parentesis '(' en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
                                        | repeat bloque_sentencia_ejecutable WHILE "(" condicion ";" {yyerror("ERROR, falta parentesis ')' en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
                                        | repeat bloque_sentencia_ejecutable WHILE  condicion ";" {yyerror("ERROR, falta parentesis en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
                                        | repeat WHILE "(" condicion ")" ";" {yyerror("ERROR, falta el cuerpo de la iteracion repeat en la linea: " + lector.getNroLinea());}
                                        ;

repeat                                  : REPEAT{
					      if (esUnaFuncion) {
                                                      // Guardar el inicio del bucle en la pila de funciones
                                                      int posicionInicio = polacaFunciones.get(indiceFuncionActual).size();
                                                      pilaFuncion.push(posicionInicio);

                                                      // Etiqueta de inicio del bucle
                                                      agregarTokenPolaca(":L" + posicionInicio);
                                                  } else {
                                                      // Guardar el inicio del bucle en la pila principal
                                                      int posicionInicio = polaca.size();
                                                      pila.push(posicionInicio);

                                                      // Etiqueta de inicio del bucle
                                                      agregarTokenPolaca(":L" + posicionInicio);
                                                  }
                                        }
                                        ;




sentencia_print					        : OUTF "(" CML ")" ";"
                                        {
                                              String cml = $3.sval;
                                              agregarTokenPolaca(cml);
                                              if (cml.startsWith("{") && cml.endsWith("}")) {
                                                DatosTablaSimbolos d = lector.tablaSimbolos.getDato(cml);
                                                lector.tablaSimbolos.borrarLexema(cml);
                                                cml = cml.substring(1, cml.length() - 1); // Elimina los "{}"
                                                d.setUso("Cadena OUTF");
                                                lector.tablaSimbolos.addToken(cml,d);
                                              } else {
                                                DatosTablaSimbolos d = lector.tablaSimbolos.getDato(cml);
                                                if (d != null){
                                                  d.setUso("Cadena OUTF");
                                                  lector.tablaSimbolos.setDato(d, cml);
                                                }
                                              }

                                              agregarTokenPolaca("OUTF");
                                        }

                                        | OUTF "(" expresion ")" ";"
                                        {
                                         agregarTokenPolaca("OUTF");
                                         }
                                        | OUTF "(" ")" ";" {yyerror("ERROR, Falta parámetro en sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        | OUTF "(" expresion ")" {yyerror("ERROR, Falta ';' en la sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        | OUTF "(" CML ")" {yyerror("ERROR, Falta ';' en la sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        | OUTF CML ";" {yyerror("ERROR, Faltan los parentesis en la sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        | OUTF expresion ";" {yyerror("ERROR, Faltan los parentesis en la sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        | OUTF "(" error ")" ";" {yyerror("ERROR, tipo invalido como parametro para la sentencia OUTF en la linea: " + lector.getNroLinea());}
                                        ;

parametro			                    : tipo ID {//datos parametro
                                            $$.sval= $1.sval + "," + $2.sval;
                                            cargarVariable($2.sval,$1.sval,"Nombre de parametro",null,null,null,null,null, null,null);
				                        }
                                        | tipo {yyerror("ERROR, falta declaracion de TIPO o NOMBRE en el parametro de la linea: " + lector.getNroLinea());}
                                        ;

invocacion_funcion          	        : ID "(" expresion ")" {
                                                        //verficar que la funcion que se invoca exista
                                                        if (lector.tablaSimbolos.obtenerToken($1.sval) == -1)
                                                            yyerror("ERROR: la funcion a la que se quiere acceder no existe");
                                                        else if (!tieneAlcance($1.sval))
                                                            yyerror("ERROR: la funcion a la que se quiere acceder en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");

                                                        agregarTokenPolaca(buscarNombreParametro($1.sval));
                                                        agregarTokenPolaca(":=");

                                                        String nombreFunc = verAlcance($1.sval);
                                                        agregarTokenPolaca(nombreFunc);

                                                        agregarTokenPolaca("CALL");
                                        }
                            	        //| "(" expresion ")" {yyerror("ERROR, falta ID en la invocacion en la linea: " + lector.getNroLinea());}
                                        | ID "(" ")"{yyerror("ERROR, falta parametro en la invocacion de la funcion en la linea: " + lector.getNroLinea());}
                                        ;

bloque_sentencia_ejecutable             : BEGIN lista_sentencias END
					  		            ;

lista_sentencias                        : lista_sentencias sentencia_ejecucion
                                        | sentencia_ejecucion
                                        ;


condicion_if            	            : IF "(" condicion ")" THEN cuerpo_then END_IF ";" {

                                                      int posicionBF = popPila();
                                                      if (esUnaFuncion) {
                                                          polacaFunciones.get(indiceFuncionActual).set(posicionBF, String.valueOf(polacaFunciones.get(indiceFuncionActual).size()-1));
                                                          //agregarTokenPolaca(":L" +  polacaFunciones.get(indiceFuncionActual).size());
                                                      } else {
                                                          polaca.set(posicionBF, String.valueOf(polaca.size()-1));
                                                          //agregarTokenPolaca(":L" + polaca.size());
                                                      }
                                        }
                                        | IF "(" condicion ")" THEN cuerpo_then ELSE cuerpo_else END_IF ";"
                                        {
                                                int posicionBF = popPila();
                                                if (esUnaFuncion) {
                                                polacaFunciones.get(indiceFuncionActual).set(posicionBF, String.valueOf(polacaFunciones.get(indiceFuncionActual).size()));
                                                 agregarTokenPolaca(":L" +  polacaFunciones.get(indiceFuncionActual).size());
                                                 }   else {
                                                    polaca.set(posicionBF, String.valueOf(polaca.size()));
                                                    agregarTokenPolaca(":L" + polaca.size());
                                                }
                                        }
                                        | IF "(" condicion ")" cuerpo_then END_IF ";" {yyerror("ERROR, Falta THEN luego de la condicion en la linea: " + lector.getNroLinea());}
                                        | IF "(" ")" THEN cuerpo_then END_IF ";" {yyerror("ERROR,falta de Condicion en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN error END_IF ";" {yyerror("ERROR,falta el bloque ejecutable en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN cuerpo_then error {yyerror("ERROR,falta END_IF; al final de la declaracion en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN cuerpo_then END_IF {yyerror("ERROR,falta ; al final de la declaracion del bloque IF en la linea: " + lector.getNroLinea());}
                                        | IF THEN cuerpo_then END_IF error {yyerror("ERROR,falta ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN cuerpo_then  cuerpo_else END_IF ";"  {yyerror("ERROR, falta ELSE luego de la sentencias de ejecucion en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN cuerpo_then ELSE cuerpo_else END_IF {yyerror("ERROR,falta ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN cuerpo_then ELSE  END_IF ";" {yyerror("ERROR, falta el bloque ejecutable en el ELSE en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion ")" THEN ELSE cuerpo_else END_IF ";" {yyerror("ERROR, falta el bloque ejecutable en el IF en la linea: " + lector.getNroLinea());}
                                        //| IF "(" condicion ")" THEN cuerpo_then ELSE cuerpo_else ";" {yyerror("ERROR,falta END_IF al final de la declaracion en la linea: " + lector.getNroLinea());}
                                        | IF condicion THEN cuerpo_then END_IF ";" {yyerror("ERROR,falta de parentesis en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion  THEN cuerpo_then END_IF ";" {yyerror("ERROR,falta un parentesis ')' en la linea: " + lector.getNroLinea());}
                                        | IF  condicion ")" THEN cuerpo_then END_IF ";" {yyerror("ERROR,falta un parentesis '(' en la linea: " + lector.getNroLinea());}
                                        | IF "(" condicion  THEN cuerpo_then ELSE cuerpo_else END_IF ";" {yyerror("ERROR,falta un parentesis ')' "); }
                                        | IF condicion ")" THEN cuerpo_then ELSE cuerpo_else END_IF ";" {yyerror("ERROR,falta un parentesis '(' "); }
                                        | IF condicion THEN cuerpo_then ELSE cuerpo_else END_IF ";" {yyerror("ERROR,falta un parentesis '()' "); }
                                        %prec LOWER_THAN_ELSE
                                        ;

cuerpo_then                             : bloque_sentencia_ejecutable
                                        {
                                            int posicion = popPila();
                                            if (esUnaFuncion) {
                                                        polacaFunciones.get(indiceFuncionActual).set(posicion, String.valueOf(polacaFunciones.get(indiceFuncionActual).size() + 2));  // Actualiza el marcador en la Polaca de Funciones
                                                        agregarCheckpoint();
                                                        agregarTokenPolaca("");  // Espacio reservado para BI.
                                                        agregarTokenPolaca("#BI");  // Marcador del BI.
                                                        agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                    } else {
                                                        polaca.set(posicion, String.valueOf(polaca.size() + 2));  // Actualiza el marcador en la Polaca Base
                                                        agregarCheckpoint();  // Solo si es necesario.
                                                        agregarTokenPolaca("");  // Espacio reservado para BI.
                                                        agregarTokenPolaca("#BI");  // Marcador del BI.
                                                        agregarTokenPolaca(":L" + polaca.size());
                                                    }
                                                    //imprimirPolaca();
                                        }
                                        | sentencia_ejecucion
                                            {

                                                int posicion = popPila();

                                                if (esUnaFuncion) {
                                                    polacaFunciones.get(indiceFuncionActual).set(posicion, String.valueOf(polacaFunciones.get(indiceFuncionActual).size() + 2));// Actualiza el marcador en la Polaca de Funciones
                                                    agregarCheckpoint();
                                                    agregarTokenPolaca("");  // Espacio reservado para BI.
                                                    agregarTokenPolaca("#BI");  // Marcador del BI.
                                                    agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                } else {
                                                    polaca.set(posicion, String.valueOf(polaca.size() + 2));  // Actualiza el marcador en la Polaca Base
                                                    agregarCheckpoint();
                                                    agregarTokenPolaca("");  // Espacio reservado para BI.
                                                    agregarTokenPolaca("#BI");  // Marcador del BI.
                                                    agregarTokenPolaca(":L" + polaca.size());
                                                }
                                                //imprimirPolaca();
                                            }

                                        ;

cuerpo_else                             : bloque_sentencia_ejecutable {}
                                        | sentencia_ejecucion
                                        ;
/*
BF se genera para saltar al bloque ELSE si la condición es falsa.
BI se genera para saltar desde el final del bloque THEN al final del IF-ELSE.
Se utiliza backpatching para completar estos saltos utilizando las posiciones almacenadas en la pila.
*/

condicion                               :  expresion comparador expresion {
                                            //agregarTokenPolaca($1.sval);
                                            //agregarTokenPolaca($3.sval);
                                            agregarTokenPolaca($2.sval);
                                            agregarCheckpoint();  // Guardamos la posición del BF en la pila para backpatching.
                                            agregarTokenPolaca("");  // Espacio reservado para BF.
                                            agregarTokenPolaca("#BF");  // Branch False si la condición es falsa.
                                        }
                                        |  expresion error expresion {yyerror("ERROR, falta comparador en comparacion en la linea: " + lector.getNroLinea());}
                                        ;

asignacion                              : lista_variables ASIGNACION lista_expresiones ';'
                                        {$$ = $3;
                                           int j = 0;
                                            Integer cantidadComas = 0;
                                            for (String elemento : expresionTemp) {
                                                       if (elemento.equals(",")) {
                                                           cantidadComas++;
                                                       }
                                                       System.out.println("expresion temp:" +elemento);
                                                   }
                                           System.out.println("size: " + listaVariablesTemp.size());
                                           if (listaVariablesTemp.size() > cantidadComas + 1){
                                            yyerror("ERROR: en la asignacion multiple hay mas Variables que Expresiones ");
                                           }
                                           else {
                                            if(listaVariablesTemp.size() < cantidadComas + 1){
                                              yyerror("ERROR: en la asignacion multiple hay mas Expresiones que Variables");
                                            }
                                           }
                                           for(int i = 0 ; i< listaVariablesTemp.size(); i++){

                                              if(!tieneAlcance(listaVariablesTemp.get(i)))
                                                                   yyerror("ERROR: la variable " + listaVariablesTemp.get(i) + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                              while(j < expresionTemp.size() && expresionTemp.get(j) != ","){


                                               if(expresionTemp.get(j).matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                                                                                       if((lector.tablaSimbolos.getDato(expresionTemp.get(j) + "@" + ambitoActual).getUso() != null)
                                                                                       && (!lector.tablaSimbolos.getDato(expresionTemp.get(j) + "@" + ambitoActual).getUso().equals("Nombre de Funcion")))
                                                                                          agregarTokenPolaca(expresionTemp.get(j) + "@" + ambitoActual);
                                                                                       lector.tablaSimbolos.borrarLexema(expresionTemp.get(j));
                                                                   }
                                               else
                                                    agregarTokenPolaca(expresionTemp.get(j));
                                               j++;
                                              }
                                              j++;
                                                                String var = verAlcance(listaVariablesTemp.get(i));
                                                                if (var != null)
                                                                    agregarTokenPolaca(var);
                                                                agregarTokenPolaca($2.sval);
                                                                lector.tablaSimbolos.borrarLexema(listaVariablesTemp.get(i));
                                                             }
                                                           esAsignacionMultiple = false;
                                                           listaVariablesTemp.clear();
                                                           expresionTemp.clear();

                                        }
                                        ;

expresion						        : expresion "+" termino
                                        {
                                            $$.ival = $1.ival + $3.ival;
                                            if(esAsignacionMultiple)
                                                expresionTemp.add("+");
                                            else
                                                agregarTokenPolaca("+");
                                        }
						    	        | expresion "-" termino
                                        {$$.ival = $1.ival - $3.ival;
                                                                        if(esAsignacionMultiple)
                                                                        	expresionTemp.add("-");
                                    				    else
                                                                        	agregarTokenPolaca("-");
                                        }
						    	        | expresion "+" "+" termino {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());
						    								 agregarTokenPolaca("+");
						    			}
						    	        | expresion "-" "+" termino {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());
						    								 agregarTokenPolaca("-");
						    			}
						    	        //| expresion termino		{yyerror("ERROR, falta de operador en la linea: " + lector.getNroLinea());}
                                        //| "+" termino {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        //| expresion "+" {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        //| expresion "-" {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        //| TOD "(" expresion ")"
                                        {

                                            agregarTokenPolaca("TOD");
                                        }
                                        | TOD "("  ")" {yyerror("ERROR, falta de expresion en la linea: " + lector.getNroLinea());}
                                        | termino {$$.sval = $1.sval;
                                        }
                                        ;

termino				                    : termino "*" factor
                                        {

                                        if(esAsignacionMultiple){
                                            expresionTemp.add($3.sval);
                                            expresionTemp.add("*");}
                                        else{
                                            if($3.sval.matches("^-?\\d+(\\.\\d+)?$") || $3.sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$"))
                                               agregarTokenPolaca($3.sval);
                                                else {
                                                      String var = verAlcance($3.sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            agregarTokenPolaca("*");
                                            }

                                        }
                                        | termino "/" factor
                                        {

                                            if(esAsignacionMultiple){
                                                expresionTemp.add($3.sval);
                                                expresionTemp.add("/");}
                                            else{
                                                if($3.sval.matches("^-?\\d+(\\.\\d+)?$") || $3.sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$"))
                                                   agregarTokenPolaca($3.sval);
                                                else {
                                                      String var = verAlcance($3.sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            agregarTokenPolaca("/");
                                            }

                                        }

                                        | termino "*" "*" factor {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
                                        | termino "*" "/" factor {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
                                        | termino "/" "/" factor {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
                                        | termino "/" "*" factor {yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
                                        //| termino "/" {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        //| termino "*" {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        | "/" factor {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        | "*" factor {yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
                                        | TOD "(" expresion ")"
                                        {

                                        agregarTokenPolaca("TOD");
                                        }
                                        | factor
                                            {$$.sval = $1.sval;
                                            if(esAsignacionMultiple){
                                                String var = verAlcance($1.sval);
                                                DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(var);
                                                if((dato != null) && (dato.getTipo().equals("struct"))){
                                                    ArrayList<String> variables = dato.getListaVar();
                                                    for(int i=0; i < variables.size(); i++){
                                                        expresionTemp.add(variables.get(i));
                                                        if(i != variables.size() - 1)
                                                            expresionTemp.add(",");
                                                    }
                                                }
                                            else
                                                expresionTemp.add($1.sval);
                                            }
                                            else
                                            {
                                                if($1.sval.matches("^-?\\d+(\\.\\d+)?$") || $1.sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$") || $1.sval.matches("^-?0[xX][0-9a-fA-F]+$"))
                                                    agregarTokenPolaca($1.sval);
                                                else {
                                                      String var = verAlcance($1.sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            }
                                        }

                                                //| error {yyerror("ERROR, mal escrita la expresion en la linea: " + lector.getNroLinea());

                                        ;

//agregar token a la polaca
factor				                    : invocacion_funcion	//{$$ = $1;}
                                        |ID	%prec '('{$$.sval= $1.sval;
                                              if(!tieneAlcance($1.sval))
                                                yyerror("ERROR: la variable " + $1.sval + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                              lector.tablaSimbolos.borrarLexema($1.sval);
                                              //agregarTokenPolaca($1.sval);
                                        }
                                        | id_compuesta_exp
                                        {   $$.sval= $1.sval;
                                            if(!tieneAlcance($1.sval))
                                              yyerror("ERROR: la variable " + $1.sval + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                            lector.tablaSimbolos.borrarLexema($1.sval);
                                            //agregarTokenPolaca($1.sval);
                                        }
                                        | LONGINT 	    {
                                                    $$.sval = $1.sval;
                                                    Long valor = Long.parseLong($1.sval);
                                                    if (valor == 2147483648L){
                                                        yyerror("ERROR, El número está fuera del rango permitido para un longint positivo en la linea: " + lector.getNroLinea());
                                                    }
                                                    else{
							    int token =   this.LONGINT;
							    lector.tablaSimbolos.addToken(val_peek(0).sval,token,"LONGINT");
					            }
                                                    //agregarTokenPolaca($1.sval);
                                        }
                                        | "-" LONGINT	{
                                                    $$.sval = "-" + $2.sval; //TODO: posible error
                                                    String lexema = '-'+ $2.sval;
                                                    Long valor = Long.parseLong($2.sval);
                                                    if (valor > 2147483648L){

                                                    }
                                                    else {
                                                        	int token =   this.LONGINT;
                                                                lector.tablaSimbolos.addToken(lexema,token,"LONGINT");
                                                    }
                                                    //agregarTokenPolaca(lexema);
                                        }

                                        | HEXA		    {
                                                    $$.sval = $1.sval;
                                                    String hexa = $1.sval;
                                                    if (hexa.startsWith("0x") || hexa.startsWith("0X")) {
                                                        hexa = hexa.substring(2);
                                                    }
                                                    long num = Long.parseLong(hexa, 16);
                                                    long maxValorAbsoluto = 2147483648L;
                                                    if (num == maxValorAbsoluto){
                                                        yyerror("ERROR, El número está fuera del rango permitido para un HEXA positivo en la linea: " + lector.getNroLinea());
                                                    }
                                                    int token =   this.LONGINT;
                                                    lector.tablaSimbolos.addToken($1.sval,token,"LONGINT");
                                                    //agregarTokenPolaca($1.sval);
                                        }
                                        | "-" HEXA	    {
                                                    $$.sval = "-" + $2.sval;
                                                    String lexema = '-'+ $2.sval;
                                                    int token =   this.LONGINT;
                                                    lector.tablaSimbolos.addToken(lexema,token,"LONGINT");
                                                    //agregarTokenPolaca(lexema);
                                        }
                                        | DOUBLE 	    {
                                                    $$.sval = $1.sval ; // valor del número
                                                    String valor = $1.sval;
                                                    try {
                                                        String numeroStr = valor;

                                                        if (numeroStr.contains("d") || numeroStr.contains("D")) {
                                                          numeroStr = numeroStr.replace('d', 'E').replace('D', 'E');
                                                        }

                                                        // Convertimos el valor a BigDecimal
                                                        BigDecimal numero = new BigDecimal(numeroStr);
                                                        BigDecimal min = new BigDecimal("2.2250738585072014E-308");
                                                        BigDecimal max = new BigDecimal("1.7976931348623157E+308");

                                                        // Comparamos el número con los límites permitidos
                                                        if ((numero.compareTo(max) > 0) || (numero.compareTo(min) < 0 && numero.compareTo(BigDecimal.ZERO) != 0)){
                                                          yyerror("ERROR, El número está fuera del rango permitido para un double positivo en la linea: " + lector.getNroLinea());
                                                        } else {
                                                          int token = DOUBLE;
                                                          lector.tablaSimbolos.addToken(valor, token, "DOUBLE");  // Añade el token
                                                          //agregarTokenPolaca($1.sval);
                                                        }

                                                      } catch (NumberFormatException e) {
                                                        yyerror("Formato de número inválido.");
                                                      }

                                        }
                                        |"-" DOUBLE     {

                                                    $$.sval = "-" + $2.sval;
                                                    String valor = $2.sval;
                                                    try {
                                                          String numeroStr = valor;
                                                          if (numeroStr.contains("d") || numeroStr.contains("D")) {
                                                            numeroStr = numeroStr.replace('d', 'E').replace('D', 'E');
                                                          }
                                                            BigDecimal numero = new BigDecimal(numeroStr).negate();
                                                            BigDecimal min = new BigDecimal("-1.7976931348623157E+308");
                                                            BigDecimal max = new BigDecimal("-2.2250738585072014E-308");
                                                            if (numero.compareTo(max) > 0 || numero.compareTo(min) < 0) {
                                                              yyerror("ERROR, El número está fuera del rango permitido para un double negativo en la linea: " + lector.getNroLinea());
                                                            } else {
                                                              int token = this.DOUBLE;
                                                              String negativo = "-" + valor;
                                                              //agregarTokenPolaca("negativo");
                                                              lector.tablaSimbolos.addToken(negativo, token,"DOUBLE");
                                                            }
                                                          } catch (NumberFormatException e) {
                                                            yyerror("Formato de número inválido.");
                                                          }
                                        }
                                        ;

lista_variables                         : lista_variables "," ID {
                                            esAsignacionMultiple = true;
                                            listaVariablesTemp.add($3.sval);
                                        }
                                        | lista_variables "," id_compuesta_var {
                                            esAsignacionMultiple = true;
                                            listaVariablesTemp.add($3.sval);
                                            }
                                        | id_compuesta_var
                                        | ID {
                                            String var = verAlcance($1.sval);
                                            DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(var);
                                            if((dato != null) && (dato.getTipo().equals("struct"))){
                                                esAsignacionMultiple = true;
                                                ArrayList<String> variables = dato.getListaVar();
                                                for(String varAux: variables){
                                                    listaVariablesTemp.add(varAux);
                                                }
                                            }
                                            else
                                                listaVariablesTemp.add($1.sval);
                                        }
                                        ;

id_compuesta_var                	    : ID "." ID
										{listaVariablesTemp.add($1.sval + "_" + $3.sval);}
                                        ;

id_compuesta_exp                	    : ID "." ID{$$.sval = $1.sval + "_" + $3.sval; }
										;

coma 				                    : "," {expresionTemp.add(",");}

lista_expresiones		                : lista_expresiones coma expresion
                                        {//expresionTemp.add($3.sval);
                                         //expresionTemp.add(",");
                                        }
                                        //| lista_expresiones expresion {yyerror("ERROR, falta de ',' en la lista en la linea: " + lector.getNroLinea());}
                                        | expresion
                                        {//expresionTemp.add($1.sval);
                                        }
                                        //| expresion error ";"
                                        ;

lista_tipos			                    : lista_tipos "," tipo
                                        {   listaTipos.add($3.sval);

                                        }
                                        | lista_tipos tipo  {yyerror("ERROR, falta de ',' en la lista en la linea: " + lector.getNroLinea());}
                                        | tipo {listaTipos.add($1.sval);}
                                        ;

tipo		 					        : DOUBLE /*{

                                            if (!tiposValidos.contains($1.sval))
                                                tiposValidos.add($1.sval);

                                        }*/
                                        | LONGINT /*{if (!tiposValidos.contains($1.sval))
                                                    tiposValidos.add($1.sval);}*/
                                        | ID {tipoId = true;
                                            lector.tablaSimbolos.borrarLexema($1.sval);
                                            String nombre = verAlcance($1.sval);
                                            DatosTablaSimbolos dato= lector.tablaSimbolos.getDato(nombre);
                                            if(dato == null || (!dato.getUso().equals("Nombre de tipo") && !dato.getUso().equals("Nombre de struct")))
                                                yyerror("ERROR: Tipo no válido: " + $1.sval + " en la línea: " + lector.getNroLinea());
                                        }
                                        | TYPEDEF ID {//$$ = $1
                                        lector.tablaSimbolos.borrarLexema($2.sval);
                                        }
                                        ;

comparador 					            : "<"
                                        | ">"
                                        | MAYOR_IGUAL
                                        | MENOR_IGUAL
                                        | DISTINTO
                                        | "="
                                        ;
%%
void yyerror(String mensaje) {
  error = true;
  // funcion utilizada para imprimir errores que produce yacc
  System.out.println( mensaje );
  errores.add(mensaje);
}

  void yywarning(String mensaje) {
    // funcion utilizada para imprimir errores que produce yacc
    System.out.println( mensaje );
    warnings.add(mensaje);
  }

String ambitoActual = "main";
public static ArrayList<String> listaVariablesTemp = new ArrayList<>();
public static ArrayList<String> listaTipos = new ArrayList<>();
public static ArrayList<String> tiposValidos = new ArrayList<>();
public static ArrayList<String> expresionTemp = new ArrayList<>();
public static ArrayList<ArrayList<String>> polacaFunciones = new ArrayList<>();
public static ArrayList<String> errores = new ArrayList<>();
public static ArrayList<String> warnings = new ArrayList<>();
public static boolean esUnaFuncion = false;
public static boolean esNuevaFuncion = false;
public static boolean esAsignacionMultiple = false;
public static boolean tipoId = false;
AnalizadorLexico lector;
public static ArrayList<String> polaca = new ArrayList<>();
public static ArrayList<String> posicionDePolaca = new ArrayList<>();
public static Stack<Integer> pila = new Stack<>(); //para bifuraciones en while/if algun otro
public static Stack<Integer> pilaFuncion = new Stack<>();// para bifurcaciones en if/while pero en funciones??
public static Stack<Integer> pilaIndiceFuncion = new Stack<>();// para guardar las posicion donde van los returns osea el indice de la funcion
public static Integer indiceFuncionActual=0;
public static Set<Integer> funcionesConRetorno = new HashSet<>();
// La clave es el nombre de la etiqueta y el valor es una lista de posiciones pendientes.
Map<String, ArrayList<int[]>> saltosPendientes = new HashMap<>();
public static boolean error = false;
public void pushPila(int valor) {
    if (esUnaFuncion) {
        pilaFuncion.push(valor);
    } else {
        pila.push(valor);
    }
}
public int popPila() {
    if (esUnaFuncion) {
        return pilaFuncion.pop();
    } else {
        return pila.pop();
    }
}

public int obtenerPosicionPolaca() {
    return polaca.size();
}
public void verificarEtiquetas() {
    for (String etiqueta : saltosPendientes.keySet()) {
        yyerror("ERROR: La etiqueta " + etiqueta + " no está definida en el código.");
    }
}
public static void agregarTokenPolaca(String lexema){
	 if (esUnaFuncion) {

                polacaFunciones.get(indiceFuncionActual).add(lexema);
         } else{

		polaca.add(lexema);
		}
}

public void imprimirPolacaFunciones() {
  if (!polacaFunciones.isEmpty()) {
    System.out.println();
    System.out.println("-------- IMPRIMIENDO CODIGO INTERMEDIO FUNCIONES (POLACA) --------");
    for (int i = 0; i < polacaFunciones.size(); i++) {
      System.out.println("Funcion: " + i);
      ArrayList<String> expresionFuncion = polacaFunciones.get(i);
      imprimir(expresionFuncion);
      System.out.println("---- Fin funcion " + i + " ----");
      System.out.println();
    }
    System.out.println("-------- FIN CODIGO INTERMEDIO FUNCIONES--------");
    System.out.println(); // Nueva línea al final de cada función
  }
}

public static void eliminarTokenPolaka() {
  polaca.remove(polaca.size() - 1);
}
public static void agregarCheckpoint(){
	if (esUnaFuncion)
	{
		pilaFuncion.push(polacaFunciones.get(indiceFuncionActual).size());
	}
	else
		pila.push(polaca.size()); //guardamos el checkpoint en caso de una difurcacion
}
int yylex(){
    return lector.yylex();
}

public static void imprimirPolaca() {
    // Imprimo la polaca inversa generada en el programa
    if (!polaca.isEmpty()) {
        System.out.println();
        System.out.println("------------ IMPRIMIENDO CODIGO INTERMEDIO (POLACA) ------------");

        for (int i = 0; i < polaca.size(); ++i) {
                System.out.println(i + " " + polaca.get(i));
        }
        System.out.println("-------- FIN CODIGO INTERMEDIO --------");
    }
}
public int obtenerPosicionPolacaEnFuncion(int indiceFuncion) {
    if (indiceFuncion < 0 || indiceFuncion >= polacaFunciones.size()) {
        yyerror("ERROR: El índice de función es inválido.");
        return -1;
    }
    ArrayList<String> polacaDeFuncion = polacaFunciones.get(indiceFuncion);
    return polacaDeFuncion.size();
}
public void agregarTokenPolacaEnFuncion( String token, int indiceFuncion) {
    if (indiceFuncion < 0 || indiceFuncion >= polacaFunciones.size()) {
        yyerror("ERROR: El índice de función es inválido.");
        return;
    }
    ArrayList<String> polacaDeFuncion = polacaFunciones.get(indiceFuncion);

    // Agregamos el token a la lista de instrucciones de la función
    polacaDeFuncion.add(token);
}
public static void imprimir(ArrayList<String> lista){
	for (int i = 0; i< lista.size();i++){
	    System.out.println(i + " " + lista.get(i));

	}
}

public void cargarVariable (String lexema, String tipo, String uso, String estructura,String limSup, String limInf, String tipoParametro, String nombreParametro, ArrayList<String> variables, Integer token){
  DatosTablaSimbolos datos = lector.tablaSimbolos.getDato(lexema); //chequear, se pisan los datos
  String auxLexema = lexema + "@" + ambitoActual;
  if(!lector.tablaSimbolos.existeLexema(auxLexema)) {
    DatosTablaSimbolos d;
    if(datos != null)
      d = new DatosTablaSimbolos(datos.getToken());
    else
      d = new DatosTablaSimbolos(token);
    lector.tablaSimbolos.borrarLexema(lexema);
    d.setTipo(tipo);
    d.setUso(uso);
    d.setAmbito(auxLexema);
    d.setEstructura(estructura);
    d.setLimites(limSup, limInf);
    d.setTipoParametro(tipoParametro);
    d.setNombreParametro(nombreParametro);
    d.setListaVar(variables);
    lector.tablaSimbolos.addToken(auxLexema,d);
  }
  else{
    if (uso.equals("Nombre de Funcion"))
        yyerror("ERROR: se esta redeclarando la funcion: " + lexema + " en la linea " + lector.getNroLinea());
    if (uso.equals("Nombre de variable"))
        yyerror("ERROR: se esta redeclarando la variable: " + lexema + " en la linea " + lector.getNroLinea());
    if (uso.equals("Nombre de tipo"))
        yyerror("ERROR: se esta redeclarando el tipo: " + lexema + " en la linea " + lector.getNroLinea());
  }
}

/*
 DatosTablaSimbolos dato1 = lector.tablaSimbolos.getDato(variable);
 DatosTablaSimbolos dato2 = lector.tablaSimbolos.getDato(expresion);
 String tipoVariable = dato1.getTipo();
 String tipoExpresion = dato2.getTipo();
 if (!tipoVariable.equals(tipoExpresion)) {
 yyerror("ERROR: incompatibilidad de tipos entre " + variable + " y " + expresion + " en la línea " + lector.getNroLinea());
 }
 */


public boolean tieneAlcance(String var){
  String auxAmbito = var + "@" + ambitoActual;
  DatosTablaSimbolos dato = null;

  while(!auxAmbito.equals(var)) {
    if(lector.tablaSimbolos.existeLexema(auxAmbito))
        return true;
    auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
  }
    return false;
}
public String buscarNombreParametro(String funcion){
  String auxAmbito = funcion + "@" + ambitoActual;
  DatosTablaSimbolos dato = null;

  while(!auxAmbito.equals(funcion)) {
    if(lector.tablaSimbolos.existeLexema(auxAmbito)) {
      DatosTablaSimbolos datoFuncion = lector.tablaSimbolos.getDato(auxAmbito);
      return datoFuncion.getNombreParametro();
    }
    auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
  }
  return null;
}

public String verAlcance(String var){
      String auxAmbito = var + "@" + ambitoActual;
      DatosTablaSimbolos dato = null;

    while(!auxAmbito.equals(var)) {
      if(lector.tablaSimbolos.existeLexema(auxAmbito))
        return auxAmbito;
      auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
    }
  return null;
}