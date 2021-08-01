package com.monds.land.repository;

import com.monds.land.domain.ZigbangSubway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZigbangSubwayRepository extends JpaRepository<ZigbangSubway, Integer> {
    List<ZigbangSubway> getAllByEnableTrue();
}
