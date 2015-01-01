package org.itishka.pointim.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.itishka.pointim.ImageSearchHelper;
import org.itishka.pointim.Utils;
import org.itishka.pointim.api.data.TextWithImages;

import java.lang.reflect.Type;

/**
 * Created by Tishka17 on 25.10.2014.
 */
public class TextParser implements JsonDeserializer<TextWithImages> {
    @Override
    public TextWithImages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TextWithImages textWithImages = new TextWithImages();
        textWithImages.text = Utils.addLinks(json.getAsJsonPrimitive().getAsString());
        textWithImages.images = ImageSearchHelper.checkImageLinks(ImageSearchHelper.getAllLinks(textWithImages.text));
        Utils.markNicks(textWithImages.text);
        Utils.markPostNumbers(textWithImages.text);
        return textWithImages;
    }

}
