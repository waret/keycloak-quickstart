package net.waret.demo.rls.repository;


import net.waret.demo.rls.domain.test.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by rajeevkumarsingh on 22/11/17.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

}
