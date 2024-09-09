package org.example

import org.example.entities.Pet
import org.example.entities.UserInformation
import org.example.repositories.PetsRepository
import org.example.repositories.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.jdbc.Sql

@Profile("initlocal")
@SpringBootTest
@Sql("/import-user.sql", "/import-pet.sql")

/**
 * This class will load the initial data into the database
 */

class LoadInitData (
    @Autowired
    val userRepository: UsersRepository,
    @Autowired
    val petsRepository: PetsRepository,
    ) {

        @Test
        fun testUsersFindAll() {
            val userList: List<UserInformation> = userRepository.findAll()
            Assertions.assertTrue(userList.size == 2)
        }

        @Test
        fun testPetsFindAll() {
            val petList: List<Pet> = petsRepository.findAll()
            Assertions.assertTrue(petList.size == 3)
        }

    }