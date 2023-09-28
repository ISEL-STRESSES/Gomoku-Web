package gomoku.server.validation

import gomoku.server.domain.user.UserDetails
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [SafePasswordValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class SafePassword(
    val message: String = "Unsafe password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class SafePasswordValidator : ConstraintValidator<SafePassword, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return false
        val regex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&#])[A-Za-z\\d\$@\$!%*?&#]{${UserDetails.MIN_PASSWORD_SIZE},${UserDetails.MAX_PASSWORD_SIZE}}\$"
        return value.matches(regex.toRegex())
    }
}
