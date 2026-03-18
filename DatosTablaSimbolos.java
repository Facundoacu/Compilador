import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Map;

public class DatosTablaSimbolos {
    private int token;
    private String tipo;
    private String uso;
    private String ambito;
    private String estructura;
    private String tipoParametro;
    private String nombreParametro;
    private ArrayList<String> variables;
    private ArrayList<String> limites;
    //private DatosEspecificos datos;

    public DatosTablaSimbolos (int token){
        this.token = token;
        this.tipo = null;
        this.uso = null;
        this.ambito = null;
        this.estructura=null;
        this.limites= new ArrayList<>();
        this.nombreParametro=null;
        this.tipoParametro=null;
        this.variables = new ArrayList<>();
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setAmbito(String amb){this.ambito = amb;}

    public void setUso(String uso){this.uso = uso;}

    public int getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }


    public String getAmbito() {
        return ambito;
    }

    public String getUso() {
        return uso;
    }
    public void seVuelve(DatosTablaSimbolos dato){
        tipo = dato.tipo;
        uso = dato.uso;
        ambito = dato.ambito;

        //datos = dato.datos
    }
    public  void agregarEnFuncion(){


    }
    public String getEstructura() {
        return estructura;
    }
    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }
    public void setLimites(String sup, String inf) {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";

        if (sup == null || inf == null) {
            return;
        }

        try {

            // Validar y convertir límites superiores
            if (sup.contains(".")) {

            } else {

            }
            if(tipo.equals("DOUBLE")){
                double supValue, infValue;
                infValue = Double.parseDouble(inf);
                supValue = Double.parseDouble(sup);
                if (infValue < supValue) {
                    limites.add(String.valueOf(infValue));
                    limites.add(String.valueOf(supValue));
                } else {
                    System.out.println(ANSI_RED + "ERROR: El límite superior es menor o igual al inferior" + ANSI_RESET);
                }
            }
            else{
                int supValue,infValue;
                supValue = Integer.parseInt(sup);
                infValue = Integer.parseInt(inf);
                if (infValue < supValue) {
                    limites.add(String.valueOf(infValue));
                    limites.add(String.valueOf(supValue));
                } else {
                    System.out.println(ANSI_RED + "ERROR: El límite superior es menor o igual al inferior" + ANSI_RESET);
                }
            }

            // Comparar y agregar límites

        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "ERROR: Formato de número inválido en límites: " + inf + " o " + sup + ANSI_RESET);
        }
    }
    public String getlimiteSup(){
        if(limites.isEmpty())
            return null;
        else
            return (limites.get(1));
    }
    public String getlimiteINF(){
        if(limites.isEmpty())
            return null;
        else
            return (limites.get(0));
    }

    public void setTipoParametro(String tipoParametro) {
        this.tipoParametro = tipoParametro;
    }

    public String getTipoParametro() {
        return tipoParametro;
    }

    public void setNombreParametro(String nombreParametro) {
        this.nombreParametro = nombreParametro;
    }

    public String getNombreParametro() {
        return nombreParametro;
    }
    public ArrayList<String> getListaVar(){
        return variables;
    }
    public void setListaVar(ArrayList<String> vars){
        variables = vars;
    }
    public void addVar(String var){
        variables.add(var);
    }
}
