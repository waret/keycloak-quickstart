package net.waret.demo.photoz.web.dto;

public class Profile {
    private String userName;
    private int totalAlbums;

    public Profile(String name, int totalAlbums) {
        this.userName = name;
        this.totalAlbums = totalAlbums;
    }

    public String getUserName() {
        return userName;
    }

    public int getTotalAlbums() {
        return totalAlbums;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTotalAlbums(int totalAlbums) {
        this.totalAlbums = totalAlbums;
    }
}
