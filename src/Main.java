import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main {

    static HashMap<String, String[][]> mp = new HashMap<String, String[][]>();
    static ArrayList<String>  arrOfKeys = new ArrayList<String>();
    static Deque<String> keys = new LinkedList<String>();
    static Deque<String[]> val = new LinkedList<String[]>();

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
                }
                mp.put(s, values);

            }
            System.out.println(Arrays.asList(mp.get("E1")[0]));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

