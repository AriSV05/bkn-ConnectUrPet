package org.example.entities

import jakarta.persistence.*

@Entity
@Table(name = "pet")
data class Pet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var image: String? = null,
    var name: String? = null,
    var size: String? = null,
    var date: String? = null,
    var personality: String? = null,
    var sex: String? = null,
    var description: String? = null,

    @OneToMany(mappedBy = "pet")
    var petVaccines: MutableList<PetVaccine>? = mutableListOf(),


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false, referencedColumnName = "id")
    var breed: Breed? = null,


    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pet) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Pets(petsID=$id, image=$image, name=$name, size=$size, date=$date, breed=$breed, personality=$personality, sex=$sex, description=$description)"
    }
}

@Entity
@Table(name = "pet_vaccine")
data class PetVaccine(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, referencedColumnName = "id")
    var pet: Pet? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false, referencedColumnName = "id")
    var vaccine: Vaccine? = null

){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PetVaccine) return false

        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "PetVaccine(id=$id, pet=$pet, vaccine=$vaccine)"
    }
}


@Entity
@Table(name = "specie")
data class Specie(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String? = null,

    @OneToMany(mappedBy = "specie")
    val vaccines: List<Vaccine>? = null,

    @OneToMany(mappedBy = "specie")
    val breeds: List<Breed>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Specie) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Species(specie=$id, name=$name)"
    }
}

@Entity
@Table(name = "breed")
data class Breed(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specie_id", nullable = false, referencedColumnName = "id")
    var specie: Specie? = null,

    var name: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Breed) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Breeds(breedID=$id, specie=$specie, name=$name)"
    }
}

@Entity
@Table(name = "vaccine")
data class Vaccine(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var name: String? = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specie_id", nullable = false, referencedColumnName = "id")
    var specie: Specie? = null,

    @OneToMany(mappedBy = "vaccine")
    val petvaccines: MutableList<PetVaccine>? = mutableListOf(),

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vaccine) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Vaccines(vaccineID=$id, specie=$specie )"
    }
}


@Entity
@Table(name = "pet_of_giver")
data class PetOfGiver(
    @Id
    var id: Int? = null,  //no necesita AUTO porque usa el id mapeado de pet

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var pet: Pet,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id", nullable = false, referencedColumnName = "id")
    var giver: Giver? = null


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PetOfGiver) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Vaccines(vaccineID=$id, pet=$pet, giver=$giver )"
    }
}