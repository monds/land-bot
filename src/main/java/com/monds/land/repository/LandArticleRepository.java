package com.monds.land.repository;

import com.monds.land.domain.LandArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandArticleRepository extends JpaRepository<LandArticle, Integer> {
}
