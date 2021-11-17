package demo.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ObjectUtil {
    public static String toCSVString(List<Object> items) {
        StringBuilder formattedString = new StringBuilder(Arrays.stream(items.get(0).getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.joining(","))).append("\n");

        for (Object item : items) {
            Field[] itemFields = item.getClass().getDeclaredFields();

            for (int i = 0; i < itemFields.length; ++i) {
                if (i > 0) {
                    formattedString.append(',');
                }

                Field field = itemFields[i];

                boolean fieldAccess = field.canAccess(item);

                if (!fieldAccess) {
                    field.setAccessible(true);
                }

                String fieldValue;

                try {
                    fieldValue = field.get(item).toString();

                    if (fieldValue.contains(",")) {
                        fieldValue = String.format("\"%s\"", fieldValue);
                    }
                } catch (IllegalAccessException e) {
                    fieldValue = "";
                }

                if (!fieldAccess) {
                    field.setAccessible(false);
                }

                formattedString.append(fieldValue);
            }

            formattedString.append('\n');
        }
        return formattedString.toString();
    }

    public static String toXMLString(List<Object> items) {
        StringBuilder formattedString = new StringBuilder("<root>\n");

        for (Object item : items) {
            formattedString.append(String.format("<%s>\n", item.getClass().getName()));

            for (Field field : item.getClass().getDeclaredFields()) {
                formattedString.append(String.format("<%s>", field.getName()));

                boolean fieldAccess = field.canAccess(item);

                if (!fieldAccess) {
                    field.setAccessible(true);
                }

                String fieldValue;

                try {
                    fieldValue = field.get(item).toString();
                } catch (IllegalAccessException e) {
                    fieldValue = "";
                }

                if (!fieldAccess) {
                    field.setAccessible(false);
                }

                formattedString.append(fieldValue);

                formattedString.append(String.format("</%s>\n", field.getName()));
            }

            formattedString.append(String.format("</%s>\n", item.getClass().getName()));
        }

        formattedString.append("</root>");

        return formattedString.toString();
    }
}

