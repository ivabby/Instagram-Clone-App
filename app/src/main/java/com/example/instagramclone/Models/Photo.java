package com.example.instagramclone.Models;

public class Photo {
    public String caption , date_created , image_path , photo_id , user_id , tags;

    public Photo(String caption, String date_created, String image_path, String photo_id, String user_id, String tags) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
    }

    public Photo(){}

    public String getCaption() {
        return caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
