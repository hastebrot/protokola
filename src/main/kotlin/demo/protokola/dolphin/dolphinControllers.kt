package demo.protokola.dolphin

import com.canoo.platform.remoting.server.DolphinAction
import com.canoo.platform.remoting.server.DolphinController
import com.canoo.platform.remoting.server.DolphinModel
import com.canoo.platform.server.spring.DolphinPlatformApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.PropertySource
import javax.annotation.PostConstruct

fun main(args: Array<String>) {
    SpringApplication.run(FooServerApplication::class.java)
}

@DolphinPlatformApplication
@PropertySource("classpath:demo/protokola/dolphin/application.properties")
open class FooServerApplication : SpringBootServletInitializer()

@DolphinController(FooConstants.FOO_CONTROLLER_NAME)
@Suppress("unused")
open class FooController {
    @DolphinModel
    private lateinit var model: FooPropertyBean

    @PostConstruct
    fun init() {
        println("--- controller init: $model")
        model.stringProperty().onChanged { event ->
            println("--- string changed to '${event.newValue}' (from '${event.oldValue}').")
        }

        model.booleanProperty().onChanged { event ->
            println("--- boolean changed to '${event.newValue}' (from '${event.oldValue}').")
        }

        model.listObservable().onChanged { event ->
            println("--- list changed with '${event.changes}.")
        }
    }

    @DolphinAction
    fun reset() {
        model.string = null
    }
}