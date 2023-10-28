package nl.kute.helper.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

// This annotation is meant for tests that simulate / demo filtering out JPA child entities
// It would be unnecessary overkill to pull the whole JPA tattle into the project just to show how to filter these out
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface Id {
}
