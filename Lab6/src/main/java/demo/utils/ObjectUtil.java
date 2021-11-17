package demo.utils;

import demo.TaskModel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ObjectUtil {
    public static String toCSVString(List<TaskModel> items) {
        StringBuilder formattedString = new StringBuilder();

        for (int i = 0; i < items.size(); ++i) {
            java.lang.Object item = items.get(i);

            if (i == 0) {
                formattedString.append(Arrays.stream(item.getClass().getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.joining(",")));
            }

            formattedString.append('\n');

            Field[] itemFields = item.getClass().getDeclaredFields();

            for (int j = 0; j < itemFields.length; ++j) {
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
                        fieldValue = "\"" + fieldValue + "\"";
                    }
                } catch (IllegalAccessException e) {
                    fieldValue = "";
                }

                if (!fieldAccess) {
                    field.setAccessible(false);
                }

                formattedString.append(fieldValue);
            }
        }
        return formattedString.toString();
    }

    public static String toXMLString(List<TaskModel> items) {
        StringBuilder formattedString = new StringBuilder("<root>\n");

        for (TaskModel item : items) {
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

