package com.openclassrooms.projet3.repository;

import org.springframework.data.repository.CrudRepository;

import com.openclassrooms.projet3.model.Rental;

public interface RentalRepository extends CrudRepository<Rental, Long>{
    
}
