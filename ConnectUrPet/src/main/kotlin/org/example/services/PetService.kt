package org.example.services

import org.example.repositories.*
import org.example.dtos.*
import org.example.dtos.PetVaccine
import org.example.dtos.Vaccine
import org.example.entities.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PetService {
    fun speciesAndBreeds(): SpeciesAndBreedsDTO
    fun petVaccines(petVaccinesRequest: PetVaccinesRequest): PetVaccinesDTO
    fun allVaccines(petVaccinesRequest: PetVaccinesRequest): VaccinesDTO
    fun allGiversPets(): AllGiversPetsDTO
    fun petsOfGiver(giver: PetsOfGiverRequest): PetsOfGiverDTO
    fun onePet(pet: OnePetRequest): OnePetDTO
    fun onePetEdit(pet: OnePetRequest): OnePetEditDTO
    fun editPet(pet: EditPetRequest): DefaultDTO
    fun addPet(pet: AddPetRequest): DefaultDTO
    fun addPetVaccines(pet: AddPetVaccineRequest): DefaultDTO
}

@Service
class AbstractPetService(
    @Autowired
    val petsRepository: PetsRepository,
    @Autowired
    val vaccinesRepository: VaccinesRepository,
    @Autowired
    val breedRepository: BreedsRepository,
    @Autowired
    val speciesRepository: SpeciesRepository,
    @Autowired
    private val petsOfGiverRepository: PetsOfGiverRepository,
    private val petVaccineRepository: PetVaccineRepository,
    private val giverRepository: GiverRepository
) : PetService {
    override fun speciesAndBreeds(): SpeciesAndBreedsDTO {
        try {
            val speciesList = speciesRepository.findAll()
            val breedsList = breedRepository.findAll()

            if (speciesList.isEmpty()) {
                throw NoSuchElementException("La lista de especies esta vacia o no se pudo recuperar.")
            }

            if (breedsList.isEmpty()) {
                throw NoSuchElementException("La lista de razas esta vacia o no se pudo recuperar.")
            }

            val speciesDTOList = speciesList.map { specie ->
                Species(
                    id = (specie.id ?: "").toString(),
                    name = specie.name ?: ""
                )
            }

            val breedsDTOList = breedsList.map { breed ->
                Breeds(
                    id = breed.id.toString(),
                    name = breed.name ?: "",
                    specieID = (breed.specie?.id ?: "").toString()
                )
            }

            return SpeciesAndBreedsDTO(
                species = speciesDTOList,
                breeds = breedsDTOList
            )

        } catch (e: Exception) {
            throw e
        }

    }

    override fun petVaccines(petVaccinesRequest: PetVaccinesRequest): PetVaccinesDTO {
        return PetVaccinesDTO(petVaccines = this.petVaccinesList(petVaccinesRequest.petID.toInt()))
    }

    override fun allVaccines(petVaccinesRequest: PetVaccinesRequest): VaccinesDTO {
        try {
            val petVaccines = this.petVaccinesList(petVaccinesRequest.petID.toInt())
            val responseVaccines = vaccinesRepository.findAll()

            if (responseVaccines.isEmpty()) {
                throw NoSuchElementException("La lista de vacunas esta vacia o no se pudo recuperar.")
            }

            val allVaccines = responseVaccines.map { vaccine ->
                Vaccine(vaccine.name ?: "", vaccine.id.toString())
            }

            return VaccinesDTO(petVaccines = petVaccines, allVaccines = allVaccines)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun allGiversPets(): AllGiversPetsDTO {
        try {
            val petsOfGiverList = petsOfGiverRepository.findAll()

            if (petsOfGiverList.isEmpty()) {
                throw NoSuchElementException("No se pudieron recuperar las mascotas de los giver.")
            }

            val allPetsIds = petsOfGiverList.map { it.pet.id }

            val allPets = petsRepository.findAllById(allPetsIds)

            if (allPets.isEmpty()) {
                throw NoSuchElementException("La lista de mascotas no se pudo recuperar.")
            }

            val petData = allPets.map { pet ->
                PetData(id = pet.id.toString(), details = this.petDetail(pet))
            }

            return AllGiversPetsDTO(petData = petData)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun petsOfGiver(giver: PetsOfGiverRequest): PetsOfGiverDTO {
        try {
            val response = petsOfGiverRepository.findPetsByGiverId(giver.giverID.toInt())

            if (response.isEmpty()) {
                throw NoSuchElementException("La lista de mascotas del giver de id ${giver.giverID} no se pudo recuperar.")
            }
            val giverDetails = response.map { pet ->
                PetsOfGiverDetails(petID = pet.id.toString(), petData = this.petDetail(pet))
            }

            return PetsOfGiverDTO(petsData = giverDetails)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onePet(pet: OnePetRequest): OnePetDTO {
        try {

            val response = petsRepository.findById(pet.petID.toInt()).orElse(null)
                ?: throw NoSuchElementException("La mascota de id ${pet.petID} no se pudo recuperar.")
            val giverID = petsOfGiverRepository.findGiverByPetId(pet.petID.toInt())

            return OnePetDTO(petData = this.petDetail(response), giverID = giverID.toString())
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onePetEdit(pet: OnePetRequest): OnePetEditDTO {
        try {
            val response = petsRepository.findById(pet.petID.toInt()).orElse(null)
                ?: throw NoSuchElementException("La mascota de id ${pet.petID} no se pudo recuperar.")

            val breeds = breedRepository.findAll().map { breed ->
                Breeds(id = breed.id.toString(), name = breed.name ?: "", specieID = breed.specie?.id.toString())
            }

            if (breeds.isEmpty()) {
                throw NoSuchElementException("La lista de razas esta vacia o no se pudo recuperar.")
            }

            val species = speciesRepository.findAll().map { specie ->
                Species(id = specie.id.toString(), name = specie.name ?: "")
            }

            if (species.isEmpty()) {
                throw NoSuchElementException("La lista de especies esta vacia o no se pudo recuperar.")
            }

            return OnePetEditDTO(petData = this.petDetail(response), breeds = breeds, species = species)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun editPet(pet: EditPetRequest): DefaultDTO {
        try {
            val onePet = petsRepository.findById(pet.petID.toInt()).orElse(null)
            val newBreed = breedRepository.findById(pet.breed.id.toInt()).orElse(null)

            if (onePet == null) {
                throw NoSuchElementException("La mascota de id ${pet.petID} no se pudo recuperar.")
            }

            if (newBreed == null) {
                throw NoSuchElementException("La raza de id ${pet.breed.id} no se pudo recuperar.")
            }

            onePet.apply {
                name = pet.name
                personality = pet.personality
                description = pet.description
                date = pet.bornDate
                size = pet.size
                sex = pet.sex
                image = "set_image"
                petVaccines = onePet.petVaccines
                breed = newBreed
            }

            petsRepository.save(onePet)
            return DefaultDTO(message = "Mascota actualizada correctamente")
        } catch (e: Exception) {
            throw e
        }
    }

    override fun addPet(pet: AddPetRequest): DefaultDTO {
        try {
            val giver = giverRepository.findGiverById(pet.idUser.toInt())
            val breed = breedRepository.findById(pet.breed.id.toInt()).orElse(null)

            if (giver == null) {
                throw NoSuchElementException("El giver no se pudo recuperar.")
            }

            if (breed == null) {
                throw NoSuchElementException("La raza de id ${pet.breed.id} no se pudo recuperar.")
            }

            val newPet = Pet(
                name = pet.name,
                personality = pet.personality,
                description = pet.description,
                date = pet.bornDate,
                size = pet.size,
                sex = pet.sex,
                image = "blank",
                petVaccines = null,
                breed = breed
            )

            val petOfGiver = PetOfGiver(
                pet = newPet,
                giver = giver
            )

            petsOfGiverRepository.save(petOfGiver)

            return DefaultDTO(message = "Mascota creada correctamente")
        } catch (e: Exception) {
            throw e
        }
    }

    @Transactional
    override fun addPetVaccines(pet: AddPetVaccineRequest): DefaultDTO {
        try {
            val petRep = petsRepository.findById(pet.petID.toInt()).orElse(null)
                ?: throw NoSuchElementException("La mascota de id ${pet.petID} no se pudo recuperar.")

            petVaccineRepository.deleteByPetId(pet.petID.toInt())

            pet.vaccinesID.map { vaccineId ->
                val vaccine = vaccinesRepository.findById(vaccineId.toInt()).orElse(null)
                petVaccineRepository.save(org.example.entities.PetVaccine(pet = petRep, vaccine = vaccine))
            }

            return DefaultDTO(message = "Vacunas actualizadas correctamente")
        } catch (e: Exception) {
            throw e
        }
    }

    fun petDetail(pet: Pet): PetDetail {

        val breedId = pet.breed?.id
        val breed = breedId?.let { breedRepository.findById(it).orElse(null) }

        val breedDetail = breed?.let {
            BreedDetail(id = it.id.toString(), name = it.name ?: "", specieName = pet.breed!!.specie?.name ?: "")
        } ?: BreedDetail("", "", "")

        val petVaccines = pet.id?.let { this.petVaccinesList(it) }

        return PetDetail(
            personality = pet.personality ?: "",
            size = pet.size ?: "",
            breed = breedDetail,
            sex = pet.sex ?: "",
            name = pet.name ?: "",
            description = pet.description ?: "",
            bornDate = pet.date ?: "",
            vaccines = petVaccines?.map { it.name } ?: emptyList()
        )
    }

    fun petVaccinesList(petID: Int): List<PetVaccine> {
        val response = petVaccineRepository.findByPetId(petID)

        val petVaccine = response.map { petVaccine ->
            PetVaccine(id = petVaccine.vaccine?.id.toString(), name = petVaccine.vaccine?.name ?: "")
        }
        return petVaccine
    }

}