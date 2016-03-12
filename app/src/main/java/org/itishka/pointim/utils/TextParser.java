package org.itishka.pointim.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.itishka.pointim.model.point.TextWithImages;

import java.lang.reflect.Type;

/**
 * Created by Tishka17 on 25.10.2014.
 */
public class TextParser implements JsonDeserializer<TextWithImages> {
    @Override
    public TextWithImages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TextWithImages textWithImages = new TextWithImages();
        String text = json.getAsJsonPrimitive().getAsString();
        textWithImages.text = TextLinkify.addLinks(text);
        textWithImages.images = ImageSearchHelper.checkImageLinks(ImageSearchHelper.getAllLinks(textWithImages.text), true);
        TextLinkify.markNicks(textWithImages.text);
        TextLinkify.markPostNumbers(textWithImages.text);
        return textWithImages;
    }

}
