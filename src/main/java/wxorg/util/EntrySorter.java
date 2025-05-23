package wxorg.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

public class EntrySorter {

    public static <T> void sortByField(List<T> list, String fieldName, boolean ascending) {
        if (list == null || list.isEmpty()) return;

        Class<?> clazz = list.get(0).getClass();

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            Comparator<T> comparator = Comparator.comparing(o -> {
                try {
                    Object value = field.get(o);
                    return (Comparable<Object>) value;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            if (!ascending) {
                comparator = comparator.reversed();
            }

            list.sort(comparator);

        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Поле '" + fieldName + "' не найдено в " + clazz.getSimpleName(), e);
        }
    }
}
