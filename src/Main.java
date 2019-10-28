import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main {

    private static HashMap<String, String[][]> mp = new HashMap<>();
    private static ArrayList<String>  arrOfKeys = new ArrayList<>();
    private static Deque<String> keys = new LinkedList<>();
    private static Deque<String[]> val = new LinkedList<>();

    private static void primeros(){
        HashMap<String, HashSet<String>> first = new HashMap<>();
        for(int i = 0; i < mp.size(); i++){
            String queueHead = keys.removeLast();
            String[][] values = mp.get(queueHead);
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
        System.out.println(Collections.singletonList(first.get("E")));
    }

    public static void main(String [] args){

        try {

            File file =
                    new File("grammar.txt");
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

    }

}

