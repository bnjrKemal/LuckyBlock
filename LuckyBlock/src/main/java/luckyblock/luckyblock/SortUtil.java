package luckyblock.luckyblock;

import java.util.*;

public class SortUtil {

    public static <K, V extends Number> List<K> mapTop(Map<K, V> map, boolean ascending) {

        if(map.size() == 0) return null;

        // Reversing the map from key->value to value->key
        Map<V, List<K>> reverseMap = new HashMap<>();
        for (K key : map.keySet()) {
            List<K> list = new ArrayList<>();
            if (reverseMap.containsKey(map.get(key))) {
                list = reverseMap.get(map.get(key));
            }
            list.add(key);
            reverseMap.put(map.get(key), list);
        }

        // Getting the keys of the reversed map and sorting the keys (descending order)
        List<V> numbers = new ArrayList<>(reverseMap.keySet());
        numbers.sort((o1, o2) -> {
            double d1 = o1.doubleValue();
            double d2 = o2.doubleValue();

            int comp = Double.compare(d1, d2);

            // if descending, just reverse the compare
            return ascending ? comp : -comp;
        });

        // Getting back the original keys from the sorted reversed map
        List<K> sorted = new ArrayList<>();
        for (V number : numbers) {
            sorted.addAll(reverseMap.get(number));
        }
        return sorted;
    }

}
