package net.waret.demo.alice.repository;

import net.waret.demo.alice.domain.Album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String>, JpaSpecificationExecutor<Album> {

    List<Album> findByUserIdAndName(String userId, String name);

    List<Album> findByUserId(String userId);

    Album findByExternalId(String externalId);

}
