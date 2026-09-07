package ca.datamagic.radiotester.dto;

public class RadioStationDTO {
    private String id = null;
    private String title = null;
    private String description = null;
    private String website = null;
    private String iconURL = null;
    private String genre = null;
    private String location = null;
    private String language = null;
    private String streamURL = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "RadioStationDTO{" +
                "description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", website='" + website + '\'' +
                ", iconURL='" + iconURL + '\'' +
                ", genre='" + genre + '\'' +
                ", location='" + location + '\'' +
                ", language='" + language + '\'' +
                ", streamURL='" + streamURL + '\'' +
                '}';
    }
}
