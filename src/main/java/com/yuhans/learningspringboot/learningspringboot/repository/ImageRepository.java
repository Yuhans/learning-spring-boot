package com.yuhans.learningspringboot.learningspringboot.repository;

import com.yuhans.learningspringboot.learningspringboot.domain.Image;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    Image findByName(String name);

}
