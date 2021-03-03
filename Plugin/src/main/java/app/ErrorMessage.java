package app;

import com.intellij.openapi.editor.colors.TextAttributesKey;

public class ErrorMessage {

    private String shortMessage;
    private String longMessage;
    private String tag;
    private TextAttributesKey type;
    private String occurrence;
    private int line;
    private String description;
    private long id;

    public ErrorMessage(String shortMessage, String longMessage, String tag, TextAttributesKey type, String occurrence, int line, String description, long id) {
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
        this.tag = tag;
        this.type = type;
        this.occurrence = occurrence;
        this.line = line;
        this.description = description;
        this.id = id;
    }

    public ErrorMessage(String tag, TextAttributesKey type, String occurrence, int line, String description, long id) {
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

    public String getTag() {
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
