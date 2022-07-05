package com.guner.springbootunittest.services;

import com.guner.springbootunittest.dto.CustomerDto;
import com.guner.springbootunittest.exceptions.IdentificationNumberNotValidException;
import com.guner.springbootunittest.models.Customer;
import com.guner.springbootunittest.repositories.CustomerRepository;
import com.guner.springbootunittest.services.validators.IdentificationNumberValidation;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository mockCustomerRepository;

    @Mock
    IdentificationNumberValidation mockIdentificationNumberValidation;

    @InjectMocks
    CustomerService underTest;

    @Test
    void create_shouldCreateCustomer(){

        //given
        Customer expected = new Customer();
        expected.setIdentificationNumber("25252567348");
        expected.setPhoneNumber("5555555");

        CustomerDto request = new CustomerDto();

        //valid identification number
        when(mockIdentificationNumberValidation.test(any())).
                thenReturn(true);

        //no customer with this identification number
        when(mockCustomerRepository.findByIdentificationNumber(any())).
                thenReturn(Optional.empty());

        //mocking save method
        when(mockCustomerRepository.save(any())).thenReturn(expected);

        //when
        Customer actual = underTest.create(request).get();

        //then
        assertAll(
                ()->assertNotNull(actual),
                ()->assertEquals(expected,actual),
                ()->assertEquals(expected.getIdentificationNumber(),actual.getIdentificationNumber()),
                ()->assertEquals(expected.getPhoneNumber(),actual.getPhoneNumber())
        );
    }

    @Test
    void create_shouldThrowException_whenIdentificationNumberNotValid(){
        //given
        CustomerDto request = new CustomerDto();

        //NOT valid identification number
        when(mockIdentificationNumberValidation.test(any())).thenReturn(false);

        //Executable executable = () -> underTest.create(request).get();

        // then
        //assertThrows(IdentificationNumberNotValidException.class, executable);
    }

    @Test
    void getAll_itShouldReturnCustomerDtoList(){

        //given
        Customer customer = new Customer();
        customer.setIdentificationNumber("25252567348");
        customer.setPhoneNumber("5554443322");
        List<Customer> customerList = Collections.singletonList(customer);

        CustomerDto response = new CustomerDto();
        response.setIdentificationNumber(customer.getIdentificationNumber());
        response.setPhoneNumber(customer.getPhoneNumber());

        //mocking fidnAll method
        when(mockCustomerRepository.findAll()).thenReturn(customerList);

        //when
        List<CustomerDto> actual = underTest.findAll();

        //then
        assertEquals(customer.getIdentificationNumber(),actual.get(0).getIdentificationNumber());
        assertEquals(customer.getPhoneNumber(),actual.get(0).getPhoneNumber());

    }

    @Test
    void getById_itShouldReturnCustomerDto(){

        //given
        Customer customer = new Customer();
        customer.setIdentificationNumber("25252567348");
        customer.setPhoneNumber("5446321287");

        CustomerDto response = new CustomerDto();
        response.setIdentificationNumber(customer.getIdentificationNumber());
        response.setPhoneNumber(customer.getPhoneNumber());

        when(mockCustomerRepository.findById(anyLong())).
                thenReturn(Optional.of(customer));

        //when
        CustomerDto actual = underTest.findById(1);

        //then
        assertEquals(response,actual);
    }

    @Test
    void getById_itShouldThrowNotFound_whenCustomerIdNotFound(){

        //given
        when(mockCustomerRepository.findById(anyLong())).
                thenReturn(Optional.empty());

        //when
        //Executable executable = () -> underTest.findById(1L);

        //then
        //assertThrows(EntityNotFoundException.class,executable);

    }

    @Test
    void deleteById_itShouldReturnStatusNotFound_whenCustomerIdNotExist(){

        //given
        when(mockCustomerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        //Executable executable = () -> underTest.deleteById(1L);

        // then
        //assertThrows(EntityNotFoundException.class, executable);
    }

}