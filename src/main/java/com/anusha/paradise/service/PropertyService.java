package com.anusha.paradise.service;

import com.anusha.paradise.config.Constants;
import com.anusha.paradise.entity.Property;
import com.anusha.paradise.model.ResponseModel;
import com.anusha.paradise.repository.PropertyRepository;
import com.anusha.paradise.repository.UserRepository;
import com.anusha.paradise.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;


@Service
public class PropertyService {

    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private JavaMailSender emailSender;

    public List<Property> fetchProducts() {
        return propertyRepository.findByStatus(Constants.STATUS_ACTIVE);
    }

    public List<Property> fetchMyProducts(String userId) {
        return propertyRepository.findByStatusAndPostedBy(Constants.STATUS_ACTIVE, userId);
    }

    @Value("${s3ImageUrl}")
    private String s3ImageUrl;

    public ResponseModel saveProduct(Property property, String userName) {
        ResponseModel response = new ResponseModel();
        try {
            property.setId(UUID.randomUUID());
            property.setStatus("A");
            property.setPostedOn(LocalDateTime.now());
            property.setPostedBy(userName);
            propertyRepository.save(property);
            response.setStatus("Success");
            response.setMessage("Operation Successful");
            response.setCreationId(property.getId().toString());
        } catch (Exception e) {
            response.setException(e.getLocalizedMessage());
        }
        return response;
    }

    public ResponseModel updateProperty(UUID propertyId, Property property, String userName) {
        ResponseModel response = new ResponseModel();
        response.setCreationId(propertyId.toString());
        try {
            propertyRepository.findById(propertyId).ifPresent(
                    savedProperty -> {
                        if (savedProperty.getPostedBy().equals(userName)) {
                            setUpdatedValues(savedProperty, property);
                            propertyRepository.save(savedProperty);
                            response.setStatus("Success");
                            response.setMessage("Operation Successful");
                        } else {
                            response.setStatus("Unsuccessful");
                            response.setMessage("Operation Unsuccessful : Not Authorized To Update This Property");
                        }
                    }
            );
        } catch (Exception e) {
            response.setStatus("Unsuccessful");
            response.setMessage("Exception Occurred");
            response.setException(e.getLocalizedMessage());
        }
        return response;
    }

    private void setUpdatedValues(Property property, Property updatedProp) {
        if (nonNull(updatedProp.getAddress()))
            property.setAddress(updatedProp.getAddress());

        if (nonNull(updatedProp.getFeatures()))
            property.setFeatures(updatedProp.getFeatures());

        if (nonNull(updatedProp.getName()))
            property.setName(updatedProp.getName());

        if (nonNull(updatedProp.getPrice()))
            property.setPrice(updatedProp.getPrice());

        if (nonNull(updatedProp.getPostalCode()))
            property.setPostalCode(updatedProp.getPostalCode());

        if (nonNull(updatedProp.getYearMade()))
            property.setYearMade(updatedProp.getYearMade());

        if (nonNull(updatedProp.getDescription()))
            property.setDescription(updatedProp.getDescription());

        if (nonNull(updatedProp.getPropertyType()))
            property.setPropertyType(updatedProp.getPropertyType());

        property.setModifiedOn(LocalDateTime.now());
    }

    public ResponseModel inactiveProperty(UUID propertyId, String userName) {
        ResponseModel response = new ResponseModel();
        try {
            propertyRepository.findById(propertyId).ifPresent(
                    savedProperty -> {
                        if (savedProperty.getPostedBy().equals(userName)) {
                            savedProperty.setStatus("I");
                            propertyRepository.save(savedProperty);
                            response.setStatus("Success");
                            response.setMessage("Operation Successful");
                        } else {
                            response.setStatus("Unsuccessful");
                            response.setMessage("Operation Unsuccessful : Not Authorized To Update This Property");
                        }
                    }
            );
        } catch (Exception e) {
            response.setStatus("Unsuccessful");
            response.setMessage("Exception Occurred");
            response.setException(e.getLocalizedMessage());
        }
        return response;
    }

    @Transactional
    public void updateImageUrl(String propertyId, String fileKey) {
        propertyRepository.findById(UUID.fromString(propertyId)).ifPresent(
                product -> {
                    if (nonNull(product.getImages())) {
                        product.getImages().add(s3ImageUrl + fileKey);
                    } else {
                        product.setImages(List.of(s3ImageUrl + fileKey));
                    }
                    propertyRepository.save(product);
                }
        );
    }

    public ResponseModel emailPropertyOwner(UUID productId, String userName) {
        ResponseModel response = new ResponseModel();
        Property prod = propertyRepository.findById(productId).orElse(null);
        User productUser = null;
        if (nonNull(prod)) {
            productUser = userRepository.findByUsername(prod.getPostedBy()).orElse(null);
        }
        User enquiryUser = userRepository.findByUsername(userName).orElse(null);
        if (nonNull(productUser) && nonNull(enquiryUser)) {
            sendEmail(productUser, enquiryUser, prod);
        }
        response.setStatus("Success");
        response.setMessage("Operation Successful");
        return response;
    }

    private void sendEmail(User productUser, User enquiryUser, Property property) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("shantanumalviya19@gmail.com");
        message.setTo(productUser.getEmail());
        message.setSubject("Enquiry for your cataloged construction work : " + property.getName());
        message.setText("A contactor has shown an interest on your added enquiry for your property : "
                + property.getName() +
                ", \n\n kindly Email the potential contractor for discussing the requirement details and quotation. \n\nHere Is The Details Of Enquirer. " +
                " Name: " + enquiryUser.getFirstname() + " " + enquiryUser.getLastname() + " " +
                " Email ID: " + enquiryUser.getEmail()+" \n\n Thanks & Regards \nTeam Homey");
        this.emailSender.send(message);
    }
}
