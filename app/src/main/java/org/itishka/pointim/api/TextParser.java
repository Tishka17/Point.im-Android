package org.itishka.pointim.api;

import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.itishka.pointim.ImageSearchHelper;
import org.itishka.pointim.Utils;
import org.itishka.pointim.api.data.TextWithImages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tishka17 on 25.10.2014.
 */
public class TextParser implements JsonDeserializer<TextWithImages> {
    @Override
    public TextWithImages deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TextWithImages textWithImages = new TextWithImages();
        textWithImages.text = Utils.addLinks(json.getAsJsonPrimitive().getAsString());
        textWithImages.images = ImageSearchHelper.checkImageLinks(ImageSearchHelper.getAllLinks(textWithImages.text));
        Utils.boldNicks(textWithImages.text);
        return textWithImages;
    }

}
