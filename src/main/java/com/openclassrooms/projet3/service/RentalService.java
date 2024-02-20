package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.Rental;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

public interface RentalService {


    public Iterable<Rental> findAllRentals();

    public Optional<Rental> findRentalById(Long id);

    public Rental createRental(String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws Exception;

    public Rental updateRental(Long id, Rental rentalDetails) ;

    public void deleteRental(Long id);

    public boolean isUserOwnerOfRental(Long rentalId);

    private String getAuthenticatedUsername() {
        return null;
    }

    public RentalDTO convertToDTO(Rental rental);
}
