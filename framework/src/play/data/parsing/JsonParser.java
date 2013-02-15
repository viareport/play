package play.data.parsing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import play.exceptions.UnexpectedException;
import play.mvc.Http;
import play.utils.Utils;

import com.google.gson.JsonElement;

/**
 * Parse JSON-encoded requests.
 */
public class JsonParser extends DataParser {
    
    private static class Entry {
        public final String key;
        public final JsonElement jsonElement;

        public Entry(String key, JsonElement jsonElement) {
            this.key = key;
            this.jsonElement = jsonElement;
        }
    }

    @Override
    public Map<String, String[]> parse(InputStream is) {

        // Encoding is either retrieved from contentType or it is the default encoding
        final String encoding = Http.Request.current().encoding;
        JsonElement parsed;
        try {
            parsed = new com.google.gson.JsonParser().parse(
                new BufferedReader(new InputStreamReader(is, encoding)));
        } catch (Throwable e) {
            throw new UnexpectedException(e);
        }
        return jsonToParams(parsed);
    }

    public Map<String, String[]> jsonToParams(JsonElement parsed) {
        Map<String, String[]> params = new HashMap<String, String[]>();
        Stack<Entry> stack = new Stack<Entry>();
        stack.add(new Entry("", parsed));
        while (! stack.isEmpty()) {
            Entry stackEntry = stack.pop();
            String key = stackEntry.key;
            JsonElement value = stackEntry.jsonElement;
            
            if (value.isJsonPrimitive()) {
                
                Utils.Maps.mergeValueInMap(params, key, value.getAsString());
                
            } else if (value.isJsonArray()) {
                
                for (JsonElement jsonElement : value.getAsJsonArray()) {
                    stack.add(new Entry(key, jsonElement));
                }
                
            } else if (value.isJsonObject()) {
                
                for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
                    String entryKey = entry.getKey();
                    if (! "".equals(key)) {
                        entryKey = key + "." + entryKey;
                    }
                    stack.add(new Entry(entryKey, entry.getValue()));
                }
                
            } // else ignore (null)
        }
        return params;
    }
}
