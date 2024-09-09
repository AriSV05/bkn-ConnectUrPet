package org.example.repositories

import org.example.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsersRepository : JpaRepository<UserInformation, Int>{
    fun findByEmail(@Param("email")email: String): Optional<UserInformation>

}

@Repository
interface AdopterRepository : JpaRepository<Adopter, Int> {

    fun findByUserInformationId(userInformationId: Int?): Optional<Adopter>
    fun findAdopterById(id: Int): Adopter?
}

@Repository
interface GiverRepository : JpaRepository<Giver, Int> {
    fun findByUserInformationId(userInformationId: Int?): Optional<Giver>
    fun findGiverById(id: Int): Giver?
}

@Repository
interface ReviewRepository : JpaRepository<Review, Int> {
    fun findReviewsByGiverId(giverID: Int): List<Review>
}

@Repository
interface ReactionRepository : JpaRepository<Reaction, Int> {
    fun findReactionById(id: Int): Reaction
    fun findReactionByGiverId(giverID: Int): List<Reaction>
}

@Repository
interface RoleRepository : JpaRepository<Role, Int>{
    fun findByName(@Param("name")name:String): Optional<Role>

}

@Repository
interface NotisRepository : JpaRepository<Notification, Int>{
    fun findAllByGiverId(id: Int): List<Notification>
}

/*@Repository
interface PrivilegeRepository : JpaRepository<Privilege, Int>*/

