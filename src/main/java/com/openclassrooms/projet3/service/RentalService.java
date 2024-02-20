package com.openclassrooms.projet3.service;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.Rental;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

public interface RentalService {


    Iterable<Rental> findAllRentals();

    Map<String, Object> getRentalsWithDTOs();

    Optional<Rental> findRentalById(Long id);

    Rental createRental(String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws Exception;

    Rental updateRental(Long id, Rental rentalDetails);

    void deleteRental(Long id);

    boolean isUserOwnerOfRental(Long rentalId);

    private String getAuthenticatedUsername() {
        return null;
    }

    RentalDTO convertToDTO(Rental rental);
}
