package org.example.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_information")
data class UserInformation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    @Temporal(TemporalType.DATE)
    var createDate: Date,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var description: String? = null,
    var location: String? = null,
    var enabled: Boolean,
    var tokenExpired: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roleList: Set<Role>? = null,

    ){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserInformation) return false
        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }
    override fun toString(): String {
        return "Users(userID=$id, create_date=$createDate, name=$name, email=$email, password=$password, token_expired=$tokenExpired)"
    }
}

@Entity
@Table(name = "adopter")
data class Adopter(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    var userInformation: UserInformation,

    @OneToMany(mappedBy = "adopter")
    val reviews: List<Review>? = null,

    /*    @OneToMany(mappedBy = "adopter")
        val petsOfAdopters: List<PetOfAdopter>? = null*/
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Adopter) return false
        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }
    override fun toString(): String {
        return "Adopter(AdopterID=$id, userID=$userInformation)"
    }
}

@Entity
@Table(name = "giver")
data class Giver(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    var userInformation: UserInformation,

    @OneToMany(mappedBy = "giver")
    val reviews: List<Review>? = null,

    @OneToMany(mappedBy = "giver")
    val petsOfGivers: List<PetOfGiver>? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Giver) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?: 0
    }

    override fun toString(): String {
        return "Giver(GiverID=$id, userID=$userInformation)"
    }
}

@Entity
@Table(name = "review")
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var text: String? = null,
    var puntuation: Float? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giverID", nullable = false, referencedColumnName = "id")
    var giver: Giver? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopterID", nullable = false, referencedColumnName = "id")
    var adopter: Adopter? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Review) return false

        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }
    override fun toString(): String {
        return "Review(reviewID=$id, description=$text, score=$puntuation, giver=$giver, adopter=$adopter)"
    }
}

@Entity
@Table(name = "role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String? = null,

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_information_id", nullable = false, referencedColumnName = "id")
    var userInformation: UserInformation? = null,*/

    @ManyToMany
    @JoinTable(
        name = "role_privilege",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")]
    )
    var privilegeList: Set<Privilege>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false

        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }
    override fun toString(): String {
        return "Role(idRole=$id, name=$name)"
    }
}

@Entity
@Table(name = "privilege")
data class Privilege(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    var userList: Set<UserInformation>,

    @ManyToMany(fetch = FetchType.LAZY)
    var roleList: Set<Role>,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Privilege) return false

        return id == other.id
    }
    override fun hashCode(): Int {
        return id ?: 0
    }
    override fun toString(): String {
        return "Privilege(id=$id, name=$name)"
    }
}


@Entity
@Table(name = "reaction")
data class Reaction(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var match: Boolean? = false,
    var view: Boolean? = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petID", nullable = false, referencedColumnName = "id")
    var pet: Pet? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopterID", nullable = false, referencedColumnName = "id")
    var adopter: Adopter? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giverID", nullable = false, referencedColumnName = "id")
    var giver: Giver? = null,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Reaction) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Reaction(reactionID=$id)" //TODO: Agregar el resto de args
    }

}


@Entity
@Table(name = "notification")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    var view: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false, referencedColumnName = "id")
    var reaction: Reaction,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id", nullable = false, referencedColumnName = "id")
    var giver: Giver
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "Notification(notificationID=$id, view=$view, reaction=$reaction, giver=$giver)"
    }
}

