package app;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;

public class ErrorMessage {

    private String shortMessage;
    private String longMessage;
    private final HighlightSeverity tag;
    private final TextAttributesKey type;
    private final String occurrence;
    private final int line;
    private final String description;
    private final long id;


    public ErrorMessage(HighlightSeverity tag, TextAttributesKey type, String occurrence, int line, String description, long id) {
        this.tag = tag;
        this.type = type;
        this.occurrence = occurrence;
        this.line = line;
        this.description = description;
        this.id = id;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getLongMessage() {
        return longMessage;
    }

    public HighlightSeverity getTag() {
        return tag;
    }

    public TextAttributesKey getType() {
        return type;
    }

    public String getOccurrence() {
        return occurrence;
    }

    public int getLine() {
        return line;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }
}
