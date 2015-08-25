package com.irengine.sandbox.repository;

import com.irengine.sandbox.domain.Wego;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Wego entity.
 */
public interface WegoRepository extends JpaRepository<Wego,Long>, JpaSpecificationExecutor<Wego> {

}
