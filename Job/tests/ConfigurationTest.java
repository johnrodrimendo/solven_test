import org.springframework.context.annotation.ComponentScan;

/**
 * Created by solven9 on 09/02/18.
 */

@ComponentScan({
        "com.affirm.common.dao",
        "com.affirm.common.model",
//        "com.affirm.common.service",
        "com.affirm.security.dao",
        "com.affirm.security.model",
        "com.affirm.security.service",
        "com.affirm.system.configuration",
        "com.affirm.abaco.client",
        "com.affirm.solven.client",
        "com.affirm.efl.model",
        "com.affirm.efl.util",
        "com.affirm.acceso",
        "com.affirm.acceso.util",
        "com.affirm.efectiva",
        "com.affirm.compartamos",
        "com.affirm.compartamos.util",
        "com.affirm.sendgrid.service"
})
public class ConfigurationTest {
}
