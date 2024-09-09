package org.example.webservices

import org.example.dtos.*
import org.example.services.GPTService
import org.example.services.PetService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${url.pet}")
class PetController(private val petService: PetService, private val gptService: GPTService) {

    @GetMapping("/speciesAndBreeds")
    @ResponseBody
    fun speciesAndBreeds() = petService.speciesAndBreeds()

    @PostMapping(
        "/petVaccines",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun petVaccines(@RequestBody petVaccinesRequest: PetVaccinesRequest) = petService.petVaccines(petVaccinesRequest)

    @PostMapping(
        "/vaccines",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun allVaccines(@RequestBody petVaccinesRequest: PetVaccinesRequest) = petService.allVaccines(petVaccinesRequest)

    @GetMapping("/giversPets")
    @ResponseBody
    fun allGiversPets() = petService.allGiversPets()

    @PostMapping(
        "/petsOfGiver",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun petsOfGiver(@RequestBody giver: PetsOfGiverRequest) = petService.petsOfGiver(giver)

    @PostMapping(
        "/onePet",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun onePet(@RequestBody pet: OnePetRequest) = petService.onePet(pet)

    @PostMapping(
        "/onePetEdit",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun onePetEdit(@RequestBody pet: OnePetRequest) = petService.onePetEdit(pet)

    @PostMapping(
        "/giverEditPet",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun giverEditPet(@RequestBody pet: EditPetRequest) = petService.editPet(pet)

    @PostMapping(
        "/addPetVaccines",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun addPetVaccines(@RequestBody pet: AddPetVaccineRequest) = petService.addPetVaccines(pet)

    @PostMapping(
        "/giverAddPet",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun giverAddPet(@RequestBody pet: AddPetRequest) = petService.addPet(pet)

    @PostMapping(
        "/recommendations",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun generateText(@RequestBody recommendationInput: RecommendationInput) = gptService.generateText(recommendationInput)

}


