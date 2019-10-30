import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Main {

    /* _____________________ LEXER _________________________________ */

    static String[] resWords = new String[]{ "af", "and", "binding","by", "body", "cap", "coenter", "col", "create","destroy", "do",
            "else", "end", "external", "fa", "fi", "file", "final", "get", "getarg", "global", "if",
            "import", "int", "mod", "new","noop", "oc", "op", "or","procedure", "process", "read",
            "real", "ref", "res", "resource", "returns", "scanf", "sem", "send","sprintf", "st",
            "stop", "to", "val", "var", "write", "writes","receive","char","reply","next","string",
            "bool","ni","co","proc","epsilon"};

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

    public static String printTok(int type, String lexeme, int row, int column){
        String nextToken = "";
        switch(type){
            case 1:
                if(h.contains(lexeme)) {// if reserved
                    System.out.println("<" + lexeme + "," + row + "," + column + ">");
                    nextToken = lexeme;
                }else {
                    System.out.println("<id," + lexeme + "," + row + "," + column + ">");
                    nextToken = lexeme;
                }
                break;

            case 2://belong to symbols
                System.out.println("<" + mpLexer.get(lexeme) + "," + row + "," + column + ">");
                nextToken = mpLexer.get(lexeme);
                break;

            case 3://is number
                System.out.println("<tk_num," + lexeme + "," + row + "," + column + ">");
                nextToken = lexeme;
                break;

            case 4://is string
                System.out.println("<tk_cadena," + lexeme + "," + row + "," + column + ">");
                nextToken = lexeme;
                break;

            default:
                break;
        }
        return nextToken;
    }
    
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

    private static void lexer(){
        init();
        try {

            File file =
                    new File("1.txt");
            Scanner sc = new Scanner(file);

            String line;

            int row = 1;
            int column = 0;
            int start = 0;
            while (sc.hasNextLine()) {
                char st='1';
                line = sc.nextLine() ;
                line = line + '\n';
                for(int i = 0; i < line.length(); i++){
                    column = i + 1;
                    String character = Character.toString(line.charAt(i));
                    switch (st){
                        case '1':
                            if(line.charAt(i) == '\n'){
                                column--;
                                break;
                            }
                            start = i;
                            if(line.charAt(i) == '#'){
                                i = line.length();
                                break;
                            }
                            else if(line.charAt(i) == ' ' || Pattern.matches("[\t]", character)) {
                                break;

                            }else if(Pattern.matches("[a-zA-Z]", character)) {
                                st = '2';
                                break;
                            }else if(line.charAt(i) >='0' && line.charAt(i) <= '9'){
                                st = '3';
                                break;
                            }else if(line.charAt(i) == '"') {
                                st = '4';
                                break;
                            }else if(line.charAt(i) == ':') {
                                st = '6';
                                break;
                            }else if(line.charAt(i) == '<' || line.charAt(i) == '>' //caracteres que pueden ir solos o acompañados
                                    || line.charAt(i) == '-' || line.charAt(i) == '+' || line.charAt(i) == '='
                                    || line.charAt(i) == '|' || line.charAt(i) == '*' || line.charAt(i) == '/'
                                    || line.charAt(i) == '&' || line.charAt(i) == '%'){
                                st = '5';
                                break;

                            }else if(line.charAt(i) == '!'){//caracteres que no pueden ir solos
                                st = '7';
                                break;

                            }else if (line.charAt(i) == '['){
                                st = '8';
                                break;
                            }else if(mp.containsKey(String.valueOf(line.charAt(i)))){//se toman los solos
                                Token t = new Token(2, String.valueOf(line.charAt(i)), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                break;
                            }else{
                                printError(row, start);
                                System.exit(0);
                            }
                        case '2':
                            if(line.charAt(i) == '#'){
                                Token t = new Token(1, line.substring(start , i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                i = line.length();
                                break;
                            }else if(line.charAt(i) >='0' && line.charAt(i) <= '9'){
                                break;
                            }else if(Pattern.matches("[a-zA-Z_]", character)) {
                                break;
                            }else if(line.charAt(i) == '-') {//variable resta
                                Token t = new Token(1, line.substring(start , i), row, start + 1);
                                Token t2 = new Token(2, String.valueOf(line.charAt(i)), row, start + 2);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                printTok(t2.type, t2.lexeme, t2.row, t2.column);
                                st = '1';
                                break;

                            }else{
                                Token t = new Token(1, line.substring(start , i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                i--;
                                break;
                            }
                        case '3':
                            if(line.charAt(i) == '#') {
                                Token t = new Token(3, line.substring(start, i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                i = line.length();
                                break;
                            }else if(line.charAt(i) >='0' && line.charAt(i) <= '9'){
                                break;

                            }else if(line.charAt(i) == '.'){//90.88.00
                                //System.out.println(line.charAt(i));
                                //System.out.println(line.substring(start, i));
                                if(line.substring(start, i).contains(".")) {
                                    Token t = new Token(3, line.substring(start , i), row, start + 1);
                                    printTok(t.type, t.lexeme, t.row, t.column);
                                    i--;
                                    st = '1';
                                    break;
                                }
                                break;

                            }else if(line.charAt(i) == '-') {//num resta
                                Token t = new Token(3, line.substring(start , i), row, start + 1);
                                Token t2 = new Token(2, String.valueOf(line.charAt(i)), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                printTok(t2.type, t2.lexeme, t2.row, t2.column);
                                st = '1';
                                break;

                            }
                            else{
                                Token t = new Token(3, line.substring(start , i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                i--;
                                break;
                            }
                        case '4':
                            if(line.charAt(i) == '\n'){
                                column = start + 1;
                                printError(row, start);
                                System.exit(0);
                            }else if(line.charAt(i) == '"'){
                                Token t = new Token(4, line.substring(start , i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else{
                                break;
                            }
                        case '5':
                            if(line.charAt(i) == '#') {
                                if(mp.containsKey(line.substring(start, i))) {
                                    Token t = new Token(2, line.substring(start, i), row, start + 1);
                                    printTok(t.type, t.lexeme, t.row, t.column);
                                }else{
                                    printError(row, start);
                                    System.exit(0);
                                }
                                i = line.length();
                                break;

                            }else if (line.charAt(i - 1) == '-' && (line.charAt(i) >='0' && line.charAt(i) <= '9')){ // negative number
                                st = '3';
                                break;
                            }else if(line.charAt(i) == '='){
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                break;
                            }else if(line.charAt(i -1) == '-' && line.charAt(i) == '>') {// tk_ejecuta
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else if(line.charAt(i) == '+' && line.charAt(i - 1) == '+') {
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else if(line.charAt(i) == '-' && line.charAt(i - 1) == '-'){
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else if(line.charAt(i) == '>'&& line.charAt(i - 1) == '>' || (line.charAt(i) == '<'&& line.charAt(i - 1) == '<')
                                    || (line.charAt(i) == '|' && line.charAt(i - 1) == '|') || (line.charAt(i) == '*' && line.charAt(i - 1) == '*')) {//right shift
                                if (line.charAt(i + 1) == ':' && line.charAt(i + 2) == '=') {

                                    Token t = new Token(2, line.substring(start, i + 3), row, start + 1);
                                    printTok(t.type, t.lexeme, t.row, t.column);
                                    i += 2;
                                    st = '1';
                                    break;
                                } else {//prints concat, shifts and exp
                                    Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                    printTok(t.type, t.lexeme, t.row, t.column);
                                    st = '1';
                                    break;
                                }
                            }else if(line.charAt(i) == ':' && line.charAt(i + 1) == '='){
                                Token t = new Token(2, line.substring(start, i + 2), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                i++;
                                st = '1';
                                break;
                            }else{
                                if(mp.containsKey(line.substring(start, i))){
                                    Token t = new Token(2, line.substring(start, i), row, start + 1);
                                    printTok(t.type, t.lexeme, t.row, t.column);
                                    st='1';
                                    i--;
                                    break;
                                }else{
                                    column--;
                                    printError(row, start);
                                    System.exit(0);
                                }

                            }
                        case '6':
                            if(line.charAt(i) == '='){
                                break;
                            }else if(line.charAt(i) == ':' && line.charAt(i - 1) == '=') {// :=: found
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else if ((line.charAt(i - 1) == '*' && line.charAt(i - 1) == '=')){
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else{
                                Token t = new Token(2, line.substring(start, i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                i--;
                                st = '1';
                                break;
                            }
                        case '7':
                            if(line.charAt(i) == '='){
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else{
                                column--;
                                printError(row, start);
                                System.exit(0);
                            }
                        case '8':
                            if(line.charAt(i) == ']'){
                                Token t = new Token(2, line.substring(start, i + 1), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                st = '1';
                                break;
                            }else{
                                Token t = new Token(2, line.substring(start, i), row, start + 1);
                                printTok(t.type, t.lexeme, t.row, t.column);
                                i--;
                                st = '1';
                                break;
                            }

                    }
                }
                row++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* _____________________ LEXER END _________________________________ */


    private static HashMap<String, String[][]> mp = new HashMap<>();
    private static ArrayList<String>  arrOfKeys = new ArrayList<>();
    private static Deque<String> keys = new LinkedList<>();
    private static Deque<String[]> val = new LinkedList<>();
    private static HashMap<String, HashSet<String>> first = new HashMap<>();
    private static HashMap<String, HashSet<String>> follow = new HashMap<>();
    private static HashMap<String, ArrayList<HashSet<String>>> pred = new HashMap<>();

    private static ArrayList<String>  arrOfTokens = new ArrayList<>();
    private static ArrayList<String>  arrOfPos = new ArrayList<>();
    private static String token = "";

    private static void primeros(){

        for(int i = 0; i < mp.size(); i++){
            String queueHead = keys.removeLast();
            String[][] values = mp.get(queueHead);
            System.out.println("queueHead: "+queueHead);
            keys.addFirst(queueHead);
            HashSet<String> pos = new HashSet<>();
            for(int j = values.length - 1; j >= 0 ; j--){
                if(!keys.contains(values[j][0])){
                    pos.add(values[j][0]);
                }else if (first.get(values[j][0]).contains("epsilon")){
                    if (values[j].length==1){
                        pos.addAll(first.get(values[j][0]));
                    }else {
                        Set<String> difference = new HashSet<>(first.get(values[j][0]));
                        difference.remove("epsilon");
                        pos.addAll(difference);
                    }
                }else{
                    pos.addAll(first.get(values[j][0]));
                }
            }
            first.put(queueHead, pos);
        }
        //System.out.println(Collections.singletonList(first.get("B")));
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
            if(keyFollow.equals(arrOfKeys.get(0))){
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

        for(int i = 0; i < mp.size(); i++) {
            String queueHead = keys.removeFirst();
            String[][] values = mp.get(queueHead);
            keys.addLast(queueHead);
            //HashSet<String> pred_i = new HashSet<>();
            ArrayList<HashSet<String>> pred_i = new ArrayList();
            for (int j = 0; j < values.length; j++) {
                HashSet<String> pred_j = new HashSet();
                //Deque pred_j = new LinkedList();
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
                pred_i.add(pred_j);
            }
            pred.put(queueHead, pred_i);

        }
        //System.out.println(Arrays.asList(pred));
    }

    static void errorSintaxis(String tokEsperados, int row, int column, String lexeme){

        System.out.println("<"+row+","+column+"> Error sintactico: se encontro: "+lexeme+"; se esperaba: "+tokEsperados);
        System.exit(1);
    }

    static void emparejar(String tokEsperado) {
        if (token == tokEsperado) {
            token = getNextToken();
        }else{
            System.out.println("Error Sintactico, se esperaba "+tokEsperado+", se encontro: "+token+"("+arrOfPos.indexOf(token)+")");
            System.exit(1);
        }
    }

    static String getNextToken(){
        int newIndex = arrOfKeys.indexOf(token);
        while (newIndex < arrOfTokens.size()){
            newIndex = arrOfKeys.indexOf(token)+1;
            token = arrOfTokens.get(newIndex);
        }
        return token;
    }

    static void generic(String noTerminal){
        String[][] rules = mp.get(noTerminal);
        String tokEsperados = "";
        for (int r = 0; r < rules.length; r++){
            String[] derivation = rules[r];
            ArrayList<HashSet<String>> predSet = pred.get(noTerminal);
            for(int i=0; i<predSet.size();i++){
                HashSet<String> values = predSet.get(i);
                Iterator<String> value = values.iterator();
                while (value.hasNext()) {
                    tokEsperados = tokEsperados+","+value.next();
                    if(token == value.next()){
                        if(keys.contains(derivation[i])){
                            generic(derivation[i]);
                        }else {
                            emparejar(derivation[i]);
                        }
                    } else {
                        //errorSintaxis(tokEsperados);
                        System.out.println("Error Sintactico, se esperaba "+tokEsperados+", se encontro: "+token+"("+arrOfPos.indexOf(token)+")");
                        System.exit(1);
                    }
                }
            }
        }
    }

    public static void main(String [] args){
        //lexer();
        try {
            //Read grammar
            File file =
                    new File("grammar2.txt");
            Scanner sc = new Scanner(file);

            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine() ;
                String[] arrOfStr = line.split("->");
                //System.out.println(Arrays.asList(arrOfStr));
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

        try {
            //Read tokens
            File fileTokens =
                    new File("tokens.txt");
            Scanner sct = new Scanner(fileTokens);

            String linet;

            while (sct.hasNextLine()) {
                linet = sct.nextLine() ;
                String[] arrOfStrt = linet.split(" ");
                //if(arrOfStrt.length == 2){
                    arrOfTokens.add(arrOfStrt[0]);
                    arrOfPos.add(arrOfStrt[1]);
                    //token = arrOfTokens.get(0);
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        primeros();
        siguientes();
        prediction();

        token = getNextToken();
        generic("instruccion_fa");

        //if (token != TOKFinArchivo)
        //    errorSintaxis(TOKFinArchivo);
    }
}

