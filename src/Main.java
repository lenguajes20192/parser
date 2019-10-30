import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main {

    /* _____________________ LEXER _________________________________ */

    static String[] resWords = new String[]{ "af", "and", "binding","by", "body", "cap", "coenter", "col", "create","destroy", "do",
            "else", "end", "external", "fa", "fi", "file", "final", "get", "getarg", "global", "if",
            "import", "int", "mod", "new","noop", "oc", "op", "or","procedure", "process", "read",
            "real", "ref", "res", "resource", "returns", "scanf", "sem", "send","sprintf", "st",
            "stop", "to", "val", "var", "write", "writes","receive","char","reply","next","string",
            "bool","ni","co","proc"};

    static String[] symbols = new String[]{"{","}",":=",":",",","[","]",";","(",")","+","++","--","<","<=",">",">=","!=",":=:","->",
            "-", "[]", "=", "%", ".", "|", "/", "*", "<<", ">>", "||", "**", "<<:=", ">>:=", "||:=", "**:=", "*:=", "/:=", "+:=",
            "-:=", "%:=", "%", "&:=", "&", "|:="};

    static String[] values = new String[]{"tk_llave_i", "tk_llave_d", "tk_asig", "tk_dos_puntos", "tk_coma", "tk_cor_izq",
            "tk_cor_der", "tk_punto_y_coma", "tk_par_izq", "tk_par_der", "tk_suma", "tk_incr", "tk_decr", "tk_menorque",
            "tk_menor_igual", "tk_mayorque", "tk_mayor_igual", "tk_distinto", "tk_swap", "tk_ejecuta",
            "tk_menos", "tk_separa", "tk_igual", "tk_mod","tk_punto","tk_or", "tk_div", "tk_multi", "tk_lshift", "tk_rshift",
            "tk_concat", "tk_exp", "tk_lsasig", "tk_rsasig", "tk_concasig", "tk_expasig", "tk_mulasig", "tk_divasig", "tk_sumasig",
            "tk_resasig", "tk_remasig", "tk_rem", "tk_andasig", "tk_and", "tk_orasig"};

    static HashSet<String> h = new HashSet<String>();
    static HashMap<String, String> mpLexer = new HashMap<String, String>();

    public static class Token {
        public int row;
        public int column;
        public String lexeme;
        public int type;

        public Token(int type, String lexemeValue, int rowValue, int columnValue){
            this.type= type;
            lexeme = lexemeValue;
            row = rowValue;
            column = columnValue;
        }

        public void printTok(){
            switch(type){
                case 1:
                    if(h.contains(lexeme))// if reserved
                        System.out.println("<" + lexeme+","+ row + ","+ column+">");
                    else
                        System.out.println("<id," + lexeme+"," + row + "," + column + ">");

                    break;

                case 2://belong to symbols
                    System.out.println("<" + mpLexer.get(lexeme) + "," + row + "," + column + ">");
                    break;

                case 3://is number
                    System.out.println("<tk_num," + lexeme + "," + row + "," + column + ">");
                    break;

                case 4://is string
                    System.out.println("<tk_cadena," + lexeme + "," + row + "," + column + ">");
                    break;

                default:
                    break;
            }
        }
    }

    static void init() {
        h.addAll(Arrays.asList(resWords));
        //System.out.println("No match")
        for (int i = 0; i < symbols.length; i++) {
            mpLexer.put(symbols[i], values[i]);
        }
    }

    static void printError(int row, int column){
        System.out.println(">>>Error_lexico(linea:" + row + ",posicion:" + (column + 1) + ")");
    }

    /* _____________________ LEXER END _________________________________ */


    private static HashMap<String, String[][]> mp = new HashMap<>();
    private static ArrayList<String>  arrOfKeys = new ArrayList<>();
    private static Deque<String> keys = new LinkedList<>();
    private static Deque<String[]> val = new LinkedList<>();
    private static HashMap<String, HashSet<String>> first = new HashMap<>();
    private static HashMap<String, HashSet<String>> follow = new HashMap<>();


    private static void primeros(){

        for(int i = 0; i < mp.size(); i++){
            String queueHead = keys.removeLast();
            String[][] values = mp.get(queueHead);
            //System.out.println(Arrays.deepToString(mp.get(queueHead)));
            //System.out.println(values.length);
            keys.addFirst(queueHead);
            HashSet<String> pos = new HashSet<>();
            for(int j = values.length - 1; j >= 0 ; j--){
                if(!keys.contains(values[j][0])){
                    pos.add(values[j][0]);
                }else if (first.get(values[j][0]).contains("epsilon") && values[j].length == 1){

                    Set<String> difference = new HashSet<>(first.get(values[j][0]));
                    difference.remove("epsilon");
                    pos.addAll(difference);

                }else if(first.get(values[j][0]).contains("epsilon") && first.get(values[j][1]) != null) {

                    Set<String> difference = new HashSet<>(first.get(values[j][0]));
                    difference.remove("epsilon");
                    pos.addAll(difference);

                    pos.addAll(first.get(values[j][1]));

                }else{
                    pos.addAll(first.get(values[j][0]));

                }
            }
            first.put(queueHead, pos);
        }
        //System.out.println(Collections.singletonList(first.get("A")));
    }

    private static void siguientes(){
        Deque<String> keysAux = new LinkedList<>();
        for (int i=0; i<keys.size(); i++){ //make a copy of keys to iterate rules
            String element = keys.removeFirst();
            keysAux.add(element);
            keys.addLast(element);
        }

        for(int i = 0; i < mp.size(); i++){
            String keyFollow = keys.removeFirst(); //KeyFollow is the NT to which Follow is being calculated
            keys.addLast(keyFollow);
            HashSet<String> sig = new HashSet<>(); //element's set to follows
            if(keyFollow == arrOfKeys.get(0)){
                sig.add("$");
            }
            for(int k = 0; k < mp.size(); k++){ //iterate rules
                String keyRule = keysAux.removeFirst();
                keysAux.addLast(keyRule);
                String[][] values = mp.get(keyRule);
                for(int j = 0; j < values.length; j++){
                    List<String> valuesList = Arrays.asList(values[j]);
                    if(valuesList.contains(keyFollow)){
                        if(valuesList.indexOf(keyFollow) == valuesList.size()-1){ //el que se esta evaluando es el ultimo
                            sig.addAll(follow.get(keyRule));
                        }
                        else if (!keys.contains(valuesList.get(valuesList.indexOf(keyFollow)+1))){  //el contiguo es un T
                            sig.add(valuesList.get(valuesList.indexOf(keyFollow)+1));
                        }else if (keys.contains(valuesList.get(valuesList.indexOf(keyFollow)+1))){ //el contiguo es un NT
                            sig.addAll(first.get(valuesList.get(valuesList.indexOf(keyFollow)+1)));
                            sig.remove("epsilon");
                        }
                    }
                }
            }
            follow.put(keyFollow, sig);
        }
        /*
        System.out.println(Collections.singletonList("Siguientes A: "+follow.get("A")));
        System.out.println(Collections.singletonList("Siguientes B: "+follow.get("B")));
        System.out.println(Collections.singletonList("Siguientes C: "+follow.get("C")));
        */
    }
    private static void prediction(){

        ArrayList<Set> pred = new ArrayList<>();
        for(int i = 0; i < mp.size(); i++) {
            String queueHead = keys.removeFirst();
            String[][] values = mp.get(queueHead);
            keys.addLast(queueHead);
            for (int j = 0; j < values.length; j++) {
                HashSet pred_j = new HashSet();
                //System.out.println(values[j][0]);
                if(values[j][0].equals("epsilon")){
                    pred_j.addAll(follow.get(queueHead));
                }else if(keys.contains(values[j][0]) && first.get(values[j][0]).contains("epsilon")){
                    pred_j.addAll(first.get(values[j][0]));
                    pred_j.addAll(follow.get(values[j][0]));
                    pred_j.remove("epsilon");
                    if(pred_j.contains("$")){
                        pred_j.remove("$");
                    }
                }else if(keys.contains(values[j][0])){
                    pred_j.addAll(first.get(values[j][0]));
                }else{
                    pred_j.add(values[j][0]);
                }
                pred.add(pred_j);
            }
        }
        System.out.println(Arrays.asList(pred));

    }

    void emparejar(TElementoLexico tokEsperado){
        if (token == tokEsperado)
            token = lexico.getNextToken();
        else
            errorSintaxis(tokEsperado);
    }

    public static void main(String [] args){

        try {

            File file =
                    new File("grammar3.txt");
            Scanner sc = new Scanner(file);

            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine() ;
                String[] arrOfStr = line.split("->");
                String[] arrOfVal = arrOfStr[1].split(" ");
                arrOfKeys.add(arrOfStr[0]);
                if(!keys.contains(arrOfStr[0])){
                    keys.addLast(arrOfStr[0]);
                }
                val.addLast(arrOfVal);
            }
            for(String s: keys){

                int arrSize = Collections.frequency(arrOfKeys, s);
                String[][] values = new String[arrSize][1];
                for(int i = 0; i < arrSize; i++) {
                    values[i] = val.pop();
                    val.addLast(values[i]);
                }
                mp.put(s, values);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        primeros();
        siguientes();
        prediction();
    }

}

