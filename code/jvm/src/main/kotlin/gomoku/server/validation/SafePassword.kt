package gomoku.server.validation

import gomoku.server.domain.User
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


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
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&#])[A-Za-z\\d\$@\$!%*?&#]{${User.MIN_PASSWORD_SIZE},${User.MAX_PASSWORD_SIZE}}\$"
        return value.matches(regex.toRegex())
    }
}
