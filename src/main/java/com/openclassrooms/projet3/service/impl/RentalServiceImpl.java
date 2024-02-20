package com.openclassrooms.projet3.service.impl;

import com.openclassrooms.projet3.dtos.RentalDTO;
import com.openclassrooms.projet3.excepton.CustomNotFoundException;
import com.openclassrooms.projet3.model.DBUser;
import com.openclassrooms.projet3.model.Rental;
import com.openclassrooms.projet3.repository.RentalRepository;
import com.openclassrooms.projet3.service.AuthenticationService;
import com.openclassrooms.projet3.service.DBUserService;
import com.openclassrooms.projet3.service.RentalService;
import com.openclassrooms.projet3.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final ImageUtils imageUtils;
    private final DBUserService dbUserService;
    private final AuthenticationService authenticationService;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, ImageUtils imageUtils, DBUserService dbUserService, AuthenticationService authenticationService) {
        this.rentalRepository = rentalRepository;
        this.imageUtils = imageUtils;
        this.dbUserService = dbUserService;
        this.authenticationService = authenticationService;
    }

    @Override
    public Iterable<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public List<RentalDTO> getRentalsWithDTOs() {
        Iterable<Rental> rentalsIterable = findAllRentals();
        return StreamSupport.stream(rentalsIterable.spliterator(), false)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<Rental> findRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    @Override
    public RentalDTO findRentalDTOById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Rental not found with id: " + id));
        return convertToDTO(rental);
    }

    @Override
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

    @Override
    public Rental updateRental(Long id, String name, int surface, double price, String description, MultipartFile picture, String ownerEmail) throws CustomNotFoundException, IOException {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Rental not found for this id: " + id));

        // check if the user is the owner of the rental
        DBUser owner = dbUserService.find(ownerEmail)
                .orElseThrow(() -> new CustomNotFoundException("Owner not found"));
        if (!rental.getOwner().equals(owner)) {
            throw new CustomNotFoundException("User is not the owner of the rental");
        }

        if (picture != null && !picture.isEmpty()) {
            String pictureUrl = imageUtils.storePicture(picture);
            rental.setPicture(pictureUrl);
        }

        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);

        return rentalRepository.save(rental);
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    @Override
    public boolean isUserOwnerOfRental(Long rentalId) {
        Rental rental = findRentalById(rentalId).orElseThrow(() -> new RuntimeException("Rental not found"));
        String authenticatedUsername = authenticationService.getAuthenticatedUsername();
        return rental.getOwner().getName().equals(authenticatedUsername);
    }

    private RentalDTO convertToDTO(Rental rental) {
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
