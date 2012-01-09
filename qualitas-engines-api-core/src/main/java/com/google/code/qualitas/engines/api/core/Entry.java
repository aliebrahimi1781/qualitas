package com.google.code.qualitas.engines.api.core;

/**
 * The Class Entry.
 */
public class Entry {

    /** The name. */
    private String name;

    /** The content. */
    private byte[] content;

    /**
     * Instantiates a new entry.
     */
    public Entry() {
    }

    /**
     * Instantiates a new entry.
     * 
     * @param name
     *            the name
     * @param content
     *            the content
     */
    public Entry(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content.
     * 
     * @param content
     *            the new content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

}
