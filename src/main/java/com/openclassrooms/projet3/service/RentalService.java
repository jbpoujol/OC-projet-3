package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.Rental;

import java.time.LocalDate;
import java.util.Optional;

public interface RentalService {


    public Iterable<Rental> findAllRentals();

    public Optional<Rental> findRentalById(Long id);

    public void saveRental(Rental Rental);

    public Rental updateRental(Long id, Rental rentalDetails) ;

    public void deleteRental(Long id);

    public RentalDTO convertToDTO(Rental rental);
}
