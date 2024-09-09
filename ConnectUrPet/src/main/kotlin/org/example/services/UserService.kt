package org.example.services


import org.example.*
import org.example.dtos.*
import org.example.repositories.*
import org.example.entities.*
import org.example.mappers.AdopterMapper
import org.example.mappers.GiverMapper
import org.example.mappers.ReactionMapper
import org.example.mappers.ReviewMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException
import jakarta.transaction.Transactional
import org.example.mappers.LoginRegisMapper
import org.example.dtos.UserLoginInput
import org.example.dtos.UserRegisterInput
import org.example.repositories.AdopterRepository
import org.example.repositories.GiverRepository
import org.example.repositories.RoleRepository
import org.example.repositories.UsersRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

interface UserService{
    fun findAdopterById(id: Int): AdopterResult?
    fun findGiverById(id: Int): GiverResult?
    fun findReviewsByGiverId(giverId: Int): List<ReviewResult>
    fun updateAdopter(adopterEdit: AdopterEditRequest): AdopterResult?
    fun updateGiver(giverEdit: GiverEditRequest): GiverResult?
    fun addReview(review: ReviewInput): ReviewResult?
    fun addReaction(reaction: ReactionDetails): ReactionResponse?
    fun findReactionsByGiverId(giverId: Int): List<ReactionResult>?
    fun deleteReactionById(id: Int): ReactionResponse?
    fun updateReaction(reactionUpdate: ReactionUpdateRequest): ReactionResponse?  // Nuevo método
    fun readNotification(notisRequest: NotisRequest): NotisResult
    fun deleteNotification(notisRequest: NotisRequest): DefaultDTO

}

@Service
class AbstractUserService(
    /*@Autowired
    val userRepository: UsersRepository,*/

    @Autowired
    val reviewRepository: ReviewRepository,

    @Autowired
    val adopterRepository: AdopterRepository,

    @Autowired
    val giverRepository: GiverRepository,

    @Autowired
    val petRepository: PetsRepository,

    @Autowired
    val reactionRepository: ReactionRepository,

    @Autowired
    val notisRepository: NotisRepository,

    @Autowired
    val giverMapper: GiverMapper,

    @Autowired
    val adopterMapper: AdopterMapper,

    @Autowired
    val reviewMapper: ReviewMapper,

    @Autowired
    val reactionMapper: ReactionMapper,

    ) : UserService {


    @Throws(NoSuchElementException::class)
    override fun findAdopterById(id: Int): AdopterResult? {
        val adopter =  adopterRepository.findAdopterById(id)

        if (adopter != null) {
            //val user = userRepository.findUserById(adopter.userInformation)
            return adopterMapper.userInformationToAdopterResult(adopter.userInformation)
        } else {
            throw NoSuchElementException(String.format("The Adopter not found!"))
        }

    }
    @Throws(NoSuchElementException::class)
    override fun findGiverById(id: Int): GiverResult? {
        val giver =  giverRepository.findGiverById(id)

        if (giver != null) {
            //val user = userRepository.findUserById()
            return giverMapper.userInformationToGiverResult(giver.userInformation)
        } else {
            throw NoSuchElementException(String.format("The Adopter not found!"))
        }
    }

    @Throws(NoSuchElementException::class)
    override fun updateAdopter(adopterEdit: AdopterEditRequest): AdopterResult? {
        val adopt = adopterRepository.findAdopterById(adopterEdit.id.toInt())

        if (adopt != null) {
            adopt.userInformation.name = adopterEdit.name
            adopt.userInformation.description = adopterEdit.description
            adopt.userInformation.email = adopterEdit.email
            adopt.userInformation.location = adopterEdit.location
            adopterRepository.save(adopt)

            return adopterMapper.userInformationToAdopterResult(adopt.userInformation)
        } else {
            throw NoSuchElementException(String.format("The Adopter not found!"))
        }
    }

    @Throws(NoSuchElementException::class)
    override fun updateGiver(giverEdit: GiverEditRequest): GiverResult? {
        val giver = giverRepository.findGiverById(giverEdit.id.toInt())

        if (giver != null) {
            giver.userInformation.name = giverEdit.name
            giver.userInformation.description = giverEdit.description
            giver.userInformation.email = giverEdit.email
            giver.userInformation.location = giverEdit.location

            giverRepository.save(giver)

            return giverMapper.userInformationToGiverResult(giver.userInformation)
        } else {
            throw NoSuchElementException(String.format("The Adopter not found!"))
        }
    }

    @Throws(NoSuchElementException::class)
    override fun findReviewsByGiverId(giverId: Int): List<ReviewResult> {
        val reviews = reviewRepository.findReviewsByGiverId(giverId)
        return reviews.map { reviewMapper.reviewToReviewResult(it) }
    }

    override fun addReview(review: ReviewInput): ReviewResult? {
        val reviewEntity = reviewMapper.reviewInputToReview(review)

        // Obtener Giver y Adopter desde sus repositorios
        val giver = giverRepository.findGiverById(review.giverId.toInt())
            ?: throw NoSuchElementException("Giver not found with id: ${review.giverId}")
        val adopter = adopterRepository.findAdopterById(review.adopterId.toInt())
            ?: throw NoSuchElementException("Adopter not found with id: ${review.adopterId}")

        // Asignar Giver y Adopter a la entidad Review
        reviewEntity.giver = giver
        reviewEntity.adopter = adopter

        val savedReview = reviewRepository.save(reviewEntity)

        return reviewMapper.reviewToReviewResult(savedReview)
    }

    override fun addReaction(reaction: ReactionDetails): ReactionResponse? {

        val reactionEntity = reactionMapper.reactionDetailsToReaction(reaction)
        // Fetch and assign the Pet, Adopter, and Giver entities
        val pet: Pet = petRepository.findPetById(reaction.pet)
        //?: throw NoSuchElementException("Pet not found with id: ${reaction.pet}")
        val adopter: Adopter = adopterRepository.findAdopterById(reaction.adopter.toInt())
            ?: throw NoSuchElementException("Adopter not found with id: ${reaction.adopter}")
        val giver: Giver = giverRepository.findGiverById(reaction.giver.toInt())
            ?: throw NoSuchElementException("Giver not found with id: ${reaction.giver}")


        // Print values to console
        println("Pet: $pet")
        println("Adopter: $adopter")
        println("Giver: $giver")

        reactionEntity.pet = pet
        reactionEntity.adopter = adopter
        reactionEntity.giver = giver

        // Save the reaction entity
        val savedReaction = reactionRepository.save(reactionEntity)
        val newNoti = Notification(view = false, reaction = reactionEntity, giver = giver)
        notisRepository.save(newNoti)

        // Convert the saved reaction to ReactionResponse and return it
        return reactionMapper.reactionToReactResponse(savedReaction)

    }
    override fun findReactionsByGiverId(giverId: Int): List<ReactionResult>? {
        val reaction = reactionRepository.findReactionByGiverId(giverId)
        return reaction.map { reactionMapper.reactionToReactionResult(it) }
    }
    override fun deleteReactionById(id: Int): ReactionResponse? {
        val reaction = reactionRepository.findReactionById(id)
        reactionRepository.delete(reaction)
        return reactionMapper.reactionToReactResponse(reaction)
    }

    override fun updateReaction(reactionUpdate: ReactionUpdateRequest): ReactionResponse? {
        val reaction = reactionRepository.findReactionById(reactionUpdate.id.toInt())

        reaction.match = reactionUpdate.match
        reaction.view = reactionUpdate.view

        val updatedReaction = reactionRepository.save(reaction)
        return reactionMapper.reactionToReactResponse(updatedReaction)
    }

    override fun readNotification(notisRequest: NotisRequest): NotisResult {
        try {
            val response = notisRepository.findAllByGiverId(notisRequest.id.toInt())
            val giver = giverRepository.findGiverById(notisRequest.id.toInt()) ?: throw NoSuchElementException("Giver not found with id: ${notisRequest.id}")

            if (response.isEmpty()){
                throw NoSuchElementException("La lista de notificaciones del giver de id ${giver.id} no se pudo recuperar.")
            }

            val notisList = response.map { it ->
                val reaction = it.giver.id?.let { it1 -> reactionRepository.findReactionByGiverId(it1) }
                val adopterName = reaction?.map { it.adopter?.userInformation?.name }?.get(0)

                Noti(message = "Nueva reaccion de $adopterName!", view = it.view) }

            return NotisResult(notisList = notisList)
        }
        catch (e: Exception) {
            throw e
        }
    }
    @org.springframework.transaction.annotation.Transactional
    override fun deleteNotification(notisRequest: NotisRequest): DefaultDTO {

        notisRepository.deleteById(notisRequest.id.toInt())
        return DefaultDTO(message = "Notificacion eliminada correctamente")
    }

}


interface RegisterService{

    /**
     * Registers a new user.
     *
     * @param userInfo the user registration input
     * @return the user login result
     */
    fun registerAdopter(userInfo: UserRegisterInput): UserLoginInput?

    fun registerGiver(userInfo: UserRegisterInput): UserLoginInput?

}



@Bean
fun passwordEncoder(): BCryptPasswordEncoder {
    return BCryptPasswordEncoder()
}

/*
@Configuration
    class SecurityConfig {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
*/

@Service
class AbstractRegisterService(
    @Autowired
    val userRepository: UsersRepository,

    @Autowired
    val adopterRepository: AdopterRepository,

    @Autowired
    val giverRepository: GiverRepository,

    @Autowired
    val loginRegisMapper: LoginRegisMapper,

    @Autowired
    val roleRepository: RoleRepository,

    @Autowired
    val passwordEncoder: BCryptPasswordEncoder  // Añadir BCryptPasswordEncoder

) : RegisterService {


    override fun registerAdopter(userInfo: UserRegisterInput): UserLoginInput? {

        val userToSave = loginRegisMapper.registerInputToUser(userInfo)

        // Obtener el rol y los privilegios asociados a ese rol
        val role = roleRepository.findById(1).orElseThrow { NoSuchElementException("Role not found") } //EL ID 1 es de Adopter

        // Asociar el usuario con el rol
        userToSave.roleList = setOf(role)
        userToSave.password = passwordEncoder.encode(userToSave.password)  // Encriptar la contraseña
        val user = userRepository.save(userToSave)

        val adopter = Adopter(userInformation = user)
        adopterRepository.save(adopter)
        return UserLoginInput(username = user.email, password = userInfo.password)
    }

    override fun registerGiver(userInfo: UserRegisterInput): UserLoginInput? {
        val userToSave = loginRegisMapper.registerInputToUser(userInfo)

        val role = roleRepository.findById(2).orElseThrow { NoSuchElementException("Role not found") } //EL ID 2 es de Giver
        userToSave.roleList = setOf(role)
        userToSave.password = passwordEncoder.encode(userToSave.password)
        val user = userRepository.save(userToSave)

        val giver = Giver(userInformation = user)
        giverRepository.save(giver)
        return UserLoginInput(username = user.email, password = userInfo.password)
    }
}


@Service
@Transactional
class AppUserDetailsService(
    @Autowired
    val userRepository: UsersRepository,
    @Autowired
    val roleRepository: RoleRepository,
    @Autowired
    val adopterRepository: AdopterRepository,
    @Autowired
    val giverRepository: GiverRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val userAuth: CustomUserDetails
        val user: UserInformation = userRepository.findByEmail(username).orElse(null)
            ?: return CustomUserDetails(
                "","", "", enabled  = true, accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true,
                getAuthorities(
                    listOf(
                        roleRepository.findByName("ROLE_USER").get()
                    )
                )
            )

        val authorities = getAuthorities(user.roleList ?: emptySet())

        val roleInfoId = getUserRoleInfo(user.id, authorities) //me entrega el id de adopter o giver


        userAuth = CustomUserDetails(
            roleInfoId, user.email, user.password, user.enabled,  accountNonExpired = true, credentialsNonExpired = true, accountNonLocked = true,
            getAuthorities(user.roleList!!.toMutableList())
        )

        return userAuth
    }

    private fun getAuthorities(roles: Collection<Role>): Collection<GrantedAuthority> {
        return roles.flatMap { role ->
            sequenceOf(SimpleGrantedAuthority(role.name)) +
                    role.privilegeList.map { privilege -> SimpleGrantedAuthority(privilege.name) }
        }.toList()
    }
    private fun getUserRoleInfo(userId: Int?, roles: Collection<GrantedAuthority>): String? {
        val roleNames = roles.map { it.authority }
        roleNames.forEach { roleName ->
            when (roleName) {
                "ROLE_ADOPTER" -> {
                    val adopter = adopterRepository.findByUserInformationId(userId).get()
                    return adopter.id.toString()
                }
                "ROLE_GIVER" -> {
                    val giver = giverRepository.findByUserInformationId(userId).get()
                    return giver.id.toString()
                }
            }
        }
        return null
    }
}


class CustomUserDetails(
    val id: String?,
    username: String?,
    password: String?,
    enabled: Boolean,
    accountNonExpired: Boolean,
    credentialsNonExpired: Boolean,
    accountNonLocked: Boolean,
    authorities: Collection<GrantedAuthority>
) : org.springframework.security.core.userdetails.User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)