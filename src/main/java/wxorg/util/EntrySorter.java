package wxorg.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EntrySorter {

    public static <T> void sortByField(List<T> list, String fieldName, boolean ascending) {
        if (list == null || list.isEmpty()) return;
        Class<?> clazz = list.get(0).getClass();

        Comparator<T> comparator = Comparator.comparing(o -> {
            try {
                Object value = ((Map<String, String>) o).get(fieldName);
                value = value != null ? value : "";
                return (Comparable<Object>) value;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (!ascending) {
            comparator = comparator.reversed();
        }

        list.sort(comparator);
    }
}
