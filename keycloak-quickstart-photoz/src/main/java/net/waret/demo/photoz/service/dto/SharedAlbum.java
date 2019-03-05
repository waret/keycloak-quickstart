package net.waret.demo.photoz.service.dto;

import net.waret.demo.photoz.domain.Album;

import java.util.ArrayList;
import java.util.List;

public class SharedAlbum {

    private Album album;
    private List<String> scopes;

    public SharedAlbum(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void addScope(String scope) {
        if (scopes == null) {
            scopes = new ArrayList<String>();
        }
        scopes.add(scope);
    }
}
