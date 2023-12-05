package gomoku.server.validation

import gomoku.server.domain.user.User
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Annotation for safe password
 * @property message message to return if the password is not safe
 * @property groups groups of the annotations
 * @property payload payload
 * @see SafePasswordValidator
 * @see Constraint
 */
@MustBeDocumented
@Constraint(validatedBy = [SafePasswordValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SafePassword(
    val message: String = "Unsafe password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * Validator for safe password
 * @see SafePassword
 * @see ConstraintValidator
 */
class SafePasswordValidator : ConstraintValidator<SafePassword, String> {

    /**
     * Checks if the password is safe
     * @param value the password to check
     * @param context the context
     * @return true if the password is safe, false otherwise
     */
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return false
        val regex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&#])[A-Za-z\\d\$@\$!%*?&#]{${User.MIN_PASSWORD_SIZE},${User.MAX_PASSWORD_SIZE}}\$"
        return value.matches(regex.toRegex())
    }
}
