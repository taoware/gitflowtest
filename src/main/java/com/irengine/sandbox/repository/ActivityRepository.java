package com.irengine.sandbox.repository;

import com.irengine.sandbox.domain.Activity;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity,Long> {

}
