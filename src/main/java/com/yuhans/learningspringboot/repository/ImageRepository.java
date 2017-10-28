package com.yuhans.learningspringboot.repository;

import com.yuhans.learningspringboot.domain.Image;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    Image findByName(String name);

}
