package com.openclassrooms.projet3.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.repository.RentalRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RentalServiceImpl implements RentalService {

    @Autowired
    public RentalRepository rentalRepository;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private DBUserService dbUserService;

    public Iterable<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> findRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental createRental(String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws Exception {
        Optional<DBUser> ownerOptional = dbUserService.find(ownerEmail);
        if (ownerOptional.isEmpty()) {
            throw new CustomNotFoundException("Owner not found");
        }
        DBUser owner = ownerOptional.get();

        String pictureUrl = imageUtils.storePicture(picture);

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setPicture(pictureUrl);
        rental.setOwner(owner);

        return rentalRepository.save(rental);
    }

    public Rental updateRental(Long id, Rental rentalDetails) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found for this id :: " + id));

        rental.setName(rentalDetails.getName());
        rental.setSurface(rentalDetails.getSurface());
        rental.setPrice(rentalDetails.getPrice());
        rental.setPicture(rentalDetails.getPicture());
        rental.setDescription(rentalDetails.getDescription());
        rental.setUpdatedAt(LocalDate.now());
        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public boolean isUserOwnerOfRental(Long rentalId) {
        Rental rental = findRentalById(rentalId).orElseThrow(() -> new RuntimeException("Rental not found"));
        String authenticatedUsername = getAuthenticatedUsername();
        return rental.getOwner().getName().equals(authenticatedUsername);
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }


    public RentalDTO convertToDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setSurface(rental.getSurface());
        dto.setPrice(rental.getPrice());
        dto.setPicture(rental.getPicture());
        dto.setDescription(rental.getDescription());
        dto.setCreated_at(rental.getCreatedAt().toString());
        dto.setUpdated_at(rental.getUpdatedAt().toString());
        dto.setOwner_id(rental.getOwner().getId());
        return dto;
    }

}
