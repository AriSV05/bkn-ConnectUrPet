package org.example.dtos

data class Vaccine(
    val name: String,
    val id: String
)
data class PetVaccine(
    val id: String,
    val name: String
)


data class AddPetRequest(
    val idUser: String,
    val name: String,
    val personality: String,
    val description: String,
    val bornDate: String,
    val size: String,
    val breed: BreedEdit,
    val sex: String,
)

data class DefaultDTO(
    val message: String,
)

data class EditPetRequest(
    val petID: String,
    val name: String,
    val personality: String,
    val description: String,
    val bornDate: String,
    val size: String,
    val breed: BreedEdit,
    val sex: String,
)

data class AddPetVaccineRequest(
    val petID: String,
    val vaccinesID: List<String>
)

data class OnePetDTO(
    val petData: PetDetail,
    val giverID:String,
)

data class OnePetEditDTO(
    val petData: PetDetail,
    val breeds: List<Breeds>,
    val species: List<Species>
)

data class PetData(
    val id:String,
    val details: PetDetail
)

data class PetDetail(
    val personality: String,
    val size: String,
    val breed: BreedDetail,
    val sex: String,
    val name: String,
    val description: String,
    val bornDate: String,
    val vaccines: List<String>
)

data class BreedEdit(
    val id:String,
    val name:String
)

data class BreedDetail (
    val id:String,
    val name:String,
    val specieName:String
)

data class OnePetRequest(
    val petID: String,
)

data class PetsOfGiverRequest(
    val giverID: String
)

data class PetsOfGiverDTO(
    val petsData:List<PetsOfGiverDetails>,

    )

data class PetsOfGiverDetails(
    val petID: String,
    val petData: PetDetail
)

data class AllGiversPetsDTO(
    val petData: List<PetData>
)

data class VaccinesDTO(
    val petVaccines: List<PetVaccine>,
    val allVaccines: List<Vaccine>
)

data class PetVaccinesRequest(
    val petID: String,
)

data class PetVaccinesDTO(
    val petVaccines: List<PetVaccine>
)

data class SpeciesAndBreedsDTO(
    val breeds: List<Breeds>,
    val species: List<Species>
)

data class Species (
    val id:String,
    val name:String
)

data class Breeds (
    val id:String,
    val name:String,
    val specieID:String
)

data class RecommendationInput(
    val size: String,
    val breed: BreedDetail,
    val sex: String,
    val name: String,
    val bornDate: String
)

data class PetCareInfo(
    val nutrition: Nutrition,
    val exercise: Exercise,
    val grooming: Grooming,
    val healthCare: HealthCare
)

data class Nutrition(
    val foodType: String,
    val portionSize: String,
    val supplements: String
)

data class Exercise(
    val daily: String,
    val activities: String
)

data class Grooming(
    val brushing: String,
    val bathing: String,
    val earCleaning: String,
    val nailTrimming: String
)

data class HealthCare(
    val vaccination: String
)