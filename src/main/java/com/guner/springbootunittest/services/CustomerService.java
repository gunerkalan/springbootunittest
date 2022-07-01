package com.guner.springbootunittest.services;

import com.guner.springbootunittest.dto.CustomerDto;
import com.guner.springbootunittest.exceptions.IdentificationNumberNotValidException;
import com.guner.springbootunittest.models.Customer;
import com.guner.springbootunittest.repositories.CustomerRepository;
import com.guner.springbootunittest.services.validators.IdentificationNumberValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final IdentificationNumberValidation identificationNumberValidation;


    @Transactional
    public Optional<Customer> create(CustomerDto request){

        boolean isValidIdentificationNumber = identificationNumberValidation.test(request.getIdentificationNumber());

        if(!isValidIdentificationNumber){
            throw new IdentificationNumberNotValidException("Identification number not valid.");
        }

        Optional<Customer> customerOptional = customerRepository.findByIdentificationNumber(request.getIdentificationNumber());
        if(customerOptional.isPresent()){
            throw new EntityExistsException("This customer already exist!");
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(request.getFirstName());
        newCustomer.setLastName(request.getLastName());
        newCustomer.setPhoneNumber(request.getPhoneNumber());
        newCustomer.setIdentificationNumber(request.getIdentificationNumber());

        return Optional.of(customerRepository.save(newCustomer));

    }

    @Transactional(readOnly = true)
    public List<CustomerDto> findAll(){
        final List<CustomerDto> customerDtoArrayList = new ArrayList<CustomerDto>();

        customerRepository.findAll().forEach(customer -> {
            CustomerDto customerDto = new CustomerDto();
            if(customer!=null){
                customerDto.setFirstName(customer.getFirstName());
                customerDto.setLastName(customer.getLastName());
                customerDto.setPhoneNumber(customer.getPhoneNumber());
                customerDto.setIdentificationNumber(customer.getIdentificationNumber());
            }
            customerDtoArrayList.add(customerDto);
        });

        return customerDtoArrayList;
    }

    @Transactional(readOnly = true)
    public CustomerDto findById(long id){

        final Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException());

        CustomerDto customerDto = new CustomerDto();
        if(customer!=null){
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setLastName(customerDto.getLastName());
            customerDto.setIdentificationNumber(customer.getIdentificationNumber());
            customerDto.setPhoneNumber(customerDto.getPhoneNumber());
        }

        return customerDto;
    }

    @Transactional
    public void deleteById(long id){
        final Customer customer = customerRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException());

        customerRepository.deleteById(id);
    }


}
