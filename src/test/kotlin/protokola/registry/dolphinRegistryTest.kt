package protokola.registry

import com.winterbe.expekt.expect
import protokola.MessageBus
import java.time.LocalDate
import kotlin.test.Test

class DolphinRegistryTest {

    @Test
    fun `register`() {
        // given:
        val registry = DolphinRegistry()
        val bus = MessageBus()
        registry.dispatchTo(bus)

        // when:
        val person1 = Person("person 1", "city 1", LocalDate.of(1960, 1, 1))
        val person2 = Person("person 2", "city 2", LocalDate.of(1977, 6, 12))
        val person3 = Person("person 3", "city 3", LocalDate.of(1982, 11, 22))

        val movie1 = Movie("movie 1", 1999, Genre.ACTION)
        movie1.actors += person1
        movie1.actors += person2
        movie1.directors += person1
        movie1.soundtrack = Soundtrack(mutableListOf(
            Track("track 1", person3),
            Track("track 2", person3)
        ))

        val movie2 = Movie("movie 2", 2005, Genre.DRAMA)
        movie2.actors += person1

        println(movie1)
        println(movie2)
        registry.register(movie1)
        registry.register(movie2)

        // then:
        expect(registry.observables).to.have.keys(movie1, movie2)
    }

}

internal data class Movie(
    var title: String,
    var year: Int,
    var genre: Genre? = null,
    var soundtrack: Soundtrack? = null,

    val languages: MutableList<String> = mutableListOf(),
    val actors: MutableList<Person> = mutableListOf(),
    val directors: MutableList<Person> = mutableListOf(),
    val producers: List<Person> = listOf(),
    val writers: List<Person> = listOf()
)

internal data class Person(
    var name: String,

    val birthplace: String? = null,
    val birthdate: LocalDate? = null
)

internal enum class Genre {
    DRAMA, COMEDY, ACTION, ROMANCE, FAMILY, SCIFI
}

internal data class Soundtrack(
    val tracks: MutableList<Track> = mutableListOf()
)

internal data class Track(
    val title: String,

    val performer: Person? = null,
    val composer: Person? = null
)
