package net.waret.demo.photoz.repository;

import net.waret.demo.photoz.domain.Album;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album, String>, JpaSpecificationExecutor<Album> {

    List<Album> findByUserIdAndName(String userId, String name);

    Page<Album> findByUserId(String userId, Pageable pageable);

    List<Album> findByUserId(String userId);

    Album findByExternalId(String externalId);

}
