import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main {

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
                        if(valuesList.indexOf(keyFollow) == valuesList.size()-1){
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

