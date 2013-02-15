package play.data.parsing;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import play.PlayBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static org.fest.assertions.Assertions.*;


public class JsonParserTest {

    @Before
    public void setup() {
        new PlayBuilder().build();
    }
    
    @Test
    public void testJsonObjectConversion() {
        //
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("stringProp", "stringValue");
        jsonObject.addProperty("intProp", 1);
        jsonObject.addProperty("booleanProp", true);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(new JsonPrimitive("a"));
        jsonArray.add(new JsonPrimitive("b"));
        jsonObject.add("arrayProp", jsonArray);
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("stringProp2", "stringValue2");
        jsonObject.add("objectProp", jsonObject2);
        
        //
        Map<String, String[]> params = new JsonParser().jsonToParams(jsonObject);
        
        //
        assertThat(params).hasSize(5);
        assertThat(params.keySet()).containsOnly("stringProp", "intProp", "booleanProp", "arrayProp", "objectProp.stringProp2");
        assertThat(params.get("stringProp")).containsOnly("stringValue");
        assertThat(params.get("intProp")).containsOnly("1");
        assertThat(params.get("booleanProp")).containsOnly("true");
        assertThat(params.get("arrayProp")).containsOnly("a", "b");
        assertThat(params.get("objectProp.stringProp2")).containsOnly("stringValue2");
    }
}
