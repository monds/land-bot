package com.monds.land.repository;

import com.monds.land.domain.ZigbangAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZigbangAddressRepository extends JpaRepository<ZigbangAddress, Integer> {
    boolean existsByAddressEquals(String address);
}
