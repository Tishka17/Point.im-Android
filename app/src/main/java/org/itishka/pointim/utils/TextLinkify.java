package org.itishka.pointim.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import org.itishka.pointim.widgets.spans.PostClickableSpan;
import org.itishka.pointim.widgets.spans.UserClickableSpan;
import org.itishka.pointim.widgets.spans.MarkdownLinkClickableSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tishka17 on 17.01.2015.
 */
public class TextLinkify {
    private static final Pattern nickPattern = Pattern.compile("(?<=^|[:(>\\s])@([\\w-]+)");
    private static final Pattern postNumberPattern = Pattern.compile("(?<=^|[:(>\\s])#(\\w+)(?>/(\\d+))?");
    private static final Pattern markdownLinkPattern = Pattern.compile("(?<=^|[:(>\\s])\\[([\\w\\W]+)\\]\\(([\\w\\W]+)\\)");

    public static Spannable addLinks(String text) {
        Spannable spannable = new SpannableString(text);
        android.text.util.Linkify.addLinks(spannable, android.text.util.Linkify.ALL);
        return spannable;
    }

    public static Spannable markNicks(Spannable text) {
        Matcher m = nickPattern.matcher(text);
        while (m.find()) {
            StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
            text.setSpan(b, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            UserClickableSpan span = new UserClickableSpan(m.group(1));
            text.setSpan(span, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return text;
    }

    public static Spannable markPostNumbers(Spannable text) {
        Matcher m = postNumberPattern.matcher(text);
        while (m.find()) {
            StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
            text.setSpan(b, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            PostClickableSpan span = new PostClickableSpan(m.group(1), m.group(2));
            text.setSpan(span, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return text;
    }

    public static Spannable markMarkdownLinks(Spannable text) {
        Matcher m = = markdownLinkPattern.matcher(text);
        while (m.find()) {
            MarkdownLinkClickableSpan span = new MarkdownLinkClickableSpan(m.group(2), m.group(1));
            // TODO: 9/2/15 Add in-text replacement of markdown link definition "[some text](http://link/)" => "some text"
            text.setSpan(span, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return text;
    }
}
