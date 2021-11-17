package demo.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MapUtil {
    public static String toCSVString(List<Map<String, Object>> items) {
        var formattedString = new StringBuilder();

        for (int i = 0; i < items.size(); ++i) {
            var item = items.get(i);

            if (i == 0) {
                formattedString.append(String.join(",", item.keySet())).append('\n');
            }

            formattedString.append(item.values().stream()
                    .map(value -> value.toString().contains(",") ? String.format("\"%s\"", value) : value.toString())
                    .collect(Collectors.joining(","))).append('\n');
        }

        return formattedString.toString();
    }

    public static String toXMLString(List<Map<String, Object>> items) {
        StringBuilder formattedString = new StringBuilder("<root>\n");

        for (var item : items) {
            formattedString.append("<sparseObject>\n");

            item.forEach((key, value) -> formattedString.append(String.format("<%s>%s</%s>\n", key, value, key)));

            formattedString.append("</sparseObject>\n");
        }

        formattedString.append("</root>");
        return formattedString.toString();
    }
}
