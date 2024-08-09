package com.example.nasaimagepicker;

/**
 * ImageItem represents an image item stored in the database for the NASA Image Picker app.
 * It contains information about the image such as its ID, URL, the date it was saved,
 * a description, and optionally, the date it was accessed.
 */
public class ImageItem {

    private long id;
    private String imageUrl;
    private String date;
    private String description;
    private String dateAccessed;

    /**
     * Constructs a new ImageItem with all fields, including the date accessed.
     *
     * @param id           The unique ID of the image.
     * @param imageUrl     The URL of the image.
     * @param date         The date the image was saved.
     * @param description  A description of the image.
     * @param dateAccessed The date the image was accessed, or null if not applicable.
     */
    public ImageItem(long id, String imageUrl, String date, String description, String dateAccessed) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.date = date;
        this.description = description;
        this.dateAccessed = dateAccessed;
    }

    /**
     * Constructs a new ImageItem without the date accessed.
     *
     * @param id          The unique ID of the image.
     * @param imageUrl    The URL of the image.
     * @param date        The date the image was saved.
     * @param description A description of the image.
     */
    public ImageItem(long id, String imageUrl, String date, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.date = date;
        this.description = description;
    }

    /**
     * Returns the unique ID of the image.
     *
     * @return The unique ID of the image.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the URL of the image.
     *
     * @return The URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the date the image was saved.
     *
     * @return The date the image was saved.
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the description of the image.
     *
     * @return The description of the image.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date the image was accessed, or null if not applicable.
     *
     * @return The date the image was accessed, or null if not applicable.
     */
    public String getDateAccessed() {
        return dateAccessed;
    }
}
