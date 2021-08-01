package com.monds.land.repository;

import com.monds.land.domain.LandRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandRegionRepository extends JpaRepository<LandRegion, String> {
}
