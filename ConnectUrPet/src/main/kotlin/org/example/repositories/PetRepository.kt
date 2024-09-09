package org.example.repositories

import org.example.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PetsRepository : JpaRepository<Pet, Int> {
    fun findPetById(id: Int): Pet
}

@Repository
interface PetsOfGiverRepository : JpaRepository<PetOfGiver, Int> {
    @Query("SELECT p.pet FROM PetOfGiver p WHERE p.giver.id = :giverId")
    fun findPetsByGiverId(giverId: Int): List<Pet>
    @Query("SELECT p.giver.id FROM PetOfGiver p WHERE p.pet.id = :petId")
    fun findGiverByPetId(petId: Int): Int
}

@Repository
interface VaccinesRepository : JpaRepository<Vaccine, Int>

@Repository
interface PetVaccineRepository : JpaRepository<PetVaccine, Int>{
    fun findByPetId(petId: Int): List<PetVaccine>
    fun deleteByPetId(petId: Int)
}

@Repository
interface BreedsRepository : JpaRepository<Breed, Int>

@Repository
interface SpeciesRepository : JpaRepository<Specie, Int>