package org.itishka.pointim.api;

import android.text.Spannable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.itishka.pointim.Utils;

import java.lang.reflect.Type;

/**
 * Created by Татьяна on 25.10.2014.
 */
public class TextParser implements JsonDeserializer<Spannable> {
    @Override
    public Spannable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Utils.addLinks(json.getAsJsonPrimitive().getAsString());
    }
}
