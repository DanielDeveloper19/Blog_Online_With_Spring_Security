package com.example.BlogOnline.Repository;


import com.example.BlogOnline.Model.Posteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosteoRepository extends JpaRepository<Posteo,Long> {

    List<Posteo> findByUserId(Long userId);

    public void deleteAll();
}
