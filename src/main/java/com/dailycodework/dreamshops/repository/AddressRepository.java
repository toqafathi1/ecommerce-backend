package com.dailycodework.dreamshops.repository;

import com.dailycodework.dreamshops.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address , Long> {
    boolean existsByIdAndUserId(Long addressId, Long userId);
}
