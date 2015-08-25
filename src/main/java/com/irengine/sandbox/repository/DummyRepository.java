package com.irengine.sandbox.repository;

import com.irengine.sandbox.domain.Dummy;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Dummy entity.
 */
public interface DummyRepository extends JpaRepository<Dummy,Long> {

}
