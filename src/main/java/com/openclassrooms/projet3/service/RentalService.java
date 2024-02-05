package com.openclassrooms.projet3.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.repository.RentalRepository;

@Service
public class RentalService {


    @Autowired
    public RentalRepository rentalRepository;

    public Iterable<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> findRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental saveRental(Rental Rental) {
        return rentalRepository.save(Rental);
    }

    public Rental updateRental(Long id, Rental rentalDetails) {
        Rental rental = rentalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rental not found for this id :: " + id));
    
        rental.setName(rentalDetails.getName());
        rental.setSurface(rentalDetails.getSurface());
        rental.setPrice(rentalDetails.getPrice());
        rental.setPicture(rentalDetails.getPicture());
        rental.setDescription(rentalDetails.getDescription());
        rental.setUpdatedAt( LocalDate.now() );
        final Rental updatedRental = rentalRepository.save(rental);
        return updatedRental;
    }
    

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
    

}
